package com.fy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import com.fy.common.FastJsonUtil;

public class TestService {

	public static void main(String[] args) throws Exception {
		
		
		ContentPojo content = new ContentPojo();
		content.setCpTradeNo("1027");
		content.setGameId(3);
		content.setUserId("100100000455");
		content.setRoleId("100100000466");
		
		content.setServerId(20);
		content.setChannelId("0");
		
		content.setItemId("1");
		content.setItemAmount(1);
		content.setPrivateField("");
		content.setMoney(600);
		content.setCurrencyType("CNY");
		content.setFee(6);
		
		content.setStatus("0");
		content.setGiftId("800701");
		content.setSign("cpTradeNo|gameId|userId|roleId|serverId|channelId|itemId|itemAmount|privateField|money|status|privateKey");
		
		String serverIp = "127.0.0.1";
		int port = 9090;
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
