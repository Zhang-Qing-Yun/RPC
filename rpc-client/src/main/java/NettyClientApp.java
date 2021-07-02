import com.qingyun.rpc.api.HelloService;
import com.qingyun.rpc.core.transport.client.ClientInvocationHandler;
import com.qingyun.rpc.core.transport.client.NettyClient;

import java.lang.reflect.Proxy;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-07-02 13:37
 **/
public class NettyClientApp {

    public static void main(String[] args) {
        NettyClient client = new NettyClient();
        HelloService helloService = (HelloService) Proxy.newProxyInstance(HelloService.class.getClassLoader(), new Class<?>[]{HelloService.class},
                new ClientInvocationHandler(client));
        System.out.println(helloService.sayHello("你好！"));
    }
}
