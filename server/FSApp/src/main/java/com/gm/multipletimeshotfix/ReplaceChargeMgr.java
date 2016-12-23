package com.gm.multipletimeshotfix;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.Callable;

import com.playerdata.charge.ChargeMgr;

public class ReplaceChargeMgr implements Callable<Object> {

	@Override
	public Object call() throws Exception {
		Field field = ChargeMgr.class.getDeclaredField("instance");
		Field fProcessOrders = ChargeMgr.class.getDeclaredField("_processOrders");
		Field fProcessOrdersHF = ChargeMgrHF.class.getDeclaredField("_processOrders");
		ChargeMgrHF instanceHF = new ChargeMgrHF();
		field.setAccessible(true);
		fProcessOrders.setAccessible(true);
		fProcessOrdersHF.setAccessible(true);
		field.set(null, instanceHF);
		Object instance = field.get(null);
		field.setAccessible(false);
		Map<String, Boolean> mapHF = (Map<String, Boolean>) fProcessOrdersHF.get(ChargeMgrHF.getInstance());
		Map<String, Boolean> map = (Map<String, Boolean>) fProcessOrders.get(ChargeMgr.getInstance());
		fProcessOrders.setAccessible(false);
		fProcessOrdersHF.setAccessible(false);
		return "替换成功！当前instance：" + instance + "，map的实例：" + map.hashCode() + "，mapHF的实例：" + mapHF.hashCode();
	}

}
