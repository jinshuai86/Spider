# Spider
Spider是一个基于Java的简易多线程爬虫框架，并且提供了默认组件。用户可以根据需要实现自己的组件
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
- [ ] 优化Redis配置，线程池参数配置。并将配置信息写入配置文件
- [ ] 优化解析页面代码
- [ ] 各个组件进行热替换
- [ ] 让爬虫更加真实的模拟客户端

# 使用说明
## 运行环境
- JDK8+
- Maven3+

## 操作步骤
- 确定`application.properties`中存放解析内容的存放路径
- 如果使用`Redis`作为种子调度器(默认使用优先队列)，需要修改`application.properties`中配置的IP以及port
- 默认解析器是解析的[河北工业大学新闻网](http://xww.hebut.edu.cn/gdyw/index.htm)中的新闻，如果需要爬取其它类型的网页，需要重写`Parser.java`接口
- 运行`Spider.java`
