package Gm.Task;

import java.util.HashMap;
import java.util.Map;

import Gm.GMManager;
import Gm.GmRequest;
import Gm.AGMHandler;

public class GMAddCoin extends AGMHandler{

	@Override
	public GmRequest createGmRequest() {
		// TODO Auto-generated method stub
		this.opType = 20028;
		this.account = GMManager.ACCOUNT_VALUE;
		this.password = GMManager.PASSWORD_VALUE;
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("roleId", "100100001496");
		args.put("value", Long.parseLong("-2"));
		
		GmRequest gmRequest = new GmRequest();
		gmRequest.setOpType(opType);
		gmRequest.setAccount(account);
		gmRequest.setPassword(password);
		gmRequest.setArgs(args);
		return gmRequest;
	}

}
