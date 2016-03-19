package com.rw.service.group;

import com.google.protobuf.ByteString;
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
public class GroupBaseManagerService implements FsService {

	@SuppressWarnings("finally")
	@Override
	public ByteString doTask(Request request, Player player) {
		GroupBaseManagerHandler baseHandler = GroupBaseManagerHandler.getHandler();
		ByteString byteString = null;
		try {
			GroupBaseMgrCommonReqMsg commonReq = GroupBaseMgrCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
			RequestType reqType = commonReq.getReqType();
			switch (reqType) {
			// ==============================帮派管理处理===========================
			case CREATE_GROUP_TYPE:// 创建帮派
				byteString = baseHandler.createGroupHandler(player, commonReq.getCreateGroupReq());
				break;
			case MODIFY_ANNOUNCEMENT_TYPE:// 修改帮派公告
				byteString = baseHandler.modifyGroupAnnouncement(player, commonReq.getModifyAnnouncementReq());
				break;
			case MODIFY_GROUP_NAME_TYPE:// 修改帮派名字
				byteString = baseHandler.modifyGroupNameHandler(player, commonReq.getModifyGroupNameReq());
				break;
			case GROUP_SETTING_TYPE:// 帮派设置
				byteString = baseHandler.groupSettingHandler(player, commonReq.getGroupSettingReq());
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
			GroupDataVersionMgr.synByVersion(player, commonReq.getVersion());
		} catch (Exception e) {
			GameLog.error("帮派模块", "分发协议Service", "出现了Exception异常", e);
		} finally {
			return byteString;
		}
	}

}