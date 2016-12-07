package com.playerdata.groupFightOnline.manager;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.group.GroupBM;
import com.common.serverdata.ServerCommonData;
import com.common.serverdata.ServerCommonDataHolder;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceHolder;
import com.playerdata.groupFightOnline.dataForClient.GFFightRecord;
import com.playerdata.teambattle.bm.TeamBattleConst;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EmailCfgDAO;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;

public class GFightOnlineResourceMgr {

	private static GFightOnlineResourceMgr instance = new GFightOnlineResourceMgr();

	public static GFightOnlineResourceMgr getInstance() {
		return instance;
	}

	public GFightOnlineResourceData get(int resourceID) {
		return GFightOnlineResourceHolder.getInstance().get(resourceID);
	}

	public void update(GFightOnlineResourceData data) {
		GFightOnlineResourceHolder.getInstance().update(data);
	}

	/**
	 * 设置占领资源点的帮派 或者是更换占有者
	 * 
	 * @param resourceID
	 * @param victoryGroupID
	 */
	public void setVictoryGroup(int resourceID, String victoryGroupID) {
		GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(resourceID);
		resData.setOwnerGroupID(victoryGroupID);
		GFightOnlineResourceHolder.getInstance().update(resData);
	}

	/**
	 * 清除资源点的占有者 同时清除战斗记录信息
	 * 
	 * @param resourceID
	 */
	public void clearVictoryGroup(int resourceID) {
		GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(resourceID);
		if (null != resData && StringUtils.isNotBlank(resData.getOwnerGroupID())) {
			resData.clearCurrentLoopData();
			GFightOnlineResourceHolder.getInstance().update(resData);
		}
	}

	public void synData(Player player) {
		GFightOnlineResourceHolder.getInstance().synData(player);
	}

	/**
	 * 添加战斗记录
	 * 
	 * @param resourceID
	 * @param record
	 */
	public void addFightRecord(int resourceID, GFFightRecord record) {
		GFightOnlineResourceHolder.getInstance().addFightRecord(resourceID, record);
	}

	/**
	 * 获取战斗记录
	 * 
	 * @param resourceID
	 * @return
	 */
	public List<GFFightRecord> getFightRecord(int resourceID) {
		return GFightOnlineResourceHolder.getInstance().getFightRecord(resourceID);
	}

	/**
	 * 发放资源点占领的每日奖励
	 */
	public void dispatchDailyReward(long exeTime) {
		long lastRefreshTime = 0;
		ServerCommonData scdData = ServerCommonDataHolder.getInstance().get();
		if (null != scdData)
			lastRefreshTime = scdData.getTbLastRefreshTime();
		if (DateUtils.isResetTime(TeamBattleConst.DAILY_REFRESH_HOUR, 0, 0, lastRefreshTime)) {
			List<GFightOnlineResourceCfg> resCfg = GFightOnlineResourceCfgDAO.getInstance().getAllCfg();
			for (GFightOnlineResourceCfg cfg : resCfg) {
				dispatchOwnerReward(cfg.getResID());
			}

			if (null != scdData) {
				scdData.setGfLastRefreshTime(exeTime);
				ServerCommonDataHolder.getInstance().update(scdData);
			}
		}
	}

	private void dispatchOwnerReward(int resourceID) {
		GFightOnlineResourceCfg resCfg = GFightOnlineResourceCfgDAO.getInstance().getCfgById(String.valueOf(resourceID));
		if (resCfg == null)
			return;
		String emailContent = String.format(EmailCfgDAO.getInstance().getCfgById(String.valueOf(resCfg.getEmailId())).getContent(), resCfg.getResName());
		GFightOnlineResourceData resData = get(resourceID);
		if (resData == null)
			return;
		if (StringUtils.isNotBlank(resData.getOwnerGroupID())) {
			Group group = GroupBM.getInstance().get(resData.getOwnerGroupID());
			if (group == null)
				return;
			List<? extends GroupMemberDataIF> memberList = group.getGroupMemberMgr().getMemberSortList(null);
			for (GroupMemberDataIF memberInfo : memberList) {
				EmailUtils.sendEmail(memberInfo.getUserId(), String.valueOf(resCfg.getEmailId()), resCfg.getOwnerDailyReward(), emailContent);
			}
		}
	}
}
