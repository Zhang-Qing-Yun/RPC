package com.qingyun.rpc.core.serializer;

/**
 * @description： 序列化和反序列化接口
 * @author: 張青云
 * @create: 2021-06-24 12:47
 **/
public interface Serializer {
    //  序列化器的类型
    int MARSHALLING = 0;


    /**
     * 将一个对象序列化成二进制
     * @param object 待序列化的对象
     * @return 序列化后的二进制数据
     */
    byte[] serialize(Object object);

    /**
     * 反序列化成对象
     * @param bytes 二进制数据
     * @param clazz 将要反序列化成的对象类型
     * @return 反序列化后的对象
     */
    Object deserialize(byte[] bytes, Class<?> clazz);

    /**
     * 获取序列化方式的类型
     * @return 序列化方式的类型
     */
    int getCode();

    /**
     * 根据序列化的类型获取对应的
     * @param code 序列化的类型
     * @return 序列化器
     */
    static Serializer getSerializerByCode(int code) {
        switch (code) {
            case MARSHALLING:
                return new MarshallingSerializer();
            default:
                return null;
        }
    }
}
