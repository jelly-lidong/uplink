# Changelog

#### This doc will not updated since 2022.08, please read the release notes in github. [https://github.com/killme2008/aviatorscript/releases](https://github.com/killme2008/aviatorscript/releases)


## 5.3.0
Main changes:

+ [Interpreter mode](https://www.yuque.com/boyan-avfmj/aviatorscript/ok8agx) to run on Android or other not-standard JVM.
+ <font style="color:rgb(36, 41, 47);">Fixed assign and define operator can't work when enable </font><font style="color:rgb(36, 41, 47);">TRACE_EVAL</font><font style="color:rgb(36, 41, 47);">. </font>[#408](https://github.com/killme2008/aviatorscript/issues/408)
+ <font style="color:rgb(36, 41, 47);">Implements </font><font style="color:rgb(36, 41, 47);">ApplicationContextAware</font><font style="color:rgb(36, 41, 47);"> for </font><font style="color:rgb(36, 41, 47);">SpringContextFunctionLoader</font><font style="color:rgb(36, 41, 47);">. Now </font><font style="color:rgb(36, 41, 47);">SpringContextFunctionLoader</font><font style="color:rgb(36, 41, 47);"> can be managed by spring container.</font>

## 5.2.7
Fixed:

+ A concurrent issue in `Expression#getVaraibleNames`and `Expression#getVaraibleFullNames`.
+ Should throw no such property exception if propert not found when using variable `a.b.c` dot syntax.

## 5.2.6
**<font style="color:#F5222D;">A strongly recommended upgrading version if you are using 5.x versions.</font>**

<font style="color:rgb(36, 41, 46);">Main changes:</font>

+ <font style="color:rgb(36, 41, 46);">Fixed:</font>
    - <font style="color:rgb(36, 41, 46);">Anonymous function can't support variadic arguments.</font>
    - <font style="color:rgb(36, 41, 46);">continue</font><font style="color:rgb(36, 41, 46);"> </font><font style="color:rgb(36, 41, 46);">statement not work with if/else or nested if statements</font><font style="color:rgb(36, 41, 46);"> </font>[#394](https://github.com/killme2008/aviatorscript/issues/394)
    - <font style="color:rgb(36, 41, 46);">LambdaFunction ThreadLocal caching may leak environment context </font>[#392](https://github.com/killme2008/aviatorscript/issues/392)
+ <font style="color:rgb(36, 41, 46);">New features:</font>
    - <font style="color:rgb(36, 41, 46);">New function: </font><font style="color:rgb(36, 41, 46);">partial(f, &args)</font><font style="color:rgb(36, 41, 46);"> that takes a function f and fewer than the normal arguments to f, and returns a fn that takes a variable number of additional args. When called, the returned function calls f with args + additional args.</font>
    - <font style="color:rgb(36, 41, 46);">Use soft reference for caching reflection result</font><font style="color:rgb(36, 41, 46);"> </font>[#386](https://github.com/killme2008/aviatorscript/issues/386)
    - <font style="color:rgb(36, 41, 46);">Added key info to exception message when sequence is null in </font><font style="color:rgb(36, 41, 46);">seq.get</font><font style="color:rgb(36, 41, 46);"> function.</font>

## 5.2.5


Main changes:

+ Auto convert java boxing types into aviator number types when invoking java methods [#369](https://github.com/killme2008/aviatorscript/issues/369)
+ Adds a [calcuator](https://github.com/killme2008/aviatorscript/blob/master/examples/calculator.av) example which <font style="color:#24292E;">evaluate</font> arithmetic expression in string.
+ Bug fixes: can't call overload getter/setter by reflection [#368](https://github.com/killme2008/aviatorscript/issues/368)
+ Used array-based hashmap for internal env to reduce memory consumption.

## 5.2.4
New features:

+ Define anonymous function by `fn`  syntax (instead of `lambda ... -> ... end` ), `let add = fn(x, y) { x + y); add(1, 2)` for example.
+ [Unpacking arguments](https://www.yuque.com/boyan-avfmj/aviatorscript/gl2p0q#ZyJeH)(as sequence) by `*args` syntax like python, for example:

```javascript
fn test(a, b, c, d) {
  a * b + c * d
}
let a = tuple(1, 2);
let list = seq.list(3, 4);

p(test(*a, *list));
```

+ Adds `AVIATOR_DEPS` environment variable to point third-party jar files directory for `aviator` shell command-line, default is `$HOME/.aviatorscript/deps` , all jar files under this directory will be added to JVM `CLASSPATH` .
+ Improve `for` statement, supports index( List/Array sequence etc.) and key/value(Map) iterating:

```javascript
let a = tuple(1, 2, 3, 4, 5, 6, 7, 8, 9);

for i, x in a {
  assert(i + 1 == x);
}

let m = seq.map("a", 1, "b", 2, "c", 3);

for k, v in m {
	if k == "a" {
	  assert(v == 1);
	}elsif k == 'b' {
	  assert(v == 2);
	}elsif k == 'c' {
	  assert(v == 3);
	}else {
	  throw "should not happen";
	}
}

```

+ `seq.array_of(Class, dimension1, dimension2, ...dimensions)` to create a multidimensional array.
+ New functions to add/retrieve/remove object's metadata:

```javascript
let a = 1;
p(meta(a));  ## retrieve meta ,null if not found

## associate key/value metadata to any objects by with_meta(obj, key, value)
a = with_meta(a, "key", tuple(1, 2, 3));

p(meta(a));  ## {"key" => [1, 2, 3]}
p(meta(a, "key")); ## [1, 2, 3]

## remove metadata by without_meta(obj, key)
a = without_meta(a, "key");
p(meta(a));  

```

+ Bugs fixed:
    - Wrong size number of `Range` .
    - JUnit dependency issue, Thanks to [DQinYuan](https://github.com/DQinYuan)



## 5.2.3
Main changes:

+ Removed commons-beanutils [#340](https://github.com/killme2008/aviatorscript/issues/340)
+ Fixed `AviatorString#toString()` may print warning message.
+ Fixed missing source file and line number in string interpolation expression when throws exception.
+ New function `is_distinct(seq)` returns true when a sequence doesn't have duplicated elements.
+ Focus on performance turning:



```plain
Aviator 5.2.3:
Benchmark                           Mode  Cnt       Score      Error   Units
PerfBenchmark.testArith            thrpt    5  108126.155 ± 6304.752  ops/ms
PerfBenchmark.testArithByAviator   thrpt    5    2565.933 ±  105.076  ops/ms
PerfBenchmark.testArithByBeetl     thrpt    5    1625.887 ±  291.247  ops/ms
PerfBenchmark.testArithByScript    thrpt    5    7050.305 ±   69.529  ops/ms
PerfBenchmark.testCond             thrpt    5   93099.759 ± 8554.585  ops/ms
PerfBenchmark.testCondByAviator    thrpt    5    1667.093 ±  112.807  ops/ms
PerfBenchmark.testCondByBeetl      thrpt    5    1617.045 ±   93.373  ops/ms
PerfBenchmark.testCondByScript     thrpt    5    6926.106 ±  267.292  ops/ms
PerfBenchmark.testObject           thrpt    5    8537.937 ±  272.512  ops/ms
PerfBenchmark.testObjectByAviator  thrpt    5    1025.725 ±   30.846  ops/ms
PerfBenchmark.testObjectByBeetl    thrpt    5     860.873 ±   33.559  ops/ms
PerfBenchmark.testObjectByScript   thrpt    5    4552.307 ±  199.507  ops/ms

Aviator 5.2.2:
Benchmark                           Mode  Cnt       Score      Error   Units
PerfBenchmark.testArith            thrpt    5  105095.308 ± 3861.646  ops/ms
PerfBenchmark.testArithByAviator   thrpt    5    2405.785 ±   78.325  ops/ms
PerfBenchmark.testArithByBeetl     thrpt    5    1628.726 ±   45.332  ops/ms
PerfBenchmark.testArithByScript    thrpt    5    7513.704 ±  286.090  ops/ms
PerfBenchmark.testCond             thrpt    5   92518.914 ± 1961.141  ops/ms
PerfBenchmark.testCondByAviator    thrpt    5     952.022 ±   32.184  ops/ms
PerfBenchmark.testCondByBeetl      thrpt    5    1647.736 ±   19.300  ops/ms
PerfBenchmark.testCondByScript     thrpt    5    7631.465 ±  404.298  ops/ms
PerfBenchmark.testObject           thrpt    5    8847.069 ±  261.799  ops/ms
PerfBenchmark.testObjectByAviator  thrpt    5     873.944 ±   26.327  ops/ms
PerfBenchmark.testObjectByBeetl    thrpt    5     826.758 ±   30.071  ops/ms
PerfBenchmark.testObjectByScript   thrpt    5    4647.178 ±  237.783  ops/ms

```

    - Benchmark improvements:
        * testArithByAviator 6.7%
        * testCondByAviator 75%
        * testObjectByAviator 17.4%



## 5.2.2
Main changes:

+ Fixed `Expression#getVariableNames()` and `Expression#getVariableFullNames()`, they will return the global uninitialized variable names. [#277](https://github.com/killme2008/aviatorscript/issues/277) [#335](https://github.com/killme2008/aviatorscript/issues/335)
+ Adds `AviatorEvaluatorInstance#setCachedExpressionByDefault(boolean)` to configure whether to cache the compiled expression by default when invoke `compile(string)`, `execute(string, [env])` methods etc, default value is false. [#330](https://github.com/killme2008/aviatorscript/issues/330)
+ Adds a new option `Options.ALLOWED_CLASS_SET` with a `Set<Class>` value to control the allowed class list in new statement and static method/field invocation. [#325](https://github.com/killme2008/aviatorscript/issues/325)
+ Adds new features `Feature.StaticFields` and `Feature.StaticMethods`. [#326](https://github.com/killme2008/aviatorscript/issues/326)

## 5.2.1
If you are trying 5.2.0, please upgrade to this verson.

1. New Features:
    - [Access java class's static methods by reflection,](https://www.yuque.com/boyan-avfmj/aviatorscript/xbdgg2#rjNeD) `Long.parseLong("3")` for example. You must `use` the class at first.
2. Breaking changes:
    - String interpolation only works on **literal string in script**. Runtime string will not work.
3. Fixed:
    - Weak reference queue memory leak in reflector.
    - String interpolation compile error in some corner cases.
    - Locked `commons-beanutils` version to 1.9.4



## 5.2.0
**Deprecated, please use 5.2.1**

1. **New Features:**
    - function overloading by parameter count, see [function_overload.av](https://github.com/killme2008/aviatorscript/blob/master/examples/function_overload.av).
    - variadic function by define variable parameter by prefixed with `&` , see  [function_varargs.av](https://github.com/killme2008/aviatorscript/blob/master/examples/function_varargs.av).
    - use statement to import java classes, such as `use java.util.Date;` , see [use.av](https://github.com/killme2008/aviatorscript/blob/master/examples/use.av).
    - access class static variables by `.` , for example `Math.PI` , see [static_vars.av](https://github.com/killme2008/aviatorscript/blob/master/examples/static_vars.av).
    - access or assign map's value by `m[key]` syntax, the key can be a object or expression.
    - when use [aviator commandline](https://www.yuque.com/boyan-avfmj/aviatorscript/ma3zs3),  there is a new internal variable `__MODULE__.dir` represents the file's current directory absolute path.You may use it in `require` for example:

```javascript
## examples/test_qsort.av

let q = require(__MODULE__.dir + '/qsort.av');

fn rand_list(n) {
  let a = seq.list();
  for i in range(0, n) {
    seq.add(a, rand(n));
  }
  return a;
}

let a = rand_list(20);
println("before sorting: " + a);
q.sort(a);
println("after sorting: " + a);
```



The you can run the script by command `aviator examples/test_qsort.av` , you don't need to change the work directory into `examples`  as before.



2. **New Functions:**
    - more sequence functions:
        * `take_while(seq, pred)`  to take elements in sequence with `pred(x)` is true.
        * `drop_while(seq, pred)`  to drop elements  in sequence with `pred(x)` is true.
        * `concat(list1, list2)` to concat two sequences.
        * `group_by(seq, keyfn)` to group elements by `keyfn(x)` .
        * `distinct(seq)` to return a sequence of the elements of coll with duplicates removed.
        * `sort(seq, comparator)` to sort sequence with a  `java.util.Comparator` .
        * `reverse(seq)` to return a seq of the items in coll in reverse order.
        * `seq.keys(map)` to return the key set of map.
        * `seq.vals(map)` to return the value set of map.
        * `zipmap(keys, vals)` to return a map with the keys mapped to the corresponding vals.
    - more math functions:
        * `math.atan(x)` 
        * `math.acos(x)` 
        * `math.asin(x)` 
        * `math.ceil(x)` 
        * `math.floor(x)` 
    - more internal functions:
        * `is_a(val, class)` Returns true when the value is a intance of the class, for example `is_a("a", String)`  returns true.
        * `comparator(pred)` Returns an implementation of java.util.Comparator based upon predicate.
        * `repeat(n, x)` Returns a list of x which it's lenght n.
        * `repeatedly(n, fn)`  the `fn` function takes no args, and returns a sequence of calls to it in `n` times.
        * `constantly(x)` Returns a function that takes any number of arguments and returns x.



3. **Improvements**:
    - improve error reporting, adds source file and source line number.
    - performance tweak for variable syntax suger such as `a.b.c` or `a.b[10].c` etc, replace the `commons-beanutils` by default, almost 2x speed up.
    - Checking if function name  is reserved when parsing script.
    - Reduce memory consumption.







> 更新: 2022-09-03 13:12:34  
> 原文: <https://www.yuque.com/boyan-avfmj/aviatorscript/bggwx2>