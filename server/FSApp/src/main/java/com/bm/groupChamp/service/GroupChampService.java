package com.bm.groupChamp.service;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.fixEquip.exp.FixExpEquipHandler;
import com.playerdata.fixEquip.norm.FixNormEquipHandler;
import com.rw.service.FsService;
import com.rwproto.FixEquipProto.CommonReqMsg;
import com.rwproto.FixEquipProto.RequestType;
import com.rwproto.RequestProtos.Request;


public class GroupChampService implements FsService {


	@Override
	public ByteString doTask(Request request, Player player) {
		
		FixExpEquipHandler expEquipHandler = FixExpEquipHandler.getInstance();
		FixNormEquipHandler normEquipHandler = FixNormEquipHandler.getInstance();
		
		ByteString byteString = null;
		try {
			CommonReqMsg commonReq = CommonReqMsg.parseFrom(request.getBody().getSerializedContent());
			
			RequestType reqType = commonReq.getReqType();
			switch (reqType) {	
			case Norm_level_up:
				byteString = normEquipHandler.levelUp(player, commonReq);
				break;
			
			default:
				GameLog.error(LogModule.GroupChamp, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
		} catch (Exception e) {
			GameLog.error(LogModule.GroupChamp, player.getUserId(), "出现了Exception异常", e);
		}
		return byteString;
		
	}
}