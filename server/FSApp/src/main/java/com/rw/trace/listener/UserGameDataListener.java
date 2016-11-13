package com.rw.trace.listener;

import java.util.ArrayList;
import java.util.List;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.ERoleAttrs;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
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
		Player player = PlayerMgr.getInstance().find(userId);
		if (oldData.getPower() != newData.getPower()) {
			List<ERoleAttrs> roleAttrsList = new ArrayList<ERoleAttrs>();
			roleAttrsList.add(ERoleAttrs.r_Power);
			TargetSellManager.getInstance().notifyRoleAttrsChange(player, roleAttrsList);
		}
		
	}

}
