package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.rw.fsutil.log.GmLog;
import com.rw.fsutil.remote.RemoteMessageService;
import com.rw.fsutil.remote.RemoteMessageServiceFactory;

public class GmRemoteMsgSenderState implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		response.addResult(resultMap);
		try {

			Map<String, Object> requestMap = request.getArgs();
			if (requestMap == null) {
				recordResult(response, resultMap, "fail", "Gm命令参数为null", null);
				return response;
			}

			Integer type = (Integer) requestMap.get("type");
			if (type == null) {
				recordResult(response, resultMap, "fail", "屏蔽开启或打开RemoteMessage缺少type参数", null);
				return response;
			}
			String status = (String) requestMap.get("status");
			if (status == null) {
				recordResult(response, resultMap, "fail", "屏蔽开启或打开RemoteMessage缺少status参数", null);
				return response;
			}
			boolean open;
			String lowerCaseStatus = status.toLowerCase();
			if (lowerCaseStatus.equals("open")) {
				open = true;
			} else if (lowerCaseStatus.equals("close")) {
				open = false;
			} else {
				recordResult(response, resultMap, "fail", "屏蔽开启或打开RemoteMessage缺少status参数不正确：" + status + ",应该为open或者close", null);
				return response;
			}
			RemoteMessageService service = RemoteMessageServiceFactory.getService(type);
			if (service == null) {
				recordResult(response, resultMap, "fail", "不存在该类型RemoteMessageService:" + type, null);
				return response;
			}
			boolean isDebugLogger = service.isDebugLogger();
			if (open == isDebugLogger) {
				recordResult(response, resultMap, "fail", "RemoteMessage debugLogger当前状态已为：" + open + ",无需设置", null);
				return response;
			}
			service.setDebugLogger(open);
			resultMap.put("result", "RemoteMessage debugLogger当前状态已为：" + service.isDebugLogger());
			response.setStatus(0);
			response.setCount(1);
			return response;
		} catch (Exception e) {
			recordResult(response, resultMap, "fail", "逻辑处理异常，查看GM日志错误信息", e);
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
