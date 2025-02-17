# 附录1： 文件 IO 模块



AviatorScript 提供了一个简单的文件 IO 模块实现，你可以直接 `require('io')` 进来使用：



```javascript
## examples/file_io.av

let io = require('io');
```



## 创建 java.io.File 对象
使用 `file(path)` 函数即可：



```javascript
let file = io.file("/tmp/aviator_test");
```



## 写入文本
使用 `spit(file, content)` 函数可以写入文本到文件（完全覆盖）：



```javascript
## Write a string into file by spit
io.spit(file, "Hello world\r\nAviator is great!");
```



spit 还可以设置写入的文件编码，使用三参数版本即可 `spit(file, content, charset)` 。 charset 是类似 `utf-8` 这样的字符串。



## 读取文本
读取文本可以用 `slurp(file)` 函数，完整读取整个文件的内容，并作为字符串返回：

```javascript
## Read fully from file by slurp
let content = io.slurp(file);
println("Content in file: "+ content);
```



## Line sequence
使用 `line_seq(file)` 可以将文件转成文本行组成的 Sequence 处理，比如可以用 for 循环迭代

```javascript
## Make text lines in file as a sequence
let lines = io.line_seq(file);

## Print lines by for-loop
for line in lines {
  println(line);
}
```



也可以使用 seq 各种函数进行处理：

```javascript
## Manipulate lines like a collection
let lines = io.line_seq(file);

lines = map(lines, lambda(line) ->
   n = count(line);
   line = line + ": " + n;
   return line;
end);

for line in lines {
  println(line);
}
```



这里我们使用 map 函数为每个行的末尾加上了行的总字符数：

```plain
Hello world: 11
Aviator is great!: 17
```



`line_seq` 函数也是一个[自定义 Sequence 的例子](https://www.yuque.com/boyan-avfmj/aviatorscript/yc4l93#ku01f)。



## 查看目录下的文件


可以使用 `list_files` 函数获取目录下的文件列表，返回的是一个 `File` 数组：

```javascript
## list files
println("Files in /tmp:");

for file in io.list_files(io.file("/tmp")) {
  println(file);
}
```



## 删除文件
使用 `delete` 函数来删除文件：

```javascript
## finally, delete the file.
io.delete(file);
println("exists: "+ io.exists(file));
```



判断文件是否存在使用 `exists` 函数，当存在的时候返回 true



## 创建 stream 和 writer


通过 `input_stream(file_or_url)` 或者 `output_stream(file)` 可以创建 InputStream 或者 OutputStream 对象，结合 [JavaMethodReflectionFunctionMissing](https://www.yuque.com/boyan-avfmj/aviatorscript/xbdgg2#azo1K) 机制可以做一些复杂的文件读写。



使用 `reader(file)` 和 `writer(file)`  可以创建 BufferedReader 和 BufferedWriter对象用于读写字符。



## Resource


`resource(name)` 函数等价于：

```java
Thread.currentThread().getContextClassLoader().getResource(name)
```



可以用于读取 classpath 下的资源文件，返回一个 URL，提供给 `input_stream(url)` 使用。



更多函数说明参见源码 [IoModule.java](https://github.com/killme2008/aviator/blob/master/src/main/java/com/googlecode/aviator/runtime/module/IoModule.java)



## 关闭资源


`close(Closable)` 用于关闭实现了 `Closeable` 接口的任何 IO 资源：

```javascript
io.close(reader);
```



> 更新: 2020-08-25 14:57:39  
> 原文: <https://www.yuque.com/boyan-avfmj/aviatorscript/xhta8g>