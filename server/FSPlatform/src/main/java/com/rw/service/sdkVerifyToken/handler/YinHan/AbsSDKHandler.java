package com.rw.service.sdkVerifyToken.handler.YinHan;

import java.io.InputStream;

import com.log.PlatformLog;
import com.rw.fsutil.json.JSONObject;
import com.rw.fsutil.util.HttpUtils;
import com.rw.fsutil.util.MD5;
import com.rw.service.sdkVerifyToken.SDKVerifyResult;
import com.rw.service.sdkVerifyToken.handler.ISDKHandler;
import com.rwproto.SDKVerifyProtos.SDKVerifyRequest;

public class AbsSDKHandler implements ISDKHandler{

	protected String URL = "http://nsdk.01234.com.cn:4003/verifyToken";
	protected String BackupURL = "http://nsdk.01234.com.cn:4003/verifyToken";
	protected String gameKey = "6489CD1B7E9AE5BD8311435";
	protected String gameId;
	protected String channel;
	protected String userId;
	protected String sid;
	protected String ext;
	protected String version;
	protected String sign;
	
	
	SDKVerifyResult sdkVerifyResult = new SDKVerifyResult();
	

	public void init(SDKVerifyRequest request) {
		// TODO Auto-generated method stub
		gameId = request.getGameId();
		channel = request.getChannel();
		userId = request.getUserId();
		sid = request.getSid();
		ext = request.getExt();
		version = request.getVersion();
		sign = MD5.getMD5String(gameId + "|" + channel + "|" + userId + "|"
				+ sid + "|" + version + "|" + gameKey);
	}

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
	
	protected String parseToJson() {
		JSONObject json = new JSONObject();
		try {

			json.put("gameId", gameId);
			json.put("channel", channel);
			json.put("userId", userId);
			json.put("sid", sid);
			json.put("ext", ext);
			json.put("version", version);
			json.put("sign", sign);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return json.toString();
	}
	
	protected void decodeJson(String value) {
		PlatformLog.info("YinHanSDKHandler", "YinHanSDKHandler[decodeJson]","json:" + value);
		if (value == null || value.length() == 0) {
			sdkVerifyResult.setBlnSuccess(false);
			sdkVerifyResult.setMsg("exception!");
			return;
		}
		try {
			JSONObject json = new JSONObject(value);
			String status = json.getString("status");
			String msg = json.getString("msg");
			
			EYinHanVerifyResult result = EYinHanVerifyResult.getEYinHanVerifyResult(status);
			boolean blnSuccess = false;
			if(result == EYinHanVerifyResult.YHYZ_000){
				
				String userId = json.getString("userId");
				blnSuccess = true;
				String[] split = channel.split("@");
				String channelId = split[0];
				String uid = channelId + "#" + userId;
				sdkVerifyResult.setId_uid(uid);
				
				
			}
			
			sdkVerifyResult.setBlnSuccess(blnSuccess);
			sdkVerifyResult.setMsg(msg);
			
			
		} catch (Exception ex) {
			sdkVerifyResult.setBlnSuccess(false);
			sdkVerifyResult.setMsg("SDK验证失败!");
			ex.printStackTrace();
		}
	}
}
