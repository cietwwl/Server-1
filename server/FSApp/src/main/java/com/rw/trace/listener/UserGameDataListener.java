package com.rw.trace.listener;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.ERoleAttrs;
import com.rw.fsutil.dao.cache.trace.SignleChangedEvent;
import com.rw.fsutil.dao.cache.trace.SingleChangedListener;
import com.rwbase.dao.user.UserGameData;
/**
 * {@link SingleChangedListener}示例文件
 * @author Jamaz
 *
 */
public class UserGameDataListener implements SingleChangedListener<UserGameData> {

	@Override
	public void notifyDataChanged(SignleChangedEvent<UserGameData> event) {
		UserGameData oldData = event.getOldRecord();
		UserGameData newData = event.getCurrentRecord();
		String userId = newData.getUserId();
		if (oldData.getPower() != newData.getPower()) {
			TargetSellManager.getInstance().notifyRoleAttrsChange(userId, ERoleAttrs.r_Power.getId());
		}
		
	}

}
