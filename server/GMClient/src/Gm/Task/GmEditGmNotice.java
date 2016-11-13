package Gm.Task;

import java.util.HashMap;
import java.util.Map;

import Gm.AGMHandler;
import Gm.GMManager;
import Gm.GmRequest;

public class GmEditGmNotice extends AGMHandler{

	@Override
	public GmRequest createGmRequest() {
		// TODO Auto-generated method stub
		this.opType = 20008;
		this.account = GMManager.ACCOUNT_VALUE;
		this.password = GMManager.PASSWORD_VALUE;

		Map<String, Object> args = new HashMap<String, Object>();
		args.put("noticeId", "2");
		args.put("title", "test");
		args.put("content", "加价代发地方案发发生的法师打发第三方案发都发商店放大法师发送的发生发打发斯蒂芬阿阿斯顿发");
		args.put("startTime", 1470976332);
		args.put("endTime", 1471976332);
		args.put("cycleInterval", 1);
		args.put("priority", 1);

		GmRequest gmRequest = new GmRequest();
		gmRequest.setOpType(opType);
		gmRequest.setAccount(account);
		gmRequest.setPassword(password);
		gmRequest.setArgs(args);
		return gmRequest;
	}

}
