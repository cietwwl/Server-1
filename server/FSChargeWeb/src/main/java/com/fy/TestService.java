package com.fy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.fy.common.FastJsonUtil;

public class TestService {

	public static void main(String[] args) throws Exception {
		
		
		ContentPojo content = new ContentPojo();
		content.setCpTradeNo("1040");
		content.setGameId(3);
//		content.setUserId("100100000455");
		content.setRoleId("100100007337");
		
		content.setServerId(27);
		content.setChannelId("0");
		
		content.setItemId("com.yh.dzfs107");
		content.setItemAmount(1);
		content.setPrivateField("");
		content.setMoney(64800);
		content.setCurrencyType("CNY");
		content.setFee(6);
		
		content.setStatus("0");
		content.setGiftId("800701");
		content.setSign("cpTradeNo|gameId|userId|roleId|serverId|channelId|itemId|itemAmount|privateField|money|status|privateKey");
		
		String serverIp = "127.0.0.1";
		int port = 10007;
		
		Resource resource = new ClassPathResource("charge.properties");
		int serverPort = 10000;
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			serverPort = Integer.parseInt(props.getProperty("serverPort"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		String url = "http://"+serverIp+":"+port+"/charge";

		String jsonContent = FastJsonUtil.toJson(content);
		post(url,jsonContent,"utf-8",1000,1000);
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
