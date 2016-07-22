package com.rw.service.gamble.datamodel;

import java.util.List;
import java.util.Random;

import com.common.RefInt;

public interface IDropGambleItemPlan {
	public int getCheckNum(int index);

	public boolean checkInList(String itemModelId);

	public int getOrdinaryGroup(Random r);

	public int getGuaranteeGroup(Random r);
	
	public int getOrdinaryGroup(Random r,RefInt planIndex);

	public int getGuaranteeGroup(Random r,RefInt planIndex);
	
	public GambleDropGroup getGuaranteeGroup(Random ranGen, List<String> checkHistory,RefInt selectedIndex);

	public GambleDropGroup getOrdinaryGroup(Random ranGen, List<String> checkHistory,RefInt selectedIndex);

	public int getExclusiveCount();

	public int getLastCheckIndex();
	
	public IDropGambleItemPlan removeHistoryFromOrdinaryGroup(int planId);
	
	/**
	 * 特殊组别：单抽
	 * @return
	 */
	public boolean isSingleGamble();
	
	public Iterable<ItemOrHeroGambleInfo> getGamblePosibles();

	public Iterable<ItemOrHeroGambleInfo> ReInitPreviewData();
}
