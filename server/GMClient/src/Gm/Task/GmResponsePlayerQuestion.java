package Gm.Task;

import java.util.HashMap;
import java.util.Map;

import Gm.AGMHandler;
import Gm.GMManager;
import Gm.GmRequest;

public class GmResponsePlayerQuestion extends AGMHandler{

	@Override
	public GmRequest createGmRequest() {
		// TODO Auto-generated method stub

		this.opType = 20019;
		this.account = GMManager.ACCOUNT_VALUE;
		this.password = GMManager.PASSWORD_VALUE;
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("Id", "1");
		args.put("serverId", "7");
		args.put("roleId", "100100001258");
		args.put("content", "test ttest test testtst");
		args.put("replyTime", "2016-05-28 16:48");
		
		GmRequest gmRequest = new GmRequest();
		gmRequest.setOpType(opType);
		gmRequest.setAccount(account);
		gmRequest.setPassword(password);
		gmRequest.setArgs(args);
		return gmRequest;

	}

}
