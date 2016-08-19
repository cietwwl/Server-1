package com.playerdata.saveteaminfo;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;



/**
 * 阵容信息的存储
 * @author 
 *
 */
public class SaveTeaminfoToServerService implements FsService {

	private SaveTeaminfoToServerHandler mHandler = SaveTeaminfoToServerHandler.getInstance();

	@SuppressWarnings("finally")
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			
//			MagicSecretReqMsg msgMSRequest = MagicSecretReqMsg.parseFrom(request.getBody().getSerializedContent());
//			msRequestType msType = msgMSRequest.getReqType();
			switch (msType) {
			case GET_MS_RANK:
				result = mHandler.getMSRankData(player, msgMSRequest);
				break;
			
			default:
				GameLog.error("阵容存储", player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), "出现了Exception异常", e);
		} catch (Exception e) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), "出现了Exception异常", e);
		} finally {
			return result;
		}
	}
}