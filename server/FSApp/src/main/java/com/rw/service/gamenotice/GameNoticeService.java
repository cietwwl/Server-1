package com.rw.service.gamenotice;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.ProtocolMessageEnum;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.NoticeProtos.NoticeRequest;
import com.rwproto.RequestProtos.Request;

/**
 * 通告处理
 * @author lida
 *
 */
@SuppressWarnings("finally")
public class GameNoticeService implements FsService<NoticeRequest, ProtocolMessageEnum>{

	@Override
	public ByteString doTask(NoticeRequest request, Player player) {
		ByteString byteString = null;
		try {
			byteString = GameNoticeHandler.getInstance().requestGameNotice(request, player);
		} catch (Exception e) {
			GameLog.error("通告模块", "分发协议Service", "出现了Exception异常", e);
		} finally {
			return byteString;
		}
	}

	@Override
	public NoticeRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		NoticeRequest noticeRequest = NoticeRequest.parseFrom(request.getBody().getSerializedContent());
		return noticeRequest;
	}

	@Override
	public ProtocolMessageEnum getMsgType(NoticeRequest request) {
		return null;
	}
	
}
