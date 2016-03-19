package com.bm.rank.peakArena;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
import com.rw.fsutil.ranking.RankingEntry;

public class PeakArenaExtension extends RankingJacksonExtension<Integer, PeakArenaExtAttribute>{

	public PeakArenaExtension(){
		super(Integer.class, PeakArenaExtAttribute.class);
	}
	
	@Override
	public void notifyEntryEvicted(RankingEntry<Integer, PeakArenaExtAttribute> entry) {
	}

	@Override
	public PeakArenaExtAttribute newEntryExtension(String key, Object param) {
		Player player = (Player)param;
		PeakArenaExtAttribute extAttr = new PeakArenaExtAttribute();
		extAttr.setFighting(player.getMainRoleHero().getFighting());
		extAttr.setHeadImage(player.getHeadImage());
		extAttr.setLevel(player.getLevel());
		extAttr.setName(player.getUserName());
		return extAttr;
	}

	
}
