package com.bm.rank.groupCompetition;

import com.bm.player.PlayerChangePopertyObserver;
import com.bm.player.PlayerChangePopertySubscribe;
import com.bm.rank.groupCompetition.killRank.GCompKillRankMgr;
import com.bm.rank.groupCompetition.scoreRank.GCompScoreRankMgr;
import com.bm.rank.groupCompetition.winRank.GCompContinueWinRankMgr;
import com.playerdata.Player;

public class GCompListenerPlayerChange extends PlayerChangePopertySubscribe {

	public GCompListenerPlayerChange(PlayerChangePopertyObserver observer) {
		super(observer);
	}

	@Override
	public void playerChangeName(Player p) {
		GCompContinueWinRankMgr.updateContinueWinRankBaseInfo(p);
		GCompScoreRankMgr.updateScoreRankInfo(p);
		GCompKillRankMgr.updateKillRankBaseInfo(p);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerChangeHeadBox(Player p) {
		// TODO Auto-generated method stub
		
	}
}
