package com.qingyun.rpc.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description： 返回状态
 * @author: 張青云
 * @create: 2021-07-01 20:53
 **/
@Getter
@AllArgsConstructor
public enum ResponseType {
    SUCCESS(200, "成功"),
    FAIL(500, "失败");

    private final int statusCode;
    private final String msg;
}
