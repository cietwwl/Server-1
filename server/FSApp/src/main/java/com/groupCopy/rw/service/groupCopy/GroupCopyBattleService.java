package com.groupCopy.rw.service.groupCopy;

import com.google.protobuf.ByteString;
import com.groupCopy.bm.groupCopy.GroupCopyDataVersionMgr;
import com.rwproto.GroupCopyBattleProto.GroupCopyBattleComReqMsg;
import com.rwproto.GroupCopyBattleProto.RequestType;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;

/*
 * @author HC
 * @date 2016年3月1日 下午3:07:01
 * @Description 
 */
public class GroupCopyBattleService implements FsService {

	@Override
	public ByteString doTask(Request request, Player player) {
		GroupCopyBattleHandler handler = GroupCopyBattleHandler.getInstance();
		ByteString byteString = null;
		try {
			GroupCopyBattleComReqMsg commonReq = GroupCopyBattleComReqMsg.parseFrom(request.getBody().getSerializedContent());
			RequestType reqType = commonReq.getReqType();
			switch (reqType) {
			// ==============================帮派管理处理===========================
			case FIGHT_BEGIN:
				byteString = handler.beginFight(player, commonReq);
				break;
			case FIGHT_END:
				byteString = handler.endFight(player, commonReq);
				break;
			default:
				GameLog.error(LogModule.COPY, "GroupCopyBattleService[doTask]", "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
			GroupCopyDataVersionMgr.synAllDataByVersion(player, commonReq.getVersion());			
			
		} catch (Exception e) {
			GameLog.error(LogModule.COPY, "GroupCopyBattleService[doTask]", "出现了Exception异常", e);
		}
		return byteString;
	}
	


}