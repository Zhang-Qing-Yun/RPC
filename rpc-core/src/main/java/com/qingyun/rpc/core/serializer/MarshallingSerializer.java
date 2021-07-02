package com.qingyun.rpc.core.serializer;

import com.qingyun.rpc.common.enumeration.ExceptionType;
import com.qingyun.rpc.common.exception.RPCException;
import lombok.extern.slf4j.Slf4j;
import org.jboss.marshalling.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @description： 使用marshalling来进行序列化
 * @author: 張青云
 * @create: 2021-06-24 13:06
 **/
@Slf4j
public class MarshallingSerializer implements Serializer{
    private final static MarshallingConfiguration configuration = new MarshallingConfiguration();
    //获取序列化工厂对象,参数serial标识创建的是java序列化工厂对象
    private final static MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");


    public MarshallingSerializer() {
        configuration.setVersion(5);
    }

    @Override
    public byte[] serialize(Object object) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            final Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
            marshaller.start(Marshalling.createByteOutput(byteArrayOutputStream));
            marshaller.writeObject(object);
            marshaller.finish();
        } catch (IOException e) {
            log.error("序列化时出错");
            throw new RPCException(ExceptionType.SERIALIZE_FAIL.getCode(), ExceptionType.SERIALIZE_FAIL.getMessage());
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            final Unmarshaller unmarshaller = marshallerFactory.createUnmarshaller(configuration);
            unmarshaller.start(Marshalling.createByteInput(byteArrayInputStream));
            Object object = unmarshaller.readObject();
            unmarshaller.finish();
            return object;
        } catch (Exception e) {
            log.error("反序列化时出错");
            throw new RPCException(ExceptionType.DESERIALIZE_FAIL.getCode(), ExceptionType.DESERIALIZE_FAIL.getMessage());
        }
    }

    @Override
    public int getCode() {
        return MARSHALLING;
    }
}
