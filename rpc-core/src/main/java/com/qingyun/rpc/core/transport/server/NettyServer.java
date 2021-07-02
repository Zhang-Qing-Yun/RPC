package com.qingyun.rpc.core.transport.server;

import com.qingyun.rpc.common.enumeration.ExceptionType;
import com.qingyun.rpc.common.exception.RPCException;
import com.qingyun.rpc.core.codec.RPCMessageDecoder;
import com.qingyun.rpc.core.codec.RPCMessageEncoder;
import com.qingyun.rpc.core.hook.ShutdownHook;
import com.qingyun.rpc.core.provide.ServiceProvideImpl;
import com.qingyun.rpc.core.registry.NacosServiceRegistry;
import com.qingyun.rpc.core.serializer.Serializer;
import com.qingyun.rpc.core.transport.server.handler.HeartBeatServerHandler;
import com.qingyun.rpc.core.transport.server.handler.NettyRequestServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static com.qingyun.rpc.core.provide.ServiceProvide.DEFAULT_SERIALIZER;

/**
 * @description： 使用Netty作为RPC的服务端
 * @author: 張青云
 * @create: 2021-06-27 09:35
 **/
@Slf4j
public class NettyServer extends AbstractRPCServer{
    //  序列化方式
    private final Serializer serializer;

    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public NettyServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProvideImpl();
        this.serializer = Serializer.getSerializerByCode(serializer);
    }

    @Override
    public void start() {
        //  添加钩子方法
        ShutdownHook.getInstance().clearRegistry(serviceRegistry);
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)  // 设置参数
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(10, 0, 0))  // 30秒未收到消息则自动关闭链路
                                    .addLast(new RPCMessageDecoder())
                                    .addLast(new RPCMessageEncoder(serializer))
                                    .addLast(new HeartBeatServerHandler())
                                    .addLast(new NettyRequestServerHandler());
                        }
                    });
            //  绑定端口，同步等待成功
            ChannelFuture future = b.bind(host, port).sync();
            //  等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动Netty服务端时发生错误");
            throw new RPCException(ExceptionType.NETTY_START_FAIL.getCode(), ExceptionType.NETTY_START_FAIL.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
