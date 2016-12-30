package com.rw.dataaccess.attachment.creator;

import java.util.ArrayList;
import java.util.List;

import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.routerServer.giftManger.RouterGiftDataItem;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class RouterGiftDataCreator implements PlayerExtPropertyCreator<RouterGiftDataItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		return false;//逻辑本身先对已有数据补发奖励再跑是否新建或初次登陆创建数据；不想大改逻辑就创建时加入空数据跳过补发阶段  ActivityDetector.getInstance().hasDailyCharge();
	}

	@Override
	public List<RouterGiftDataItem> firstCreate(
			PlayerPropertyParams params) {
		List<RouterGiftDataItem> itemList = new ArrayList<RouterGiftDataItem>();
		return itemList;
	}

	@Override
	public List<RouterGiftDataItem> checkAndCreate(
			RoleExtPropertyStore<RouterGiftDataItem> store,
			PlayerPropertyParams params) {
		return null;
	}
}
