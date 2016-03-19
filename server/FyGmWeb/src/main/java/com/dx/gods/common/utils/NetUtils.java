package com.dx.gods.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dx.gods.common.log.GMLogger;
import com.dx.gods.controller.admin.common.DXAdminController;

public class NetUtils  extends DXAdminController{

	/**
	 * action给jsp传值
	 * 
	 * @param request
	 * @param message
	 */
	public static String packMessage(HttpServletRequest request, String message) {
		request.setAttribute("mes", message);
		return MESSAGE;
	}

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
			ex.printStackTrace();
			return null;
		}
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
	 * 打包response
	 * @param response
	 * @param content
	 */
	public static void packResponse(HttpServletResponse response, String content) {
		try {
			response.setContentType("text/plain;charset=utf-8");
			response.getWriter().write(content);
		} catch (Exception e) {
			GMLogger.error("组装reponse信息出现异常:" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static InputStream packActionResult(String value) {
		try {
			InputStream result = new ByteArrayInputStream(
					value.getBytes("UTF8"));
			return result;
		} catch (Exception ex) {
			GMLogger.error("组装inputstream出现异常:" + ex.getMessage());
			return null;
		}

	}
}
