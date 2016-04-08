package Gm.Task;

import java.util.HashMap;
import java.util.Map;

import Gm.AGMHandler;
import Gm.GMManager;
import Gm.GmRequest;

public class GMGetRankList extends AGMHandler{

	@Override
	public GmRequest createGmRequest() {
		// TODO Auto-generated method stub
		this.opType = 77777;
		this.account = GMManager.ACCOUNT_VALUE;
		this.password = GMManager.PASSWORD_VALUE;
		
		Map<String, Object> args = new HashMap<String, Object>();

		
//			FIGHTING,
//			LEVEL, 
//			PRIEST_ARENA, 
//			SWORDMAN_ARENA,
//			WARRIOR_ARENA,
//			MAGICAN_ARENA,
//			GROUP
		
		args.put("rankInfoType", "GROUP");
		args.put("offset", 1);
		args.put("limit", 10);
		
		GmRequest gmRequest = new GmRequest();
		gmRequest.setOpType(opType);
		gmRequest.setAccount(account);
		gmRequest.setPassword(password);
		gmRequest.setArgs(args);
		return gmRequest;
	}

}
