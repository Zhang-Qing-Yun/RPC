package com.qingyun.rpc.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description： 自定义异常类
 * @author: 張青云
 * @create: 2021-06-24 13:19
 **/
@Data
@AllArgsConstructor
public class RPCException extends RuntimeException{
    private Integer code;
    private String msg;
}