package com.rw.service.platformNotice;

import com.google.protobuf.ByteString;
import com.rw.account.Account;
import com.rw.service.RequestService;
import com.rwproto.NoticeProtos.NoticeRequest;
import com.rwproto.RequestProtos.Request;

public class PlatformNoticeService  implements RequestService{

	@Override
	public ByteString doTask(Request request, Account account) {
		// TODO Auto-generated method stub

		ByteString result = null;
		try{
			NoticeRequest noticeReqest = NoticeRequest.parseFrom(request.getBody().getSerializedContent());
			result = PlatformNoticeHandler.getInstance().requestNotice(noticeReqest, account);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

}
