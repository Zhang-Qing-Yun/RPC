package com.qingyun.rpc.core.registry;

import java.net.InetSocketAddress;

/**
 * @description： 向服务注册中心发布一个服务
 * @author: 張青云
 * @create: 2021-06-26 23:16
 **/
public interface ServiceRegistry {
    /**
     * 注册一个服务
     * @param serviceName 服务名
     * @param inetSocketAddress 提供服务的地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);

    /**
     * 删除本机在注册中心的所有服务实例
     */
    void clearAllRegistry();
}
