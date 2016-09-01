package com.rw.service.gamenotice;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
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
public class GameNoticeService implements FsService<NoticeRequest, ENoticeType>{

	@Override
	public ByteString doTask(NoticeRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString byteString = null;
		try {
			 ENoticeType type = request.getType();
			switch (type) {
			case GameNotice:
				byteString = GameNoticeHandler.getInstance().requestGameNotice(request, player);
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

	@Override
	public NoticeRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		NoticeRequest noticeRequest = NoticeRequest.parseFrom(request.getBody().getSerializedContent());
		return noticeRequest;
	}

	@Override
	public ENoticeType getMsgType(NoticeRequest request) {
		// TODO Auto-generated method stub
		return request.getType();
	}
	
}
