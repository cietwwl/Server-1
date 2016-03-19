package com.gm.task;

import java.util.List;
import java.util.Map;

import com.common.playerFilter.PlayerFilterCondition;
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
		long taskId = Long.parseLong(args.get("taskId").toString());
		emailData.setTaskId(taskId);
		String title = (String)args.get("title");
		emailData.setTitle(title);
		String content = (String)args.get("content");
		emailData.setContent(content);
		
		String itemDictJson =  (String)args.get("itemDict");
		List<GmItem> itemList = parseItemList(itemDictJson);
		StringBuilder attachment = new StringBuilder();
		for (GmItem gmItem : itemList) {
			attachment.append(gmItem.getCode()).append("~").append(gmItem.getAmount()).append(",");
		}
		emailData.setEmailAttachment(attachment.toString());
		
		int coolTimeInMinute = (Integer)args.get("coolTime");
		final long minuteSpanInMilli = 60*1000L;
		long currentTimeMillis = System.currentTimeMillis();
		
		emailData.setCoolTime(currentTimeMillis+coolTimeInMinute*minuteSpanInMilli);
		int expireTimeInday = (Integer)args.get("expireTime");
		emailData.setDeleteType(EEmailDeleteType.GET_DELETE);
		
		final int daySpanInSecond = 24*60*3600;
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
		return JsonUtil.readList(itemDictJson, GmItem.class);
	}
	
	public static List<PlayerFilterCondition> parseCondition(String conditionListJson){
		return JsonUtil.readList(conditionListJson, PlayerFilterCondition.class);
	}

}
