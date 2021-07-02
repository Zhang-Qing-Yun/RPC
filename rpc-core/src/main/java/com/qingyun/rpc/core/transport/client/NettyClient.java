package com.qingyun.rpc.core.transport.client;

import com.qingyun.rpc.common.entity.RPCMessage;
import com.qingyun.rpc.common.entity.Request;
import com.qingyun.rpc.common.enumeration.ExceptionType;
import com.qingyun.rpc.common.enumeration.MessageType;
import com.qingyun.rpc.common.exception.RPCException;
import com.qingyun.rpc.core.codec.RPCMessageDecoder;
import com.qingyun.rpc.core.codec.RPCMessageEncoder;
import com.qingyun.rpc.core.loadbalancer.LoadBalancer;
import com.qingyun.rpc.core.loadbalancer.RandomLoadBalancer;
import com.qingyun.rpc.core.registry.NacosServiceDiscovery;
import com.qingyun.rpc.core.registry.ServiceDiscovery;
import com.qingyun.rpc.core.serializer.Serializer;
import com.qingyun.rpc.core.transport.client.handler.NettyRespClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @description： 使用Netty作为客户端
 * @author: 張青云
 * @create: 2021-07-01 18:55
 **/
@Slf4j
public class NettyClient implements RPCClient{
    private final EventLoopGroup group;
    private final Bootstrap b;

    private final Serializer serializer;
    private final LoadBalancer loadBalancer;
    private final ServiceDiscovery serviceDiscovery;
    private final ChannelManager channelManager;


    public NettyClient() {
        //  使用随机负载均衡机制作为默认负载均衡
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }

    public NettyClient(int serializerType) {
        //  使用随机负载均衡机制作为默认负载均衡
        this(serializerType, new RandomLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public NettyClient(int serializerType, LoadBalancer loadBalancer) {
        this.serializer = Serializer.getSerializerByCode(serializerType);
        this.loadBalancer = loadBalancer;
        this.serviceDiscovery = new NacosServiceDiscovery(this.loadBalancer);
        group = new NioEventLoopGroup();
        b = new Bootstrap();
        init(serializer);
        channelManager = new ChannelManager(b);
    }


    @Override
    public Object send(RPCMessage rpcMessage) {
        //  发送Request类型的消息
        if (rpcMessage.getHeader().getType() == MessageType.REQUEST_TYPE.getTypeId()) {
            return sendRequest(rpcMessage);
        }

        return null;
    }

    private void init(Serializer serializer) {
        b.group(group).channel(NioSocketChannel.class)
                //  开启了TCP的 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。
                .option(ChannelOption.TCP_NODELAY, true)  // 设置参数
                //  连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                    //TODO：心跳
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new RPCMessageDecoder())
                            .addLast(new RPCMessageEncoder(serializer))
                            .addLast(new NettyRespClientHandler());
            }
        });
    }

    /**
     * 发送Request类型的消息
     * @param rpcMessage 消息
     * @return CompletableFuture对象
     */
    private CompletableFuture<RPCMessage> sendRequest(RPCMessage rpcMessage) {
        CompletableFuture<RPCMessage> future = new CompletableFuture<>();
        Request request = (Request) rpcMessage.getBody();
        //  使用负载均衡机制从注册中心的所有服务提供者中选择一个
        InetSocketAddress address = serviceDiscovery.lookupService(request.getInterfaceName());
        Channel channel = channelManager.getOrCreateChannel(address);
        RequestsManager.addRequest(rpcMessage.getHeader().getId(), future);
        //  发送数据
        channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener)future1 -> {
            if (future1.isSuccess()) {
                log.info("客户端发送消息: 【{}】成功", rpcMessage.getHeader().getId());
            } else {
                //  关闭该通道
                future1.channel().close();
                future.completeExceptionally(future1.cause());
                RequestsManager.remove(rpcMessage.getHeader().getId());
                log.error("发送消息时有错误发生: ", future1.cause());
            }
        });
        return future;
    }
}
