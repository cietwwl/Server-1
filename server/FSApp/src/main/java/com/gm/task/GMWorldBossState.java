package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.rw.fsutil.log.GmLog;
import com.rw.manager.ServerSwitch;


/**
 * GM修改世界boss状态   指令：openWorldBoss 0/1  0是关闭，1是开启
 * @author Alex
 * 2016年11月30日 上午11:20:07
 */
public class GMWorldBossState implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {

		GmResponse response = new GmResponse();
		HashMap<String, Object> reMap = new HashMap<String, Object>();
		response.addResult(reMap);
		
		try {
			
			Map<String, Object> requestMap = request.getArgs();
			if(requestMap == null){
				recordResult(response, reMap, "fail", "Gm命令参数为null", null);
				return response;
			}
			
			Integer status = (Integer) requestMap.get("openWorldBoss");
			if(status == null){
				recordResult(response, reMap, "fail", "屏蔽或开启世界boss缺少openWorldBoss参数", null);
				return response;
			}
			
			if(status == 0){//关闭
				ServerSwitch.setOpenWorldBoss(false);
			}else if(status == 1){ //开启
				ServerSwitch.setOpenWorldBoss(true);
			}else{
				recordResult(response, reMap, "fail", "屏蔽或开启世界boss的openWorldBoss参数错误，应该为0或1，收到的参数是" + status, null);
				return response;
			}
			
			
			reMap.put("result", "世界boss当前状态已为开启：" + ServerSwitch.isOpenWorldBoss());
			response.setStatus(0);
			response.setCount(1);
			
		} catch (Exception e) {
			recordResult(response, reMap, "fail", "逻辑处理异常，查看GM日志错误信息", e);
		}
		return response;
	}

	private void recordResult(GmResponse response, HashMap<String, Object> resultMap, String key, String reason, Exception e) {
		if (e != null) {
			GmLog.error("屏蔽开启或打开RemoteMessage异常", e);
		}
		resultMap.put(key, reason);
		response.setCount(0);
		response.setStatus(1);
	}
	
}
