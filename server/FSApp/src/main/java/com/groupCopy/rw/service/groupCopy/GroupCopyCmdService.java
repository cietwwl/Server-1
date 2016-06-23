package com.groupCopy.rw.service.groupCopy;

import com.google.protobuf.ByteString;
import com.groupCopy.bm.groupCopy.GroupCopyDataVersionMgr;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GroupCopyCmdProto.GroupCopyCmdReqMsg;
import com.rwproto.GroupCopyCmdProto.GroupCopyReqType;
import com.rwproto.RequestProtos.Request;

/**
 * 帮派副本消息分发器
 * @author Alex
 * 2016年6月13日 上午9:59:25
 */
public class GroupCopyCmdService implements FsService{

	@Override
	public ByteString doTask(Request request, Player player) {
		GroupCopyCmdHandler cmdHandler = GroupCopyCmdHandler.getInstance();
		ByteString bStr= null;
		try {
			GroupCopyCmdReqMsg reqMsg = GroupCopyCmdReqMsg.parseFrom(request.getBody().getSerializedContent());
			GroupCopyReqType reqType = reqMsg.getReqType();
			switch (reqType) {
			case GET_INFO:
				bStr = cmdHandler.getGroupCopyInfo(player, reqMsg);
				break;
			case GET_DROP_APPLY_INFO:
				bStr = cmdHandler.getDropApplyInfo(player,reqMsg);
				break;
			default:
				GameLog.error(LogModule.GroupCopy, "GroupCopyCmdService[doTask]", "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
			
		} catch (Exception e) {
			GameLog.error(LogModule.GroupCopy, "GroupCopyCmdService[doTask]", "出现了Exception异常", e);
		}
		
		return bStr;
	}

}
