package com.fy.http;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
		ExecutorService service = Executors.newFixedThreadPool(30);
		for (int i = 0; i < 10000; i++) {
			service.submit(new Runnable() {
				
				public void run() {
					String url = "http://127.0.0.1:8080/charge";
			    	Map<String, Object> params = new HashMap<String, Object>();
			    	params.put("userName", "testU");
			    	params.put("password", "testP");
					try {
						HttpClientUtil.post(url, params);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
		}
		
		while (true) {
			Thread.sleep(1000);			
		}
		
		
	}
	
	
}
