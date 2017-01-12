package com.gm.multipletimeshotfix;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;

/**
 * @Author HC
 * @date 2017年1月11日 上午6:05:20
 * @desc 
 **/

public class HotFixActivityExchange implements Callable<Object>{

	@Override
	public Object call() throws Exception {
		Field field = ActivityExchangeTypeMgr.class.getDeclaredField("instance");
		field.setAccessible(true);
		HotFixActivityExchangeTypeMgr hotFixActivityExchange = new HotFixActivityExchangeTypeMgr();
		field.set(ActivityExchangeTypeMgr.getInstance(), hotFixActivityExchange);
		field.setAccessible(false);
		return null;
	}

}
