package com.rw.service.groupCopy;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GroupCopyAdminProto.GroupCopyAdminComReqMsg;
import com.rwproto.GroupCopyAdminProto.RequestType;
import com.rwproto.RequestProtos.Request;

/*
 * @author HC
 * @date 2016年3月1日 下午3:07:01
 * @Description 
 */
public class GroupCopyAdminService implements FsService<GroupCopyAdminComReqMsg, RequestType> {

	@Override
	public ByteString doTask(GroupCopyAdminComReqMsg request, Player player) {
		// TODO Auto-generated method stub
		GroupCopyAdminHandler handler = GroupCopyAdminHandler.getInstance();
		ByteString byteString = null;
		try {
			RequestType reqType = request.getReqType();
			switch (reqType) {
			// ==============================帮派管理处理===========================
			case OPEN_COPY:// 开启地图
				byteString = handler.open(player, request);
				break;
			case RESET_COPY:// 重置帮派地图
				byteString = handler.reset(player, request);
				break;
			case GET_APPLY_REWARD_INFO://查看可分配奖励情况
				byteString = handler.getAllRewardApplyInfo(player);
				break;
			case GET_CHATER_DAMAGE://查看成员章节伤害
				byteString = handler.getAllMemberChaterDamage(player, request);
				break;
			case CHOSE_DIST_ROLE://选择分配成员
				byteString = handler.choseDistRole(player, request);
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

	@Override
	public GroupCopyAdminComReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		GroupCopyAdminComReqMsg commonReq = GroupCopyAdminComReqMsg.parseFrom(request.getBody().getSerializedContent());
		return commonReq;
	}

	@Override
	public RequestType getMsgType(GroupCopyAdminComReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
	


}