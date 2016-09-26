package com.playerdata.saveteaminfo;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.BattleCommon.BattleCommonReqMsg;
import com.rwproto.BattleCommon.RequestType;
import com.rwproto.RequestProtos.Request;



/**
 * 阵容信息的存储
 * @author 
 *
 */
public class SaveTeaminfoToServerService implements FsService<BattleCommonReqMsg, RequestType> {

	private SaveTeaminfoToServerHandler mHandler = SaveTeaminfoToServerHandler.getInstance();

	@SuppressWarnings("finally")
	@Override
	public ByteString doTask(BattleCommonReqMsg request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			RequestType msType = request.getReqType();

			switch (msType) {
			case Updata:
				result = mHandler.putTeamInfoToServer(player, request);
				break;
			
			default:
				GameLog.error("阵容存储", player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
		} catch (Exception e) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), "出现了Exception异常", e);
		} finally {
			return result;
		}
	}

	@Override
	public BattleCommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		BattleCommonReqMsg msgMSRequest = BattleCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
		return msgMSRequest;
	}

	@Override
	public RequestType getMsgType(BattleCommonReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}