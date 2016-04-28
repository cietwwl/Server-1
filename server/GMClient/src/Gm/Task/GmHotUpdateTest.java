package Gm.Task;

import java.util.HashMap;
import java.util.Map;

import Gm.AGMHandler;
import Gm.GMManager;
import Gm.GmRequest;

public class GmHotUpdateTest extends AGMHandler{

	@Override
	public GmRequest createGmRequest() {
		this.opType = 1011;
		this.account = GMManager.ACCOUNT_VALUE;
		this.password = GMManager.PASSWORD_VALUE;
		
		Map<String, Object> args = new HashMap<String, Object>();
//		args.put("path", "com.gm.fix.AddRedPoint");
		args.put("path", "com.RemoveStoreRedPoint");
		GmRequest gmRequest = new GmRequest();
		gmRequest.setOpType(opType);
		gmRequest.setAccount(account);
		gmRequest.setPassword(password);
		gmRequest.setArgs(args);
		return gmRequest;
	}

}
