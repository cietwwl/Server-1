package com.rwbase.dao.group;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.log.GameLog;
import com.rwbase.dao.group.pojo.cfg.GroupBaseConfigTemplate;
import com.rwbase.dao.group.pojo.cfg.dao.GroupConfigCfgDAO;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

/*
 * @author HC
 * @date 2016年1月26日 下午5:24:40
 * @Description 检测帮派解散的数据时效
 */
public class GroupCheckDismissTask {
	private static ObjectMapper MAPPER = new ObjectMapper();
	/**
	 * 解散帮派的列表
	 */
	private static ConcurrentHashMap<String, Long> dismissGroupMap;

	static {
		dismissGroupMap = new ConcurrentHashMap<String, Long>();
	}

	/**
	 * 检查离线帮派数据
	 */
	public static void check() {
		// 如果没有任何的帮派数据就返回
		if (dismissGroupMap == null || dismissGroupMap.isEmpty()) {
			return;
		}

		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			return;
		}

		long dismissCoolingTime = TimeUnit.SECONDS.toMillis(gbct.getDismissCoolingTime());
		long now = System.currentTimeMillis();

		boolean hasRemove = false;
		// 解散帮派的数据
		Enumeration<String> keys = dismissGroupMap.keys();
		while (keys.hasMoreElements()) {
			String groupId = keys.nextElement();// 帮派Id
			Long hasValue = dismissGroupMap.get(groupId);
			if (hasValue == null) {
				continue;
			}

			long dismissTime = hasValue.longValue();// 请求解散的时间

			// 超出了冷却时间，就要解散帮派
			if (now - dismissTime >= dismissCoolingTime) {
				GameLog.info("帮派解散的时效", groupId, "已经完成了解散工作", null);
				GroupBM.dismiss(groupId);
				// 移除内存数据
				dismissGroupMap.remove(groupId);
				hasRemove = true;
			}
		}

		// 有改变
		if (hasRemove) {
			updateDismissInfoAttribute();
		}
	}

	/**
	 * 初始化数据
	 */
	public static void initDismissGroupInfo() {
		String json = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.GROUP_DISMISS);
		if (StringUtils.isEmpty(json)) {
			return;
		}

		MapType type = TypeFactory.defaultInstance().constructMapType(ConcurrentHashMap.class, String.class, Long.class);
		try {
			dismissGroupMap = MAPPER.readValue(json, type);
			// 起服后直接检查一次
			check();
		} catch (JsonParseException e) {
			GameLog.error("解散帮派处理", "系统检查解散倒计时时效", "json转换对象出现了JsonParseException异常", e);
		} catch (JsonMappingException e) {
			GameLog.error("解散帮派处理", "系统检查解散倒计时时效", "json转换对象出现了JsonMappingException异常", e);
		} catch (IOException e) {
			GameLog.error("解散帮派处理", "系统检查解散倒计时时效", "json转换对象出现了IOException异常", e);
		}
	}

	/**
	 * 解散帮派的处理
	 * 
	 * @param groupId
	 * @param now
	 */
	public static void addDismissGroupInfo(String groupId, long now) {
		if (dismissGroupMap.containsKey(groupId)) {
			return;
		}

		dismissGroupMap.put(groupId, now);

		// 通知到GameWorld刷新数据
		updateDismissInfoAttribute();

		// 记录一个Log
		GameLog.info("帮派解散的时效", groupId, "新增加了当前这个帮派的解散任务", null);
	}

	/**
	 * 取消帮派解散的处理
	 * 
	 * @param groupId
	 */
	public static void removeDismissGroupInfo(String groupId) {
		dismissGroupMap.remove(groupId);

		// 通知到GameWorld刷新数据
		updateDismissInfoAttribute();

		// 记录一个Log
		GameLog.info("帮派解散的时效", groupId, "减少了当前这个帮派的解散任务", null);
	}

	/**
	 * 更新解散帮派的数据
	 */
	private static void updateDismissInfoAttribute() {
		// 通知到GameWorld刷新数据
		try {
			String json = MAPPER.writeValueAsString(dismissGroupMap);
			GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.GROUP_DISMISS, json);
		} catch (JsonGenerationException e) {
			GameLog.error("解散帮派处理", "系统检查解散倒计时时效", "转换json出现了JsonGenerationException异常", e);
		} catch (JsonMappingException e) {
			GameLog.error("解散帮派处理", "系统检查解散倒计时时效", "转换json出现了JsonMappingException异常", e);
		} catch (IOException e) {
			GameLog.error("解散帮派处理", "系统检查解散倒计时时效", "转换json出现了IOException异常", e);
		}
	}
}