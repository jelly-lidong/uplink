# 附录3：aviator 命令行

## aviator 命令行


AviatorScript 还提供了一个命令行工具，方便地直接执行脚本。



### 安装和执行


1. 安装，请下载 [aviator](https://raw.githubusercontent.com/killme2008/aviator/master/bin/aviator) 文件，保存到某个在系统 `PATH` 路径里的目录（比如 `~/bin` ）：

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

3. 执行命令，会自动下载并安装 aviator jar 文件到 `~/.aviatorscript` 目录下：

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

4. 将下列脚本保存为文件  `hello.av` ：

```javascript
p('Hello, AviatorScript!');
```

5. 执行脚本：

```bash
$ aviator hello.av
Hello, AviatorScript!
```

6. 也可以直接在命令行执行一段脚本，通过 `-e` 选项：

```bash
$ aviator -e "p('Hello, AviatorScript!');"
Hello, AviatorScript!
null

```

最后的 null 是整个表达式的执行结果。



### 命令行参数
在脚本中可以通过 `ARGV` 数组来访问到：

```javascript
p("ARGV count is: " + count(ARGV));

for arg in ARGV {
  p(arg);
}

p("ARGV[0] = "  + ARGV[0]);
```

保存为 `test.av` 并执行：

```bash
$ aviator test.av 1 2 3
ARGV count is: 3
1
2
3
ARGV[0] = 1
```

我们传入了命令行参数 `1 2 3` ，因此 `count(ARGV)` 返回 3，通过 for 循环迭代了数组并打印，最终我们还单独打印了 `ARGV[0]` ，也就是第一个命令行参数。



### 升级和其他命令
可以通过 `upgrade` 来升级 AviatorScript:

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



安装文件在 `~/.aviatorscript/self-installs/` 下，可以删除重新执行 `aviator` 就会重新安装。



查看版本通过 `-v` 选项：

```bash
$ aviator -v
AviatorScript 5.0.0-RC3-SNAPSHOT
```



### 第三方依赖包


从 5.2.4 开始，如果想在命令行调用脚本过程中需要使用到第三方类库，可以将 jar 包放入 user home 目录下的 `~/.aviatorscript/deps` 目录，将自动加入启动后的 JVM 的 `CLASSPATH` ，你也可以通过 `AVIATOR_DEPS` 环境变量来修改这个目录。



> 更新: 2021-03-10 11:48:45  
> 原文: <https://www.yuque.com/boyan-avfmj/aviatorscript/ma3zs3>