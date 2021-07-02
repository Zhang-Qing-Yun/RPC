package com.qingyun.rpc.core.transport.server.handler;

import com.qingyun.rpc.common.entity.Header;
import com.qingyun.rpc.common.entity.RPCMessage;
import com.qingyun.rpc.common.entity.Request;
import com.qingyun.rpc.common.entity.Response;
import com.qingyun.rpc.common.enumeration.MessageType;
import com.qingyun.rpc.core.provide.ServiceProvide;
import com.qingyun.rpc.core.provide.ServiceProvideImpl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @description： Netty服务端的handler，用于处理Request
 * @author: 張青云
 * @create: 2021-07-01 14:39
 **/
@Slf4j
public class NettyRequestServerHandler extends SimpleChannelInboundHandler<RPCMessage> {
    //  本机的服务提供者
    private static final ServiceProvide provide;

    static {
        provide = new ServiceProvideImpl();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCMessage rpcMessage) throws Exception {
        //  判断是不是request
        if (rpcMessage.getHeader() != null && rpcMessage.getHeader().getType() == MessageType.REQUEST_TYPE.getTypeId()) {
            Request request = (Request) rpcMessage.getBody();
            log.info("接收到客户端调用请求:{}", request);
            Object res = handleRequest(request);
            Channel channel = ctx.channel();
            //  将调用结果返回给调用者
            if (channel.isActive() && channel.isWritable()) {
                //  构建返回的消息
                Header header = new Header(rpcMessage.getHeader().getId(), MessageType.RESPONSE_TYPE.getTypeId());
                Response body = Response.success(res);
                RPCMessage resMessage = new RPCMessage(header, body);
                channel.writeAndFlush(resMessage);
            } else {
                log.error("通道不可写，【id={}】调用结果未传给调用者", rpcMessage.getHeader().getId());
            }
        } else {
            //  不是request类型则直接透传
            ctx.fireChannelRead(rpcMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    /**
     * 处理一个服务调用
     * @param request 服务调用请求
     * @return 调用的结果
     */
    private Object handleRequest(Request request) {
        Object service = provide.getServiceByName(request.getInterfaceName());
        Object res = null;
        //  使用反射来调用方法
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            res = method.invoke(service, request.getParameters());
            log.info("请求【{}】调用成功", request);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.info("请求【{}】调用失败", request);
        }
        return res;
    }
}
