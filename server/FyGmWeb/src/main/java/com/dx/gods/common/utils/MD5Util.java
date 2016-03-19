package com.dx.gods.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.dx.gods.common.log.GMLogger;

public class MD5Util {
	
	/**
	 * 默认的密码字符串组合，用来将字节转换成16进制表示的字符，apache校验下载的文件的正确性用的就是默认的这个组合
	 */
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	
	protected static MessageDigest messagedigest = null;
	
	static{
		try{
			messagedigest = MessageDigest.getInstance("MD5");
			
		}catch(NoSuchAlgorithmException nsaex){
			GMLogger.error(nsaex.getMessage());
			nsaex.printStackTrace();
		}
	}
	
	/**
	 * 生成字符窜的md5校验值
	 * @param s
	 * @return
	 */
	public static String getMD5String(String s){
		return getMD5String(s.getBytes());
	}
	
	/**
	 * 判断字符串的md5校验码是否与一个已知的md5码相匹配
	 * @param password
	 * @param md5PwdStr
	 * @return
	 */
	public static boolean checkPassword(String password, String md5PwdStr){
		String s = getMD5String(password);
		return s.equals(md5PwdStr);
	}
	
	/**
	 * 生成文件的MD5值
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String getFileMD5String(File file)throws IOException{
		InputStream fis;
		fis = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int numRead = 0;
		while((numRead = fis.read(buffer))>0){
			messagedigest.update(buffer, 0 , numRead);
		}
		fis.close();
		return bufferToHex(messagedigest.digest());
	}
	
	public static String getMD5String (byte[] bytes){
		messagedigest.update(bytes);
		return bufferToHex(messagedigest.digest());
		
	}
	
	private static String bufferToHex(byte bytes[]){
		return bufferToHex(bytes, 0, bytes.length);
	}
	
	private static String bufferToHex(byte bytes[], int m, int n){
		StringBuffer stringBuffer = new StringBuffer(2*n);
		int k = m+n;
		for(int l= m ;l<k;l++){
			appendHexPair(bytes[l], stringBuffer);
		}
		return stringBuffer.toString();
	}
	
	private static void appendHexPair(byte bt, StringBuffer stringbuffer){
		char c0 = hexDigits[(bt&0xf0)>>4];  //取字节中高4位的数字转换, >>>为逻辑右移，将符号位一起右移，此处未发现两种符号有何不同
		char c1 = hexDigits[bt & 0xf];   //取字节中低4位的数字转换
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}
}
