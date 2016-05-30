package com.playerdata;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.util.DateUtils;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.upgrade.VersionUpdateCfg;
import com.rwbase.dao.upgrade.VersionUpdateCfgDao;
import com.rwbase.dao.upgrade.pojo.TableUpgradeData;
import com.rwbase.dao.upgrade.pojo.TableUpgradeHolder;

public class UpgradeMgr {
	private TableUpgradeHolder tableUpgradeHolder;
	
	protected Player m_Player = null;
	
	public boolean init(Player pOwner){
		if(pOwner.isRobot()){
			return true;
		}
		this.m_Player = pOwner;
		tableUpgradeHolder = new TableUpgradeHolder(pOwner.getUserId());
		return true;
	}
	
	
	public void doCheckUpgrade(String clientVersion){
		String version = clientVersion.substring(clientVersion.indexOf("_")+1, clientVersion.length());
		VersionUpdateCfg cfg = VersionUpdateCfgDao.getInstance().getCfgById(version);
		if(cfg == null){
			return;
		}
		long forceUpdateTime = DateUtils.getTime(cfg.getForceUpdateTime());
		String upgradeVersionNo = cfg.getVersionNo();
		
		TableUpgradeData tableUpgradeData = tableUpgradeHolder.getTableUpgradeData();
		boolean blnUpdate = true;
		if(tableUpgradeData == null){
			tableUpgradeData = new TableUpgradeData();
			blnUpdate = false;
		}
		
		String versionNo = tableUpgradeData.getVersionNo();
		long achieveRewardTime = tableUpgradeData.getAchieveRewardTime();
		
		tableUpgradeData.setOwnerId(this.m_Player.getUserId());
		if(!upgradeVersionNo.equals(versionNo) && achieveRewardTime < forceUpdateTime && m_Player.getUserDataMgr().getCreateTime() < forceUpdateTime){
			
			tableUpgradeData.setAchieveRewardTime(System.currentTimeMillis());
			tableUpgradeData.setVersionNo(upgradeVersionNo);
			
			//处理发奖
			EmailData emailData = new EmailData();
			emailData.setSender(cfg.getSender());
			emailData.setTitle(cfg.getEmailTitle());
			emailData.setContent(cfg.getEmailContent());
			String rewards = cfg.getRewards();
			StringBuilder attachment = new StringBuilder();
			
			String[] values = rewards.split(";");
			
	        for(String value : values){
	            if (StringUtils.isBlank(value)) {
	                continue;
	            }
	            String[] scripts = value.split(",");
	            int itemId = Integer.parseInt(scripts[0]);
	            int itemCount = Integer.parseInt(scripts[1]);
	            attachment.append(itemId).append("~").append(itemCount).append(",");
	            emailData.setEmailAttachment(attachment.toString());
				emailData.setDeleteType(EEmailDeleteType.GET_DELETE);
	        }
			
			EmailUtils.sendEmail(this.m_Player.getUserId(), emailData);
		}
		tableUpgradeHolder.update(tableUpgradeData, blnUpdate);
	}
}
