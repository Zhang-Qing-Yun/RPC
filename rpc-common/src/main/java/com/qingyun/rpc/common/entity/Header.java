package com.qingyun.rpc.common.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @description： 消息头
 * @author: 張青云
 * @create: 2021-06-20 19:18
 **/
@Data
@ToString
@NoArgsConstructor
public class Header implements Serializable {
    /**
     * 魔数，4个字节
     */
    private final int crcCode = 0xabcd0101;

    /**
     * 消息长度，该属性在编码的时候填充
     */
    private int length;

    /**
     * 消息的唯一id
     */
    private String id;

    /**
     * 消息类型
     */
    private int type;


    public Header(String id, int type) {
        this.id = id;
        this.type = type;
    }
}
