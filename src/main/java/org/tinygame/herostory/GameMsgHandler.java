package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.cmdHandler.IcmdHandler;
import org.tinygame.herostory.cmdHandler.UserEntryCmdHandler;
import org.tinygame.herostory.cmdHandler.UserMoveToCmdHandler;
import org.tinygame.herostory.cmdHandler.WhoElseIsHereCmdHandler;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;


/**
 * 自定义的消息处理器
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        //一个用户连上之后就会被加到这个_channelGroup里来
        Broadcaster.addChannel(ctx.channel());
    }

    /**
     * 当客户端离线时调用这个函数
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        Broadcaster.removeChannel(ctx.channel());
        //先拿到用户id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if(userId == null) {
            return;
        }

        UserManager.removeUserById(userId);
        GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
        resultBuilder.setQuitUserId(userId);

        GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        //经过protobuf的反序列化后此时的msg 已经不是 BinaryWebSocketFrame 了
        System.out.println("收到客户端消息，msgClazz="+msg.getClass().getName()+",msg="+msg);

        IcmdHandler<? extends GeneratedMessageV3> cmdHandler = null;

        if(msg instanceof GameMsgProtocol.UserEntryCmd) {
            cmdHandler = new UserEntryCmdHandler();
        } else if(msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
            cmdHandler = new WhoElseIsHereCmdHandler();
        } else if(msg instanceof GameMsgProtocol.UserMoveToCmd) {
            cmdHandler = new UserMoveToCmdHandler();
        }
        if(null != cmdHandler) {
            cmdHandler.handle(ctx,cost(msg));
        }

    }

    //处理cmd
    private static <Tcmd extends GeneratedMessageV3> Tcmd cost(Object msg) {
        if(null == msg) {
            return null;
        } else {
            return (Tcmd) msg;
        }
    }
}
