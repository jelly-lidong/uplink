# 10. 数组、集合和 Sequence

这一章，我们将介绍如何在 AviatorScript 如何方便地创建和操作数组、集合。同时介绍在此之上的 Sequence 抽象。 AviatorScript 中将数组和集合都抽象成一个序列集合 Sequence，在此之上可以用同一套高阶函数方便地操作任意的数组或者集合，同时你还可以自定义 seq 函数以及 Sequence 实现，可以将任何类似“集合”的东西包装成 Sequence 实现，就可以复用同一套高阶函数，这是非常强大的抽象。比如我们可以将文件里的行抽象成 sequence，那么就可以用 for 循环遍历文件行，用 map 转换行数据。



### [10.1 数组和集合](https://www.yuque.com/boyan-avfmj/aviatorscript/ban32m)
### [10.2 Sequence](https://www.yuque.com/boyan-avfmj/aviatorscript/yc4l93)


> 更新: 2020-04-23 10:54:25  
> 原文: <https://www.yuque.com/boyan-avfmj/aviatorscript/zg7bf9>