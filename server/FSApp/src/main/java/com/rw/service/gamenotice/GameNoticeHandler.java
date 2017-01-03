package com.rw.service.gamenotice;

import java.util.List;

import com.bm.notice.Notice;
import com.bm.notice.NoticeMgr;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwproto.NoticeProtos.NoticeRequest;
import com.rwproto.NoticeProtos.NoticeResponse;
import com.rwproto.NoticeProtos.tagNoticeInfo;

/**
 * 通告处理
 * 
 * @author lida
 *
 */
public class GameNoticeHandler {
	private static GameNoticeHandler instance = new GameNoticeHandler();

	public GameNoticeHandler() {
	}

	public static GameNoticeHandler getInstance() {
		return instance;
	}

	public ByteString requestGameNotice(NoticeRequest request, Player player) {
		NoticeResponse.Builder response = NoticeResponse.newBuilder();

		List<Notice> noticeList = NoticeMgr.getInstance().getNoticeList();
		int level = player.getLevel();
		if (noticeList.size() > 0) {
			long currentTimeMillis = System.currentTimeMillis();
			for (Notice notice : noticeList) {
				if(notice.getPushLevel() > level){
					continue;
				}
				
				if (notice.getStartTime() <= currentTimeMillis && notice.getEndTime() >= currentTimeMillis) {
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
