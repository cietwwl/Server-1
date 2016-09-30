package com.rwbase.dao.fresherActivity.pojo;

import java.util.ArrayList;
import java.util.List;

import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.fsutil.util.DateUtils;
import com.rw.manager.GameManager;
import com.rw.service.FresherActivity.FresherActivityChecker;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;

public class FresherActivityCreator implements PlayerExtPropertyCreator<FresherActivityItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {

		return null;
	}

	@Override
	public boolean validateOpenTime(long currentTimeMillis) {

		return true;
	}

	@Override
	public List<FresherActivityItem> firstCreate(PlayerPropertyParams params) {
		List<FresherActivityItem> result = new ArrayList<FresherActivityItem>();
		List<FresherActivityCfg> allCfg = FresherActivityCfgDao.getInstance().getAllCfg();
		String userId = params.getUserId();
		User user = UserDataDao.getInstance().getByUserId(userId);
		for (FresherActivityCfg fresherActivityCfg : allCfg) {
			int cfgId = fresherActivityCfg.getCfgId();
			eActivityType type = fresherActivityCfg.geteType();
			
			FresherActivityItem fresherActivityItem = new FresherActivityItem();
			fresherActivityCfg.setCfgId(cfgId);
			fresherActivityItem.setType(type);
			fresherActivityItem.setStatus((byte)0);
			
			long openTime = 0;
			if (fresherActivityCfg.getStartTimeType() == FresherActivityChecker.START_TYPE_OPENTIME) {
				openTime = GameManager.getOpenTime();
			} else {
				long createTime = user.getCreateTime();
				openTime = DateUtils.getHour(createTime, 5); // 五点为重置时间
				// 当前创建时间已经转天而且小于5点 应该算上一天
				if (createTime < openTime) {
					openTime -= 24 * 60 * 60 * 1000l;
				}
			}
			fresherActivityItem.setStartTime(fresherActivityCfg.getStartTime() * FresherActivityChecker.DAY_TIME + openTime);
			if (fresherActivityCfg.getEndTime() == -1) {
				fresherActivityItem.setEndTime(-1);
			} else {
				fresherActivityItem.setEndTime(fresherActivityCfg.getStartTime() * FresherActivityChecker.DAY_TIME + openTime + fresherActivityCfg.getEndTime() * FresherActivityChecker.DAY_TIME);
			}
			result.add(fresherActivityItem);
		}
		return result;
	}

	@Override
	public List<FresherActivityItem> checkAndCreate(PlayerExtPropertyStore<FresherActivityItem> store, PlayerPropertyParams params) {
		
		return null;
	}

}
