import com.qingyun.rpc.common.entity.Header;
import com.qingyun.rpc.common.entity.RPCMessage;
import com.qingyun.rpc.common.entity.Request;
import com.qingyun.rpc.common.enumeration.MessageType;
import com.qingyun.rpc.core.serializer.MarshallingSerializer;
import com.qingyun.rpc.core.serializer.ProtostuffSerializer;
import com.qingyun.rpc.core.serializer.Serializer;
import org.junit.Before;
import org.junit.Test;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-06-24 22:52
 **/
public class SerializerTest {
    Serializer serializer = new ProtostuffSerializer();
    RPCMessage requestMsg = new RPCMessage();

    @Before
    public void init() {
        Header header = new Header("1", MessageType.REQUEST_TYPE.getTypeId());
        Request request = new Request();
        request.setMethodName("hello");
        request.setInterfaceName("helloService");
        request.setParameters(new Object[]{new String("123")});
        request.setParamTypes(new Class[]{String.class});
        requestMsg.setHeader(header);
        requestMsg.setBody(request);
    }

    @Test
    public void serializerTest() {
        byte[] bytes = serializer.serialize(requestMsg);
        System.out.println(bytes.length);
    }

    @Test
    public void deSerializerTest() {
        byte[] bytes = serializer.serialize(requestMsg);
        RPCMessage o = (RPCMessage)serializer.deserialize(bytes, RPCMessage.class);
        System.out.println(o);
    }

}
