package com.playerdata.teambattle.bm;

import com.bm.player.PlayerChangePopertyObserver;
import com.bm.player.PlayerChangePopertySubscribe;
import com.playerdata.Player;
import com.playerdata.teambattle.data.UserTeamBattleDataHolder;

public class TBListenerPlayerChange extends PlayerChangePopertySubscribe {

	public TBListenerPlayerChange(PlayerChangePopertyObserver observer) {
		super(observer);
	}

	@Override
	public void playerChangeName(Player p) {
		UserTeamBattleDataHolder.getInstance().updateArmyInfoSimple(p);
	}

	@Override
	public void playerChangeLevel(Player p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerChangeVipLevel(Player p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerChangeTemplateId(Player p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerChangeHeadIcon(Player p) {
		UserTeamBattleDataHolder.getInstance().updateArmyInfoSimple(p);
	}

	@Override
	public void playerChangeHeadBox(Player p) {
		// TODO Auto-generated method stub
		
	}
	
	public static void heroChangeHandler(Player player){
		UserTeamBattleDataHolder.getInstance().updateArmyInfoSimple(player);
	}
}
