package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class UserMoveToCmdHandler implements IcmdHandler<GameMsgProtocol.UserMoveToCmd>{

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd msg) {
        System.out.println("进来了 userMoveTocmd");
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if(null == userId) {
            return;
        }

        GameMsgProtocol.UserMoveToCmd cmd = msg;

        GameMsgProtocol.UserMoveToResult.Builder resutBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
        resutBuilder.setMoveUserId(userId);
        resutBuilder.setMoveToPosX(cmd.getMoveToPosX());
        resutBuilder.setMoveToPosY(cmd.getMoveToPosY());

        GameMsgProtocol.UserMoveToResult newResult = resutBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
