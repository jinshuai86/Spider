package com.jinshuai.util.http;

import com.jinshuai.core.scheduler.Scheduler;
import com.jinshuai.core.scheduler.impl.PriorityQueueScheduler;
import com.jinshuai.entity.UrlSeed;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.ByteArrayBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: JS
 * @date: 2018/3/22
 * @description:
 *  创建单例HttpUtils，获取HttpClient实例执行HTTP请求根据状态码解析响应体。
 */
@Slf4j
public class HttpUtils {

    private static final ThreadLocal<HttpGet> httpGetContainer = new ThreadLocal<>();

    private static final ThreadLocal<HttpEntity> httpEntityContainer = new ThreadLocal<>();

    private static volatile HttpUtils HTTPUTILS;

    private static Scheduler scheduler;

    private PoolingHttpClientConnectionManager httpClientConnectionManager;

    private CloseableHttpClient httpClient;

    private static final int MAX_TOTAL_CONNECTIONS = 20;
    private static final int SOCKET_TIMEOUT = 5000;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 200;
    private static final int CONNECTION_REQUEST_TIMEOUT = 5000;
    private static final int CONNECT_TIMEOUT = 5000;

    /**
     * 获取HttpUtils单例
     * */
    public static HttpUtils getSingleInstance() {
        if (HTTPUTILS == null) {
            synchronized (HttpUtils.class) {
                if (HTTPUTILS == null) {
                    HTTPUTILS = new HttpUtils();
                }
            }
        }
        return HTTPUTILS;
    }

    HttpUtils() {
        init();
    }

    private void init() {
        configHttpPool();
        configHttpClient();
        scheduler = new PriorityQueueScheduler();
    }

    /**
     * 配置HTTP连接池
     *
     * */
    private void configHttpPool() {
        try {
            // 配置SSL
            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .build();

//            HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.getDefaultHostnameVerifier();
            // 关闭域名证书验证
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext, hostnameVerifier);

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslsf)
                    .build();

            // 将SSL集成到HttpConnectionManager
            httpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // 设置HTTP连接池最大连接数
            httpClientConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
            // 每个路由最大的连接数
            httpClientConnectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
            // 设置socket超时时间
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(SOCKET_TIMEOUT).build();
            httpClientConnectionManager.setDefaultSocketConfig(socketConfig);
        } catch (Exception e) {
            log.error("SSL配置出错",e);
        }
    }

    /**
     * 配置HttpClient
     *
     * */
    private void configHttpClient() {
        // 请求配置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .build();
        // 将配置信息应用到HttpClient
        if (httpClientConnectionManager == null) {
            log.error("httpClientConnectionManager未被初始化");
            return;
        }
        httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(httpClientConnectionManager)
                .build();
    }

    /**
     * 配置HttpGet
     *
     * */
    private HttpGet getHttpGet(String urlStr) {
        URL url;
        URI uri = null;
        try {
            url = new URL(urlStr);
            uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
        } catch (MalformedURLException | URISyntaxException e) {
            log.error("字符串格式不正确[{}]",urlStr,e);
        }
        HttpGet httpGet = new HttpGet(uri);
        // 添加请求头header
        httpGet.addHeader("Accept", "*/*");
        httpGet.addHeader("Accept-Encoding", "gzip, deflate");
        httpGet.addHeader("Connection", "keep-alive");
        int randomUserAgent = new Random().nextInt(UserAgentArray.USER_AGENT.length);
        httpGet.addHeader("User-Agent",UserAgentArray.USER_AGENT[randomUserAgent]);

        return httpGet;
    }

    /**
     * 发Get请求
     *
     * */
    private void sendRequest(String urlStr) {
        HttpGet httpGet = httpGetContainer.get();
        try {
            HttpResponse response = httpClient.execute(httpGet);
            // 根据状态码执行不同的操作
            int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode / 100) {
                case 2:
                    executeStrategy(HttpUtils.SuccessStrategy.getInstance(), urlStr, response);
                    break;
                case 3:
                    executeStrategy(HttpUtils.RedirectStrategy.getInstance(), urlStr, response);
                    break;
                case 4:
                    executeStrategy(HttpUtils.ClientErrorStrategy.getInstance(), urlStr, response);
                    break;
                case 5:
                    executeStrategy(HttpUtils.ServerErrorStrategy.getInstance(), urlStr, response);
                    break;
            }
        } catch (IOException e) {
            log.error("IO出错[{}]", urlStr, e);
        }
    }

    /**
     * 获取 HttpEntity
     *
     * */
    public String getContent(String urlStr) {
        // url为空或者不是http协议
        if (urlStr == null || !urlStr.startsWith("http")) {
            return null;
        }
        // 防止SSL过程中的握手警报 http://dovov.com/ssljava-1-7-0unrecognized_name.html
        if (urlStr.startsWith("https")) {
            System.setProperty("jsse.enableSNIExtension", "false");
        }
        String content = null;
        try {
            httpGetContainer.set(getHttpGet(urlStr));
            sendRequest(urlStr);
            HttpEntity httpEntity = httpEntityContainer.get();
            if (httpEntity == null) {
                log.error("HttpEntity为空");
                return null;
            }
            InputStream inputStream = httpEntity.getContent();
            content = parseStream(inputStream, httpEntity);
        } catch (IOException e) {
            log.error("获取响应流失败", e);
        } catch (Exception e) {
            log.error("获取内容异常", e);
        } finally {
            httpGetContainer.get().releaseConnection();
            httpGetContainer.remove();
        }
        return content;
    }

    /**
     * 解析响应流
     *
     * */
    private String parseStream(InputStream inputStream, HttpEntity httpEntity) {
        String pageContent = null;
        // 获取页面编码：1. 从响应头content-type 2. 如果没有则从返回的HTML中获取Meta标签里的编码
        ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(4096);
        byte[] tempStore = new byte[4096];
        int count;
        try {
            // read(tempStore) 会重新从零开始存->刷新字节数组 ,并返回读到的字节数量
            while ((count = inputStream.read(tempStore)) != -1) {
                byteArrayBuffer.append(tempStore, 0, count);
            }
            // TODO:下面复制粘贴的：https://github.com/xjtushilei/ScriptSpider
            // 根据获取的字节编码转为String类型
            String charset = "UTF-8";
            ContentType contentType = ContentType.getOrDefault(httpEntity);
            Charset charsets = contentType.getCharset();
            pageContent = new String(byteArrayBuffer.toByteArray());
            // 如果响应头中含有content-type字段，直接读取然后设置编码即可。
            if (null != charsets) {
                charset = charsets.toString();
            } else {
                // 发现HttpClient带的功能有问题，这里自己又写了一下。
                Pattern pattern = Pattern.compile("<head>([\\s\\S]*?)<meta([\\s\\S]*?)charset\\s*=(\")?(.*?)\"");
                Matcher matcher = pattern.matcher(pageContent.toLowerCase());
                if (matcher.find()) {
                    charset = matcher.group(4);
                }
            }
            pageContent = new String(byteArrayBuffer.toByteArray(),charset);
        } catch (IOException e) {
            log.error("处理流失败", e);
        }
        return pageContent;
    }

    /**
     * 执行具体的策略
     *
     * */
    private void executeStrategy(StatusHandler statusHandler, String url, HttpResponse response) {
        statusHandler.process(url, response);
    }

    /**
     * 2XX 策略
     * 成功获取响应时对应的执行策略
     *
     * */
    public static class SuccessStrategy implements StatusHandler {

        private static final StatusHandler statusHandler = new SuccessStrategy();

        static StatusHandler getInstance() {
            return statusHandler;
        }

        @Override
        public void process(String url, HttpResponse response) {
            httpEntityContainer.set(response.getEntity());
        }

    }

    /**
     * 3XX 策略
     * 重定向时对应的执行策略
     *
     * */
    public static class RedirectStrategy implements StatusHandler {

        private static final StatusHandler statusHandler = new RedirectStrategy();

        static StatusHandler getInstance() {
            return statusHandler;
        }

        @Override
        public void process(String url, HttpResponse response) {
            Header location = response.getFirstHeader("Location");
            // 将location对应的URL放到仓库中
            scheduler.push(new UrlSeed(location.getValue(), 5));
            log.error("301: 资源已被重定向[{}]", url);
        }

    }

    /**
     * 4XX 策略
     * 主要处理需要认证的资源401，需要授权的资源403，以及不存在的资源404
     * 当请求次数过多以后，就容易报403
     * 当 401，403时，将资源放到低优先级的队列或者消息队列中，额外处理。 TODO
     * */
    public static class ClientErrorStrategy implements StatusHandler {

        private static final StatusHandler statusHandler = new ClientErrorStrategy();

        static StatusHandler getInstance() {
            return statusHandler;
        }

        @Override
        public void process(String url, HttpResponse response) {
            int status = response.getStatusLine().getStatusCode();

            if (status == 401 || status == 403) {
                log.error("401: 无权访问此资源[{}]", url);
            } else if (status == 404) {
                log.error("404: 请求的资源不存在[{}]", url);
            }
        }

    }

    /**
     * 5XX 策略
     * 远端服务器出错，应对办法是暂时停止爬虫 TODO
     * */
    public static class ServerErrorStrategy implements StatusHandler {

        private static final StatusHandler statusHandler = new ServerErrorStrategy();

        static StatusHandler getInstance() {
            return statusHandler;
        }

        @Override
        public void process(String url, HttpResponse response) {
            log.error("500: 远端服务器出错[{}]", url);
            log.info("由于远程服务器出错，爬虫休息 5 秒后，尝试继续执行任务.....");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error("sleep error", e);
            }

        }

    }

    /**
     * Test HttpUtils
     *
     *  具体逻辑：HttpClient用封装好的HttpGet发送get请求，获取HttpEntity，从HttpEntity中获取响应内容以及响应头
     *  从响应头Content-Type中获取charset编码格式，如果响应头中没有编码格式响应头，就从响应内容中解析meta标签获取编码格式
     *  然后将字节数组按响应头中的编码格式创建字符串
     * */
    public static void main(String[] args) {
        String url1 = "https://jinshuai86.github.io/about";
        String url2 = "http://port.patentstar.cn/bns/PtDataSvc.asmx?op=GetPatentData&_strPID=CN105961023A&_PdTpe=CnDesXmlTxt";
        String url3 = "https://www.toutiao.com/";
        String url4 = "http://xww.hebut.edu.cn";
        String url5 = "http://www.baidu.com";
        String url7 = "https://www.douban.com";
        for (int i = 0; i < 100; i++) {
            System.out.println(i + " ============ ");
            System.out.println(HttpUtils.getSingleInstance().getContent(url3));
        }
    }

}