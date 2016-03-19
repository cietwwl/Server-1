package com.rw.fsutil.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class HttpUtils {
	
	/**
	 * 以post形式发送message
	 * @param strUrl
	 * @param message
	 * @return
	 */
	public static InputStream sentHttpPostMsg(String strUrl, String message) {
		try {
			URL url = new URL(strUrl);
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(
					connection.getOutputStream(), "8859_1");
			out.write(message);
			out.flush();
			out.close();
			InputStream in = connection.getInputStream();
			return in;
		} catch (Exception ex) {
			//ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 打包消息
	 * @param key
	 * @param value
	 * @param sb
	 */
	public static void packNetMessage(Object key, Object value, StringBuilder sb){
		sb.append(key).append("=").append(value).append("&");
	}
	
	/**
	 * inputstream to string
	 * @param response
	 */
	public static String getInputStreamToString(InputStream response) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		String result = "";
		try {
			while ((i = response.read()) != -1) {
				baos.write(i);
			}
			result = new String(baos.toString().getBytes("UTF-8"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}
}
