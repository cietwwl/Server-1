package Gm.Task;

import java.util.HashMap;
import java.util.Map;

import Gm.AGMHandler;
import Gm.GMManager;
import Gm.GmRequest;

public class GmViewGameNotice extends AGMHandler {

	@Override
	public GmRequest createGmRequest() {
		// TODO Auto-generated method stub
		this.opType = 20006;
		this.account = GMManager.ACCOUNT_VALUE;
		this.password = GMManager.PASSWORD_VALUE;

		Map<String, Object> args = new HashMap<String, Object>();
		args.put("serverId", "0");

		GmRequest gmRequest = new GmRequest();
		gmRequest.setOpType(opType);
		gmRequest.setAccount(account);
		gmRequest.setPassword(password);
		gmRequest.setArgs(args);
		return gmRequest;
	}

}
