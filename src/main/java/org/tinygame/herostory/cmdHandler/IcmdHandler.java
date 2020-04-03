package org.tinygame.herostory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

public interface IcmdHandler<Tcmd extends GeneratedMessageV3> {

    /**
     * 处理指令
     * @param ctx
     * @param cmd
     */
    void handle(ChannelHandlerContext ctx, Tcmd cmd);

}
