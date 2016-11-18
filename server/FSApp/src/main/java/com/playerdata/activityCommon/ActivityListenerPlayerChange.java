package com.playerdata.activityCommon;

import com.bm.player.PlayerChangePopertyObserver;
import com.bm.player.PlayerChangePopertySubscribe;
import com.playerdata.Player;

public class ActivityListenerPlayerChange extends PlayerChangePopertySubscribe {

	public ActivityListenerPlayerChange(PlayerChangePopertyObserver observer) {
		super(observer);
	}

	@Override
	public void playerChangeName(Player p) {
		
	}

	@Override
	public void playerChangeLevel(Player p) {
		ActivityMgrHelper.getInstance().synActivityData(p);
	}

	@Override
	public void playerChangeVipLevel(Player p) {
		ActivityMgrHelper.getInstance().synActivityData(p);
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
