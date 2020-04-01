package org.tinygame.herostory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 自定义的消息处理器
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        //经过protobuf的反序列化后此时的msg 已经不是 BinaryWebSocketFrame 了
        System.out.println("收到客户端消息，msgClazz="+msg.getClass().getName()+",msg="+msg);


    }
}
