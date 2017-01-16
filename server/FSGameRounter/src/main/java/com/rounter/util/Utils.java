package com.rounter.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.rounter.param.impl.Request9Game;
import com.rounter.param.impl.Response9Game;

/**
 * 系统要用到的公用方法，例如加密解密
 * 
 * @author Alex
 *
 *         2016年12月11日 下午5:40:01
 */
public class Utils {

	/**
	 * 9游的加密方式
	 * 
	 * @param resp
	 * @return
	 */
	public static Response9Game encrypt9Game(Response9Game resp) {
		return resp;
	}

	/**
	 * 9游的解密方式
	 * 
	 * @param request
	 *            请求的参数
	 * @return
	 */
	public static Request9Game decrypt9Game(Request9Game request) {

		return request;
	}

	/**
	 * 先AES加密再base64编码
	 * 
	 * @param key
	 * @param iv
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String key, String iv, String text)
			throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/NOPadding");
		int blockSize = cipher.getBlockSize();
		byte[] dataBytes = text.getBytes();
		int plaintextLen = dataBytes.length;
		if (plaintextLen % blockSize != 0) {
			plaintextLen = plaintextLen
					+ (blockSize - plaintextLen % blockSize);
		}
		byte[] plaintext = new byte[plaintextLen];
		System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		byte[] encryptBytes = cipher.doFinal(plaintext);
		return new String(Base64.encodeBase64(encryptBytes));

	}

	/**
	 * aes解密，先base64 decode
	 * 
	 * @param key
	 * @param iv
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String key, String iv, String text)
			throws Exception {
		byte[] baseDecryptBytes = Base64.decodeBase64(text);
		Cipher cipher = Cipher.getInstance("AES/CBC/NOPadding");
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());

		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		byte[] orginBytes = cipher.doFinal(baseDecryptBytes);
		return new String(orginBytes).trim();
	}
	
	

	public static void main(String[] args) throws Exception {
		String data = "{\"platform\":2,\"accountId\":\"1a6400b435a1c5ca643a28a09760ae32\",\"gameId\":535733}";
        String key = "1234567890123456";
        String iv = "1234567890123456";
        String encryptResult = encrypt(key,iv,data);
        System.out.println(encryptResult);
        String decryptResult = decrypt(key,iv,encryptResult);
        System.out.println(decryptResult);

	}

}
