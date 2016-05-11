package com.fy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
		
		content.setStatus("0");
		content.setGiftId("800701");
		content.setSign("cpTradeNo|gameId|userId|roleId|serverId|channelId|itemId|itemAmount|privateField|money|status|privateKey");
		
//		Map<String, Object> params = new HashMap<String, Object>();		
//		params.put("content", FastJsonUtil.toJson(content));
		
		String serverIp = "127.0.0.1";
		int port = 9090;
		String url = "http://"+serverIp+":"+port+"/charge";
//		HttpPost httppost = new HttpPost(url);
//		httppost.setEntity(new StringEntity(FastJsonUtil.toJson(content),"UTF-8"));
//		
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		httpClient.execute(httppost);
		
		String jsonContent = FastJsonUtil.toJson(content);
		post(url,jsonContent,"utf-8",1000,1000);
		
		
//		String resp = HttpClientUtil.post("127.0.0.1", 9090, params);
//		System.out.println(resp);
	}
	
	public static void post(String url ,String requestData ,String charset,int connectTimeout,int responseTimeout) throws IOException{
		HttpURLConnection con = null;
		OutputStream out = null;
		InputStream in = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		String[] response = new String[2];
		try{
			con = (HttpURLConnection)(new URL(url).openConnection());
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setConnectTimeout(connectTimeout);
			con.setReadTimeout(responseTimeout);
			out = con.getOutputStream();
			out.write(requestData.getBytes(charset));
			out.flush();
			if(HttpURLConnection.HTTP_OK== con.getResponseCode()){
				response[0] = String.valueOf(HttpURLConnection.HTTP_OK);
				in = con.getInputStream();
				inputStreamReader = new InputStreamReader(in, charset);
				reader = new BufferedReader(inputStreamReader);
				StringBuilder buffer = new StringBuilder("");
				String line = null;
				while((line = reader.readLine()  )!= null){
					buffer.append(line);
				}
				response[1] = buffer.toString();
				
				
			}else{
				response[0] = String.valueOf(con.getResponseCode());
			}
		}finally{
			
		}
			
	}
	
}
