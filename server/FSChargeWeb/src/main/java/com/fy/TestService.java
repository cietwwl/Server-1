package com.fy;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.fy.common.FastJsonUtil;
import com.fy.http.HttpClientUtil;

public class TestService {

	public static void main(String[] args) throws Exception {
		
//		String test="%7B%22fee%22%3A0%2C%22gameId%22%3A0%2C%22itemAmount%22%3A0%2C%22money%22%3A0%2C%22serverId%22%3A1%7D";
////		String utf = new String(test.getBytes("gb2312"),"UTF-8");
//		String decode = URLDecoder.decode(test, "utf-8");
//		System.out.println(decode);
		
		
		ContentPojo content = new ContentPojo();
		content.setCpTradeNo("1023");
		content.setGameId(3);
		content.setUserId("100100000353");
		content.setRoleId("10010000");
		
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
		
//		Map<String, Object> params = new HashMap<String, Object>();		
//		params.put("content", FastJsonUtil.toJson(content));
		
		String serverIp = "127.0.0.1";
		int port = 9090;
		String url = "http://"+serverIp+":"+port+"/charge";
		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(new StringEntity(FastJsonUtil.toJson(content),"UTF-8"));
		
//		String resp = HttpClientUtil.post("127.0.0.1", 9090, params);
//		String resp = ChargeService?
		System.out.println("!!!!!!!!!!     ");
		
	}
	
}
