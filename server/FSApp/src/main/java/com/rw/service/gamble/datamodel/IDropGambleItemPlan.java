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

	int getExclusiveCount();
}
