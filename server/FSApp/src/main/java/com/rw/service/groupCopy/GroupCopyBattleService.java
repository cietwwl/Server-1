package com.rw.service.groupCopy;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GroupCopyBattleProto.GroupCopyBattleComReqMsg;
import com.rwproto.GroupCopyBattleProto.RequestType;
import com.rwproto.RequestProtos.Request;

/*
 * @author HC
 * @date 2016年3月1日 下午3:07:01
 * @Description 
 */
public class GroupCopyBattleService implements FsService<GroupCopyBattleComReqMsg, RequestType> {

	@Override
	public ByteString doTask(GroupCopyBattleComReqMsg request, Player player) {
		// TODO Auto-generated method stub
		GroupCopyBattleHandler handler = GroupCopyBattleHandler.getInstance();
		ByteString byteString = null;
		try {
			RequestType reqType = request.getReqType();
			switch (reqType) {
			// ==============================帮派管理处理===========================
			case FIGHT_BEGIN:
				byteString = handler.beginFight(player, request);
				break;
			case FIGHT_END:
				byteString = handler.endFight(player, request);
				break;
			case ENTER_APPLY:
				byteString = handler.applyEnterCopy(player,request);
				break;
			default:
				GameLog.error(LogModule.COPY, "GroupCopyBattleService[doTask]", "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
//			GroupCopyDataVersionMgr.synAllDataByVersion(player, commonReq.getVersion());			
			
		} catch (Exception e) {
			GameLog.error(LogModule.COPY, "GroupCopyBattleService[doTask]", "出现了Exception异常", e);
		}
		return byteString;
	}

	@Override
	public GroupCopyBattleComReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		GroupCopyBattleComReqMsg commonReq = GroupCopyBattleComReqMsg.parseFrom(request.getBody().getSerializedContent());
		return commonReq;
	}

	@Override
	public RequestType getMsgType(GroupCopyBattleComReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
	


}