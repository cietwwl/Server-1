package Gm.Task;

import java.util.HashMap;
import java.util.Map;

import Gm.AGMHandler;
import Gm.GMManager;
import Gm.GmRequest;

public class GmEditLevel extends AGMHandler{

	@Override
	public GmRequest createGmRequest() {
		// TODO Auto-generated method stub
		this.opType = 20075;
		this.account = GMManager.ACCOUNT_VALUE;
		this.password = GMManager.PASSWORD_VALUE;

		Map<String, Object> args = new HashMap<String, Object>();
//		args.put("roleId", "100100002755");
		args.put("roleId", "100100000854");
		args.put("value", 1);

		GmRequest gmRequest = new GmRequest();
		gmRequest.setOpType(opType);
		gmRequest.setAccount(account);
		gmRequest.setPassword(password);
		gmRequest.setArgs(args);
		return gmRequest;
	}

}
