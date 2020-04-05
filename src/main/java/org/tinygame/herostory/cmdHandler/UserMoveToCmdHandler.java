package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.MoveState;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class UserMoveToCmdHandler implements IcmdHandler<GameMsgProtocol.UserMoveToCmd>{

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        if (null == ctx
                || null == cmd) {
            return;
        }

        // 获取用户 Id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == userId) {
            return;
        }

        // 获取移动用户
        User moveUser = UserManager.getUserById(userId);
        if (null == moveUser) {
            System.out.println("未找到用户, userId = {}"+ userId);
            return;
        }

        // 获取移动状态
        MoveState mvState = moveUser.moveState;
        // 设置位置和开始时间
        mvState.fromPosX = cmd.getMoveFromPosX();
        mvState.fromPosY = cmd.getMoveFromPosY();
        mvState.toPosX = cmd.getMoveToPosX();
        mvState.toPosY = cmd.getMoveToPosY();
        mvState.startTime = System.currentTimeMillis();

        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveFromPosX(mvState.fromPosX);
        resultBuilder.setMoveFromPosY(mvState.fromPosY);
        resultBuilder.setMoveToPosX(mvState.toPosX);
        resultBuilder.setMoveToPosY(mvState.toPosY);
        resultBuilder.setMoveStartTime(mvState.startTime);

        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
