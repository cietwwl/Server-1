package com.groupCopy.bm.groupCopy;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import com.bm.group.GroupBM;
import com.groupCopy.bm.GroupHelper;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMailCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMailCfgDao;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.groupCopy.rwbase.dao.groupCopy.db.ApplyInfo;
import com.groupCopy.rwbase.dao.groupCopy.db.DropInfo;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyDistIDManager;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.ItemDropAndApplyTemplate;
import com.rw.service.Email.EmailUtils;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;


/**
 * 邮件处理器
 * @author Alex
 * 2016年7月4日 下午4:25:46
 */
public class GroupCopyMailHelper {
	

	private static final GroupCopyMailHelper instance = new GroupCopyMailHelper();
	
	private GroupCopyMailHelper(){
	}
	
	public static GroupCopyMailHelper getInstance(){
		return instance;
	}
	
	/**
	 * 分发帮派奖励
	 */
	public void dispatchGroupWarPrice(){
		
		List<String> idList = GroupCopyDistIDManager.getInstance().getGroupIDList();
		for (String id : idList) {
			Group group = GroupBM.get(id);
			if(group == null){
				continue;
			}
//			System.out.println("准备发放帮派副本奖励，帮派名：" + group.getGroupBaseDataMgr().getGroupData().getGroupName());
			group.getGroupCopyMgr().checkAndSendGroupPriceMail(group.getGroupBaseDataMgr().getGroupData().getGroupName());
		}
		
	}

	
	/**
	 * 发送章节重置邮件
	 * @param receverID
	 * @param roleName
	 * @param copyName
	 */
	public void sendResetGroupCopyMail(String receverID, String roleName, String copyName){
		List<String> args = new ArrayList<String>();
		GroupCopyMailCfg mailCfg = GroupCopyMailCfgDao.getInstance().getConfig();
		EmailData emailData = EmailUtils.createEmailData(mailCfg.getResetMailID(), "", args);
		String content = String.format(emailData.getContent(), roleName, copyName);
		emailData.setContent(content);
		EmailUtils.sendEmail(receverID, emailData);
	}
	
	
	/**
	 * 发送帮派定时奖励邮件
	 * @param template
	 * @param dropInfo
	 * @param applyInfo
	 * @param groupName TODO
	 */
	public boolean checkAndSendMail(ItemDropAndApplyTemplate template,
			DropInfo dropInfo, ApplyInfo applyInfo, String groupName) {
		String mailReward = template.getItemID() + "~" + 1;
		
		GroupCopyMailCfg mailCfg = GroupCopyMailCfgDao.getInstance().getConfig();
		EmailData emailData = EmailUtils.createEmailData(mailCfg.getPersonMailID(), mailReward, new ArrayList<String>());
		String content = String.format(emailData.getContent(), groupName);
		emailData.setContent(content);
		boolean sendEmail = EmailUtils.sendEmail(applyInfo.getRoleID(), emailData);
//		System.out.println("发送帮派定时奖励，角色名：" + applyInfo.getRoleName() + ", 帮派名:" + groupName);
		return sendEmail;
	}

	/**
	 * 发送帮派副本通关奖励邮件
	 * @param groupID 帮派id 
	 * @param finalHitRoleName  最后一击角色名
	 * @param inExtralTime  是否在限定时间内
	 * @param chaterID TODO
	 */
	public void sendGroupCopyFinishMail(String groupID, String finalHitRoleName,
			boolean inExtralTime, String chaterID) {
		StringBuilder mailContent = new StringBuilder();
		int totalRewardCount = 0;//总奖励金额
		Group group = GroupBM.get(groupID);
		String groupName = group.getGroupBaseDataMgr().getGroupData().getGroupName();
		
		String firstDamageRoleName = group.getGroupCopyMgr().getFirstDamageRoleName(chaterID);
		GroupCopyMapCfg mapCfg = GroupCopyMapCfgDao.getInstance().getCfgById(chaterID);
		
		totalRewardCount += mapCfg.getPassReward();
		
		
		GroupCopyMailCfg mailCfg = GroupCopyMailCfgDao.getInstance().getConfig();
		String content = String.format(mailCfg.getComMailContent(), groupName,
				mapCfg.getName(), firstDamageRoleName, finalHitRoleName, mapCfg.getPassReward());
		mailContent.append(content);
		if(inExtralTime){
			//限定时间通关
			String limintContent = String.format(mailCfg.getLTMailContent(), mapCfg.getExtraReward());
			mailContent.append(limintContent);
			totalRewardCount += mapCfg.getExtraReward();
		}
		List<? extends GroupMemberDataIF> list = group.getGroupMemberMgr().getMemberSortList(null);
		
		String reward = eSpecialItemId.GuildCoin.getValue() +"~" + totalRewardCount;
		EmailData mailData = null;
		
		for (GroupMemberDataIF data : list) {
			int index = group.getGroupCopyMgr().getRoleInDamageRankIndex(chaterID, data.getUserId());
			if(index > 0){
				//进入伤害排行榜
				int extralValue = mapCfg.getExtralValue(index);
				if(extralValue > 0){
					String extraContent = String.format(mailCfg.getHDMailContent(), index, extralValue);
					mailData = EmailUtils.createEmailData(mailCfg.getPassMailID(), eSpecialItemId.GuildCoin.getValue() +"~" + (totalRewardCount+extralValue),
							new ArrayList<String>());
					mailData.setContent(mailContent.toString() + extraContent);
				}
			}else{
				mailData = EmailUtils.createEmailData(mailCfg.getPassMailID(), reward, new ArrayList<String>());
				mailData.setContent(mailContent.toString());
			}
			
			EmailUtils.sendEmail(data.getUserId(), mailData);
			System.out.println("发送帮派副本通关奖励邮件,接收角色：" + data.getName());
		}
		
		
		
	}
	
}
