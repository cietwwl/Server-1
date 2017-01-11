package com.gm.multipletimeshotfix;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

import com.bm.targetSell.TargetSellManager;

public class TargetSellRoleGetItemCallable implements Callable<Boolean>{

	@Override
	public Boolean call() throws Exception {
		Field field = TargetSellManager.class.getDeclaredField("manager");
		field.setAccessible(true);
		TargetSellRoleGetItemHotfix hotfix = new TargetSellRoleGetItemHotfix();
		field.set(TargetSellManager.getInstance(), hotfix);
		field.setAccessible(false);
		return true;
	}

}
