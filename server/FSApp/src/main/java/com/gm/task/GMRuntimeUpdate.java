package com.gm.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.log.GameLog;

public class GMRuntimeUpdate implements IGmTask {
	
	private static Map<String, Long> runtimeUpdateHistories = new LinkedHashMap<String, Long>(); // key=类的路径，value=最后执行时间
	private static Map<String, Long> runtimeUpdateHistoriesRO = Collections.unmodifiableMap(runtimeUpdateHistories);
	
	public static void addHistories(Map<String, Long> map) {
		if (map != null) {
			runtimeUpdateHistories.putAll(map);
		}
	}
	
	public static Map<String, Long> getHistories() {
		return runtimeUpdateHistoriesRO;
	}
	
	private void recordHotUpdate(List<String> newPathList) {
		synchronized (runtimeUpdateHistories) {
			for (String path : newPathList) {
				runtimeUpdateHistories.put(path, System.currentTimeMillis());
			}
			try {
				GmUtils.recordHotfixHistory(runtimeUpdateHistories);
			} catch (Exception e) {
				GameLog.error("GMRuntimeUpdate", "recordHotUpdate", "保存热更记录出错！");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private GmResponse response(int status, Map<String, Object> map) {
		GmResponse response = new GmResponse();
		response.setStatus(status);
		response.setCount(1);
		response.setResult(Arrays.asList(map));
		return response;
	}

	@Override
	public GmResponse doTask(GmRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Class<? extends Callable<?>>> classPathList = GmUtils.getAllHotFixes();
			if (classPathList.size() > 0) {
				List<String> chassPathName = new ArrayList<String>(classPathList.size());
				Class<? extends Callable<?>> clazz;
				for (int i = 0, size = classPathList.size(); i < size; i++) {
					clazz = classPathList.get(i);
					String clazzName = clazz.getName();
					if (runtimeUpdateHistories.containsKey(clazzName)) {
						continue;
					}
					try {
						Callable<?> task = clazz.newInstance();
						Object objResult = task.call();
						map.put(clazzName, "DONE, RESULT=" + objResult);
					} catch (Exception e) {
						map.put(clazzName, "EXCEPTION, MSG=" + e.getMessage());
					}
					chassPathName.add(clazzName);
				}
				recordHotUpdate(chassPathName);
				return this.response(0, map);
			} else {
				map.put("FAIL", "No Runtime Update Class found!");
				return this.response(1, map);
			}
		} catch (Exception e) {
			map.put("reason", "Exception, " + e.getMessage());
			return this.response(1, map);
		}
	}

}
