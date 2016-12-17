package com.bm.rank.teaminfo;

import com.bm.player.PlayerChangePopertyObserver;
import com.bm.player.PlayerChangePopertySubscribe;
import com.playerdata.Player;

/*
 * @author HC
 * @date 2016年4月18日 下午10:11:06
 * @Description 
 */
public class TeamInfoListenerPlayerChange extends PlayerChangePopertySubscribe {

	public TeamInfoListenerPlayerChange(PlayerChangePopertyObserver observer) {
		super(observer);
	}

	@Override
	public void playerChangeName(Player p) {
		AngelArrayTeamInfoHelper.updateRankingEntry(p, AngelArrayTeamInfoCall.nameCall);
	}

	@Override
	public void playerChangeLevel(Player p) {
		AngelArrayTeamInfoHelper.updateRankingEntryWhenPlayerLevelChange(p);
	}

	@Override
	public void playerChangeVipLevel(Player p) {
		AngelArrayTeamInfoHelper.updateRankingEntry(p, AngelArrayTeamInfoCall.vipCall);
	}

	@Override
	public void playerChangeTemplateId(Player p) {
	}

	@Override
	public void playerChangeHeadIcon(Player p) {
		AngelArrayTeamInfoHelper.updateRankingEntry(p, AngelArrayTeamInfoCall.headIdCall);
	}

	@Override
	public void playerChangeHeadBox(Player p) {
		// TODO Auto-generated method stub
		
	}
}