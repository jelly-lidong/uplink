# 调用  Java 类方法和 Function Missing

## 调用 Java 方法


默认情况下， aviator 能调用的函数列表是受限的，这是基于安全和控制的角度考虑，不允许表达式调用任意方法。



如果你想调用某个对象的 java 方法，aviator 提供了下列两种方式：



+ [自定义函数](https://www.yuque.com/boyan-avfmj/aviatorscript/ra28g1#417b17f5)包装下，比较推荐的方式，可以避免反射。
+ [导入方法作为自定义函数](https://www.yuque.com/boyan-avfmj/aviatorscript/ra28g1#5325e1a6)，基于反射的方式自动导入和调用，性能相对较差一些（JDK 8 上大概有 3 倍左右的差距）



不过对于一些用户来说，他的表达式都是内部的，完全受控，无论是自定义函数还是导入方法的方式都太麻烦了，更希望能直接调用某个对象的方法，无需导入或者自定义函数。



针对这个需求， aviator 4.2.5 引入了一个新的方式：基于反射的自动方法发现和调用。原来对象的 `object.method(args)` 调用方式，转化成 `method(object, args)` 就可以：



```java
    // 启用基于反射的方法查找和调用
    AviatorEvaluator.setFunctionMissing(JavaMethodReflectionFunctionMissing.getInstance());
    // 调用 String#indexOf
    System.out.println(AviatorEvaluator.execute("indexOf('hello world', 'w')"));
    // 调用 Long#floatValue
    System.out.println(AviatorEvaluator.execute("floatValue(3)"));
    // 调用 BigDecimal#add
    System.out.println(AviatorEvaluator.execute("add(3M, 4M)"));
```



这个方式提供了最大的方法调用灵活性，只要将调用的对象作为第一个参数传入，就会自动查找该对象是否拥有对应的 public 实例方法，如果有，就转为反射调用进行。



当然也存在缺陷：



+ 性能相比自定义函数较差，接近 3 倍的差距，原因也是反射。
+ 无法调用静态方法，静态方法调用仍然需要采用其他两种方式。
+ 如果第一个参数为 null，无法找出方法，因为没有对象  class 信息。



## Function Missing


上述方式的底层机制是 Function Missing， aviator 4.2.5 提供了新的 `FunctionMissing` 扩展点，当实现并设置了该处理器后，没有找到的函数调用都将调用该处理器执行，具体例子参见 [FunctionMissingExample](https://github.com/killme2008/aviator/blob/master/src/test/java/com/googlecode/aviator/example/FunctionMissingExample.java)。



> 更新: 2020-02-28 18:10:11  
> 原文: <https://www.yuque.com/boyan-avfmj/aviatorscript/bsbiz0>