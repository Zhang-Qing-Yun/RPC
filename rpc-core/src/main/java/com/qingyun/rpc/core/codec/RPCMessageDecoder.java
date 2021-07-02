package com.qingyun.rpc.core.codec;

import com.qingyun.rpc.common.entity.Header;
import com.qingyun.rpc.common.entity.RPCMessage;
import com.qingyun.rpc.common.enumeration.ExceptionType;
import com.qingyun.rpc.common.exception.RPCException;
import com.qingyun.rpc.core.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


/**
 * @description： 消息的解码器，并且可以解决粘包拆包问题
 * @author: 張青云
 * @create: 2021-06-24 23:11
 **/
@Slf4j
public class RPCMessageDecoder extends LengthFieldBasedFrameDecoder {
    private final int crcCode = 0xabcd0101;


    public RPCMessageDecoder() {
        super(1024*1024, 8, 4, -12, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        //  返回的是整包消息或空
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        //  如果是空则说明是个半包消息，直接返回继续读取后续的码流
        if (frame == null) {
            return null;
        }
        

        //  读取序列化方式
        int serializerType = frame.readInt();
        Serializer serializer = Serializer.getSerializerByCode(serializerType);
        if (serializer == null) {
            log.error("序列化器不能为空！");
            throw new RPCException(ExceptionType.SERIALIZER_IS_NULL.getCode(), ExceptionType.SERIALIZER_IS_NULL.getMessage());
        }
        //  读取魔数
        int magic = frame.readInt();
        if (crcCode != magic) {
            log.error("不识别的消息：{}", magic);
            throw new RPCException(ExceptionType.UNKNOWN_PROTOCOL.getCode(), ExceptionType.UNKNOWN_PROTOCOL.getMessage());
        }
        RPCMessage rpcMessage = new RPCMessage();
        Header header = new Header();
        header.setLength(frame.readInt());
        //  读入消息id
        int idArraySize = frame.readInt();
        byte[] array = new byte[idArraySize];
        frame.readBytes(array);
        header.setId(new String(array, StandardCharsets.UTF_8));
        //  读入消息类型
        header.setType(frame.readInt());
        //  读入附件
        int attachmentCount = frame.readInt();
        if (attachmentCount > 0) {
            Map<String, Object> attachment = new HashMap<>(attachmentCount);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            int valueSize = 0;
            byte[] valueArray = null;
            Object value = null;
            for(int i = 0; i < attachmentCount; i++) {
                keySize = frame.readInt();
                keyArray = new byte[keySize];
                frame.readBytes(keyArray);
                key = new String(keyArray, StandardCharsets.UTF_8);
                valueSize = frame.readInt();
                valueArray = new byte[valueSize];
                frame.readBytes(keyArray);
                Object valueObject = serializer.deserialize(valueArray, Object.class);
                attachment.put(key, valueObject);
            }
            header.setAttachment(attachment);
        }
        Object body = null;
        if(frame.readableBytes() > 4) {
            int bodySize = frame.readInt();
            byte[] bodyArray = new byte[bodySize];
            frame.readBytes(bodyArray);
            body = serializer.deserialize(bodyArray, Object.class);
            rpcMessage.setBody(body);
        }
        rpcMessage.setHeader(header);
        return rpcMessage;
    }
}
