package com.rounter.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 系统要用到的公用方法，例如加密解密
 * 
 * @author Alex
 *
 *         2016年12月11日 下午5:40:01
 */
public class Utils {

	static Logger logger = LoggerFactory.getLogger(Utils.class);
	/**
	 * 九游的apikey
	 */
	private static final String ApiKey_9Game = "dedffebe8e6dd8423a4cceca03effaca";
	
	/**
	 * 九游的加密key
	 */
	private static final String EncryptKey_9Game = "d259ksx59wS68x5M";
	
	/**
	 * 九游json的key
	 */
	private static final String Param_9Game = "params";
	
	/**
	 * 获取9游签名
	 * @param caller
	 * @param content 签名内容(加密后的data内容)
	 * @return
	 */
	public static String get9GameSign(String caller, String content){
		return MD5.getMD5String(caller + Param_9Game + content + ApiKey_9Game);
	}
	
	/**
	 * 9游的加密方式
	 * 
	 * @param dataContent JSON格式中data节点下的一组key-value参数 (json字符串)
	 * @return
	 */
	public static String encrypt9Game(String dataContent) {
		
		try {
			return encrypt(EncryptKey_9Game, EncryptKey_9Game, dataContent);
		} catch (Exception e) {
			logger.error("加密9游消息时出现异常!!,消息内容：{}", dataContent);
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 9游的解密方式
	 * 
	 * @param decryptString
	 *            要解密的字符串
	 * @return 解密后返回的JSON字符串
	 */
	public static String decrypt9Game(String decryptString) {

		String str = "";
		try {
			str = decrypt(EncryptKey_9Game, EncryptKey_9Game, decryptString);
		} catch (Exception e) {
			logger.error("解密9游消息时出现异常!!,消息内容：{}", decryptString);
			e.printStackTrace();
		}
		return str;
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
	private static String encrypt(String key, String iv, String text)
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
	private static String decrypt(String key, String iv, String text)
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
