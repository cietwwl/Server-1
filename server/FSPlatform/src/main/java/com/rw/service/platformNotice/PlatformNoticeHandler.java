package com.rw.service.platformNotice;

import com.google.protobuf.ByteString;
import com.rw.account.Account;
import com.rw.platform.PlatformFactory;
import com.rwbase.dao.platformNotice.TablePlatformNotice;
import com.rwproto.NoticeProtos.ENoticeType;
import com.rwproto.NoticeProtos.NoticeRequest;
import com.rwproto.NoticeProtos.NoticeResponse;
import com.rwproto.NoticeProtos.tagNoticeInfo;

public class PlatformNoticeHandler {
	private static PlatformNoticeHandler instance;
	
	private PlatformNoticeHandler(){}
	
	public static PlatformNoticeHandler getInstance(){
		if(instance == null){
			instance = new PlatformNoticeHandler();
		}
		return instance;
	}
	
	public ByteString requestNotice(NoticeRequest request, Account account){
		ENoticeType type = request.getType();
		NoticeResponse.Builder response = NoticeResponse.newBuilder();
		response.setType(type);
		TablePlatformNotice platformNotice = PlatformFactory.getPlatformService().getPlatformNotice();
		
		if (platformNotice != null) {
			long currentTimeMillis = System.currentTimeMillis();
			if (currentTimeMillis >= (platformNotice.getStartTime()*1000) && currentTimeMillis <= (platformNotice.getEndTime()*1000)) {
				tagNoticeInfo.Builder noticeInfo = tagNoticeInfo.newBuilder();
				noticeInfo.setTitle(platformNotice.getTitle());
				noticeInfo.setContent(platformNotice.getContent());
				response.addNotice(noticeInfo);
			}
		}
		return response.build().toByteString();
	}
}
