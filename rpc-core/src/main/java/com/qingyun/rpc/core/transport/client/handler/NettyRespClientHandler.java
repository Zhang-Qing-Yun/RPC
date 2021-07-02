package com.qingyun.rpc.core.transport.client.handler;

import com.qingyun.rpc.common.entity.RPCMessage;
import com.qingyun.rpc.common.enumeration.MessageType;
import com.qingyun.rpc.core.transport.client.RequestsManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @description： 处理远程调用的返回结果的handle
 * @author: 張青云
 * @create: 2021-07-01 21:04
 **/
@Slf4j
public class NettyRespClientHandler extends SimpleChannelInboundHandler<RPCMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCMessage rpcMessage) throws Exception {
        //  判断是不是response
        if (rpcMessage.getHeader() != null && rpcMessage.getHeader().getType() == MessageType.RESPONSE_TYPE.getTypeId()) {
            log.info("客户端接收到请求【requestId={}】的返回值", rpcMessage.getHeader().getId());
            RequestsManager.complete(rpcMessage);
        } else {
            //  不是response类型则直接透传
            ctx.fireChannelRead(rpcMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
