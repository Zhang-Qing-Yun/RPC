package com.qingyun.rpc.core.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * @description： 随机负载均衡机制
 * @author: 張青云
 * @create: 2021-06-22 23:11
 **/
public class RandomLoadBalancer implements LoadBalancer{
    @Override
    public Instance select(List<Instance> instances, String serviceName, String requestId) {
        int index = new Random().nextInt(instances.size());
        return instances.get(index);
    }
}
