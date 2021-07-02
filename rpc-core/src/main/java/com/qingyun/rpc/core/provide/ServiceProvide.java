package com.qingyun.rpc.core.provide;

import com.qingyun.rpc.core.serializer.Serializer;

/**
 * @description： 保存一份本机提供的服务
 * @author: 張青云
 * @create: 2021-07-01 13:05
 **/
public interface ServiceProvide {
    //  默认的序列化方式
    int DEFAULT_SERIALIZER = Serializer.MARSHALLING;


    /**
     * 缓存一个服务
     * @param serviceName 服务名
     * @param service 服务提供者
     * @param <T> 服务提供者类型
     */
    <T> void addService(String serviceName, T service);

    /**
     * 根据服务名获取服务提供者
     * @param serviceName 服务名
     * @return 服务提供者
     */
    Object getServiceByName(String serviceName);
}
