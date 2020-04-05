package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(null == msg || !(msg instanceof GeneratedMessageV3)) {
            super.write(ctx, msg, promise);
            return;
        }

        int msgCode = GameMessageRecognizer.getMsgCodeByMsgClazz(msg.getClass());

        if(msgCode <= -1){
            System.out.println("无法识别消息");
            return;
        }
        //System.out.println("看返回几次");

        byte[] byteArray = ((GeneratedMessageV3)msg).toByteArray();

        ByteBuf byteBuf = ctx.alloc().buffer();
        //用不上消息长度，先写一个0
        byteBuf.writeShort((short)0);
        byteBuf.writeShort((short)msgCode);
        byteBuf.writeBytes(byteArray);

        //收消息和发消息都用这个
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);
        super.write(ctx,frame,promise);

    }
}
