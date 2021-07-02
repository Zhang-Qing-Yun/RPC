package com.qingyun.rpc.core.transport.client;

import com.qingyun.rpc.common.entity.Header;
import com.qingyun.rpc.common.entity.RPCMessage;
import com.qingyun.rpc.common.entity.Request;
import com.qingyun.rpc.common.entity.Response;
import com.qingyun.rpc.common.enumeration.ExceptionType;
import com.qingyun.rpc.common.enumeration.MessageType;
import com.qingyun.rpc.common.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @description： 动态代理的InvocationHandler，用于处理远程调用
 * @author: 張青云
 * @create: 2021-07-01 21:08
 **/
@Slf4j
public class ClientInvocationHandler implements InvocationHandler {
    //  真正的实现者
    private RPCClient target;

    public ClientInvocationHandler(RPCClient target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("远程调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        //  构建request类型的消息
        Header header = new Header(UUID.randomUUID().toString(), MessageType.REQUEST_TYPE.getTypeId());
        Request request = new Request(method.getDeclaringClass().getName(), method.getName(), args, method.getParameterTypes());
        RPCMessage rpcMessage = new RPCMessage(header, request);
        //  远程调用后的返回消息
        RPCMessage resMessage = null;
        if (target instanceof NettyClient) {
            CompletableFuture<RPCMessage> future = (CompletableFuture<RPCMessage>) target.send(rpcMessage);
            //  阻塞等待结果
            resMessage = future.get();
            if (resMessage == null || !resMessage.getHeader().getId().equals(rpcMessage.getHeader().getId())) {
                log.error("远程调用方法: {}#{}失败", method.getDeclaringClass().getName(), method.getName());
                throw new RPCException(ExceptionType.CALL_FAIL.getCode(), ExceptionType.CALL_FAIL.getMessage());
            }
            //  request类型的消息的返回消息一定为response类型
            Response response = (Response) resMessage.getBody();
            return response.getData();
        }
        return null;
    }
}
