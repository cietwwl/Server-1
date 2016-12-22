package com.gm.multipletimeshotfix;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.Callable;

import com.gm.GmHandler;
import com.gm.GmServer;
import com.gm.task.IGmTask;
import com.rw.fsutil.util.SpringContextUtil;

public class AddVipExpGmTask implements Callable<Object>{

	@SuppressWarnings("unchecked")
	@Override
	public Object call() throws Exception {
		GmServer server = SpringContextUtil.getBean("gmServer");
		Field handlerField = GmServer.class.getDeclaredField("gmHandler");
		handlerField.setAccessible(true);
		GmHandler handler = (GmHandler)handlerField.get(server);
		handlerField.setAccessible(false);
		Field declaredField = GmHandler.class.getDeclaredField("taskMap");
		declaredField.setAccessible(true);
		Map<Integer, IGmTask> taskMap = (Map<Integer, IGmTask>)declaredField.get(handler);
		declaredField.setAccessible(false);
		taskMap.put(20079, new GmFixAddVipExp());
		return null;
	}

}
