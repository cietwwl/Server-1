package Gm.Task;

import java.util.HashMap;
import java.util.Map;

import Gm.AGMHandler;
import Gm.GMManager;
import Gm.GmRequest;

public class GmFindHeroNormalAndExpEquipList extends AGMHandler{

	@Override
	public GmRequest createGmRequest() {
		this.opType = 20061;
		this.account = GMManager.ACCOUNT_VALUE;
		this.password = GMManager.PASSWORD_VALUE;	

		
		Map<String, Object> args = new HashMap<String, Object>();
//		args.put("roleId", "100100002755");
		args.put("roleId", "100100002757");
		
		
		GmRequest gmRequest = new GmRequest();
		gmRequest.setOpType(opType);
		gmRequest.setAccount(account);
		gmRequest.setPassword(password);
		gmRequest.setArgs(args);
		return gmRequest;
	}

}
