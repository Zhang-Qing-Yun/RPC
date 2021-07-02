package com.qingyun.rpc.core.provide;

import com.qingyun.rpc.common.enumeration.ExceptionType;
import com.qingyun.rpc.common.exception.RPCException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @description： 保存本机提供的服务
 * @author: 張青云
 * @create: 2021-07-01 13:10
 **/
public class ServiceProvideImpl implements ServiceProvide{
    //  保存服务名及其提供者的映射关系
    private static final Map<String, Object> map = new ConcurrentHashMap<>();
    //  保存本机提供的服务
    private static final Set<String> serviceSet = new CopyOnWriteArraySet<>();


    @Override
    public <T> void addService(String serviceName, T service) {
        if (serviceSet.contains(serviceName)) {
            return;
        }
        map.put(serviceName, service);
        serviceSet.add(serviceName);
    }

    @Override
    public Object getServiceByName(String serviceName) {
        Object service = map.get(serviceName);
        if (service == null) {
            throw new RPCException(ExceptionType.SERVICE_NOT_FIND.getCode(), ExceptionType.SERVICE_NOT_FIND.getMessage());
        }
        return service;
    }
}
