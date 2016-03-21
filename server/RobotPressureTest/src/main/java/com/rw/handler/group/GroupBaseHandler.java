package com.rw.handler.group;

import java.util.Random;

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
	private static final Random r = new Random();
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
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

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
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

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
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		ModifyAnnouncementReqMsg.Builder req = ModifyAnnouncementReqMsg.newBuilder();
		req.setAnnouncement(announcement);

		commonReq.setModifyAnnouncementReq(req);

		return client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), getMsgReciver("修改帮派公告"));
	}

	private static final GroupValidateType[] validate = new GroupValidateType[] { GroupValidateType.FIRST_VALIDATE, GroupValidateType.JOIN_REFUSED, GroupValidateType.NON_VALIDATE };
	private static final int[] applyLevel = new int[] { 10, 15, 20, 25, 30, 35, 40 };

	/**
	 * 帮派设置
	 * 
	 * @param client
	 * @param newIcon
	 * @param declaration
	 * @param validate
	 * @param applyLevel
	 * @return
	 */
	public boolean groupSetting(Client client) {
		GroupBaseMgrCommonReqMsg.Builder commonReq = GroupBaseMgrCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.GROUP_SETTING_TYPE);
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		GroupSettingReqMsg.Builder req = GroupSettingReqMsg.newBuilder();
		req.setGroupIcon("Guild/icon_Guild_01");
		req.setValidateType(validate[r.nextInt(validate.length)]);
		req.setDeclaration("压测哈哈哈哈啊的宣言噢噢噢噢");
		req.setApplyLevel(applyLevel[r.nextInt(applyLevel.length)]);

		commonReq.setGroupSettingReq(req);

		return client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), getMsgReciver("修改帮派设置"));
	}

	/**
	 * 解散帮派
	 * 
	 * @param client
	 * @return
	 */
	public boolean dismissGroup(Client client) {
		GroupBaseMgrCommonReqMsg.Builder commonReq = GroupBaseMgrCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.DISMISS_THE_GROUP_TYPE);
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		return client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), getMsgReciver("解散帮派"));
	}

	/**
	 * 取消解散帮派
	 * 
	 * @param client
	 * @return
	 */
	public boolean cancelDismissGroup(Client client) {
		GroupBaseMgrCommonReqMsg.Builder commonReq = GroupBaseMgrCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.CANCEL_DISMISS_THE_GROUP_TYPE);
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		return client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), getMsgReciver("取消解散帮派"));
	}

	/**
	 * 获取帮派日志
	 * 
	 * @param client
	 * @return
	 */
	public boolean getGroupLog(Client client) {
		GroupBaseMgrCommonReqMsg.Builder commonReq = GroupBaseMgrCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.THE_LOG_OF_GROUP_TYPE);
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		return client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), getMsgReciver("获取帮派日志"));
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