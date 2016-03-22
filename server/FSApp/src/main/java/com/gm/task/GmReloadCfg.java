package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.rw.fsutil.cacheDao.CfgCsvReloader;

public class GmReloadCfg implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			
			CfgCsvReloader.reload();
			
			response.setStatus(0);
			response.setCount(1);
			
		} catch (Exception ex) {
			response.setStatus(1);
			resultMap.put("reason", "配置reload 发生异常："+ex.getMessage());
			response.addResult(resultMap);
		}
		return response;
	}
	

}
