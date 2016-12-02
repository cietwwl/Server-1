package com.rw.service.sdkVerifyToken;

import java.util.HashMap;

import com.bm.login.AccoutBM;
import com.google.protobuf.ByteString;
import com.log.PlatformLog;
import com.rw.service.sdkVerifyToken.handler.ISDKHandler;
import com.rw.service.sdkVerifyToken.handler.YinHan.DefaultSDKHandler;
import com.rw.service.sdkVerifyToken.handler.YinHan.YinHanSDKHandler;
import com.rw.service.sdkVerifyToken.handler.YinHan.ZYinHanSDKHandler;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwproto.SDKVerifyProtos.SDKVerifyRequest;
import com.rwproto.SDKVerifyProtos.SDKVerifyResponse;
import com.rwproto.SDKVerifyProtos.eSDKVerifyResultType;


public class SDKVerifyHandler {
	private static SDKVerifyHandler instance;
	
	private final static HashMap<ESDKType, Class<?>> SDKMap = new HashMap<ESDKType, Class<?>>();
	
	static{
		SDKMap.put(ESDKType.DEFAULT_SDK, DefaultSDKHandler.class);
		SDKMap.put(ESDKType.YINHAN_SDK, YinHanSDKHandler.class);
		SDKMap.put(ESDKType.ZYINHAN_SDK, ZYinHanSDKHandler.class);
	}
	
	private SDKVerifyHandler(){};
	
	public static SDKVerifyHandler getInstance()
	{
		if(instance == null){
			instance = new SDKVerifyHandler();
		}
		return instance;
	}
	
	public ByteString processSDKVerifyHandler(SDKVerifyRequest sdkVerifyRequest){
		SDKVerifyResponse.Builder response = SDKVerifyResponse.newBuilder();
		try {
			ESDKType sdkType = ESDKType.getSDKType(sdkVerifyRequest.getSdkType());
			Class<?> cInstance = SDKMap.get(sdkType);
			System.out.println("------------------sdkType:" + sdkType);
			ISDKHandler handler = (ISDKHandler) cInstance.newInstance();
			handler.init(sdkVerifyRequest);
			SDKVerifyResult verifySDK = handler.verifySDK();
			
			if(verifySDK.isBlnSuccess()){
				//SDK登陆成功 判断是否有游戏账号
				TableAccount newAccount = AccoutBM.getInstance().getByOpenAccount(verifySDK.getId_uid());
				response.setResultType(eSDKVerifyResultType.SUCCESS);
				response.setMsg(verifySDK.getMsg());
				response.setUserId(verifySDK.getId_uid());
				response.setAccountId(newAccount == null ? "":newAccount.getAccountId());
				response.setPassword(newAccount == null ? "" : newAccount.getPassword());
			}else{
				response.setResultType(eSDKVerifyResultType.FAIL);
				response.setMsg(verifySDK.getMsg());
			}
			
		} catch (Exception ex) {
			response.setResultType(eSDKVerifyResultType.FAIL);
			response.setMsg("服务器繁忙，请稍候尝试。");
			PlatformLog.error("SDKVerifyHandler", "SDKVerifyHandler[processSDKVerifyHandler]", "", ex);
		}
		return response.build().toByteString();
	}
}
