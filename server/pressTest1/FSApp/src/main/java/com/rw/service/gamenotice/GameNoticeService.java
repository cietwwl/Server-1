package com.rw.service.gamenotice;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.NoticeProtos.ENoticeType;
import com.rwproto.NoticeProtos.NoticeRequest;
import com.rwproto.RequestProtos.Request;

/**
 * 通告处理
 * @author lida
 *
 */
@SuppressWarnings("finally")
public class GameNoticeService implements FsService{

	
	@Override
	public ByteString doTask(Request request, Player player) {
		ByteString byteString = null;
		try {
			NoticeRequest noticeRequest = NoticeRequest.parseFrom(request.getBody().getSerializedContent());
			 ENoticeType type = noticeRequest.getType();
			switch (type) {
			case GameNotice:
				byteString = GameNoticeHandler.getInstance().requestGameNotice(noticeRequest, player);
				break;
			default:
				GameLog.error("通告模块", "分发协议Service", "接收到了一个Unknown的消息，无法处理");
				break;
			}
		} catch (Exception e) {
			GameLog.error("通告模块", "分发协议Service", "出现了Exception异常", e);
		} finally {
			return byteString;
		}
	}
	
}
