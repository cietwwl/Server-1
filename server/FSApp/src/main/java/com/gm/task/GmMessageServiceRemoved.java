package com.gm.task;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.ProtocolMessageEnum;
import com.rw.controler.FsNettyControler;
import com.rw.fsutil.log.GmLog;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.FsService;
import com.rwproto.MsgDef.Command;

public class GmMessageServiceRemoved implements IGmTask {

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

			String commandName = (String) requestMap.get("command");

			Command command = Command.valueOf(commandName);
			if (command == null) {
				recordResult(response, resultMap, "fail", "找不到Gm命令需要屏蔽的模块名称：" + commandName, null);
				return response;
			}

			Field field = FsNettyControler.class.getDeclaredField("commandMap");
			field.setAccessible(true);

			FsNettyControler controler = SpringContextUtil.getBean("fsNettyControler");
			synchronized (controler) {
				Map<Command, FsService<GeneratedMessage, ProtocolMessageEnum>> commandMap = (Map<Command, FsService<GeneratedMessage, ProtocolMessageEnum>>) field.get(controler);
				Map<Command, FsService<GeneratedMessage, ProtocolMessageEnum>> copy = new HashMap<Command, FsService<GeneratedMessage, ProtocolMessageEnum>>(commandMap);
				FsService<GeneratedMessage, ProtocolMessageEnum> service = copy.remove(command);
				if (service == null) {
					recordResult(response, resultMap, "fail", "该模块不存在或已被屏蔽：" + commandName, null);
					return response;
				}
				field.set(controler, copy);
				GmLog.info("移除消息处理：" + commandName + "," + service);
			}
			resultMap.put("result", "修改成功");
			response.setStatus(0);
			response.setCount(1);
			return response;
		} catch (SecurityException e) {
			recordResult(response, resultMap, "fail", "无法执行", e);
		} catch (NoSuchFieldException e) {
			recordResult(response, resultMap, "fail", "属性名字被修改，需通过热更新来屏蔽", e);
		} catch (IllegalArgumentException e) {
			recordResult(response, resultMap, "fail", "逻辑传参异常", e);
		} catch (IllegalAccessException e) {
			recordResult(response, resultMap, "fail", "逻辑传参异常", e);
		} catch (Exception e) {
			recordResult(response, resultMap, "fail", "逻辑处理异常，查看GM日志错误信息", e);
		}

		return null;
	}

	private void recordResult(GmResponse response, HashMap<String, Object> resultMap, String key, String reason, Exception e) {
		if (e != null) {
			GmLog.error("屏蔽MessageService入口异常", e);
		}
		resultMap.put(key, reason);
		response.setCount(0);
		response.setStatus(1);
	}
}
