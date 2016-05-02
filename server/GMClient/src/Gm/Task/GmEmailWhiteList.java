package Gm.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Gm.AGMHandler;
import Gm.GMManager;
import Gm.GmRequest;
import Gm.Task.Params.EmailData;
import Gm.Task.Params.GmItem;
import Gm.Task.Params.GmMailCondition;
import Json.JSONArray;
import Json.JsonUtil;

public class GmEmailWhiteList extends AGMHandler{

	@Override
	public GmRequest createGmRequest() {
		// TODO Auto-generated method stub
		Map<String, Object> args = new HashMap<String, Object>();
		try {
			this.opType = 20014;
			this.account = GMManager.ACCOUNT_VALUE;
			this.password = GMManager.PASSWORD_VALUE;

			EmailData emailData = new EmailData();
			//emailData.setTaskId(1);
			emailData.setTitle("停服更新奖励");
			emailData.setContent("亲爱的各位队长：\r\n        近期神秘商人小哥的装备手抖贱卖了错误的价格，让正经刷关冲刺榜首的各位队长大大悲愤莫名，友谊的小船说翻就翻。特此发放安抚奖励，鼓励各位不要气馁！\r\n        小媚儿将不离不弃陪伴着各位队长大大们！冲刺加油！\r\n\r\n小媚儿");
			List<GmItem> list = new ArrayList<GmItem>();
			GmItem i1 = new GmItem();
			i1.setAmount(200000);
			i1.setCode(1);
			i1.setType(1);
			list.add(i1);
			GmItem i2 = new GmItem();
			i2.setAmount(1000);
			i2.setCode(2);
			i2.setType(1);
			list.add(i2);
//			GmItem i3 = new GmItem();
//			i3.setAmount(30);
//			i3.setCode(805001);
//			i3.setType(1);
//			list.add(i3);
			
			emailData.setItemDict(JsonUtil.writeValue(list));
			emailData.setCoolTime(0);
			emailData.setExpireTime(7);
			List<GmMailCondition> listCondition = new ArrayList<GmMailCondition>();
			GmMailCondition condition = new GmMailCondition();
			condition.setType(2);
			condition.setMaxValue(1461254400);
			condition.setMinValue(1460995200);
			listCondition.add(condition);
			
			emailData.setConditionList(JsonUtil.writeValue(listCondition));
			
			String writeValue = JsonUtil.writeValue(emailData);
			args = JsonUtil.readToMap(writeValue, Object.class);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		GmRequest gmRequest = new GmRequest();
		gmRequest.setOpType(opType);
		gmRequest.setAccount(account);
		gmRequest.setPassword(password);
		gmRequest.setArgs(args);
		return gmRequest;
	}

}
