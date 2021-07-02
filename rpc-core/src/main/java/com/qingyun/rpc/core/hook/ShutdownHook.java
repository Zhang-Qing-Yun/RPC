package com.qingyun.rpc.core.hook;

import com.qingyun.rpc.core.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * @description： 关闭前的钩子
 * @author: 張青云
 * @create: 2021-07-01 14:09
 **/
@Slf4j
public class ShutdownHook {
    //  单例
    private static final ShutdownHook shutdownHook = new ShutdownHook();

    private ShutdownHook() { }

    public static ShutdownHook getInstance() {
        return shutdownHook;
    }

    /**
     * 在JVM关闭前的操作
     * @param registry 注册中心
     */
    public void clearRegistry(ServiceRegistry registry) {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                //  删除所有本机在服务注册中心的全部服务实例
                registry.clearAllRegistry();
            }
        }));
        log.info("已添加钩子方法，将在JVM关闭时删除本机在注册中心的全部实例");
    }
}
