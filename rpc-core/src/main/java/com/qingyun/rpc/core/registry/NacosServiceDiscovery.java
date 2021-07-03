package com.qingyun.rpc.core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.qingyun.rpc.common.enumeration.ExceptionType;
import com.qingyun.rpc.common.exception.RPCException;
import com.qingyun.rpc.common.util.NacosUtils;
import com.qingyun.rpc.core.loadbalancer.LoadBalancer;
import com.qingyun.rpc.core.loadbalancer.RandomLoadBalancer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @description： 注册中心时nacos的服务发现
 * @author: 張青云
 * @create: 2021-06-26 23:27
 **/
@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery{
    private LoadBalancer loadBalancer;

    public NacosServiceDiscovery() {
        //  使用随机负载均衡器作为默认的负载均衡机制
        loadBalancer = new RandomLoadBalancer();
    }

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName, String requestId) {
        try {
            List<Instance> instances = NacosUtils.getAllInstances(serviceName);
            if (instances.size() == 0) {
                log.info("获取不到服务[{}]", serviceName);
                throw new RPCException(ExceptionType.NO_SERVICE.getCode(), ExceptionType.NO_SERVICE.getMessage());
            }
            Instance instance = loadBalancer.select(instances, serviceName, requestId);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("获取服务列表失败");
            throw new RPCException(ExceptionType.PULL_SERVICE_FAIL.getCode(), ExceptionType.PULL_SERVICE_FAIL.getMessage());
        }
    }
}
