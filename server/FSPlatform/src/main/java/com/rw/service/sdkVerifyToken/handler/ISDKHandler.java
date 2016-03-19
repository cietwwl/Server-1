package com.rw.service.sdkVerifyToken.handler;

import com.rw.service.sdkVerifyToken.SDKVerifyResult;
import com.rwproto.SDKVerifyProtos.SDKVerifyRequest;

public interface ISDKHandler {
	
	public void init(SDKVerifyRequest request);
	
	public SDKVerifyResult verifySDK();
}
