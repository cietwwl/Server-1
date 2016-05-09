package com.playerdata.activity.rankType.service;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityRankTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityRankTypeProto.RequestType;
import com.rwproto.RequestProtos.Request;


public class ActivityRankTypeService implements FsService {


	@Override
	public ByteString doTask(Request request, Player player) {
		
		ActivityRankTypeHandler handler = ActivityRankTypeHandler.getInstance();
		
		ByteString byteString = null;
		try {
			ActivityCommonReqMsg commonReq = ActivityCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
			
			RequestType reqType = commonReq.getReqType();
			switch (reqType) {	
			case Get_Rank_Info:// 获取奖励
				byteString = handler.getRankInfo(player, commonReq);
				break;
			
			default:
				GameLog.error(LogModule.ComActivityRank, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
		} catch (Exception e) {
			GameLog.error(LogModule.ComActivityRank, player.getUserId(), "出现了Exception异常", e);
		}
		return byteString;
		
	}
}