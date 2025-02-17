# 协议解析框架设计文档

## 1. 核心概念

### 1.1 协议结构
协议由以下几个部分组成：
- Protocol: 协议对象，包含header、body和check三个部分
- Header: 协议头，包含一组固定的节点
- Body: 协议体，可以包含节点、节点组和子协议体
- NodeGroup: 节点组，可以包含节点和子节点组，支持重复
- Check: 校验部分，包含一组校验节点

### 1.2 节点类型
支持的节点类型包括：
- INT: 整数类型，支持1-8字节
- FLOAT: 单精度浮点数，4字节
- DOUBLE: 双精度浮点数，8字节
- HEX: 十六进制数据，支持0x前缀
- BIT: 位操作数据，支持0b前缀
- ENUM: 枚举类型，支持多种进制表示
- STRING: 字符串类型
- TIMESTAMP: 时间戳，支持多种格式
- CRC16: 16位CRC校验
- CRC32: 32位CRC校验
- DYNAMIC: 动态类型
- PADDING: 填充数据

### 1.3 节点属性
每个节点都具有以下属性：
- name: 节点名称
- type: 节点类型
- length: 节点长度
- lengthUnit: 长度单位(BYTE/BIT)
- byteOrder: 字节序(BIG/LITTLE)
- value: 节点值
- refValue: 引用值表达式
- convert: 转换器
- optional: 是否可选
- validation: 值校验表达式
- enumValues: 枚举值定义（仅用于ENUM类型）

### 1.4 值格式支持
1. 整数值格式：
   - 十进制：123
   - 十六进制：0xFF
   - 二进制：0b1010
   - 八进制：0777

2. 枚举值格式：
   ```xml
   <node name="指令类型" type="ENUM" value="温度采集">
       <enum value="0x01">温度采集</enum>
       <enum value="0b0010">姿态调整</enum>
       <enum value="03">数据采集</enum>
       <enum value="4">状态查询</enum>
   </node>
   ```

3. 时间戳格式：
   - Unix时间戳（毫秒）：1577934001000
   - 标准日期时间：2020-01-02 12:00:01
   - ISO格式：2020-01-02T12:00:01
   - 带毫秒格式：2020-01-02 12:00:01.123
   - 斜杠分隔：2020/01/02 12:00:01
   - 紧凑格式：20200102120001

## 2. 核心功能实现

### 2.1 节点处理器链
采用责任链模式实现节点处理：
```java
public abstract class AbstractNodeHandler {
    protected AbstractNodeHandler nextHandler;
    protected final NodeType supportType;
    
    public abstract byte[] encode(Node node, Object value);
    public abstract Object decode(Node node, byte[] bytes, int offset);
}
```

### 2.2 表达式处理
支持三种类型的表达式：

1. 引用表达式：
```java
// 格式：${node1}.value 或 ${../node2}.length
public class ReferenceExpression implements Expression {
    // 解析节点引用并获取值
    public Object evaluate(Object value, ExpressionContext context);
}
```

2. 函数表达式：
```java
// 格式：day(x - '2020-01-01') 或 crc16(${../body})
public class FunctionExpression implements Expression {
    // 解析函数调用并计算结果
    public Object evaluate(Object value, ExpressionContext context);
}
```

3. 简单表达式：
```java
// 格式：123 或 "hello"
public class SimpleExpression implements Expression {
    // 处理直接值
    public Object evaluate(Object value, ExpressionContext context);
}
```

### 2.3 依赖处理
处理节点间的依赖关系：
```java
private static class DeferredNode {
    final Node node;
    final ByteBuf buffer;
    final boolean isEncode;
    final Set<String> dependencies;
}

// 延迟处理有依赖的节点
private void processNode(Node node, ByteBuf buffer, boolean isEncode) {
    if (hasUnresolvedDependencies(node)) {
        deferNode(node, buffer, isEncode);
        return;
    }
    processCurrentNode(node, buffer, isEncode);
}
```

### 2.4 校验处理
校验节点通过函数表达式实现：
```java
public abstract class AbstractCheckFunction implements Function {
    // 获取节点的字节数据
    protected byte[] getNodeBytes(Node node);
    
    // 获取协议体的字节数据
    protected byte[] getBodyBytes(ProtocolBody body);
}
```

## 3. 使用示例

### 3.1 简单协议
```xml
<protocol>
    <header>
        <node name="version" type="INT" length="1" defValue="1"/>
        <node name="length" type="INT" length="2" refValue="${body}.length"/>
    </header>
    <body>
        <node name="type" type="INT" length="1"/>
        <node name="data" type="HEX" length="10"/>
    </body>
    <check>
        <node name="crc" type="INT" length="2" convert="crc16(${../body})"/>
    </check>
</protocol>
```

### 3.2 复杂协议
```xml
<protocol>
    <header>
        <node name="magic" type="HEX" length="2" defValue="0xAA55"/>
        <node name="version" type="INT" length="1"/>
        <node name="totalLength" type="INT" length="2" refValue="${body}.length"/>
    </header>
    <body>
        <node name="messageType" type="INT" length="1"/>
        <nodeGroup name="dataGroup" repeat="${dataCount}">
            <node name="dataType" type="INT" length="1"/>
            <node name="dataLength" type="INT" length="2"/>
            <nodeGroup name="subDataGroup" repeat="${subDataCount}">
                <node name="subType" type="INT" length="1"/>
                <node name="subData" type="DYNAMIC" length="${subDataLength}"/>
            </nodeGroup>
            <node name="data" type="DYNAMIC" length="${dataLength}"/>
        </nodeGroup>
        <node name="timestamp" type="TIMESTAMP" length="8"/>
    </body>
    <check>
        <node name="checksum" type="INT" length="1" convert="checksum(${../body})"/>
        <node name="crc" type="INT" length="2" convert="crc16(${../body})"/>
    </check>
</protocol>
```

### 3.3 代码示例
```java
// 创建协议处理器
ProtocolProcessor processor = new DefaultProtocolProcessor();

// 编码
Protocol protocol = loadProtocol("protocol.xml");
setProtocolValues(protocol);  // 设置节点值
byte[] bytes = processor.encode(protocol);

// 解码
Protocol decodedProtocol = processor.decode(bytes);
Object value = decodedProtocol.getBody().getNode("data").getValue();
```

## 4. 扩展点

### 4.1 添加新的节点类型
1. 在NodeType枚举中添加新类型
2. 创建对应的NodeHandler实现
3. 在DefaultProtocolProcessor中注册处理器

### 4.2 添加新的函数
1. 创建Function接口的实现类
2. 在FunctionRegistry中注册函数
3. 在协议中使用新函数

### 4.3 添加新的校验类型
1. 创建AbstractCheckFunction的实现类
2. 在FunctionRegistry中注册校验函数
3. 在校验节点中使用新的校验函数

## 5. 注意事项

### 5.1 性能考虑
- 使用Netty的ByteBuf进行字节处理
- 缓存表达式解析结果
- 优化节点依赖处理

### 5.2 使用限制
- 节点名称在同一层级内必须唯一
- 节点组可以嵌套，但要注意控制嵌套深度
- 节点组的重复次数必须能在运行时确定
- 校验节点必须在被校验数据之后
- 支持的时间格式必须符合预定义的模式
- 枚举值必须使用支持的进制格式
- 引用路径必须正确（支持相对路径和绝对路径）
- 注意处理字节序

### 5.3 错误处理
- 检查循环依赖
- 验证节点类型和长度
- 处理表达式计算异常
- 校验失败处理
- 无效的枚举值定义
- 不支持的时间格式
- 非法的数值格式
