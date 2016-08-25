package com.rw.trace.listener;

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
		int power1 = oldData.getPower();
		int power2 = newData.getPower();
		if (oldData.getPower() != newData.getPower()) {
			System.out.println(oldData.getUserId() + ":" + power1 + "," + power2);
		}
		
	}

}
