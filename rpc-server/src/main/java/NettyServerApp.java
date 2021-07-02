import com.qingyun.rpc.api.HelloService;
import com.qingyun.rpc.core.serializer.Serializer;
import com.qingyun.rpc.core.transport.server.NettyServer;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-07-01 16:50
 **/
public class NettyServerApp {

    public static void main(String[] args) {
        NettyServer server = new NettyServer("127.0.0.1", 9998, Serializer.MARSHALLING);
        server.publishService(HelloService.class.getName(), new HelloServiceImpl());
        server.start();
    }
}
