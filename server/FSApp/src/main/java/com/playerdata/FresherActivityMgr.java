package com.playerdata;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.readonly.FresherActivityMgrIF;
import com.rw.manager.GameManager;
import com.rw.service.FresherActivity.FresherActivityChecker;
import com.rw.service.FresherActivity.FresherActivityCheckerResult;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItem;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemHolder;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemIF;

public class FresherActivityMgr implements FresherActivityMgrIF {
	private FresherActivityItemHolder fresherActivityItemHolder;
	private FresherActivityChecker fresherActivityChecker = new FresherActivityChecker();
	protected Player m_Player = null;

	public boolean init(Player pOwner) {
		// 临时判断是否是机器人
		if (!pOwner.getUserId().contains(GameManager.getServerId())) {
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
	
	public void doCheck(eActivityType type){
		if(m_Player == null || !m_Player.getUserId().contains(GameManager.getServerId())){
			return;
		}
//		FresherActivityCheckerResult returnResult = fresherActivityChecker.checkActivityCondition(m_Player, type);
//		if (returnResult == null) {
//			return;
//		}
//		fresherActivityItemHolder.completeFresherActivity(m_Player, returnResult);
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
		fresherActivityChecker.achieveActivityReward(player, cfgId, fresherActivityCfg.geteType(), fresherActivityItemHolder);
		return null;
	}

	public List<String> getFresherActivityList() {
		ArrayList<String> configList = new ArrayList<String>();
		if(fresherActivityItemHolder == null){
			return configList;
		}
		List<FresherActivityItem> fresherActivityItemList = fresherActivityItemHolder.getFresherActivityItemList();
		
		for (FresherActivityItem item : fresherActivityItemList) {
			if(item.getType() == eActivityType.A_Final && item.getStartTime() <= System.currentTimeMillis() && !item.isGiftTaken()){
				configList.add(String.valueOf(item.getCfgId()));
			}
			if(item.getStartTime() > System.currentTimeMillis()){
				continue;
			}
			if (item.isFinish() && !item.isGiftTaken()) {
				configList.add(String.valueOf(item.getCfgId()));
			}
		}
		return configList;
	}
}
