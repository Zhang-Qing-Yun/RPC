package com.qingyun.rpc.core.transport.client.handler;

import com.qingyun.rpc.common.entity.RPCMessage;
import com.qingyun.rpc.common.enumeration.MessageType;
import com.qingyun.rpc.core.transport.client.HeartBeatTask;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @description： 客户端心跳机制的处理器
 * @author: 張青云
 * @create: 2021-07-02 19:02
 **/
@Slf4j
public class HeartBeatClientHandler extends SimpleChannelInboundHandler<RPCMessage> {
    private volatile ScheduledFuture<?> heartBeat;
    /**
     * 通道就绪时，启动定时任务
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        //  创建定时任务
        heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCMessage msg) throws Exception {
        //  判断是不是Pong类型的消息
        if (msg.getHeader() != null && msg.getHeader().getType() == MessageType.PONG.getTypeId()) {
            log.info("客户端收到服务端发送的心跳回复信息【requestId：【{}】】", msg.getHeader().getId());
        } else {
            //  透传
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                //  在规定时间内没有收到客户端发送的数据, 主动断开连接
                log.info("长时间未收到心跳消息，断开连接！");
                if (heartBeat != null) {
                    heartBeat.cancel(true);
                    heartBeat = null;
                }
                ctx.close();
            } else {
                super.userEventTriggered(ctx, evt);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        cause.printStackTrace();
        ctx.close();
    }
}
