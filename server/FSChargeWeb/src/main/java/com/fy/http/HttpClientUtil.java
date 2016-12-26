package com.fy.http;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.alibaba.druid.support.json.JSONUtils;


public class HttpClientUtil {

	private static Map<String,HttpClientSender> senderMap = new ConcurrentHashMap<String,HttpClientSender>();
	
	
	
	
	public static String post(String serverIp, int port, Map<String,?> params) throws Exception{
		String url = "http://"+serverIp+":"+port+"/charge";
		return get(url ).post(params);
	}
	
	private static String post(String url,Map<String,?> params) throws Exception{
		return get(url).post(params);
	}
	
	private static synchronized HttpClientSender get(String url){
		
		HttpClientSender httpClientSender = senderMap.get(url);
		if(httpClientSender == null){
			httpClientSender = new HttpClientSender(url);
			senderMap.put(url, httpClientSender);
		}
		return httpClientSender;
	}
	
	
	public static void main(String[] args) throws InterruptedException {
//		ExecutorService service = Executors.newFixedThreadPool(30);
//		for (int i = 0; i < 10000; i++) {
//			service.submit(new Runnable() {
//				
//				public void run() {
//					String url = "http://127.0.0.1:8080/charge";
//			    	Map<String, Object> params = new HashMap<String, Object>();
//			    	params.put("userName", "testU");
//			    	params.put("password", "testP");
//					try {
//						HttpClientUtil.post(url, params);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//				}
//			});
//		}
//		
//		while (true) {
//			Thread.sleep(1000);			
//		}
		String url = "http://119.29.162.42:10006/FSGameRounter/9game/roleinfo";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("id", 123456897);
		Map<String, String> client = new HashMap<String, String>();
		client.put("caller", "ka.game");
		client.put("ex", "tttt");
		params.put("client", client);
		Map<String, String> data = new HashMap<String, String>();
		data.put("params", "kcidjeaoaldfgof");
		params.put("data", data);
		params.put("encrypt", "md5");
		params.put("sign", "kclfdmf5s4f5r2bhs25ds23g5t");
		try {
			HttpPost post = new  HttpPost(url);
			HttpClientBuilder postClient = HttpClientBuilder.create();
			String jsonString = JSONUtils.toJSONString(params);
			System.out.println(jsonString);
			StringEntity entity = new StringEntity(jsonString, "utf-8");
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			post.setEntity(entity);
			CloseableHttpClient httpClient = postClient.build();
			CloseableHttpResponse response = httpClient.execute(post);
			if(response.getStatusLine().getStatusCode() == 200){
				HttpEntity he = response.getEntity();
				System.out.println("response string:"+ EntityUtils.toString(he, "UTF-8"));
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
