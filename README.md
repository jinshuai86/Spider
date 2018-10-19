# finished：
1. [HttpClient](https://hc.apache.org/httpcomponents-client-ga/)：配置了Http连接池，完成了Http请求和处理Http响应<br>
2. [Jsoup](https://jsoup.org/)：解析响应的内容。
3. 配置线程池，[Redis](https://redis.io/)缓存URL种子
4. 持久化解析结果到文本文件

# unfinished:
2. 优化Redis配置，线程池参数配置。并将配置信息写入配置文件
3. 解析页面代码待优化