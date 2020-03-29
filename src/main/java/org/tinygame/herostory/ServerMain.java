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
        b.channel(NioServerSocketChannel.class);
        b.childHandler(new ChannelInitializer<SocketChannel>(){
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new HttpServerCodec(),
                        new HttpObjectAggregator(6553),
                        new WebSocketServerProtocolHandler("/websocket")
                );
            }
        });

        try {
            ChannelFuture f = b.bind(12345).sync();
            if(f.isSuccess()) {
                System.out.println("服务器启动成功");
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
