package com.gm.task;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;

public class GMRuntimeUpdate implements IGmTask {
	
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
			List<String> classPathList = GmUtils.getHotUpdateInfo().getT2();
			if (classPathList.size() > 0) {
				String classPath;
				for (int i = 0, size = classPathList.size(); i < size; i++) {
					classPath = classPathList.get(i);
					try {
						@SuppressWarnings("unchecked")
						Callable<? extends Object> task = (Callable<? extends Object>) Class.forName(classPath).newInstance();
						Object objResult = task.call();
						map.put(classPath, objResult);
					} catch (Exception e) {
						map.put(classPath, e.getMessage());
					}
				}
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
