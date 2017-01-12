package com.rw.service.sdkVerifyToken.handler.YinHan;

import java.io.InputStream;

import com.log.PlatformLog;
import com.rw.fsutil.util.HttpUtils;
import com.rw.service.sdkVerifyToken.SDKVerifyResult;


public class DefaultSDKHandler extends AbsSDKHandler{
	
	protected String URL = "http://nsdk.01234.com.cn:4003/verifyToken";
	protected String BackupURL = "http://nsdk.01234.com.cn:4003/verifyToken";
	
	public SDKVerifyResult verifySDK() {
		// TODO Auto-generated method stub
		//组装json字符串
		String para = parseToJson();
		PlatformLog.info("YinHanSDKHandler", "YinHanSDKHandler[verifySDK]","............send json:" + para);
		
		System.out.println("------------URL" + URL);
		InputStream streamResult = HttpUtils.sentHttpPostMsg(URL, para);
		if(streamResult == null){
			System.out.println("------------BackupURL" + BackupURL);
			streamResult = HttpUtils.sentHttpPostMsg(BackupURL, para);
		}
		String strResult = HttpUtils.getInputStreamToString(streamResult);
		
		//解析json
		decodeJson(strResult);
		
		
		return sdkVerifyResult;
	}
}
