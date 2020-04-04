package org.tinygame.herostory;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 消息解码器
 * 服务端收到客户端的消息
 */
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!(msg instanceof BinaryWebSocketFrame)) {
            return;
        }
        // WebSocket 二进制消息会通过 HttpServerCodec 解码成 BinaryWebSocketFrame 类对象
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
        ByteBuf byteBuf = frame.content();

        byteBuf.readShort(); //读取消息的长度
        int msgCode = byteBuf.readShort(); //读取消息的编号

        // 获取消息构建者
        Message.Builder msgBuilder = GameMessageRecognizer.getBuilderByMsgCode(msgCode);
        if (null == msgBuilder) {
            System.out.println("无法识别的消息, msgCode = {}"+ msgCode);
            return;
        }

        //拿到消息体
        byte[] msgBody = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(msgBody);


        msgBuilder.clear();
        msgBuilder.mergeFrom(msgBody);

        Message newMsg = msgBuilder.build();

        //重新触发 channelRead函数
        if(newMsg != null) {
            ctx.fireChannelRead(newMsg);
        }

    }
}
