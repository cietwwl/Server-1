package com.playerdata.randomname;

import com.google.protobuf.ByteString;
import com.rwproto.RandomNameServiceProtos.RandomNameRequest;
import com.rwproto.RandomNameServiceProtos.RandomNameResponse;
import com.rwproto.RandomNameServiceProtos.ResultType;

public class RandomNameHandler {

	private static RandomNameHandler instance = new RandomNameHandler();
	
	public static RandomNameHandler getInstance() {
		return instance;
	}
	
	public ByteString fetchRandomName(RandomNameRequest request) {
		RandomNameResponse.Builder respBuilder = RandomNameResponse.newBuilder();
		respBuilder.setReqType(request.getReqType());
		String accountId = request.getAccountId();
		String randomName = RandomNameMgr.getInstance().getRandomName(accountId, request.getIsFemale());
		if(randomName != null) {
			respBuilder.setResultType(ResultType.SUCCESS);			
			respBuilder.setName(randomName);
		} else {
			respBuilder.setResultType(ResultType.FAIL);
			respBuilder.setTips("没有更多的名字，请自行创建名字！");
		}
		return respBuilder.build().toByteString();
	}
}
