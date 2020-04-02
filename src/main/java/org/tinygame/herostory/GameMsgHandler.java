package org.tinygame.herostory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;


/**
 * 自定义的消息处理器
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {


    private static final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 用户字典  似乎并不是线程安全的
     */
    private static final Map<Integer,User> _userMap = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        //一个用户连上之后就会被加到这个_channelGroup里来
        _channelGroup.add(ctx.channel());
    }

    /**
     * 当客户端离线时调用这个函数
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        _channelGroup.remove(ctx.channel());
        //先拿到用户id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if(userId == null) {
            return;
        }

        _userMap.remove(userId);
        GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
        resultBuilder.setQuitUserId(userId);

        GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();
        _channelGroup.writeAndFlush(newResult);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        //经过protobuf的反序列化后此时的msg 已经不是 BinaryWebSocketFrame 了
        System.out.println("收到客户端消息，msgClazz="+msg.getClass().getName()+",msg="+msg);

        if(msg instanceof GameMsgProtocol.UserEntryCmd) {
            System.out.println("进来了 user");
            //从指令对象中获取用户 id和英雄形象
            GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) msg;
            int userId = cmd.getUserId();
            String heroActor = cmd.getHeroAvatar();

            //需要用builder放置字段
            GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
            resultBuilder.setUserId(userId);
            resultBuilder.setHeroAvatar(heroActor);

            //将用户加入到字典中
            User newUser = new User();
            newUser.userId = userId;
            newUser.heroAvatar = heroActor;
            _userMap.put(newUser.userId,newUser);

            //将用户Id 附着到Channel
            //移动消息不放用户id   如果加了用户id 消息逻辑不安全  我操作别人的移动，恶意修改，就变外挂了
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);


            //构建结果并发送  返回给客户端需要编码器
            GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
            _channelGroup.writeAndFlush(newResult);
            //System.out.println(_channelGroup.size());

        } else if(msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
            System.out.println("进来了 who else");
            GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();
            for(User currUser:_userMap.values()) {
                if(null == currUser) {
                    continue;
                }

                GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder =
                        GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
                userInfoBuilder.setUserId(currUser.userId);
                userInfoBuilder.setHeroAvatar(currUser.heroAvatar);
                resultBuilder.addUserInfo(userInfoBuilder);
            }
            GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
            ctx.writeAndFlush(newResult);
        } else if(msg instanceof GameMsgProtocol.UserMoveToCmd) {
            System.out.println("进来了 userMoveTocmd");
            Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

            if(null == userId) {
                return;
            }

            GameMsgProtocol.UserMoveToCmd cmd = (GameMsgProtocol.UserMoveToCmd) msg;

            GameMsgProtocol.UserMoveToResult.Builder resutBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
            resutBuilder.setMoveUserId(userId);
            resutBuilder.setMoveToPosX(cmd.getMoveToPosX());
            resutBuilder.setMoveToPosY(cmd.getMoveToPosY());

            GameMsgProtocol.UserMoveToResult newResult = resutBuilder.build();
            _channelGroup.writeAndFlush(newResult);
        }
    }
}
