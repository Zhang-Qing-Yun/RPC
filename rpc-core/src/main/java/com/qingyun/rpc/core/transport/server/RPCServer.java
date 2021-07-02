package com.qingyun.rpc.core.transport.server;

/**
 * @description： RPC服务端
 * @author: 張青云
 * @create: 2021-06-26 23:04
 **/
public interface RPCServer {
    /**
     * 启动RPC服务端
     */
    void start();

    /**
     * 服务端向注册中心发布一个服务
     * @param serviceName 服务名
     * @param service 具体提供服务的对象
     * @param <T> 提供服务的类型
     */
    <T> void publishService(String serviceName, T service);
}
