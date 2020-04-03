package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class UserEntryCmdHandler implements IcmdHandler<GameMsgProtocol.UserEntryCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd msg) {
        System.out.println("进来了 user");
        //从指令对象中获取用户 id和英雄形象
        GameMsgProtocol.UserEntryCmd cmd = msg;
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
        UserManager.addUser(newUser);

        //将用户Id 附着到Channel
        //移动消息不放用户id   如果加了用户id 消息逻辑不安全  我操作别人的移动，恶意修改，就变外挂了
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);


        //构建结果并发送  返回给客户端需要编码器
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
        //System.out.println(_channelGroup.size());
    }
}
