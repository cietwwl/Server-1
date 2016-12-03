package com.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fy.json.JSONObject;

public class VersionWebTest {
	
	public static void main(String[] args) {

		URL pageUrl;
		try {
//			pageUrl = new URL("http://119.29.157.158:10005/FSVerWeb-0.0.1-SNAPSHOT/service");
			pageUrl = new URL("http://192.168.2.129:8080/FSVerWeb/service");
//			pageUrl = new URL("http://106.75.143.227:10005/FSVerWeb/service");
//			pageUrl = new URL("http://123.59.146.63:10005/FSVerWeb-0.0.1-SNAPSHOT/service");
//			pageUrl = new URL("http://192.168.2.247:8070/FSVerWeb/service");
			HttpURLConnection connection = (HttpURLConnection) pageUrl.openConnection();

			HttpURLConnection connection2 = connection;
			connection2.setRequestMethod("POST");

			connection2.setDoInput(true);

			connection2.setDoOutput(true);

			connection2.setRequestProperty("Content-Type", "application/json");

			JSONObject json = new JSONObject();
			json.put("channel", "ios");
			json.put("main", 1);
			json.put("sub", 0);
			json.put("third", 0);
			json.put("patch", 0);
//			json.put("package", "com.rainwings.dzfs");
			String content = json.toString();
			byte[] contentbyte = content.getBytes("UTF-8");

			connection2.setRequestProperty("Content-Length", ""
					+ contentbyte.length);

			connection2.setRequestProperty("Cache-Control", "no-cache");

			connection2.setRequestProperty("Pragma", "no-cache");

			connection2.setRequestProperty("Expires", "-1");

			OutputStream out = connection2.getOutputStream();

			out.write(contentbyte);
			out.flush();

			out.close();

			InputStream inputStream = connection2.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			String readLine = reader.readLine();
			System.out.println(readLine);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
