package com.rw.service.store;

import com.bm.player.PlayerChangePopertyObserver;
import com.bm.player.PlayerChangePopertySubscribe;
import com.bm.rank.teaminfo.AngelArrayTeamInfoCall;
import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.playerdata.Player;

public class StoreListenerPlayerChange extends PlayerChangePopertySubscribe{

	public StoreListenerPlayerChange(PlayerChangePopertyObserver observer) {
		super(observer);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void playerChangeVipLevel(Player p) {
		p.getStoreMgr().notifyVipUpgrade();
	}


	@Override
	public void playerChangeName(Player p) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void playerChangeLevel(Player p) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void playerChangeTemplateId(Player p) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void playerChangeHeadIcon(Player p) {
		// TODO Auto-generated method stub
		
	}
}
