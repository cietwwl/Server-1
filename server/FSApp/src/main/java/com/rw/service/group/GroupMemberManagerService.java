package com.rw.service.group;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.group.GroupDataVersionMgr;
import com.rw.service.FsService;
import com.rwproto.GroupCommonProto.RequestType;
import com.rwproto.GroupMemberMgrProto.GroupMemberMgrCommonReqMsg;
import com.rwproto.RequestProtos.Request;

/*
 * @author HC
 * @date 2016年3月1日 下午3:11:43
 * @Description 
 */
public class GroupMemberManagerService implements FsService {

	@SuppressWarnings("finally")
	@Override
	public ByteString doTask(Request request, Player player) {
		GroupMemberManagerHandler memberHandler = GroupMemberManagerHandler.getHandler();
		ByteString byteString = null;
		try {
			GroupMemberMgrCommonReqMsg commonReq = GroupMemberMgrCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
			RequestType reqType = commonReq.getReqType();
			switch (reqType) {
			// ==============================帮派成员管理处理===========================
			case GET_APPLY_MEMBER_LIST_TYPE:// 获取申请列表
				byteString = memberHandler.getApplyMemberListHandler(player);
				break;
			case GROUP_MEMBER_RECEIVE_TYPE:// 接受帮派成员
				byteString = memberHandler.groupMemberReceiveHandler(player, commonReq.getGroupMemberReceiveReq());
				break;
			case NOMINATE_POST_TYPE:// 成员任命
				byteString = memberHandler.groupNominatePostHandler(player, commonReq.getGroupNominatePostReq());
				break;
			case CANCEL_NOMINATE_TYPE:// 任命取消
				byteString = memberHandler.cancelNominateHandler(player, commonReq.getGroupCancelNominatePostReq());
				break;
			case GROUP_EMAIL_FOR_ALL_TYPE:// 全员邮件
				byteString = memberHandler.groupEmailForAllHandler(player, commonReq.getGroupEmailForAllReq());
				break;
			case KICK_MEMBER_TYPE:// 踢出帮派成员
				byteString = memberHandler.kickMemberHandler(player, commonReq.getKickMemberReq());
				break;
			default:
				GameLog.error("帮派成员管理模块", "分发协议Service", "接收到了一个Unknown的消息，无法处理");
				break;
			}
			GroupDataVersionMgr.synByVersion(player, commonReq.getVersion());
		} catch (Exception e) {
			GameLog.error("帮派模块", "分发协议Service", "出现了Exception异常", e);
		} finally {
			return byteString;
		}
	}
}