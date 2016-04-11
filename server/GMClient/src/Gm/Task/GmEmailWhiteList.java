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
			
			emailData.setTitle("2016-03-25");
			emailData.setContent("!!!!@亲爱的玩家您好，本次删档测试不知不觉已然接近尾声，感谢大家对《斗战封神》的支持，以下为封测福利内容，请查收！欢迎大家加入我们的官方Q群182674039，和小伙伴一起继续交流吧！");
			List<GmItem> list = new ArrayList<GmItem>();
			GmItem i1 = new GmItem();
			i1.setAmount(10000);
			i1.setCode(2);
			i1.setType(1);
			list.add(i1);
//			GmItem i2 = new GmItem();
//			i2.setAmount(10000000);
//			i2.setCode(1);
//			i2.setType(1);
//			list.add(i2);
//			GmItem i3 = new GmItem();
//			i3.setAmount(30);
//			i3.setCode(805001);
//			i3.setType(1);
//			list.add(i3);
			
			emailData.setItemDict(JsonUtil.writeValue(list));
			emailData.setCoolTime(60);
			emailData.setExpireTime(9);
			List<GmMailCondition> listCondition = new ArrayList<GmMailCondition>();
			GmMailCondition condition = new GmMailCondition();
			condition.setType(1);
			condition.setMaxValue(50);
			condition.setMinValue(1);
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
