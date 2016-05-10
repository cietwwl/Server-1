package com.fy;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fy.common.FastJsonUtil;
import com.fy.http.HttpClientUtil;

public class TestService {

	public static void main(String[] args) throws Exception {
		
		
		ContentPojo content = new ContentPojo();
		content.setServerId(1);
		content.setChannelId("中国电信");
		
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("content", FastJsonUtil.toJson(content));
		
		String serverIp = "127.0.0.1";
		int port = 9090;
		String url = "http://"+serverIp+":"+port+"/charge";
		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(new StringEntity(FastJsonUtil.toJson(content),"UTF-8"));
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		httpClient.execute(httppost);
		
		String resp = HttpClientUtil.post("127.0.0.1", 9090, params);
		System.out.println(resp);
	}
	
	
	
}
