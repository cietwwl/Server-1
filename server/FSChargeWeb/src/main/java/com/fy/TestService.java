package com.fy;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.fy.common.FastJsonUtil;
import com.fy.http.HttpClientUtil;

public class TestService {

	public static void main(String[] args) throws Exception {
		
//		String test="%7B%22fee%22%3A0%2C%22gameId%22%3A0%2C%22itemAmount%22%3A0%2C%22money%22%3A0%2C%22serverId%22%3A1%7D";
////		String utf = new String(test.getBytes("gb2312"),"UTF-8");
//		String decode = URLDecoder.decode(test, "utf-8");
//		System.out.println(decode);
		
		
		ContentPojo content = new ContentPojo();
		content.setServerId(20);
		content.setChannelId("中国电信");
		
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("content", FastJsonUtil.toJson(content));
		String resp = HttpClientUtil.post("127.0.0.1", 9090, params);
		System.out.println(resp);
	}
	
}
