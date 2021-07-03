package com.qingyun.rpc.core.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description： 一致性哈希算法
 * @author: 張青云
 * @create: 2021-07-03 23:03
 **/
public class ConsistentHashLoadBalancer implements LoadBalancer{
    //  保存所有服务对应的哈希环
    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    public Instance select(List<Instance> instances, String serviceName, String requestId) {
        //  服务列表的hash值，当服务列表发生变化时hash值也会发生变化
        int identityHashCode = instances.hashCode();

        //  获取该服务对应的hash环
        ConsistentHashSelector selector = selectors.get(serviceName);
        //  检查服务列表是否发生了变化
        //  如果发生了变化则重新构建哈希环，但是原来已有的节点的位置(hash值)不会发生变化
        if (selector == null || selector.identityHashCode != identityHashCode) {
            selectors.put(serviceName, new ConsistentHashSelector(instances, 160, identityHashCode));
            selector = selectors.get(serviceName);
        }
        String[] instance = selector.select(requestId).split(":");
        Instance res = new Instance();
        res.setIp(instance[0]);
        res.setPort(Integer.parseInt(instance[1]));
        return res;
    }

    //  一个服务的哈希环
    private static class ConsistentHashSelector {
        //  使用TreeMap来充当哈希环
        private final TreeMap<Long, String> virtualInvokers;
        //  标志，用来检查该哈希环对应的服务的所有提供者是否发生改变
        private final int identityHashCode;

        ConsistentHashSelector(List<Instance> invokers, int replicaNumber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;

            //  初始化哈希环
            for (Instance instance : invokers) {
                //  真实节点的ip和地址
                String invoker = instance.getIp() + ":" + instance.getPort();
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = md5(invoker + i);
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        //  添加一个虚拟节点
                        virtualInvokers.put(m, invoker);
                    }
                }
            }
        }

        //  使用md5对key进行一次计算，降低冲突概率
        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            return md.digest();
        }

        //  计算hash值，该算法来自于Dubbo
        static long hash(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }

        //  从该服务对应的哈希环上选择一个节点
        public String select(String requestId) {
            //  先对请求id计算md5，再计算hash值，然后选择大于等于该hash值的第一个节点
            byte[] digest = md5(requestId);
            return selectForKey(hash(digest, 0));
        }

        public String selectForKey(long hashCode) {
            Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();

            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }

            return entry.getValue();
        }
    }
}
