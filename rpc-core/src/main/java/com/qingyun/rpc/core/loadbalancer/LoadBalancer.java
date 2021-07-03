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
     * 调用该方法的一定是Request类型的消息
     * @param instances 全部元素
     * @param serviceName 服务名
     * @param requestId 请求id
     * @return 实例
     */
    Instance select(List<Instance> instances, String serviceName, String requestId);
}
