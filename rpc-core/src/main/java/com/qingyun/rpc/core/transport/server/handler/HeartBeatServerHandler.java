package com.qingyun.rpc.core.transport.server.handler;

import com.qingyun.rpc.common.entity.Header;
import com.qingyun.rpc.common.entity.RPCMessage;
import com.qingyun.rpc.common.enumeration.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @description： 服务端处理心跳消息的handler
 * @author: 張青云
 * @create: 2021-07-02 19:45
 **/
@Slf4j
public class HeartBeatServerHandler extends SimpleChannelInboundHandler<RPCMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCMessage msg) throws Exception {
        //  判断是不是Ping类型的消息
        if (msg.getHeader() != null && msg.getHeader().getType() == MessageType.PING.getTypeId()) {
            log.info("服务端收到客户端的心跳消息【requestId：【{}】】", msg.getHeader().getId());
            //  回送心跳应答消息
            Header header = new Header(msg.getHeader().getId(), MessageType.PONG.getTypeId());
            RPCMessage pong = new RPCMessage(header, null);
            ctx.writeAndFlush(pong);
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
        cause.printStackTrace();
        ctx.close();
    }
}
