package com.qingyun.rpc.common.enumeration;

/**
 * @description： 有关消息类型的枚举类
 * @author 張青云
 * @create 2021-06-20 19:30
 **/
public enum MessageType {
    //  心跳请求
    HEARTBEAT_TYPE(0),
    //  客户端请求
    REQUEST_TYPE(1),
    //  服务端响应
    RESPONSE_TYPE(2);


    private final int typeId;

    MessageType(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }
}
