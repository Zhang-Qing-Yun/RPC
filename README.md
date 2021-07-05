# RPC

#### 介绍
基于Netty的手写RPC项目

#### 整体过程
0.服务提供者将服务发布到服务注册中心

1.客户端通过动态代理发起远程过程调用

2.客户端到注册中心去拉取要调用服务的所有提供者

3.客户端通过负载均衡算法选择一个服务，得到该提供者的地址

4.客户端查看是否和服务提供者建立过连接，如果有则直接获取到channel，否则通过Netty建立连接并保存channel

5.客户端使用该channel发送消息

6.服务端接收到消息，通过反射来调用相应的方法并执行

7.服务端返回方法执行结果