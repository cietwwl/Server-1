package com.gm.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.rw.fsutil.cacheDao.CfgCsvReloader;

public class GmHotUpdate implements IGmTask {

	private static String REASON_KEY = "reason";
	private static String PATH = "path";
	private static String SUCCESS = "success";
	
	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();

		try {
			Map<String, Object> map = request.getArgs();
			if (map == null || map.isEmpty()) {
				return response(REASON_KEY, "缺少执行参数", response);
			}
			String classPath = (String) map.get(PATH);
			if (classPath == null || classPath.isEmpty()) {
				return response(REASON_KEY, "缺少执行类路径", response);
			}
			Class clazz = Class.forName(classPath);
			if (clazz == null) {
				return response(REASON_KEY, "加载指定路径失败：" + classPath, response);
			}
			Object o = clazz.newInstance();
			if (!(o instanceof Callable)) {
				return response(REASON_KEY, "指定类路径类型出错：" + o.getClass(), response);
			}
			Callable r = (Callable) o;
			Object result = r.call();
			response.setStatus(0);
			response.setCount(1);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put(SUCCESS, result);
			response.addResult(resultMap);
			return response;
		} catch (Exception ex) {
			return response(REASON_KEY, "hotUpdate exception:" + ex.getMessage(), response);
		}
	}

	private GmResponse response(String key, String reason, GmResponse response) {
		response.setStatus(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(key, reason);
		response.addResult(resultMap);
		return response;
	}
}
