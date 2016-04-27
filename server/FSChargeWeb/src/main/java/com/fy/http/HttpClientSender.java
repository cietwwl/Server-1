package com.fy.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * HttpClient工具类
 * 
 * @return
 * @author allen
 * @create 2015年12月18日
 */
class HttpClientSender {

    private final int timeOut = 10 * 1000;

    private CloseableHttpClient httpClient = null;
    
    private String url;

    public HttpClientSender(String url){
    	 String hostname = url.split("/")[2];
         int port = 80;
         if (hostname.contains(":")) {
             String[] arr = hostname.split(":");
             hostname = arr[0];
             port = Integer.parseInt(arr[1]);
         }
         httpClient = createHttpClient(2, 2, 2, hostname, port);
         this.url = url;
    }

    private void config(HttpRequestBase httpRequestBase) {
       
    	// 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeOut)
                .setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
        httpRequestBase.setConfig(requestConfig);
    }


    /**
     * 创建HttpClient对象
     * 
     * @return
     * @author allen
     * @create 2015年12月18日
     */
    private CloseableHttpClient createHttpClient(int maxTotal, int maxPerRoute, int maxRoute, String hostname, int port) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", plainsf).register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                    int executionCount, HttpContext context) {
                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setRetryHandler(httpRequestRetryHandler).build();

        return httpClient;
    }

    private void setPostParams(HttpPost httpost,
            Map<String, ?> params) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * GET请求URL获取内容
     * 
     * @param url
     * @return
     * @author allen
     * @throws Exception 
     * @create 2015年12月18日
     */
    public String post( Map<String, ?> params) throws Exception {
        HttpPost httppost = new HttpPost(url);
        config(httppost);
        setPostParams(httppost, params);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httppost, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            return result;
        }  finally {
            try {
                if (response != null){
                	response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    /**
//     * GET请求URL获取内容
//     * 
//     * @param url
//     * @return
//     * @author allen
//     * @create 2015年12月18日
//     */
//    public static String get(String url) {
//        HttpGet httpget = new HttpGet(url);
//        config(httpget);
//        CloseableHttpResponse response = null;
//        try {
//            response = getHttpClient(url).execute(httpget,
//                    HttpClientContext.create());
//            HttpEntity entity = response.getEntity();
//            String result = EntityUtils.toString(entity, "utf-8");
//            EntityUtils.consume(entity);
//            return result;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (response != null)
//                    response.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }

  
    public static void main(String[] args) throws Exception {
    	String url = "http://127.0.0.1:8080/charge";
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("userName", "testU");
    	params.put("password", "testP");
		new HttpClientSender(url).post( params );
	}

   
}