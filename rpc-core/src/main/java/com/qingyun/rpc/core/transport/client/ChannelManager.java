package com.qingyun.rpc.core.transport.client;

import com.qingyun.rpc.common.enumeration.ExceptionType;
import com.qingyun.rpc.common.exception.RPCException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * @description： 管理本机已建立连接的channel
 * @author: 張青云
 * @create: 2021-07-01 21:40
 **/
@Slf4j
public class ChannelManager {
    private static final Map<String, Channel> channels = new ConcurrentHashMap<>();
    private final Bootstrap b;

    public ChannelManager(Bootstrap b) {
        this.b = b;
    }

    /**
     * 获取Channel，如果已有则直接返回，否则建立连接并保存channel
     * @param address 地址
     * @return channel
     */
    public Channel getOrCreateChannel(InetSocketAddress address) {
        String key = address.toString();
        Channel channel = null;
        //  判断是否已有
        if (channels.containsKey(key)) {
            channel = channels.get(key);
            //  判断channel是否可用
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channels.remove(key);
            }
        }
        //  建立连接
        try {
            channel = connect(b, address);
        } catch (ExecutionException | InterruptedException e) {
            log.error("客户端连接【{}】失败", address.toString());
            throw new RPCException(ExceptionType.CONN_SERVER_FAIL.getCode(), ExceptionType.CONN_SERVER_FAIL.getMessage());
        }
        channels.put(key, channel);
        return channel;
    }

    //  建立连接
    private Channel connect(Bootstrap b, InetSocketAddress address) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        b.connect(address).addListener((ChannelFutureListener) future -> {
            //  连接成功
            if (future.isSuccess()) {
                log.info("客户端连接【{}】成功", address.toString());
                //  给completableFuture添加执行结果，让所有等待在它上面的解除阻塞并获取到添加的结果
                completableFuture.complete(future.channel());
            } else {
                log.error("客户端连接【{}】失败", address.toString());
                throw new RPCException(ExceptionType.CONN_SERVER_FAIL.getCode(), ExceptionType.CONN_SERVER_FAIL.getMessage());
            }
        });
        return completableFuture.get();
    }
}
