package com.bm.targetSell.listener;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.ERoleAttrs;
import com.rw.fsutil.dao.cache.trace.SignleChangedEvent;
import com.rw.fsutil.dao.cache.trace.SingleChangedListener;
import com.rw.manager.ServerSwitch;
import com.rwbase.dao.openLevelTiggerService.OpenLevelTiggerServiceMgr;
import com.rwbase.dao.user.User;

/**
 * 玩家数据变化监听器，这里监听exp,vip, level
 * @author Alex
 * 2016年9月13日 下午3:56:55
 */
public class UserDataListener implements SingleChangedListener<User>{

	@Override
	public void notifyDataChanged(SignleChangedEvent<User> event) {
		if(!ServerSwitch.isOpenTargetSell()){
			return;
		}
		User oldRecord = event.getOldRecord();
		User currentRecord = event.getCurrentRecord();
		String userId = currentRecord.getUserId();
		
		if(oldRecord.getLevel() != currentRecord.getLevel()){
			System.out.println("benefit system record role level change ,new level:" + currentRecord.getLevel()+ ",old level:" + oldRecord.getLevel());
			TargetSellManager.getInstance().notifyRoleAttrsChange(userId, ERoleAttrs.r_Level.getId());
			OpenLevelTiggerServiceMgr.getInstance().tiggerServiceByLevel(userId,oldRecord,currentRecord);
		}
		if(oldRecord.getVip() != currentRecord.getVip()){

			TargetSellManager.getInstance().notifyRoleAttrsChange(userId, ERoleAttrs.r_VipLevel.getId());
		}
		if(oldRecord.getLastLoginTime() != currentRecord.getLastLoginTime()){
			TargetSellManager.getInstance().notifyRoleAttrsChange(userId, ERoleAttrs.r_LastLoginTime.getId());
		}
	}


}
