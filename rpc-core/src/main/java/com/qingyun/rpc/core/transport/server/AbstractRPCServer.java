package com.qingyun.rpc.core.transport.server;

import com.qingyun.rpc.core.provide.ServiceProvide;
import com.qingyun.rpc.core.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @description： 服务提供者
 * @author: 張青云
 * @create: 2021-06-27 09:33
 **/
@Slf4j
public abstract class AbstractRPCServer implements RPCServer {
    protected String host;
    protected int port;
    protected ServiceRegistry serviceRegistry;
    protected ServiceProvide serviceProvider;

    @Override
    public <T> void publishService(String serviceName, T service) {
        //  向注册中心注册一个服务
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
        //  本地保存一份服务
        serviceProvider.addService(serviceName, service);
        log.info("服务【{}】发布成功", serviceName);
    }
}
