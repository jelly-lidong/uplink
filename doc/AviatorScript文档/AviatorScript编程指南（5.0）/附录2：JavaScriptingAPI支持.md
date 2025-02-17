# 附录2： Java Scripting API 支持

在 [6.3 节](https://www.yuque.com/boyan-avfmj/aviatorscript/xbdgg2)我们看到了如何在 AviatorScript 中调用 Java 函数，这里我们将介绍如何用 Java 提供的脚本 API 来调用 AviatorScript 脚本中的函数等。



AviatorScript 内置了对** **[**Java Scripting API**](https://docs.oracle.com/javase/7/docs/technotes/guides/scripting/programmer_guide/#evalfile) 的支持，并且提供了 `AviatorScriptEngineFactory` 的 SPI 实现，只要你的 classpath 包含了 aviator 的 jar 引用，就可以直接使用。我们来看一些例子（所有示例代码在源码 example 的 [scripting 目录](https://github.com/killme2008/aviator/tree/master/src/test/java/com/googlecode/aviator/example/scripting)）。



## 获取执行引擎


通过 `ScriptEngineManager` 可以获得 AviatorScript 的执行引擎：

```java
package com.googlecode.aviator.example.scripting;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptEngineExample {
  public static void main(final String[] args) {
    final ScriptEngineManager sem = new ScriptEngineManager();
    ScriptEngine engine = sem.getEngineByName("AviatorScript");

  }
}
```



接下来我们将使用这个 engine 做各种例子演示。



## 配置执行引擎
可以从 `ScriptEngine` 里获取底层的 `AviatorEvaluatorInstance` 引用，进行引擎的相关配置：

```java
package com.googlecode.aviator.example.scripting;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Feature;
import com.googlecode.aviator.Options;
import com.googlecode.aviator.script.AviatorScriptEngine;

public class ConfigureEngine {
  public static void main(final String[] args) throws Exception {
    final ScriptEngineManager sem = new ScriptEngineManager();
    ScriptEngine engine = sem.getEngineByName("AviatorScript");
    AviatorEvaluatorInstance instance = ((AviatorScriptEngine) engine).getEngine();
    // Use compatible feature set
    instance.setOption(Options.FEATURE_SET, Feature.getCompatibleFeatures());
    // Doesn't support if in compatible feature set mode.
    engine.eval("if(true) { println('support if'); }");
  }
}

```



默认的引擎处于下列模式：

1. 全语法特性支持
2. 缓存编译模式
3. [启用基于反射的 java 方法调用](https://www.yuque.com/boyan-avfmj/aviatorscript/xbdgg2#azo1K)



## 求值


最简单的，你可以直接执行一段 AviatorScript 脚本，调用 `eval(script)` 方法即可



```java
package com.googlecode.aviator.example.scripting;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class EvalScriptExample {
  public static void main(final String[] args) throws Exception {
    final ScriptEngineManager sem = new ScriptEngineManager();
    ScriptEngine engine = sem.getEngineByName("AviatorScript");
    engine.eval("print('Hello, World')");
  }
}

```



这将打印 `Hello, World` 到控制台，调用了 print 函数，



如果你的脚本是文件，也可以用 `eval(reader)` 方法：

```java
import javax.script.*;
public class EvalFile {
    public static void main(String[] args) throws Exception {
        // create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();
        // create AviatorScript engine
        ScriptEngine engine = factory.getEngineByName("AviatorScript");
        // evaluate AviatorScript code from given file - specified by first argument
        engine.eval(new java.io.FileReader(args[0]));
    }
}

```



文件名通过执行的第一个参数指定。



**默认引擎处于缓存表达式模式。**



## 注入变量
可以注入全局变量到脚本，并执行：

```java
package com.googlecode.aviator.example.scripting;

import java.io.File;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptVars {
  public static void main(final String[] args) throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("AviatorScript");

    File f = new File("test.txt");
    // expose File object as variable to script
    engine.put("file", f);

    // evaluate a script string. The script accesses "file"
    // variable and calls method on it
    engine.eval("print(getAbsolutePath(file))");
  }

}

```



这里我们将文件 `f` 通过 `engine.put` 方法作为全局变量注入，然后执行脚本 `print(getAbsolutePath(file))` ，打印文件的绝对路径。



**默认引擎启用了**[**基于 java 反射的方法调用模式。**](https://www.yuque.com/boyan-avfmj/aviatorscript/xbdgg2#azo1K)



## 编译脚本并执行


AviatorScript 也支持了 Scripting API 的预编译模式：



```java
package com.googlecode.aviator.example.scripting;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class CompileScript {
  public static void main(final String[] args) throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("AviatorScript");

    Compilable compilable = (Compilable) engine;
    CompiledScript script = compilable.compile("a + b");

    final Bindings bindings = engine.createBindings();
    bindings.put("a", 99);
    bindings.put("b", 1);
    System.out.println(script.eval(bindings));
  }

}
```



我们将表达式 `a+b` 编译成一个 `CompiledScript` 对象，接下来通过 `createBindings` 创建了一个环境绑定，将 a 和 b 分别绑定为 99 和 1，然后执行 `eval(bindings)` ，结果为 100。



默认编译也是启用缓存表达式模式。



## 调用脚本函数


在 java 中调用 script 函数也同样支持：

```java
package com.googlecode.aviator.example.scripting;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class InvokeScriptFunction {
  public static void main(final String[] args) throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("AviatorScript");

    // AviatorScript code in a String
    String script = "fn hello(name) { print('Hello, ' + name); }";
    // evaluate script
    engine.eval(script);

    // javax.script.Invocable is an optional interface.
    // Check whether your script engine implements or not!
    // Note that the AviatorScript engine implements Invocable interface.
    Invocable inv = (Invocable) engine;

    // invoke the global function named "hello"
    inv.invokeFunction("hello", "Scripting!!" );
  }
}

```



我们在脚本里定义了 `hello` 函数，然后通过 `Invocable` 接口就可以在 java 代码里调用并传入参数：

```plain
Hello, Scripting!!
```



在 AviatorScript 中可以使用 map 和闭包来[模拟面向对象编程](https://www.yuque.com/boyan-avfmj/aviatorscript/ksghfc#mEcAx)，同样，我们可以在 java 代码里调用 AviatorScript 中“对象”的方法：

```java
package com.googlecode.aviator.example.scripting;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class InvokeScriptMethod {
  public static void main(final String[] args) throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("AviatorScript");

    // AviatorScript code in a String. This code defines a script object 'obj'
    // with one method called 'hello'.
    String script =
        "let obj = seq.map(); obj.hello = lambda(name) -> print('Hello, ' + name); end;";
    // evaluate script
    engine.eval(script);

    // javax.script.Invocable is an optional interface.
    // Check whether your script engine implements or not!
    // Note that the AviatorScript engine implements Invocable interface.
    Invocable inv = (Invocable) engine;

    // get script object on which we want to call the method
    Object obj = engine.get("obj");

    // invoke the method named "hello" on the script object "obj"
    inv.invokeMethod(obj, "hello", "Script Method !!");
  }
}

```



我们定义了对象 `obj` ，它有一个方法 `hello(name)` ，在 java 代码里通过 `engine.get("obj")` 获取该对象，然后通过 `Invocable` 接口调用  `invokeMethod(obj, 方法名，方法参数列表)` 就可以调用到该对象的方法。



## 使用脚本实现 Java 接口


我们可以用 AviatorScript 脚本实现 java 中的接口，然后将函数或者对象方法转成该接口在 java 代码里使用，比如我们用 AviatorScript 实现 `Runnable` 接口：

```java
package com.googlecode.aviator.example.scripting;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class RunnableImpl {
  public static void main(final String[] args) throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("AviatorScript");

    // AviatorScript code in a String
    String script = "fn run() { println('run called'); }";

    // evaluate script
    engine.eval(script);

    Invocable inv = (Invocable) engine;

    // get Runnable interface object from engine. This interface methods
    // are implemented by script functions with the matching name.
    Runnable r = inv.getInterface(Runnable.class);

    // start a new thread that runs the script implemented
    // runnable interface
    Thread th = new Thread(r);
    th.start();
  }
}

```



我们在  AviatorScript 实现了一个 `run()` 函数， 接下来就可以从引擎里获取一个 `Runnable` 接口实现，它会自动去调用已定义的 run 函数并执行，然后我们将获取的 Runnable 实例用在了线程里：

```plain
run called
```



不仅是函数，对于“对象”也同样可以的：

```java
package com.googlecode.aviator.example.scripting;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class RunnableImplObject {
  public static void main(final String[] args) throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("AviatorScript");

    // AviatorScript code in a String
    String script =
        "let obj = seq.map(); obj.run = lambda() -> println('run method called'); end; ";

    // evaluate script
    engine.eval(script);

    // get script object on which we want to implement the interface with
    Object obj = engine.get("obj");

    Invocable inv = (Invocable) engine;

    // get Runnable interface object from engine. This interface methods
    // are implemented by script methods of object 'obj'
    Runnable r = inv.getInterface(obj, Runnable.class);

    // start a new thread that runs the script implemented
    // runnable interface
    Thread th = new Thread(r);
    th.start();
  }
}

```



你可以将某个对象的方法转成特定接口的实现。



## 多 Scope 支持


在上面的注入变量一节，我们注入了全局变量，Scripting API 也支持多个全局变量环境同时执行，相互隔离，这是通过 `ScriptContext` 实现，你可以把他理解成一个类似 `Map<String, Object>` 的映射：

```java
package com.googlecode.aviator.example.scripting;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

public class MultiScopes {
  public static void main(final String[] args) throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("AviatorScript");

    engine.put("x", "hello");
    // print global variable "x"
    engine.eval("println(x);");
    // the above line prints "hello"

    // Now, pass a different script context
    ScriptContext newContext = new SimpleScriptContext();
    Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);

    // add new variable "x" to the new engineScope
    engineScope.put("x", "world");

    // execute the same script - but this time pass a different script context
    engine.eval("println(x);", newContext);
    // the above line prints "world"
  }
}

```



在 `newContext` 的 engine 级别绑定里我们重新定义了 x 为 `world` 字符串，并传入 `eval` 执行，两者打印的结果将不同：

```plain
hello
world
```







> 更新: 2020-04-11 21:10:52  
> 原文: <https://www.yuque.com/boyan-avfmj/aviatorscript/bds23b>