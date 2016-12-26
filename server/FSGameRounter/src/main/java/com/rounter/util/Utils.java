package com.rounter.util;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;


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
	private static final String Param_9Game = "params=";
	
	/**
	 * 获取9游签名
	 * @param caller
	 * @param content 签名内容(加密后的data内容)
	 * @return
	 */
	public static String get9GameSign(String caller, String content){
		return MD5.getMD5String(caller + Param_9Game + content + ApiKey_9Game).toLowerCase();
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
	
	public void md5test(){
		String ttr = "changyouparams=a13cPwhP3rK0JRdJsOPUxWlxHCN/gNP6Nh3wL1vguxwCB0YNiV804+5I0kh7jPxMxnm8k19BSK+36qYeBzHm5w==202cb962234w4ers2aa";
		String sign = "7134253783fb479a8dea968e49e56485";
		String md5String = MD5.getMD5String(ttr);
		System.out.println("md5 string:" + md5String + ", eques:" + md5String.equals(sign));

	}
	
	

	public static void main(String[] args) throws Exception {
//		String data = "{\"platform\":2,\"accountId\":\"1a6400b435a1c5ca643a28a09760ae32\",\"gameId\":535733}";
//        String key = "1234567890123456";
//        String iv = "1234567890123456";
//        String encryptResult = encrypt(key,iv,data);
//        System.out.println(encryptResult);
//        String decryptResult = decrypt(key,iv,encryptResult);
//        System.out.println(decryptResult);
//		String data = "PbM+3g//dB6YWuVE8tqLo6zRTw73i2EiC0WW0Z+Am9Bz0ZAdoDpbEE7oOaW5NcC8Ah57OJCelTMPUcReQx4BLzsMsy36gzTC8cfHvY1R+ybEMyg2dIT195SPTP+91hfNOvhbS51zAe64P3pSCGoX3zQh0JhHBPNJvwuSN+XwbLQ=";
//		String data ="krIvZ7vrTVEvx1fYH2OZ7iA8pgvNJQ4ZdsdSud2ytkaGp+JXHeOYWljoq6EKZez1yIDbo6GxWo/3K3Tn0aAXOw==";
//		String string = decrypt9Game(data);
//		System.out.println(string);
		
//		roleInfoTest();
		sendGiftTest();
		
	}
	
	public static void roleInfoTest(){
		String url = "http://119.29.162.42:10006/FSGameRounter/9game/roleinfo";
		//String url = "http://192.168.2.113:8080/FSGameRounter/9game/roleinfo";
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("id", 1330395827);
		JSONObject jsonData = new JSONObject();
		jsonData.put("accountId", "74fa29958e725ab3d2ba0d2e6bfedde8");
		jsonData.put("gameId", 667981);
		jsonData.put("platform", 2);
		String encryptData = encrypt9Game(jsonData.toJSONString());
		Map<String, String> param = new HashMap<String, String>();
		param.put("params", encryptData);
		
		jsonObj.put("data", param);
		
		
		JSONObject jsonClient = new JSONObject();
		jsonClient.put("caller", "ka.9game");
		jsonClient.put("ex", "5987412");
		jsonObj.put("client", jsonClient);
		jsonObj.put("encrypt", "md5");
		String signStr = get9GameSign(jsonClient.getString("caller"), encryptData);
		jsonObj.put("sign", signStr);
		
		
		try {
			HttpPost post = new  HttpPost(url);
			HttpClientBuilder postClient = HttpClientBuilder.create();
			String jsonString = jsonObj.toJSONString();
			System.out.println(jsonString);
			StringEntity entity = new StringEntity(jsonString, "utf-8");
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			post.setEntity(entity);
			CloseableHttpClient httpClient = postClient.build();
			CloseableHttpResponse response = httpClient.execute(post);
			if(response.getStatusLine().getStatusCode() == 200){
				HttpEntity he = response.getEntity();
				String string = EntityUtils.toString(he, "UTF-8");
				System.out.println("response string:"+ string);
				JSONObject oj = JSONObject.parseObject(string);
				String ss = oj.getString("data");
				System.out.println(decrypt9Game(ss));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static  void sendGiftTest(){
		String url = "http://119.29.162.42:10006/FSGameRounter/9game/sendgift";
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("id", 1330395827);
		JSONObject jsonData = new JSONObject();
		jsonData.put("accountId", "74fa29958e725ab3d2ba0d2e6bfedde8");
		jsonData.put("gameId", 667981);
		jsonData.put("platform", 2);
		jsonData.put("kaId", "10001");
		jsonData.put("serverId", "2");
		jsonData.put("roleId", "100100000181");
		jsonData.put("getDate", "2016-12-14");
		String encryptData = encrypt9Game(jsonData.toJSONString());
		Map<String, String> param = new HashMap<String, String>();
		param.put("params", encryptData);
		
		jsonObj.put("data", param);
		
		
		JSONObject jsonClient = new JSONObject();
		jsonClient.put("caller", "ka.9game");
		jsonClient.put("ex", "5987412");
		jsonObj.put("client", jsonClient);
		jsonObj.put("encrypt", "md5");
		String signStr = get9GameSign(jsonClient.getString("caller"), encryptData);
		jsonObj.put("sign", signStr);
		
		
		try {
			HttpPost post = new  HttpPost(url);
			HttpClientBuilder postClient = HttpClientBuilder.create();
			String jsonString = jsonObj.toJSONString();
			System.out.println(jsonString);
			StringEntity entity = new StringEntity(jsonString, "utf-8");
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			post.setEntity(entity);
			CloseableHttpClient httpClient = postClient.build();
			CloseableHttpResponse response = httpClient.execute(post);
			if(response.getStatusLine().getStatusCode() == 200){
				HttpEntity he = response.getEntity();
				String string = EntityUtils.toString(he, "UTF-8");
				System.out.println("response string:"+ string);
				JSONObject oj = JSONObject.parseObject(string);
				String ss = oj.getString("data");
				if(ss != null)
				System.out.println(decrypt9Game(ss));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
