package org.tinygame.herostory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 消息解码器
 * 服务端收到客户端的消息
 */
public class GameMsgDeoder extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        if(!(msg instanceof BinaryWebSocketFrame)) {
            return;
        }
        // WebSocket 二进制消息会通过 HttpServerCodec 解码成 BinaryWebSocketFrame 类对象
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
        ByteBuf byteBuf = frame.content();

        byteBuf.readShort(); //读取消息的长度
        int msgCode = byteBuf.readShort(); //读取消息的编号

        //拿到消息体
        byte[] msgBody = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(msgBody);

        switch (msgCode) {
            case GameMsgProtocol.MsgCode.USER_ATTK_CMD_VALUE:
                GameMsgProtocol.UserEntryCmd.parseFrom(msgBody);
                break;
        }
    }
}
