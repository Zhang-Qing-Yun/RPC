import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;

import java.util.ArrayList;


/**
 * @description：
 * @author: 張青云
 * @create: 2021-06-22 23:59
 **/
@Slf4j
public class LogTest {
    public static void main(String[] args) {
//        log.info("123456");
//        ArrayList<String> list = new ArrayList<>();
//        list.hashCode();
//        "13".hashCode();


        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();

        list1.add("123");
        list1.add("456");
        list1.add("789");

        list2.add("123");
        list2.add("456");
        list2.add("789");

//        list2.add("789");
//        list2.add("456");
//        list2.add("123");

        System.out.println(list1);
        System.out.println(list1.hashCode());
        System.out.println(list2);
        System.out.println(list2.hashCode());

        Assert.assertEquals(list1.hashCode(), list2.hashCode());
    }
}
