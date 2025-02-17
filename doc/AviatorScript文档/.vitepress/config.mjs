
  import {fixHtmlTags} from './bundle.mjs'
  export default {
    title: "AviatorScript文档",
    themeConfig: {
      search: {
        provider: 'local'
      },
      sidebar: [{"text":"4.0功能详解","link":"/4.0功能详解.md"},{"text":"AviatorScript编程指南（5.0）","collapsed":true,"items":[{"text":"1.介绍","link":"/AviatorScript编程指南（5.0）/1.%E4%BB%8B%E7%BB%8D.html"},{"text":"10.数组、集合和Sequence","items":[{"text":"10.1数组和集合","link":"/AviatorScript编程指南（5.0）/10.数组、集合和Sequence/10.1%E6%95%B0%E7%BB%84%E5%92%8C%E9%9B%86%E5%90%88.html"},{"text":"10.2Sequence","link":"/AviatorScript编程指南（5.0）/10.数组、集合和Sequence/10.2Sequence.html"}],"collapsed":false,"link":"/AviatorScript编程指南（5.0）/10.数组、集合和Sequence/"},{"text":"11.高阶主题：序列化等","link":"/AviatorScript编程指南（5.0）/11.%E9%AB%98%E9%98%B6%E4%B8%BB%E9%A2%98%EF%BC%9A%E5%BA%8F%E5%88%97%E5%8C%96%E7%AD%89.html"},{"text":"2.HelloWorld","items":[{"text":"2.1编译和执行","link":"/AviatorScript编程指南（5.0）/2.HelloWorld/2.1%E7%BC%96%E8%AF%91%E5%92%8C%E6%89%A7%E8%A1%8C.html"},{"text":"2.2解释运行","link":"/AviatorScript编程指南（5.0）/2.HelloWorld/2.2%E8%A7%A3%E9%87%8A%E8%BF%90%E8%A1%8C.html"}],"collapsed":false,"link":"/AviatorScript编程指南（5.0）/2.HelloWorld/"},{"text":"3.基本类型和语法","items":[{"text":"3.1基本类型及运算","link":"/AviatorScript编程指南（5.0）/3.基本类型和语法/3.1%E5%9F%BA%E6%9C%AC%E7%B1%BB%E5%9E%8B%E5%8F%8A%E8%BF%90%E7%AE%97.html"},{"text":"3.2运算符","link":"/AviatorScript编程指南（5.0）/3.基本类型和语法/3.2%E8%BF%90%E7%AE%97%E7%AC%A6.html"},{"text":"3.3注释","link":"/AviatorScript编程指南（5.0）/3.基本类型和语法/3.3%E6%B3%A8%E9%87%8A.html"},{"text":"3.4变量","link":"/AviatorScript编程指南（5.0）/3.基本类型和语法/3.4%E5%8F%98%E9%87%8F.html"},{"text":"3.5作用域","link":"/AviatorScript编程指南（5.0）/3.基本类型和语法/3.5%E4%BD%9C%E7%94%A8%E5%9F%9F.html"},{"text":"3.6多行表达式和return","link":"/AviatorScript编程指南（5.0）/3.基本类型和语法/3.6%E5%A4%9A%E8%A1%8C%E8%A1%A8%E8%BE%BE%E5%BC%8F%E5%92%8Creturn.html"},{"text":"3.7创建对象和new","link":"/AviatorScript编程指南（5.0）/3.基本类型和语法/3.7%E5%88%9B%E5%BB%BA%E5%AF%B9%E8%B1%A1%E5%92%8Cnew.html"},{"text":"3.8use语句引用Java类","link":"/AviatorScript编程指南（5.0）/3.基本类型和语法/3.8use%E8%AF%AD%E5%8F%A5%E5%BC%95%E7%94%A8Java%E7%B1%BB.html"}],"collapsed":false,"link":"/AviatorScript编程指南（5.0）/3.基本类型和语法/"},{"text":"4.条件语句","link":"/AviatorScript编程指南（5.0）/4.%E6%9D%A1%E4%BB%B6%E8%AF%AD%E5%8F%A5.html"},{"text":"5.循环语句","link":"/AviatorScript编程指南（5.0）/5.%E5%BE%AA%E7%8E%AF%E8%AF%AD%E5%8F%A5.html"},{"text":"6.Statement语句和值","link":"/AviatorScript编程指南（5.0）/6.Statement%E8%AF%AD%E5%8F%A5%E5%92%8C%E5%80%BC.html"},{"text":"7.异常处理","link":"/AviatorScript编程指南（5.0）/7.%E5%BC%82%E5%B8%B8%E5%A4%84%E7%90%86.html"},{"text":"8.函数和闭包","items":[{"text":"8.1函数","link":"/AviatorScript编程指南（5.0）/8.函数和闭包/8.1%E5%87%BD%E6%95%B0.html"},{"text":"8.2匿名函数和闭包","link":"/AviatorScript编程指南（5.0）/8.函数和闭包/8.2%E5%8C%BF%E5%90%8D%E5%87%BD%E6%95%B0%E5%92%8C%E9%97%AD%E5%8C%85.html"},{"text":"8.3自定义函数和调用Java方法","link":"/AviatorScript编程指南（5.0）/8.函数和闭包/8.3%E8%87%AA%E5%AE%9A%E4%B9%89%E5%87%BD%E6%95%B0%E5%92%8C%E8%B0%83%E7%94%A8Java%E6%96%B9%E6%B3%95.html"},{"text":"8.4函数和Runnable、Callable","link":"/AviatorScript编程指南（5.0）/8.函数和闭包/8.4%E5%87%BD%E6%95%B0%E5%92%8CRunnable%E3%80%81Callable.html"}],"collapsed":false,"link":"/AviatorScript编程指南（5.0）/8.函数和闭包/"},{"text":"9.exports和模块","link":"/AviatorScript编程指南（5.0）/9.exports%E5%92%8C%E6%A8%A1%E5%9D%97.html"},{"text":"附录1：文件IO模块","items":[{"text":"附录1.1：使用Java自定义模块","link":"/AviatorScript编程指南（5.0）/附录1：文件IO模块/%E9%99%84%E5%BD%951.1%EF%BC%9A%E4%BD%BF%E7%94%A8Java%E8%87%AA%E5%AE%9A%E4%B9%89%E6%A8%A1%E5%9D%97.html"}],"collapsed":false,"link":"/AviatorScript编程指南（5.0）/附录1：文件IO模块/"},{"text":"附录2：JavaScriptingAPI支持","link":"/AviatorScript编程指南（5.0）/%E9%99%84%E5%BD%952%EF%BC%9AJavaScriptingAPI%E6%94%AF%E6%8C%81.html"},{"text":"附录3：aviator命令行","link":"/AviatorScript编程指南（5.0）/%E9%99%84%E5%BD%953%EF%BC%9Aaviator%E5%91%BD%E4%BB%A4%E8%A1%8C.html"}],"link":"/AviatorScript编程指南（5.0）/"},{"text":"Changelog","link":"/Changelog.md"},{"text":"Milestones","link":"/Milestones.md"},{"text":"函数库列表","link":"/函数库列表.md"},{"text":"如何升级到5.0大版本？（老用户必读）","link":"/如何升级到5.0大版本？（老用户必读）.md"},{"text":"完整选项说明","link":"/完整选项说明.md"},{"text":"最佳实践","link":"/最佳实践.md"},{"text":"用户指南（5.0之前版本）","link":"/用户指南（5.0之前版本）.md"},{"text":"设计及实现","collapsed":true,"items":[{"text":"AviatorScript5.3设计","link":"/设计及实现/AviatorScript5.3%E8%AE%BE%E8%AE%A1.html"}]},{"text":"调用Java类方法和FunctionMissing","link":"/调用Java类方法和FunctionMissing.md"}]
    },
    vite: {
      optimizeDeps: {
        include: []
      }
    },
    markdown: {
      html: true,
      breaks: true,
      config(md) {
        // 包装原始 render 方法，捕获解析异常
        const originalRender = md.render.bind(md);
        md.render = (src, env) => {
          try {
            let newMd = fixHtmlTags(originalRender(src, env))
            newMd = newMd.replace('<html><head></head><body>', '')
            newMd = newMd.replace('</body></html>', '')
            return newMd
          } catch (error) {
            console.error("Markdown/HTML parsing error:", error);
            return src
          }
        }
      }
    }
  }
  