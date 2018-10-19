package com;

import org.apache.http.util.ByteArrayBuffer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author: JS
 * @date: 2018/9/11
 * @description:
 */

public class HttpRequestUtil {

    /**
     * 向指定URL发送GET方法的请求
     * 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param url
     *            发送请求的URL
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url) {
        StringBuilder result = new StringBuilder();
        String res = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Accept-Encoding", "gzip");
            conn.setRequestProperty("Content-Type", "text/html; charset=UTF-8");
            // 定义 BufferedReader输入流来读取URL的响应
            InputStream inputStream = realUrl.openStream();
            in = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
                res += line;
            }
        } catch (Exception e) {
            result = new StringBuilder("{\"resCode\":\"1\",\"errCode\":\"1001\",\"resData\":\"\"}");
            e.printStackTrace();
            System.out.println("远程服务未开启");
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            String s = new String(result.toString().getBytes(),"UTF-8");
            System.out.println(s);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {

        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
//			System.out.println("发送 POST 请求出现异常！" + e);
            result = new StringBuilder("{\"resCode\":\"1\",\"errCode\":\"1001\",\"resData\":\"\"}");
            e.printStackTrace();
            System.out.println("远程服务未开启");
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        Object o = result;
        return result.toString();
    }

    private void sendGet() throws Exception {
        String url = "http://www.pss-system.gov.cn/sipopublicsearch/patentsearch/showNavigationClassifyNum-showBasicClassifyNumPageByIPC.shtml?params=D7B3D1618C9AC685055FF6612F62529676324C8B6E7F921902B2C40318E0E7BB";
        URL obj = new URL(url);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        //添加请求头
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.81 Safari/537.36");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        InputStream inputStream = con.getInputStream();
        ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(4096);
        byte[] tempStore = new byte[4096];
        int count = 0;
        try {

            while ((count = inputStream.read(tempStore)) != -1) {
                byteArrayBuffer.append(tempStore, 0, count);
            }
            String inputLine = new String(byteArrayBuffer.toByteArray());

            StringBuffer response = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String str = response.toString();
            str = new String(str.getBytes(), "gbk");
            //打印结果
            System.out.println(str);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        public static void main (String[] args) throws Exception {
            HttpRequestUtil httpRequestUtil = new HttpRequestUtil();
            httpRequestUtil.sendGet();
//        String result = httpRequestUtil.sendGet("http://www.pss-system.gov.cn/sipopublicsearch/patentsearch/showNavigationClassifyNum-showBasicClassifyNumPageByIPC.shtml?params=D7B3D1618C9AC685055FF6612F62529676324C8B6E7F921902B2C40318E0E7BB");
//      String result = httpRequestUtil.sendPost("http://www.pss-system.gov.cn/sipopublicsearch/patentsearch/showNavigationClassifyNum-showBasicClassifyNumPageByIPC.shtml","params=D7B3D1618C9AC685055FF6612F62529676324C8B6E7F921902B2C40318E0E7BB");
//        System.out.println(result);
//      String result = httpRequestUtil.sendFile("http://www.pss-system.gov.cn/sipopublicsearch/patentsearch/showNavigationClassifyNum-showBasicClassifyNumPageByIPC.shtml","params=D7B3D1618C9AC685055FF6612F62529676324C8B6E7F921902B2C40318E0E7BB","bInputStream");
        }
    }

