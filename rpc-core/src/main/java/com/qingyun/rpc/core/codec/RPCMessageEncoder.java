package com.qingyun.rpc.core.codec;

import com.qingyun.rpc.common.entity.RPCMessage;
import com.qingyun.rpc.common.enumeration.ExceptionType;
import com.qingyun.rpc.common.exception.RPCException;
import com.qingyun.rpc.core.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @description： 消息的编码器
 * @author: 張青云
 * @create: 2021-06-24 13:12
 **/
public class RPCMessageEncoder extends MessageToByteEncoder<RPCMessage> {
    //  序列化方式
    private final Serializer serializer;


    public RPCMessageEncoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RPCMessage msg, ByteBuf sendBuf) throws Exception {
        if(msg == null || msg.getHeader() == null) {
            throw new RPCException(ExceptionType.MESSAGE_IS_NULL.getCode(), ExceptionType.MESSAGE_IS_NULL.getMessage());
        }
        //  先编码序列化方式，这个不是 消息对象 里的数据
        sendBuf.writeInt(serializer.getCode());

        //  对消息头进行编码
        sendBuf.writeInt(msg.getHeader().getCrcCode());
        sendBuf.writeInt(msg.getHeader().getLength());  // 这里的长度会在编码结束后修改为总长度
        //  编码消息的id，String会编码成byte，先编码一个int来表示byte的大小
        byte[] id = msg.getHeader().getId().getBytes(StandardCharsets.UTF_8);
        sendBuf.writeInt(id.length);
        sendBuf.writeBytes(id);
        //  编码消息类型
        sendBuf.writeInt(msg.getHeader().getType());

        //  编码消息体
        if (msg.getBody() != null) {
            byte[] bodyArray = serializer.serialize(msg.getBody());
            sendBuf.writeInt(bodyArray.length);
            sendBuf.writeBytes(bodyArray);
        } else {
            //  消息体为空时，写入一个0
            sendBuf.writeInt(0);
        }

        //  修改消息总长度
        sendBuf.setInt(8, sendBuf.readableBytes());
    }
}
