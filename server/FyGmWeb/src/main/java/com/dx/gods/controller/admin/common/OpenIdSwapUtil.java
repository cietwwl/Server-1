package com.dx.gods.controller.admin.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public class OpenIdSwapUtil {
	public static void writeToDb(String[] qq, String[] openIds) {
		// String url =
		// "jdbc:mysql://192.168.1.100:3306/dx_aoe?user=root&password=mysql@123&characterResultSets=utf8&characterEncoding=utf8";
		// Connection connection = null;
		// try {
		// Class.forName("com.mysql.jdbc.Driver").newInstance();
		// connection = DriverManager.getConnection(url);
		// connection.isReadOnly();
		// String sql = "update user_index set qq=? where userId=?";
		// PreparedStatement ps = connection.prepareStatement(sql);
		// for (int i = 0; i < qq.length; i++) {
		// if (!openIds[i].equals("null") && !qq[i].equals("null")) {
		// ps.setString(1, qq[i]);
		// ps.setString(2, openIds[i]);
		// ps.addBatch();
		// }
		// }
		// ps.executeBatch();
		// ps.close();
		// connection.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	public static String getOpenId(String qq) {
		qq = qq.replaceAll(" ", "").replaceAll("\r\n", ",").replaceAll("([,]+)", ",");
		String[] qq_array = qq.split(",");
		String[] result = new String[qq_array.length];
		String s = null;
		try {
			if (qq != null && !qq.trim().equals("")) {
				URL uri = new URL("http://openapi.sp0309.3g.qq.com/mgr/uintool.jsp?appId=900000886&qq=" + qq);
				BufferedReader br = new BufferedReader(new InputStreamReader(uri.openStream(), "gb2312"));
				while ((s = br.readLine()) != null) {
					if (s != null && !s.trim().equals("") && s.contains("qq")) {
						s = s.replaceAll("</p><p>", "").replaceAll(" ", "");
						break;
					}
				}
				br.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < qq_array.length; i++) {
			if (s != null && s.contains(qq_array[i])) {
				result[i] = s.substring(s.indexOf(qq_array[i]) + qq_array[i].length() + 7, s
						.indexOf(qq_array[i])
						+ qq_array[i].length() + 39);
			} else {
				result[i] = "null";
			}
		}
		writeToDb(qq_array, result);
		String sss = "";
		for (String string : result) {
			sss += string + "  <a href=\"/XtolGM/admin/getUrl.a?userId=" + string + "\">查书签</a> " + "<br />";
		}
		return sss;
	}

	public static String getQQ(String openIds) {
		openIds = openIds.replaceAll(" ", "").replaceAll("\r\n", ",").replaceAll("([,]+)", ",");
		String[] openId_array = openIds.split(",");
		String[] result = new String[openId_array.length];
		String s = null;
		String[] set = null;
		try {
			if (openIds != null && !openIds.trim().equals("")) {
				URL uri = new URL("http://openapi.sp0309.3g.qq.com/mgr/uintool.jsp?appId=900000886&oid="
						+ openIds);
				BufferedReader br = new BufferedReader(new InputStreamReader(uri.openStream(), "GBK"));
				while ((s = br.readLine()) != null) {
					if (s != null && !s.trim().equals("") && s.contains("qq")) {
						set = s.replaceAll(" ", "").replaceAll("^.*?openId", "openId")
								.replaceAll("</p>$", "").split("</p><p>");
						break;
					}
				}
				br.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < openId_array.length; i++) {
			result[i] = "null";
			for (int j = 0; j < set.length; j++) {
				if (set[j] != null && set[j].contains(openId_array[i])) {
					result[i] = set[j].substring(set[j].indexOf("qq:") + 3, set[j].length());
					break;
				}
			}
		}
		writeToDb(result, openId_array);
		String sss = "";
		for (String string : result) {
			sss += string + "<br />";
		}
		return sss;
	}

	public static void main(String[] args) {
		String qq = "826571491\r\n781860468";
		System.out.println(getOpenId(qq));
		qq = "CFDCF679A384E04564EF279BD2938F8E\r\nC94B0BCC74927B952ACC19AA8D788AD7\r\n02DF66EA9F946BA8F284CD1EDD58457E";
		System.out.println(getQQ(qq));
		System.exit(0);
	}
}
