package com.rw.handler.group;

import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.handler.group.msg.GroupBaseMsgReceiver;
import com.rwproto.GroupBaseMgrProto.CreateGroupReqMsg;
import com.rwproto.GroupBaseMgrProto.GroupBaseMgrCommonReqMsg;
import com.rwproto.GroupBaseMgrProto.GroupSettingReqMsg;
import com.rwproto.GroupBaseMgrProto.ModifyAnnouncementReqMsg;
import com.rwproto.GroupBaseMgrProto.ModifyGroupNameReqMsg;
import com.rwproto.GroupCommonProto.GroupValidateType;
import com.rwproto.GroupCommonProto.RequestType;
import com.rwproto.MsgDef.Command;

/*
 * @author HC
 * @date 2016年3月15日 下午5:17:42
 * @Description 帮派基础数据处理
 */
public class GroupBaseHandler {
	private static GroupBaseHandler handler = new GroupBaseHandler();

	public static GroupBaseHandler getHandler() {
		return handler;
	}

	private static final Command command = Command.MSG_GROUP;//
	private static final String functionName = "帮派基础管理";

	private GroupBaseHandler() {
	}

	/**
	 * 创建帮派
	 * 
	 * @param client
	 * @param groupName
	 * @return
	 */
	public boolean createGorup(Client client, String groupName) {
		GroupBaseMgrCommonReqMsg.Builder commonReq = GroupBaseMgrCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.CREATE_GROUP_TYPE);

		CreateGroupReqMsg.Builder req = CreateGroupReqMsg.newBuilder();
		req.setGroupName(groupName);
		req.setIcon("Guild/icon_Guild_01");

		commonReq.setCreateGroupReq(req);

		return client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), getMsgReciver("创建帮派"));
	}

	/**
	 * 修改帮派名字
	 * 
	 * @param client
	 * @param newName
	 * @return
	 */
	public boolean modifyGroupName(Client client, String newName) {
		GroupBaseMgrCommonReqMsg.Builder commonReq = GroupBaseMgrCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.MODIFY_GROUP_NAME_TYPE);

		ModifyGroupNameReqMsg.Builder req = ModifyGroupNameReqMsg.newBuilder();
		req.setGroupName(newName);

		commonReq.setModifyGroupNameReq(req);

		return client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), getMsgReciver("修改帮派名字"));
	}

	/**
	 * 修改帮派公告
	 * 
	 * @param client
	 * @param announcement
	 * @return
	 */
	public boolean modifyGroupAnnouncement(Client client, String announcement) {
		GroupBaseMgrCommonReqMsg.Builder commonReq = GroupBaseMgrCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.MODIFY_ANNOUNCEMENT_TYPE);

		ModifyAnnouncementReqMsg.Builder req = ModifyAnnouncementReqMsg.newBuilder();
		req.setAnnouncement(announcement);

		commonReq.setModifyAnnouncementReq(req);

		return client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), getMsgReciver("修改帮派公告"));
	}

	/**
	 * 帮派设置
	 */
	public boolean groupSetting(Client client, String newIcon, String declaration, GroupValidateType validate, int applyLevel) {
		GroupBaseMgrCommonReqMsg.Builder commonReq = GroupBaseMgrCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.GROUP_SETTING_TYPE);

		GroupSettingReqMsg.Builder req = GroupSettingReqMsg.newBuilder();
		req.setGroupIcon(newIcon);
		req.setValidateType(validate);
		req.setDeclaration(declaration);
		req.setApplyLevel(applyLevel);

		commonReq.setGroupSettingReq(req);

		return client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), getMsgReciver("修改帮派设置"));
	}

	/**
	 * 获取MsgReciver
	 * 
	 * @param protoType
	 * @return
	 */
	private MsgReciver getMsgReciver(String protoType) {
		return new GroupBaseMsgReceiver(command, functionName, protoType);
	}
}