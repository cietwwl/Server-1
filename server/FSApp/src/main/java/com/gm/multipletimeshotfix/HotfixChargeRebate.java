package com.gm.multipletimeshotfix;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

import com.playerdata.activity.chargeRebate.ActivityChargeRebateMgr;

public class HotfixChargeRebate implements Callable<Object>{

	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		ActivityChargeRebateMgr instance = ActivityChargeRebateMgr.getInstance();
		Field field = ActivityChargeRebateMgr.class.getDeclaredField("_instance");
		field.setAccessible(true);
		field.set(instance, new FixChargeRebate());
		return null;
	}

}
