import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;

@RunWith(JUnit4.class)
public class AviatorTest {

    @Test
    public void test1() {
        //发送规则表达式
        String expression = "(age > 35 && age < 75) && avgYear > 10000 && level == 1";
        //编译表达式
        Expression compileExp = AviatorEvaluator.compile(expression);
        //设置变量
        Map<String, Object> map = new HashMap<>();
        map.put("age", 40);
        map.put("level", 2);
        map.put("avgYear", 20000);
        //执行表达式
        System.out.println(compileExp.execute(map));//结果: false
    }

    //表达式传值
    @Test
    public void test(){
        //表达式传值
        Map<String, Object> env = new HashMap<>();
        env.put("name", "world");
        String str = "'hello ' + name";
        String r  = (String) AviatorEvaluator.execute(str, env);
        System.out.println(r);//hello world
    }

    @Test
    public void test3(){
        //算数表达式
        Long sum = (Long) AviatorEvaluator.execute("1 + 2 + 3");
        System.out.println(sum);//6
        //逻辑表达式
        boolean result = (boolean) AviatorEvaluator.execute("3 > 1");
        System.out.println(result);//true
        String r1  = (String) AviatorEvaluator.execute("100 > 80 ? 'yes' : 'no'");
        System.out.println(r1);//yes
    }

    @Test
    public void test4(){
        //算数表达式
        Object execute = AviatorEvaluator.execute("(1  & 0) == 0 ? println('open') : println('close')");
        System.out.println(execute);//6

    }

    //函数调用
//    @Test
//    public void function() {
//        //函数调用
//        Long r2  = (Long) AviatorEvaluator.execute("string.length('hello')");
//        System.out.println(r2);//5
//        //调用自定义函数
//        //注册函数
//        AviatorEvaluator.addFunction(new CustomFunction());
//        //调用函数
//        System.out.println(AviatorEvaluator.execute("add(2,3)"));//5.0
//        //删除函数
//        //AviatorEvaluator.removeFunction("multi");
//    }

}
