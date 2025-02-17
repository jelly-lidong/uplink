# 2. Hello World

AviatorScript 是一门寄生在 JVM  (Hosted on the JVM）上的语言，类似 clojure/scala/kotlin 等等。我们先从一个 hello world 例子开始。



## 从 Java 中调用


首先，在你的 java 项目里引用下 AviatorScript 的依赖：



```xml
<dependency>
  <groupId>com.googlecode.aviator</groupId>
  <artifactId>aviator</artifactId>
  <version>{version}</version>
</dependency>
```



version 版本可以在[ maven 找到](https://search.maven.org/search?q=g:com.googlecode.aviator%20AND%20aviator)，本指南编写的版本面向 5.0.0 及以上版本， 5.0 以下请参考老版本的[用户指南](https://www.yuque.com/boyan-avfmj/aviatorscript/ra28g1)。



接下来编写你的第一个 AviatorScript 脚本，放到项目的根目录下的 `examples` 子目录



```javascript
## examples/hello.av

println("hello, AviatorScript!");
```



其中 `## examples/hello.av` 是注释，说明例子所在的文件（后续的例子都将带上这个说明，你可以在 [examples](https://github.com/killme2008/aviator/tree/master/examples) 目录找到所有的例子。

这段代码非常简单，调用 `println` 函数，打印字符串 `hello, AviatorScript!` 。



其次，编写一个类来运行测试脚本：



```java
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.Options;

/**
 * Run a script under examples folder.
 *
 * @author dennis(killme2008@gmail.com)
 *
 */
public class RunScriptExample {

  public static void main(final String[] args) throws Exception {
    // Compile the script into a Expression instance.
    Expression exp = AviatorEvaluator.getInstance().compileScript("examples/hello.av");
    // Run the exprssion.
    exp.execute();

  }

}

```





其实你可以直接 clone aviator 的仓库 [https://github.com/killme2008/aviator/](https://github.com/killme2008/aviator/) ，运行其中的 [RunScriptExample](https://github.com/killme2008/aviator/blob/master/src/test/java/com/googlecode/aviator/example/RunScriptExample.java) 就可以测试 `examples`  目录下的所有例子。



这段代码逻辑很简单：

1. 使用 `AviatorEvaluatorInstance#compileScript` 方法编译脚本到 `Expression` 对象
2. 调用 `Expression#execute()` 方法执行



输出：



```plain
hello, AviatorScript!

```



其中两行是日志，打印的就是 `hello, AviatorScript!` 



这样就完成了一个你的第一个 AviatorScript 的编写、编译和执行。从 Java 中调用脚本还可以采用 Java 的 Scripting API，参见《[附录2 Java Scripting API 支持](https://www.yuque.com/boyan-avfmj/aviatorscript/bds23b)》。



## aviator 命令行


AviatorScript 还提供了一个命令行工具，方便地直接执行脚本。



### 安装和执行


1. 安装，请下载 [aviator](https://raw.githubusercontent.com/killme2008/aviator/master/bin/aviator) 文件，保存到某个在系统 `PATH` 路径里的目录（比如 `~/bin` ）：

```bash
$ wget https://raw.githubusercontent.com/killme2008/aviator/master/bin/aviator
--2020-04-26 21:41:04--  https://raw.githubusercontent.com/killme2008/aviator/master/bin/aviator
Resolving localhost (localhost)... ::1, 127.0.0.1
Connecting to localhost (localhost)|::1|:1081... failed: Connection refused.
Connecting to localhost (localhost)|127.0.0.1|:1081... connected.
Proxy request sent, awaiting response... 200 OK
Length: 5289 (5.2K) [text/plain]
Saving to: ‘aviator’
```

2. 修改为可执行：

```bash
$ chmod u+x aviator
```

3. 执行命令，会自动下载并安装 aviator jar 文件到 `~/.aviatorscript` 目录下：

```bash
$ aviator
Downloading AviatorScript now...
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   153  100   153    0     0     94      0  0:00:01  0:00:01 --:--:--    94
100  505k  100  505k    0     0   199k      0  0:00:02  0:00:02 --:--:--  663k
Usage: java com.googlecode.aviator.Main [file] [args]
     : java com.googlecode.aviator.Main -e [script]
     : java com.googlecode.aviator.Main -v
```

4. 将下列脚本保存为文件  `hello.av` ：

```javascript
p('Hello, AviatorScript!');
```

5. 执行脚本：

```bash
$ aviator hello.av
Hello, AviatorScript!
```

6. 也可以直接在命令行执行一段脚本，通过 `-e` 选项：

```bash
$ aviator -e "p('Hello, AviatorScript!');"
Hello, AviatorScript!
null

```

最后的 null 是整个表达式的执行结果。



### 命令行参数
在脚本中可以通过 `ARGV` 数组来访问到：

```javascript
p("ARGV count is: " + count(ARGV));

for arg in ARGV {
  p(arg);
}

p("ARGV[0] = "  + ARGV[0]);
```

保存为 `test.av` 并执行：

```bash
$ aviator test.av 1 2 3
ARGV count is: 3
1
2
3
ARGV[0] = 1
```

我们传入了命令行参数 `1 2 3` ，因此 `count(ARGV)` 返回 3，通过 for 循环迭代了数组并打印，最终我们还单独打印了 `ARGV[0]` ，也就是第一个命令行参数。



### 升级和其他命令
可以通过 `upgrade` 来升级 AviatorScript:

```bash
$ aviator upgrade
The script at /Users/boyan/bin/aviator will be upgraded to the latest stable version.
Do you want to continue [Y/n]? Y

Upgrading...
/Users/boyan/bin/aviator: line 123: [darwin18: command not found
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   137  100   137    0     0     99      0  0:00:01  0:00:01 --:--:--    99
100  5289  100  5289    0     0   2700      0  0:00:01  0:00:01 --:--:-- 5165k

The self-install jar already exists at /Users/boyan/.aviatorscript/self-installs/aviator-5.0.0.jar.
If you wish to re-download, delete it and rerun "/Users/boyan/bin/aviator self-install".
```

因为当前已经是最新版本，所以不会重新安装。



安装文件在 `~/.aviatorscript/self-installs/` 下，可以删除重新执行 `aviator` 就会重新安装。



查看版本通过 `-v` 选项：

```bash
$ aviator -v
AviatorScript 5.0.0-RC3-SNAPSHOT
```



## [2.1 编译和执行](https://www.yuque.com/boyan-avfmj/aviatorscript/fycwgt)






> 更新: 2020-04-26 21:52:48  
> 原文: <https://www.yuque.com/boyan-avfmj/aviatorscript/tvahat>