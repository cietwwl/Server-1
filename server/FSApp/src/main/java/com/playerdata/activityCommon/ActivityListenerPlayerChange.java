package com.playerdata.activityCommon;

import java.util.Collections;

import com.bm.player.PlayerChangePopertyObserver;
import com.bm.player.PlayerChangePopertySubscribe;
import com.playerdata.Player;
import com.playerdata.activityCommon.modifiedActivity.ActivityModifyItem;

public class ActivityListenerPlayerChange extends PlayerChangePopertySubscribe {

	public ActivityListenerPlayerChange(PlayerChangePopertyObserver observer) {
		super(observer);
	}

	@Override
	public void playerChangeName(Player p) {
		
	}

	@Override
	public void playerChangeLevel(Player p) {
		ActivityMgrHelper.getInstance().synActivityData(p, Collections.<ActivityModifyItem> emptyList());
	}

	@Override
	public void playerChangeVipLevel(Player p) {
		ActivityMgrHelper.getInstance().synActivityData(p, Collections.<ActivityModifyItem> emptyList());
	}

	@Override
	public void playerChangeTemplateId(Player p) {
		
	}

	@Override
	public void playerChangeHeadIcon(Player p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerChangeHeadBox(Player p) {
		// TODO Auto-generated method stub
		
	}
}
