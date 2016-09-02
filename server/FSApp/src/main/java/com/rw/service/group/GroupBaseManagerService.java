package com.rw.service.group;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.group.GroupDataVersionMgr;
import com.rw.service.FsService;
import com.rwproto.GroupBaseMgrProto.GroupBaseMgrCommonReqMsg;
import com.rwproto.GroupCommonProto.RequestType;
import com.rwproto.RequestProtos.Request;

/*
 * @author HC
 * @date 2016年3月1日 下午3:07:01
 * @Description 
 */
public class GroupBaseManagerService implements FsService<GroupBaseMgrCommonReqMsg, RequestType> {

	@SuppressWarnings("finally")
	@Override
	public ByteString doTask(GroupBaseMgrCommonReqMsg request, Player player) {
		// TODO Auto-generated method stub
		GroupBaseManagerHandler baseHandler = GroupBaseManagerHandler.getHandler();
		ByteString byteString = null;
		try {
			RequestType reqType = request.getReqType();
			switch (reqType) {
			// ==============================帮派管理处理===========================
			case CREATE_GROUP_TYPE:// 创建帮派
				byteString = baseHandler.createGroupHandler(player, request.getCreateGroupReq());
				break;
			case MODIFY_ANNOUNCEMENT_TYPE:// 修改帮派公告
				byteString = baseHandler.modifyGroupAnnouncement(player, request.getModifyAnnouncementReq());
				break;
			case MODIFY_GROUP_NAME_TYPE:// 修改帮派名字
				byteString = baseHandler.modifyGroupNameHandler(player, request.getModifyGroupNameReq());
				break;
			case GROUP_SETTING_TYPE:// 帮派设置
				byteString = baseHandler.groupSettingHandler(player, request.getGroupSettingReq());
				break;
			case DISMISS_THE_GROUP_TYPE:// 解散帮派
				byteString = baseHandler.dismissTheGroupHandler(player);
				break;
			case CANCEL_DISMISS_THE_GROUP_TYPE:// 取消解散帮派
				byteString = baseHandler.cancelDismissTheGroupHandler(player);
				break;
			case THE_LOG_OF_GROUP_TYPE:// 帮派日志
				byteString = baseHandler.getGroupLogHandler(player);
				break;
			default:
				GameLog.error("帮派模块", "分发协议Service", "接收到了一个Unknown的消息，无法处理");
				break;
			}
			GroupDataVersionMgr.synByVersion(player, request.getVersion());
		} catch (Exception e) {
			GameLog.error("帮派模块", "分发协议Service", "出现了Exception异常", e);
		} finally {
			return byteString;
		}
	}

	@Override
	public GroupBaseMgrCommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		GroupBaseMgrCommonReqMsg commonReq = GroupBaseMgrCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
		return commonReq;
	}

	@Override
	public RequestType getMsgType(GroupBaseMgrCommonReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}

}