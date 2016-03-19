package com.rw.service.sdkVerifyToken;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.account.Account;
import com.rw.service.RequestService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.SDKVerifyProtos.SDKVerifyRequest;

public class SDKVerifyService implements RequestService{

	public ByteString doTask(Request request, Account account) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			SDKVerifyRequest sdkVerifyRequest = SDKVerifyRequest.parseFrom(request.getBody().getSerializedContent());
			result = SDKVerifyHandler.getInstance().processSDKVerifyHandler(sdkVerifyRequest);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return result;
	}

}
