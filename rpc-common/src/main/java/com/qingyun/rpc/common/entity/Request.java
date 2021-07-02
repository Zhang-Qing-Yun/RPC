package com.qingyun.rpc.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description： 一个请求类型，远程调用类型
 * @author: 張青云
 * @create: 2021-06-20 18:59
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request implements Serializable {
    /**
     * 远程调用的服务的接口名
     */
    private String interfaceName;

    /**
     * 该接口的方法
     */
    private String methodName;

    /**
     * 方法的参数列表
     */
    private Object[] parameters;

    /**
     * 参数对应的类型
     */
    private Class<?>[] paramTypes;
}
