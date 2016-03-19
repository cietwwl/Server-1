package com.gm.task;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.common.playerFilter.PlayerFilterCondition;
import com.gm.util.GmUtils;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailData;

public class GmEmailHelper{

	private static EmailData sendToAllEmailData;
	
	private static List<PlayerFilterCondition> conditionList;

	public static EmailData getSendToAllEmailData() {
		return sendToAllEmailData;
	}

	public static void setSendToAllEmailData(EmailData sendToAllEmailData) {
		GmEmailHelper.sendToAllEmailData = sendToAllEmailData;
	}

	public static List<PlayerFilterCondition> getConditionList() {
		return conditionList;
	}

	public static void setConditionList(List<PlayerFilterCondition> conditionList) {
		GmEmailHelper.conditionList = conditionList;
	}

	public static EmailData getEmailData(Map<String, Object> args){
		EmailData emailData = new EmailData();
		emailData.setSender("GM");
		if (args.get("taskId") != null) {
			long taskId = Long.parseLong(args.get("taskId").toString());
			emailData.setTaskId(taskId);
		}
		String title = GmUtils.parseString(args, "title");
		emailData.setTitle(title);
		String content = GmUtils.parseString(args, "content");
		emailData.setContent(content);
		
		String itemDictJson = GmUtils.parseString(args, "itemDict");
		if (StringUtils.isNotBlank(itemDictJson)) {
			List<GmItem> itemList = parseItemList(itemDictJson);
			StringBuilder attachment = new StringBuilder();
			for (GmItem gmItem : itemList) {
				attachment.append(gmItem.getCode()).append("~").append(gmItem.getAmount()).append(",");
			}
			emailData.setEmailAttachment(attachment.toString());
			emailData.setDeleteType(EEmailDeleteType.GET_DELETE);
		}
		
		int coolTimeInMinute = GmUtils.parseInt(args, "coolTime");
		final long minuteSpanInMilli = 60*1000L;
		long currentTimeMillis = System.currentTimeMillis();
		
		emailData.setCoolTime(currentTimeMillis+coolTimeInMinute*minuteSpanInMilli);
		int expireTimeInday = GmUtils.parseInt(args, "expireTime");
		emailData.setDeleteType(EEmailDeleteType.DELAY_TIME);
		emailData.setExpireTime(expireTimeInday);
		
		final int daySpanInSecond = 24*60*60;
		if (expireTimeInday != 0) {
			emailData.setDelayTime(expireTimeInday * daySpanInSecond);
		}
		if (args.get("beginTime") != null && args.get("endTime") != null) {
			long beginTimeInSecond = (Long) args.get("beginTime");
			long endTimeInSecond = (Long) args.get("endTime");
			emailData.setBeginTime(currentTimeMillis + beginTimeInSecond * 1000);
			emailData.setEndTime(currentTimeMillis + endTimeInSecond * 1000);
		}
		return emailData;
		
	}
	
	private static List<GmItem> parseItemList(String itemDictJson){
		return FastJsonUtil.deserializeList(itemDictJson, GmItem.class);
	}
	
	public static List<PlayerFilterCondition> parseCondition(String conditionListJson){
		return FastJsonUtil.deserializeList(conditionListJson, PlayerFilterCondition.class);
	}

	public static void main(String[] args){
		String a = "[{\"maxValue\":30,\"minValue\":1,\"type\":1}]";
		List<PlayerFilterCondition> parseCondition = parseCondition(a);
		for (PlayerFilterCondition playerFilterCondition : parseCondition) {
			System.out.println(playerFilterCondition.getMaxValue());
			System.out.println(playerFilterCondition.getMinValue());
			System.out.println(playerFilterCondition.getType());
		}
	}
}
