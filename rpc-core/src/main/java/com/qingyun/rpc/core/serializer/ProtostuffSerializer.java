package com.qingyun.rpc.core.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description：    使用protostuff来序列化一个Pojo对象
 *                  protostuff是基于protobuf的一种序列化方式，可以在几乎不影响性能的情况下，避免使用.proto文件而直接序列化POJO对象
 * @author: 張青云
 * @create: 2021-07-05 17:20
 **/
public class ProtostuffSerializer implements Serializer{

    //  缓存Schema，每个类对应一个Schema
    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();
    //  避免每次序列化都重新申请Buffer空间
    private static LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    @Override
    public byte[] serialize(Object obj) {
        Class clazz = obj.getClass();
        Schema schema = getSchema(clazz);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
        return data;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        Schema schema = getSchema(clazz);
        Object obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    @Override
    public int getCode() {
        return PROTOSTUFF;
    }

    private static Schema getSchema(Class clazz) {
        Schema schema = schemaCache.get(clazz);
        if (schema == null) {
            //这个schema通过RuntimeSchema进行懒创建并缓存
            //所以可以一直调用RuntimeSchema.getSchema(),这个方法是线程安全的
            schema = RuntimeSchema.getSchema(clazz);
            schemaCache.put(clazz, schema);
        }
        return schema;
    }
}
