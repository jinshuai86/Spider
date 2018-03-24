package com;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author: JS
 * @date: 2018/3/22
 * @description:
 */
public class TestHttpClient{

    private static CloseableHttpClient httpClient = HttpClients.createDefault();
    private static ResponseHandler<String> responseHandler;
    private static CloseableHttpResponse response;
    private static HttpEntity httpEntity;

    public static void main(String[] args) {
        testPost();
    }

    static void testPost() {
        try {
            HttpPost httpPost = new HttpPost("http://ikc.hebut.edu.cn/view/User/Login.ashx");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("userid", "js_214"));
            nvps.add(new BasicNameValuePair("userpassword", "123456"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            response = httpClient.execute(httpPost);
            System.out.println(response.getStatusLine());
            httpEntity = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(httpEntity);
            EntityUtils.toString(httpEntity);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void testGet() {
        HttpGet httpGet = new HttpGet(getURI("https","baike.baidu.com","/item/数据库引擎",null));
        try {
            response = httpClient.execute(httpGet);
            httpEntity = response.getEntity();            System.out.println(response.getStatusLine());
            System.out.println("Executing request " + httpGet.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = TestHttpClient.getSingleResponseHandlerInstance();
            String responseBody = httpClient.execute(httpGet, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
                httpClient.close();
                response.getEntity();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static ResponseHandler getSingleResponseHandlerInstance() {
        if (responseHandler == null) {
            synchronized (TestHttpClient.class) {
                if (responseHandler == null) {
                    responseHandler = new ResponseHandler<String>() {
                        public String handleResponse(final HttpResponse httpResponse) throws ClientProtocolException, IOException {
                            int status = httpResponse.getStatusLine().getStatusCode();
                            if (status >= 200 && status < 300) {
                                HttpEntity entity = httpResponse.getEntity();
                                return entity != null ? EntityUtils.toString(entity,"UTF-8") : null;
                            } else {
                                throw new ClientProtocolException("Unexpected response status: " + status);
                            }
                        }
                    };
                }
            }
        }
        return responseHandler;
    }

    static URI getURI(String scheme, String host, String path, HashMap<String,String> ...parameters) {
        URI uri = null;
        try {
            uri = new URIBuilder()
                    .setScheme(scheme)
                    .setHost(host)
                    .setPath(path)
                    .setParameter("btnG", "Google Search")
                    .setParameter("aq", "f")
                    .setParameter("oq", "")
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }

}