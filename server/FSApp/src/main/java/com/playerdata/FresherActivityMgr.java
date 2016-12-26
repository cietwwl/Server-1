package com.playerdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.readonly.FresherActivityMgrIF;
import com.rw.service.FresherActivity.FresherActivityChecker;
import com.rw.service.FresherActivity.FresherActivityCheckerResult;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIActivityCode;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.fresherActivity.FresherActivityFinalRewardCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityFinalRewardCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItem;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemHolder;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemIF;

public class FresherActivityMgr implements FresherActivityMgrIF {
	private FresherActivityItemHolder fresherActivityItemHolder;
	private FresherActivityChecker fresherActivityChecker = new FresherActivityChecker();
	protected Player m_Player = null;

	public boolean init(Player pOwner) {
		// 临时判断是否是机器人
		if (pOwner.isRobot()) {
			return true;
		}
		this.m_Player = pOwner;
		fresherActivityItemHolder = new FresherActivityItemHolder(pOwner.getUserId());
		return true;
	}

	public void syn(int version) {
		// 防止同步没有改对象的数据
		if (fresherActivityItemHolder == null) {
			return;
		}
		fresherActivityItemHolder.synAllData(m_Player, version);
	}

	public void doCheck(eActivityType type) {
		if (m_Player == null || m_Player.isRobot()) {// 是机器人的情况下就不用检查开服活动
			return;
		}

		FresherActivityCheckerResult returnResult = fresherActivityChecker.checkActivityCondition(m_Player, type);
		if (returnResult == null) {
			return;
		}
		fresherActivityItemHolder.completeFresherActivity(m_Player, returnResult);
	}

	@Override
	public List<FresherActivityItemIF> getFresherActivityItems(eActivityType type) {
		// TODO Auto-generated method stub
		List<FresherActivityItem> items = fresherActivityItemHolder.getFresherActivityItemsByType(type);
		if (items == null) {
			return new ArrayList<FresherActivityItemIF>();
		} else {
			return new ArrayList<FresherActivityItemIF>(items);
		}
	}

	public void save() {
	}

	public String achieveFresherActivityReward(Player player, int cfgId) {

		FresherActivityItem item = fresherActivityItemHolder.getFresherActivityItemsById(cfgId);
		FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(cfgId);
		if (item.getStartTime() > System.currentTimeMillis()) {
			return "当前活动还没有开始!";
		}
		if (!item.isFinish() && item.getType() != eActivityType.A_Final) {
			return "当前活动还没有完成！";
		}
		if (item.isGiftTaken() || item.isClosed()) {
			return "当前活动已经领取奖励并结束!";
		}
		BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.SEVER_BEGIN_ACTIVITY_ONE, 0, cfgId);
		fresherActivityChecker.achieveActivityReward(player, cfgId, fresherActivityCfg.geteType(), fresherActivityItemHolder);

		return null;
	}

	public List<String> getFresherActivityList() {
		ArrayList<String> configList = new ArrayList<String>();
		if (fresherActivityItemHolder == null) {
			return configList;
		}
		List<FresherActivityItem> fresherActivityItemList = fresherActivityItemHolder.getFresherActivityItemList();

		FresherActivityItem finalActItem = null;// 最终的任务

		int finishCount = 0;// 已经完成的数量
		int totalCount = 0;// 总数量

		long now = System.currentTimeMillis();
		for (FresherActivityItem item : fresherActivityItemList) {

			if (item.getType() == eActivityType.A_Final) {
				finalActItem = item;
			}

			if (item.isFinish() && item.getStartTime() <= now) {
				finishCount++;
			}

			// 不能算最终奖励的个数
			if (item.getType() != eActivityType.A_Final) {
				totalCount++;
			}

			if (item.getStartTime() > now) {
				continue;
			}

			if (item.isFinish() && !item.isGiftTaken() && item.getType() != eActivityType.A_Final) {
				configList.add(String.valueOf(item.getCfgId()));
			}
		}

		// 检查最终的红点
		if (finalActItem != null && finalActItem.getStartTime() <= now && !finalActItem.isGiftTaken()) {
			double result = (double) finishCount / totalCount * 100;
			boolean canAchieve = false;
			int achieveId = StringUtils.isEmpty(finalActItem.getCurrentValue()) ? -1 : Integer.parseInt(finalActItem.getCurrentValue());
			List<FresherActivityFinalRewardCfg> allCfg = FresherActivityFinalRewardCfgDao.getInstance().getAllCfg();
			for (FresherActivityFinalRewardCfg fresherActivityFinalRewardCfg : allCfg) {
				if (fresherActivityFinalRewardCfg.getId() > achieveId) {
					if (result >= fresherActivityFinalRewardCfg.getProgress()) {
						canAchieve = true;
					}
				}
			}

			if (canAchieve) {
				configList.add(String.valueOf(finalActItem.getCfgId()));
			}
		}

		return configList;
	}
}
