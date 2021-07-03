package com.qingyun.rpc.core.registry;

import java.net.InetSocketAddress;

/**
 * @description： 服务发现
 * @author: 張青云
 * @create: 2021-06-26 23:25
 **/
public interface ServiceDiscovery {

    /**
     * 根据服务名从注册中心中寻找一个服务提供者
     * @param serviceName 服务名
     * @param requestId 请求的id
     * @return 服务提供者
     */
    InetSocketAddress lookupService(String serviceName, String requestId);
}
