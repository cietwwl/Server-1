package com.rwbase.dao.fresherActivity.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.util.DateUtils;
import com.rw.manager.GameManager;
import com.rw.service.FresherActivity.FresherActivityChecker;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;

public class FresherActivityCreator implements PlayerExtPropertyCreator<FresherActivityBigItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {

		return null;
	}

	@Override
	public List<FresherActivityBigItem> firstCreate(PlayerPropertyParams params) {
		List<FresherActivityBigItem> result = new ArrayList<FresherActivityBigItem>();
		Map<Integer, FresherActivityBigItem> map = new HashMap<Integer, FresherActivityBigItem>();
		List<FresherActivityCfg> allCfg = FresherActivityCfgDao.getInstance().getAllCfg();
		String userId = params.getUserId();
		long createTime = params.getCreateTime();
		for (FresherActivityCfg fresherActivityCfg : allCfg) {
			eActivityType type = fresherActivityCfg.geteType();
			
			if(map.containsKey(type.getType())){
				FresherActivityBigItem fresherActivityBigItem = map.get(type.getType());
				createNewFresherActivity(fresherActivityCfg, fresherActivityBigItem, userId, createTime);
			}else{
				FresherActivityBigItem fresherActivityBigItem = new FresherActivityBigItem();

				fresherActivityBigItem.setId(type.getType());
				fresherActivityBigItem.setActivityType(type);
				createNewFresherActivity(fresherActivityCfg, fresherActivityBigItem, userId, createTime);
				result.add(fresherActivityBigItem);
				map.put(type.getType(), fresherActivityBigItem);
			}
		}
		return result;
	}

	@Override
	public List<FresherActivityBigItem> checkAndCreate(RoleExtPropertyStore<FresherActivityBigItem> store, PlayerPropertyParams params) {
		
		return null;
	}

	private boolean createNewFresherActivity(FresherActivityCfg fresherActivityCfg, FresherActivityBigItem fresherActivityBigItem, String ownerId, long createTime){
		long current = System.currentTimeMillis();
		FresherActivityItem fresherActivityItem = new FresherActivityItem();

		
		refreshActivityTime(fresherActivityItem, fresherActivityCfg, createTime);
		long endTime = fresherActivityItem.getEndTime();
		if (endTime != -1 && endTime <= current) {
			return false;
		}
		int cfgId = fresherActivityCfg.getCfgId();
		fresherActivityItem.setCfgId(cfgId);
		fresherActivityItem.setType(fresherActivityCfg.geteType());
		String maxValue = fresherActivityCfg.getMaxValue();
		if(maxValue != null && !maxValue.equals("")){
			fresherActivityItem.setCurrentValue("0/" + fresherActivityCfg.getMaxValue());
		}
		List<FresherActivityItem> itemList = fresherActivityBigItem.getItemList();
		itemList.add(fresherActivityItem);
		return true;
	}
	
	/**
	 * 读取配置表的时间
	 * 
	 * @param fresherActivityItem
	 * @param fresherActivityCfg
	 */
	private void refreshActivityTime(FresherActivityItem fresherActivityItem, FresherActivityCfg fresherActivityCfg, long createTime) {
		long openTime = 0;
		if(fresherActivityCfg.getStartTimeType() == FresherActivityChecker.START_TYPE_OPENTIME){
			openTime = GameManager.getOpenTime();
		}else{
			openTime = DateUtils.getHour(createTime, 5);   //五点为重置时间
			//当前创建时间已经转天而且小于5点 应该算上一天
			if(createTime < openTime){
				openTime -= 24*60*60*1000l;
			}
		}
		fresherActivityItem.setStartTime(fresherActivityCfg.getStartTime() * FresherActivityChecker.DAY_TIME + openTime);
		if (fresherActivityCfg.getEndTime() == -1) {
			fresherActivityItem.setEndTime(-1);
		} else {
			fresherActivityItem.setEndTime(fresherActivityCfg.getStartTime() * FresherActivityChecker.DAY_TIME + openTime + fresherActivityCfg.getEndTime() * FresherActivityChecker.DAY_TIME);
		}
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		return true;
	}
}
