package com.rw.service.group.helper;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupConst;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.playerdata.readonly.PlayerIF;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailCfg;
import com.rwbase.dao.email.EmailCfgDAO;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;

/*
 * @author HC
 * @date 2016年3月9日 下午3:07:35
 * @Description 帮派的Helper类
 */
public class GroupHelper {

	private static final String EMPTY_STRING = "";

	/**
	 * 获取帮派名字
	 * 
	 * @param userId
	 * @return
	 */
	public static String getGroupName(String userId) {
		PlayerIF player = PlayerMgr.getInstance().getReadOnlyPlayer(userId);
		if (player == null) {
			return EMPTY_STRING;
		}

		UserGroupAttributeDataIF userGroupAttributeData = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(userId);
		if (userGroupAttributeData == null) {
			return EMPTY_STRING;
		}

		String groupId = userGroupAttributeData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return EMPTY_STRING;
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			return EMPTY_STRING;
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			return EMPTY_STRING;
		}

		return groupData.getGroupName();
	}

	/**
	 * 获取个人的工会Id
	 * 
	 * @param userId
	 * @return
	 */
	public static String getUserGroupId(String userId) {
		PlayerIF player = PlayerMgr.getInstance().getReadOnlyPlayer(userId);
		if (player == null) {
			return EMPTY_STRING;
		}

		UserGroupAttributeDataMgr mgr = UserGroupAttributeDataMgr.getMgr();
		UserGroupAttributeDataIF data = mgr.getUserGroupAttributeData(userId);
		if (data == null) {
			return EMPTY_STRING;
		}

		return data.getGroupId();
	}

	/**
	 * 判断个人是否有帮派
	 * 
	 * @param userId
	 * @return
	 */
	public static boolean hasGroup(String userId) {
		PlayerIF player = PlayerMgr.getInstance().getReadOnlyPlayer(userId);
		if (player == null) {
			return false;
		}

		UserGroupAttributeDataIF userGroupAttributeData = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(userId);
		if (userGroupAttributeData == null) {
			return false;
		}
		String groupId = userGroupAttributeData.getGroupId();
		return !StringUtils.isEmpty(groupId);
	}

	/**
	 * 发送加入帮派的邮件
	 * 
	 * @param userId
	 * @param groupName
	 */
	public static void sendJoinGroupMail(String userId, String groupName) {
		EmailCfg emailCfg = EmailCfgDAO.getInstance().getEmailCfg(GroupConst.JOIN_GROUP_MAIL_ID);
		String newContent = String.format(emailCfg.getContent(), groupName);

		EmailData emailData = new EmailData();
		emailData.setTitle(emailCfg.getTitle());
		emailData.setContent(newContent);
		emailData.setDeleteType(EEmailDeleteType.valueOf(emailCfg.getDeleteType()));
		emailData.setDelayTime(emailCfg.getDelayTime());// 整个帮派邮件只保留7天
		emailData.setSender(emailCfg.getSender());
		EmailUtils.sendEmail(userId, emailData);
	}

	/**
	 * 发送退出或者被踢出帮派的邮件
	 * 
	 * @param userId
	 * @param groupName
	 */
	public static void sendQuitGroupMail(String userId, String groupName) {
		EmailCfg emailCfg = EmailCfgDAO.getInstance().getEmailCfg(GroupConst.KICK_GROUP_MAIL_ID);

		String newContent = String.format(emailCfg.getContent(), groupName);

		EmailData emailData = new EmailData();
		emailData.setTitle(emailCfg.getTitle());
		emailData.setContent(newContent);
		emailData.setDeleteType(EEmailDeleteType.valueOf(emailCfg.getDeleteType()));
		emailData.setDelayTime(emailCfg.getDelayTime());// 整个帮派邮件只保留7天
		emailData.setSender(emailCfg.getSender());

		EmailUtils.sendEmail(userId, emailData);
	}

	public static String getGroupId(Player player) {
		UserGroupAttributeDataIF userGroupAttributeData = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(player.getUserId());
		String groupId = null;
		if (userGroupAttributeData != null) {
			groupId = userGroupAttributeData.getGroupId();
		}
		return groupId;
	}

	public static Group getGroup(Player player) {
		String groupId = getGroupId(player);
		return GroupBM.get(groupId);
	}
}