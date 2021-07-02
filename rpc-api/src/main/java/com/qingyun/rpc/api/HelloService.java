package com.qingyun.rpc.api;

/**
 * @description: 客户端想要调用的接口，该接口在服务端进行实现
 * @author: 張青云
 * @create: 2021-06-20 18:29
 **/
public interface HelloService {

    String sayHello(String word);

}
