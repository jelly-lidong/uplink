# AviatorScript 5.3 设计





## 指令设计


类似 JVM bytecode，基于 stack。





### 操作指令
以下指令如果没有特殊说明，都将执行弹出栈上的 n 个元素，并执行对应指令操作，并将结果压入栈。

| 指令 | 操作数 | 结果数 | 描述 |
| --- | --- | --- | --- |
| load x | 0 | 1 | 加载立即数、变量等。 |
| new_lambda name | 0 | 1 | 创建名称为 name 的匿名函数 |
| add | 2 | 1 | 加法 |
| sub | 2 | 1 | 减法 |
| mult | 2 | 1 | 乘法 |
| div | 2 | 1 | 除法 |
| exp | 2 | 1 | 幂运算 |
| mod | 2 | 1 | 模运算 |
| match | 2 | 1 | 正则匹配，结果为布尔值 |
| neg | 1 | 1 | 相反数 |
| not | 1 | 1 | 逻辑否 |
| and | 2 | 1 | 逻辑与 |
| or | 2 | 1 | 逻辑或 |
| lt, le | 2 | 1 | 小于，小于等于 |
| gt, ge | 2 | 1 | 大于，大于等于 |
| eq | 2 | 1 | 等于 |
| ne | 2 | 1 | 不等于 |
| bit_and | 2 | 1 | 位与 |
| bit_or | 2 | 1 | 位或 |
| bit_xor | 2 | 1 | 异或 |
| bit_not | 1 | 1 | 取反 |
| shift_left | 2 | 1 | 左移 |
| shift_right | 2 | 1 | 右移 |
| unsigned_shift_right | 2 | 1 | 无符号右移 |
| assign | 2 或者 3 | 1 | 赋值 |
| def | 2 | 1 | 定义并赋值 |
| index | 2 | 1 | “索引”取值 |
| send name,arity | 可变 | 2 | 方法调用, name 指定函数名， arity 指定参数个数，将从栈中弹出参数，并将结果入栈。<br/>**特殊形式： send <top>, arity，默认将使用栈顶指针为函数。** |
| branchif label | 1 | | 如果栈顶为真，跳转到指定“label”对应的代码块（不弹出栈顶） |
| branchunless label | 1 | | 如果栈顶为不真，跳转到指定“label”对应的代码块（不弹出栈顶） |
| goto label | 1 | | 跳转到指定label的代码块 goto label |
| clear | 0 | | 清空栈 |
| assert type | 0 | | 确认栈顶元素类型是否为 type 类型， type 可以是 bool,string 和 number |




## 解释器


+ IR 指令接口：



```java
public interface IR {
  void eval(InterpretContext context);
}

```

具体指令都实现为该接口的一个子类，如 `LoadIR`等。

+ `InterpretCodeGenerator` 指令代码生成器，根据 parser 生成指定的指令(IR)序列。
+ `InterpretExpression`编译后的脚本对象，保存生成的指令序列 `List<IR>instruments`。
+ `InterpretContext`单次解释执行的上下文。
+ 指令执行过程（在 `InterpretExpression#executeDirectly`方法中），每次从 `InterpretContext`获取当前指令 pc，执行，如果遇到跳转就继续执行，否则获取下一条指令：



```java
    IR ir = null;
    while ((ir = ctx.getPc()) != null) {
      ir.eval(ctx);
      if (ir instanceof JumpIR) {
        if (ir != ctx.getPc()) {
          // jump successfully, we don't move pc to next.
          continue;
        }
      }
      if (!ctx.next()) {
        break;
      }
    }
```





## Parser 重构






> 更新: 2021-09-13 13:50:06  
> 原文: <https://www.yuque.com/boyan-avfmj/aviatorscript/efmxgg>