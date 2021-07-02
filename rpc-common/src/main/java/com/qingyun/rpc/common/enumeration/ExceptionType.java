package com.qingyun.rpc.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-06-24 13:20
 **/
@Getter
@AllArgsConstructor
public enum ExceptionType {
    MESSAGE_IS_NULL(1, "消息或消息头不能为空"),
    SERIALIZE_FAIL(2, "序列化时出错"),
    DESERIALIZE_FAIL(3, "反序列化时出错"),
    UNKNOWN_PROTOCOL(4, "不识别的消息"),
    SERIALIZER_IS_NULL(5, "序列化器不能为空"),
    CONN_NACOS_FAIL(6, "连接nacos失败"),
    DEREGISTER_FAIL(7, "注销服务失败"),
    REGISTRY_FAIL(8, "注册失败"),
    PULL_SERVICE_FAIL(9, "从注册中心获取服务失败"),
    NO_SERVICE(10, "获取不到服务"),
    SERVICE_NOT_FIND(11, "服务不存在"),
    NETTY_START_FAIL(12, "netty服务端启动失败"),
    CALL_FAIL(13, "远程调用方法失败"),
    CONN_SERVER_FAIL(14, "连接服务端失败"),
    SEND_FAIL(15, "发送消息失败");

    private final int code;
    private final String message;
}
