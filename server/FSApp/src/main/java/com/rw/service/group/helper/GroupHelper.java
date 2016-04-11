package com.rw.service.group.helper;

import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.playerdata.PlayerMgr;
import com.playerdata.readonly.PlayerIF;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.GroupConstCfg;
import com.rwbase.dao.group.pojo.cfg.dao.GroupConstCfgDAO;
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

		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
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

		return player.getUserGroupAttributeDataMgr().getUserGroupAttributeData().getGroupId();
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

		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
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
		GroupConstCfg groupConstCfg = GroupConstCfgDAO.getCfgDAO().getGroupConstCfg();

		String newContent = String.format(groupConstCfg.getJoinGroupMailContent(), groupName);

		EmailData emailData = new EmailData();
		emailData.setTitle(groupConstCfg.getJoinGroupMailTitle());
		emailData.setContent(newContent);
		emailData.setDeleteType(EEmailDeleteType.DELAY_TIME);
		emailData.setDelayTime((int) TimeUnit.DAYS.toMillis(7));// 整个帮派邮件只保留7天
		emailData.setSender(groupConstCfg.getMailSender());

		EmailUtils.sendEmail(userId, emailData);
	}

	/**
	 * 发送退出或者被踢出帮派的邮件
	 * 
	 * @param userId
	 * @param groupName
	 */
	public static void sendQuitGroupMail(String userId, String groupName) {
		GroupConstCfg groupConstCfg = GroupConstCfgDAO.getCfgDAO().getGroupConstCfg();

		String newContent = String.format(groupConstCfg.getQuitGroupMailContent(), groupName);

		EmailData emailData = new EmailData();
		emailData.setTitle(groupConstCfg.getQuitGroupMailTitle());
		emailData.setContent(newContent);
		emailData.setDeleteType(EEmailDeleteType.DELAY_TIME);
		emailData.setDelayTime((int) TimeUnit.DAYS.toMillis(7));// 整个帮派邮件只保留7天
		emailData.setSender(groupConstCfg.getMailSender());

		EmailUtils.sendEmail(userId, emailData);
	}
}