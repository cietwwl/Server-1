package com.rw.dataaccess.attachment.creator;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityDailyRechargeCreator implements PlayerExtPropertyCreator<ActivityDailyRechargeTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return true;//逻辑本身先对已有数据补发奖励再跑是否新建或初次登陆创建数据；不想大改逻辑就创建时加入空数据跳过补发阶段  ActivityDetector.getInstance().hasDailyCharge();
	}

	@Override
	public List<ActivityDailyRechargeTypeItem> firstCreate(
			PlayerPropertyParams params) {
		List<ActivityDailyRechargeTypeItem> itemList = new ArrayList<ActivityDailyRechargeTypeItem>();
		return itemList;
	}

	@Override
	public List<ActivityDailyRechargeTypeItem> checkAndCreate(
			RoleExtPropertyStore<ActivityDailyRechargeTypeItem> store,
			PlayerPropertyParams params) {
		return null;
	}

}
