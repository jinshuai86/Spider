package com.jinshuai.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.ByteArrayBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: JS
 * @date: 2018/3/22
 * @description:
 *  创建单例HttpUtils，获取HttpClient实例执行HTTP请求根据状态码解析响应体。
 */
public class HttpUtils {

    private static volatile HttpUtils HTTPUTILS;

    private PoolingHttpClientConnectionManager httpClientConnectionManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    private static final int MAX_TOTAL_CONNECTIONS = 200;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 20;
    private static final int SOCKET_TIMEOUT = 10000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;

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
        configHttpPool();
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

            HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.getDefaultHostnameVerifier();

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
            LOGGER.error("SSL配置出错",e);
        }
    }

    /**
     * 获取HttpClient
     *
     * */
    private CloseableHttpClient getHttpClient() {
        // 请求配置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .build();
        // 将配置信息应用到HttpClient
        if (httpClientConnectionManager == null) {
            LOGGER.error("httpClientConnectionManager未被初始化");
        }
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(httpClientConnectionManager)
                .build();
        return httpClient;
    }

    /**
     * 发Get请求
     *
     * */
    private HttpEntity sendRequest(final String urlString) {
        HttpUtils httpUtils;
        CloseableHttpClient httpClient;
        HttpGet httpGet = getHttpGet(urlString);
        HttpResponse response;
        HttpEntity httpEntity = null;
        try {
            httpUtils = HttpUtils.getSingleInstance();
            httpClient = httpUtils.getHttpClient();
            response = httpClient.execute(httpGet);
            // 根据状态码执行不同的操作
            int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case 200:
                    httpEntity = response.getEntity();
                    break;
                case 400:
                    LOGGER.error("下载400错误代码，请求出现语法错误[{}]",urlString);
                    break;
                case 403:
                    LOGGER.error("下载403错误代码，资源不可用[{}]",urlString);
                    break;
                case 404:
                    LOGGER.error("下载404错误代码，无法找到指定资源地址[{}]",urlString);
                    break;
                case 503:
                    LOGGER.error("下载503错误代码，服务不可用[{}]",urlString);
                    break;
                case 504:
                    LOGGER.error("下载504错误代码，网关超时[{}]",urlString);
                    break;
                default:
                    LOGGER.error("错误代码[{}],请求失败[{}]",statusCode,urlString);
            }
        } catch (IOException e) {
            LOGGER.error("IO出错[{}]",e);
        }
        return httpEntity;
    }

    /**
     * 获取HttpGet
     *
     * */
    private HttpGet getHttpGet(final String urlString) {
        URL url = null;
        URI uri = null;
        try {
            url = new URL(urlString);
            uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpGet httpGet = new HttpGet(uri);
        // 添加请求头header
        httpGet.addHeader("Accept", "*/*");
        httpGet.addHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        httpGet.addHeader("Connection", "keep-alive");
        httpGet.addHeader("Accept-Encoding", "gzip, deflate");
        int randomUserAgent = new Random().nextInt(UserAgentArray.USER_AGENT.length);
        httpGet.addHeader("User-Agent",UserAgentArray.USER_AGENT[randomUserAgent]);

        return httpGet;
    }

    /**
     * 获取 HttpEntity
     *
     * */
    public String getContent(final String urlString) {
        // url为空或者不是http协议
        if (urlString == null || !urlString.startsWith("http")) {
            return null;
        }
        // 防止SSL过程中的握手警报 TODO http://dovov.com/ssljava-1-7-0unrecognized_name.html
        if (urlString.startsWith("https")) {
            System.setProperty("jsse.enableSNIExtension", "false");
        }
        String content = null;
        try {
            HttpEntity httpEntity = sendRequest(urlString);
            if (httpEntity == null) {
                LOGGER.error("HttpEntity为空");
                return null;
            }
            InputStream inputStream = httpEntity.getContent();
            content = parseStream(inputStream, httpEntity);
        } catch (IOException e) {
            LOGGER.error("获取响应流失败[{}]",e);
        } catch (Exception e) {
            LOGGER.error("捕获异常[{}]",e);
        }
        return content;
    }

    /**
     * 解析响应流
     *
     * */
    private String parseStream(final InputStream inputStream, final HttpEntity httpEntity) {
        String pageContent = null;
        // 获取页面编码：1. 从响应头content-type 2. 如果没有则从返回的HTML中获取Meta标签里的编码
        ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(4096);
        byte[] tempStore = new byte[4096];
        int count = 0;
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
            LOGGER.error("处理流失败[{}]",e);
        }
        return pageContent;
    }

    /**
     * Test HttpUtils
     * */
    public static void main(String[] args) {
        String url2 = "http://202.113.112.30";
//        String url2 = "http://port.patentstar.cn/bns/PtDataSvc.asmx?op=GetPatentData&_strPID=CN105961023A&_PdTpe=CnDesXmlTxt";
        System.out.println(HttpUtils.getSingleInstance().getContent(url2));
    }

}