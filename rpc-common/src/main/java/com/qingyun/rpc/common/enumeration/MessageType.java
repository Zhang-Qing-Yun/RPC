package com.qingyun.rpc.common.enumeration;

import com.qingyun.rpc.common.entity.Request;
import com.qingyun.rpc.common.entity.Response;

/**
 * @description： 有关消息类型的枚举类
 * @author 張青云
 * @create 2021-06-20 19:30
 **/
public enum MessageType {
    //  心跳请求
    PING(MessageType.PING_MESSAGE),
    //  客户端请求
    REQUEST_TYPE(MessageType.REQUEST_MESSAGE),
    //  服务端响应
    RESPONSE_TYPE(MessageType.RESPONSE_MESSAGE),
    //  心跳回复
    PONG(MessageType.PONG_MESSAGE);

    //  属性
    private final int typeId;

    //  常量
    private static final int PING_MESSAGE = 0;
    private static final int REQUEST_MESSAGE = 1;
    private static final int RESPONSE_MESSAGE = 2;
    private static final int PONG_MESSAGE = 3;


    MessageType(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }

    public static Class<?> getBodyType(int typeId) {
        switch (typeId) {
            case REQUEST_MESSAGE:
                return Request.class;
            case RESPONSE_MESSAGE:
                return Response.class;
            default:
                return null;
        }
    }
}
