import com.qingyun.rpc.api.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * @description： 远程调用方法真正的实现类，即服务的提供者
 * @author: 張青云
 * @create: 2021-06-30 23:34
 **/
@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String word) {
        log.info("服务端接收到客户端调用sayHello({})", word);
        return "服务端已处理sayHello(" + word +")";
    }
}
