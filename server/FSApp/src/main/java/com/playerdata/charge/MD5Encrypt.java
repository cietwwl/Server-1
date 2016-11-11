package com.playerdata.charge;

import java.net.URLDecoder;

public class MD5Encrypt {
	public static final String CHARSET_NAME_UTF8 = EncryptUtil.CHARSET_NAME_UTF8;
	private static final String ENCRYPT_MD5 = "MD5";

	public static String MD5Encode(String origin) {
		return EncryptUtil.encrypt(origin, ENCRYPT_MD5, CHARSET_NAME_UTF8);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(URLDecoder.decode("%7B%22status%22%3A1%2C%22info%22%3A%22sign%E4%B8%8D%E8%83%BD%E4%B8%BA%E7%A9%BA%22%2C%22userinfo%22%3A%7B%7D%7D", "UTF-8"));
	}
}
