package com.rw.service.gamble.datamodel;

import java.util.List;
import java.util.Random;

public interface IDropGambleItemPlan {
	public int getCheckNum(int index);

	public boolean checkInList(String itemModelId);

	public int getOrdinaryGroup(Random r);

	public int getGuaranteeGroup(Random r);
	
	public GambleDropGroup getGuaranteeGroup(Random ranGen, List<String> checkHistory);

	public GambleDropGroup getOrdinaryGroup(Random ranGen, List<String> checkHistory);

	public int getExclusiveCount();

	public int getLastCheckIndex();
	
	/**
	 * 特殊组别：单抽
	 * @return
	 */
	public boolean isSingleGamble();
	
	public Iterable<ItemOrHeroGambleInfo> getGamblePosibles();

	public Iterable<ItemOrHeroGambleInfo> ReInitPreviewData();
}
