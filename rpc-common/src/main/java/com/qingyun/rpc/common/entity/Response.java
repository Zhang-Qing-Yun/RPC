package com.qingyun.rpc.common.entity;

import com.qingyun.rpc.common.enumeration.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @description： request的响应类型
 * @author: 張青云
 * @create: 2021-07-01 20:49
 **/
@Data
@AllArgsConstructor
public class Response implements Serializable {
    /**
     * 状态码
     */
    private int statusCode;

    /**
     * 说明
     */
    private String msg;

    /**
     * 返回的数据
     */
    private Object data;

    /**
     * 获取一个成功时的返回对象
     * @param data 数据
     * @return 成功的response
     */
    public static Response success(Object data) {
        return new Response(ResponseType.SUCCESS.getStatusCode(), ResponseType.SUCCESS.getMsg(), data);
    }

    /**
     * 构建一个失败时的返回对象
     * @param data 数据
     * @return 失败的response
     */
    public static Response fail(Object data) {
        return new Response(ResponseType.FAIL.getStatusCode(), ResponseType.FAIL.getMsg(), data);
    }
}
