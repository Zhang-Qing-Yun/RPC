package com.qingyun.rpc.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description： 客户端和服务端之间进行通信的消息实体
 * @author: 張青云
 * @create: 2021-06-20 19:16
 **/
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RPCMessage implements Serializable {
    /**
     * 消息头
     */
    private Header header;

    /**
     * 消息体
     */
    private Object body;
}
