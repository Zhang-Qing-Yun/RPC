package com.qingyun.rpc.core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.qingyun.rpc.common.enumeration.ExceptionType;
import com.qingyun.rpc.common.exception.RPCException;
import com.qingyun.rpc.common.util.NacosUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @description： 使用nacos做服务注册中心
 * @author: 張青云
 * @create: 2021-06-26 23:18
 **/
@Slf4j
public class NacosServiceRegistry implements ServiceRegistry{
    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtils.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e) {
            log.error("向nacos中注册服务时出错");
            throw new RPCException(ExceptionType.REGISTRY_FAIL.getCode(), ExceptionType.REGISTRY_FAIL.getMessage());
        }
    }

    @Override
    public void clearAllRegistry() {
        NacosUtils.clearAllRegistry();
    }
}
