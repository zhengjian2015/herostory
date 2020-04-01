package org.tinygame.herostory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class ServerMain {
    public static void main(String[] args) {
        //处理服务端连接的 类似大管家
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //bossGroup处理完塞给workGroup,类似服务员
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup,workGroup);
        b.channel(NioServerSocketChannel.class);// 服务器信道的处理方式
        b.childHandler(new ChannelInitializer<SocketChannel>(){ // 客户端信道的处理器方式
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new HttpServerCodec(),// Http 服务器编解码器
                        new HttpObjectAggregator(6553),// 内容长度限制
                        new WebSocketServerProtocolHandler("/websocket"), // WebSocket 协议处理器, 在这里处理握手、ping、pong 等消息
                        new GameMsgDecoder(),     // 自定义的消息解码器
                        new GameMsgEncoder(),     //自定义消息编码器
                        new GameMsgHandler()      // 自定义的消息处理器
                );
            }
        });

        try {
            // 绑定 12345 端口,
            // 注意: 实际项目中会使用 argArray 中的参数来指定端口号
            ChannelFuture f = b.bind(12345).sync();
            if(f.isSuccess()) {
                System.out.println("服务器启动成功");
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
