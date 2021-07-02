package com.qingyun.rpc.common.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.qingyun.rpc.common.enumeration.ExceptionType;
import com.qingyun.rpc.common.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @description： nacos的工具类
 * @author: 張青云
 * @create: 2021-06-25 22:48
 **/
@Slf4j
public class NacosUtils {
    //  nacos地址
    private static final String NACOS_ADDR = "8.140.166.139:8848";
    //  操作nacos的对象，static变量，只加载和初始化一次
    private static final NamingService nacosService;
    //  本机地址，即提供服务的生产者地址，在第一次向nacos中注册服务时初始化
    private static InetSocketAddress localAddr;
    //  本机对外提供的服务的集合
    private static final Set<String> serviceSet = new HashSet<>();

    static {
        nacosService = init();
    }

    //  初始化，连接到nacos
    private static NamingService init() {
        try {
            return NamingFactory.createNamingService(NACOS_ADDR);
        } catch (NacosException e) {
            log.error("连接nacos[{}]时出错", NACOS_ADDR);
            throw new RPCException(ExceptionType.CONN_NACOS_FAIL.getCode(), ExceptionType.CONN_NACOS_FAIL.getMessage());
        }
    }

    /**
     * 向nacos中注册一个服务
     * @param serviceName 服务名
     * @param address 提供服务的主机地址
     * @throws NacosException
     */
    public static void registerService(String serviceName, InetSocketAddress address) throws NacosException {
        nacosService.registerInstance(serviceName, address.getHostName(), address.getPort());
        localAddr = address;
        serviceSet.add(serviceName);
    }

    /**
     * 从nacos中获取指定服务的全部提供者
     * @param serviceName 服务名
     * @return 全部提供者
     * @throws NacosException
     */
    public static List<Instance> getAllInstances(String serviceName) throws NacosException {
        return nacosService.getAllInstances(serviceName);
    }

    /**
     * 从nacos中删除本机所提供的全部服务
     */
    public static void clearAllRegistry() {
        if(localAddr != null && !serviceSet.isEmpty()) {
            String host = localAddr.getHostName();
            int ip = localAddr.getPort();
            Iterator<String> iterator = serviceSet.iterator();
            while (iterator.hasNext()) {
                String name = iterator.next();
                try {
                    nacosService.deregisterInstance(name, host, ip);
                    iterator.remove();
                    log.info("注销服务[{}->{}:{}]成功", name, host, ip);
                } catch (NacosException e) {
                    log.error("注销服务[{}]失败", name);
                    //  这里不再抛出异常，失败就失败吧，不做任何处理
                }
            }
        }
    }
}
