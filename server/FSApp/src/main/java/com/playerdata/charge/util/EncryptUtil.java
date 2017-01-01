package com.playerdata.charge.util;

import java.security.MessageDigest;

public class EncryptUtil {
	
	public static final String CHARSET_NAME_UTF8 = "UTF-8";

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}
	
	public static String encrypt(String original, String encryptType, String charset) {
		String resultString = null;
		try {
			resultString = new String(original);
			MessageDigest md = MessageDigest.getInstance(encryptType);
			resultString = EncryptUtil.byteArrayToHexString(md.digest(resultString.getBytes(charset)));
		} catch (Exception ex) {
		}
		return resultString;
	}

}

