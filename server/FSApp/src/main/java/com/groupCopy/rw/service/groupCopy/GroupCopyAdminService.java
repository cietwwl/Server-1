package com.groupCopy.rw.service.groupCopy;

import com.google.protobuf.ByteString;
import com.groupCopy.bm.groupCopy.GroupCopyDataVersionMgr;
import com.rwproto.GroupCopyAdminProto.GroupCopyAdminComReqMsg;
import com.rwproto.GroupCopyAdminProto.RequestType;
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
public class GroupCopyAdminService implements FsService {

	@Override
	public ByteString doTask(Request request, Player player) {
		GroupCopyAdminHandler handler = GroupCopyAdminHandler.getInstance();
		ByteString byteString = null;
		try {
			GroupCopyAdminComReqMsg commonReq = GroupCopyAdminComReqMsg.parseFrom(request.getBody().getSerializedContent());
			RequestType reqType = commonReq.getReqType();
			switch (reqType) {
			// ==============================帮派管理处理===========================
			case OPEN_COPY:// 开启地图
				byteString = handler.open(player, commonReq);
				break;
			case RESET_COPY:// 重置帮派地图
				byteString = handler.reset(player, commonReq);
				break;
			case GET_APPLY_REWARD_INFO://查看可分配奖励情况
				byteString = handler.getAllRewardApplyInfo(player);
				break;
			case GET_CHATER_DAMAGE://查看成员章节伤害
				byteString = handler.getAllMemberChaterDamage(player, commonReq);
				break;
			case CHOSE_DIST_ROLE://选择分配成员
				byteString = handler.choseDistRole(player, commonReq);
				break;
			default:
				
				GameLog.error(LogModule.COPY, "GroupCopyAdminService[doTask]", "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
//			GroupCopyDataVersionMgr.synByVersion(player, commonReq.getVersion());			
			
		} catch (Exception e) {
			GameLog.error(LogModule.COPY, "GroupCopyAdminService[doTask]", "出现了Exception异常", e);
		} 
		return byteString;
	}
	


}