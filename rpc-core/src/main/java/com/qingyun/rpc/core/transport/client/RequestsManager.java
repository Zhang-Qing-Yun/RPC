package com.qingyun.rpc.core.transport.client;

import com.qingyun.rpc.common.entity.RPCMessage;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description： 管理正在执行远程调用中的request类型消息的集合
 * @author: 張青云
 * @create: 2021-07-01 23:51
 **/
public class RequestsManager {
    private static final Map<String, CompletableFuture<RPCMessage>> requests = new ConcurrentHashMap<>();

    public static void addRequest(String requestId, CompletableFuture<RPCMessage> future) {
        requests.put(requestId, future);
    }

    public static void remove(String requestId) {
        requests.remove(requestId);
    }

    public static void complete(RPCMessage rpcMessage) {
        CompletableFuture<RPCMessage> future = requests.remove(rpcMessage.getHeader().getId());
        if (future != null) {
            //  添加执行结果，使得等待在该对象上的地方获得结果并解除阻塞
            future.complete(rpcMessage);
        }
    }
}
