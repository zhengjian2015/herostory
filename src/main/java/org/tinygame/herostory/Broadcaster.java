package org.tinygame.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public final class Broadcaster {

    /**
     * 客户端信道数组， 一定要使用static,否则无法实现群发
     */
    private static final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 私有化类默认构造器
     */
    private Broadcaster() {

    }

    /**
     * 添加信道
     * @param channel
     */
    public static void addChannel(Channel channel) {
        _channelGroup.add(channel);
    }

    /**
     * 删除信道
     * @param channel
     */
    public static void removeChannel(Channel channel) {
        _channelGroup.remove(channel);
    }

    /**
     * 广播信道
     */
    public static void broadcast(Object msg) {
        if(null == msg) {
            return;
        }

        _channelGroup.writeAndFlush(msg);
    }


}
