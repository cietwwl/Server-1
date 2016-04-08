package Gm.Task;

import java.util.HashMap;
import java.util.Map;

import Gm.AGMHandler;
import Gm.GMManager;
import Gm.GmRequest;

public class GmEditGameNotice extends AGMHandler{

	@Override
	public GmRequest createGmRequest() {
		// TODO Auto-generated method stub
		this.opType = 20005;
		this.account = GMManager.ACCOUNT_VALUE;
		this.password = GMManager.PASSWORD_VALUE;

		Map<String, Object> args = new HashMap<String, Object>();
//		args.put("noticeId", "9");
		args.put("title", "testtesttesttestst");
		args.put("content", "一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十"
				+ "一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十"
				+ "一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十"
				+ "一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十"
				+ "一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十"
				+ "一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十"
				+ "一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十"
				+ "一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十"
				+ "一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十"
				+ "一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十"
				+ "一二三四五六七八九十一二三四五六七八九十");
		args.put("startTime", 1458144000);
		args.put("endTime", 1458748800);

		GmRequest gmRequest = new GmRequest();
		gmRequest.setOpType(opType);
		gmRequest.setAccount(account);
		gmRequest.setPassword(password);
		gmRequest.setArgs(args);
		return gmRequest;
	}

}
