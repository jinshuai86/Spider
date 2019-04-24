# Spider
Spider是一个基于Java的简易多线程爬虫框架，并且提供了默认组件。而且用户可以根据需要实现自己的组件  
- 具体的流程
  - 首先在调度器中添加初始种子，开启线程池。
  - 工作线程开始从种子调度器中取URL种子
  - 使用下载器获取URL对应的页面内容
  - 使用解析器解析页面内容，将页面里的URL封装成URL种子，添加到种子调度器中。将页面中的内容通过持久器持久化。
  
![流程图](./spider-flowchart.svg)

# 使用说明

## 开发环境必需软件
- JDK8+
- Maven3+
- lombok

## 操作步骤
- 修改`application.properties`中存放解析内容的路径`dir`
- 如果使用`Redis`作为种子调度器(默认使用优先队列)，需要修改`application.properties`中配置的`ip`、`port`和`password`。如果你的Redis不需要密码验证，就不用修改文件里的`password`属性。
- 默认解析器是解析的[河北工业大学新闻网](http://xww.hebut.edu.cn/gdyw/index.htm)中的新闻，如果需要爬取其它类型的网页，需要重写`Parser.java`接口以及提供给种子调度器的初始种子
```Java
        Spider.build()
                .addUrlSeed(new UrlSeed("http://xww.hebut.edu.cn/gdyw/index.htm", 5))
                .setTargetTaskNumbers(100)
                .run();
```
- 运行`Spider.java`

# 项目结构

```Shell
├── spider                                        // 根目录
│   ├── logs                                      // 日志文件
│   ├── src                                       // 源码
│   ├── |——main
│   ├── ├──|——java/com/jinshuai                          
│   ├── ├──├──|——core                             // 核心组件
│   ├── ├──├──|————downloader                     // 下载器
│   ├── ├──├──|————parser                         // 解析器
│   ├── ├──├──|————saver                          // 持久器
│   ├── ├──├──|————scheduler                      // URL调度器
│   ├── ├──├──|——entity                           // 实体
│   ├── ├──├──|——util                             // 工具
│   ├── ├──|——resources                           // 资源目录
│   ├── ├──|——|——application.properties           // 配置文件
│   ├── |——test                                   // 单元测试
```

# 进度
## Finished
- [x] 配置了[Http连接池](https://hc.apache.org/httpcomponents-client-ga/)，完成了Http请求和处理Http响应<br>
- [x] [解析](https://jsoup.org/)响应的内容
- [x] 配置线程池，通过[Redis](https://redis.io/)缓存URL种子
- [x] 持久化解析结果
- [x] 添加新的种子调度器（优先队列结合布隆过滤器）

## TODO
- [ ] 实时解析失败日志，将失败URL重新加入爬取仓库，设置失败次数限制，超过指定次数就放弃。
- [ ] 各个组件进行热替换
- [ ] 优化解析页面代码

# 参考
- **代码和设计思路**参考自[https://github.com/xjtushilei/ScriptSpider](https://github.com/xjtushilei/ScriptSpider)