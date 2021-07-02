package com.qingyun.rpc.core.transport.client;

import com.qingyun.rpc.common.entity.RPCMessage;
import com.qingyun.rpc.core.serializer.Serializer;

/**
 * @description： RPC客户端
 * @author: 張青云
 * @create: 2021-06-26 23:04
 **/
public interface RPCClient {

    //  默认的序列化方式
    int DEFAULT_SERIALIZER = Serializer.MARSHALLING;


    /**
     * 发送消息
     * @param rpcMessage 消息内容
     * @return 远程调用的返回值
     */
    Object send(RPCMessage rpcMessage);
}
