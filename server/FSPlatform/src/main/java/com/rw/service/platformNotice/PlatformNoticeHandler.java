package com.rw.service.platformNotice;

import java.util.List;

import com.bm.notice.Notice;
import com.bm.notice.NoticeMgr;
import com.google.protobuf.ByteString;
import com.rw.account.Account;
import com.rw.platform.PlatformFactory;
import com.rwbase.dao.platformNotice.TablePlatformNotice;
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
		NoticeResponse.Builder response = NoticeResponse.newBuilder();
		List<Notice> noticeList = NoticeMgr.getInstance().getNoticeList();
		if (noticeList.size() > 0) {
			
			long currentTimeMillis = System.currentTimeMillis();
			for (Notice notice : noticeList) {
				if (currentTimeMillis >= notice.getStartTime() && currentTimeMillis <= notice.getEndTime()) {
					tagNoticeInfo.Builder noticeInfo = tagNoticeInfo.newBuilder();
					noticeInfo.setNoticeId(notice.getId());
					noticeInfo.setSort(notice.getSort());
					noticeInfo.setTagTitle(notice.getTagTitle());
					noticeInfo.setTagIcon(notice.getTagIcon());
					noticeInfo.setTitle(notice.getTitle());
					int annoceType = notice.getAnnoceType();
					noticeInfo.setAnnonceType(annoceType);
					if (annoceType == NoticeMgr.AnnonceType_Word) {
						noticeInfo.setAnnounceTitle(notice.getAnnounceTitle());
						noticeInfo.setAnnounceContent(notice.getAnnounceContent());
					}
					if (annoceType == NoticeMgr.AnnonceType_Pic) {
						noticeInfo.setBg(notice.getBg());
					}
					if (notice.getIsShowButton() != 0) {
						noticeInfo.setIsShowButton(notice.getIsShowButton());
						noticeInfo.setButtonText(notice.getButtonText());
						noticeInfo.setGotoType(notice.getGotoType());
						noticeInfo.setGotoTypeId(notice.getGotoTypeId());

					}
					noticeInfo.setIsConfig(notice.isConfigNotice() ? 1 : 0);
					response.addNotice(noticeInfo);
				}
			}
		}
		return response.build().toByteString();
	}
}
