package com.rw.service.PeakArena.datamodel;

import com.bm.rank.ListRankingJacksonExtension;

public class PeakArenaExtension extends ListRankingJacksonExtension<PeakArenaExtAttribute>{

	public PeakArenaExtension(){
		super(PeakArenaExtAttribute.class);
	}
}
