package com.qingyun.rpc.core.transport.client;

import com.qingyun.rpc.common.entity.Header;
import com.qingyun.rpc.common.entity.RPCMessage;
import com.qingyun.rpc.common.enumeration.MessageType;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * @description： 心跳任务
 * @author: 張青云
 * @create: 2021-07-02 19:31
 **/
@Slf4j
public class HeartBeatTask implements Runnable{
    private final ChannelHandlerContext ctx;

    public HeartBeatTask(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        RPCMessage beatMessage = buildPingMessage();
        ctx.writeAndFlush(beatMessage).addListener(future -> {
            if (future.isSuccess()) {
                log.info("客户端向服务端发送了心跳消息【requestId：{}】", beatMessage.getHeader().getId());
            } else {
                log.error("心跳消息发送失败！");
            }
        });

    }

    /**
     * 构建一个心跳消息
     * @return 心跳消息
     */
    private RPCMessage buildPingMessage() {
        Header header = new Header(UUID.randomUUID().toString(), MessageType.PING.getTypeId());
        return new RPCMessage(header, null);
    }
}
