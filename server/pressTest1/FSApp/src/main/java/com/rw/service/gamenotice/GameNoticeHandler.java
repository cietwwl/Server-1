package com.rw.service.gamenotice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rw.manager.GameManager;
import com.rwbase.dao.gameNotice.TableGameNotice;
import com.rwbase.dao.gameNotice.pojo.GameNoticeDataHolder;
import com.rwproto.NoticeProtos.ENoticeType;
import com.rwproto.NoticeProtos.NoticeRequest;
import com.rwproto.NoticeProtos.NoticeResponse;
import com.rwproto.NoticeProtos.tagNoticeInfo;

/**
 * 通告处理 
 * @author lida
 *
 */
public class GameNoticeHandler {
	private static GameNoticeHandler instance;
	
	public GameNoticeHandler(){}
	
	public static GameNoticeHandler getInstance(){
		if(instance == null){
			instance = new GameNoticeHandler();
		}
		return instance;
	}
	
	public ByteString requestGameNotice(NoticeRequest request, Player player){
		ENoticeType type = request.getType();
		NoticeResponse.Builder response = NoticeResponse.newBuilder();
		response.setType(type);
		
		GameNoticeDataHolder gameNotice = GameManager.getGameNotice();
		
		HashMap<Integer, TableGameNotice> gameNotices = gameNotice.getGameNotices();
		if(gameNotices.size() > 0){
			long currentTimeMillis = System.currentTimeMillis();
			for (Iterator<Entry<Integer, TableGameNotice>> iterator = gameNotices.entrySet().iterator(); iterator.hasNext();) {
				Entry<Integer, TableGameNotice> entry = iterator.next();
				TableGameNotice notice = entry.getValue();
				if (notice.getStartTime() <= currentTimeMillis && notice.getEndTime() >= currentTimeMillis) {
					tagNoticeInfo.Builder noticeInfo = tagNoticeInfo.newBuilder();
					noticeInfo.setTitle(notice.getTitle());
					noticeInfo.setContent(notice.getContent());
					response.addNotice(noticeInfo);
				}
			}
		}
		return response.build().toByteString();
	}
	
}
