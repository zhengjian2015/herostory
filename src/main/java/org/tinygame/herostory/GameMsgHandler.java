package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.cmdHandler.*;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;
import sun.applet.Main;


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
        // 通过主线程处理器处理消息
        MainThreadProcessor.getInstance().process(ctx, (GeneratedMessageV3) msg);
        //new MainThreadProcessor().process(ctx, (GeneratedMessageV3) msg);
    }


}
