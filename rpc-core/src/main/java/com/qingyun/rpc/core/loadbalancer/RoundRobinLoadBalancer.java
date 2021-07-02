package com.qingyun.rpc.core.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description： 轮询负载均衡机制
 * @author: 張青云
 * @create: 2021-06-22 23:30
 **/
public class RoundRobinLoadBalancer implements LoadBalancer{
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public Instance select(List<Instance> instances) {
        if(index.get() >= instances.size()) {
            index.set(index.get() % instances.size());
        }
        Instance instance = instances.get(index.get());
        index.incrementAndGet();
        return instance;
    }
}
