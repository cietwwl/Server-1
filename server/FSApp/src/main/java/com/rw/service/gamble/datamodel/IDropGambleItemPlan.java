package com.rw.service.gamble.datamodel;

import java.util.Random;

public interface IDropGambleItemPlan {
	public int getCheckNum(int index);

	public boolean checkInList(String itemModelId);

	public int getOrdinaryGroup(Random r);

	public int getGuaranteeGroup(Random r);
}
