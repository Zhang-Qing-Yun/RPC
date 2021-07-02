package com.qingyun.rpc.core.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @description： 负载均衡选择器
 * @author: 張青云
 * @create: 2021-06-22 23:07
 **/
public interface LoadBalancer {

    /**
     * 从全部元素中通过负载均衡机制选择一个
     * @param instances 全部元素
     * @return
     */
    Instance select(List<Instance> instances);
    //  TODO: 哈希一致性算法
}
