package Gm.Task;

import java.util.HashMap;
import java.util.Map;

import Gm.AGMHandler;
import Gm.GMManager;
import Gm.GmRequest;

public class GmViewEmailList extends AGMHandler{

	@Override
	public GmRequest createGmRequest() {
		// TODO Auto-generated method stub
				this.opType = 20040;
				this.account = GMManager.ACCOUNT_VALUE;
				this.password = GMManager.PASSWORD_VALUE;
				
				Map<String, Object> args = new HashMap<String, Object>();
//				args.put("taskId", 1);
				args.put("serverId", 4);
				
				GmRequest gmRequest = new GmRequest();
				gmRequest.setOpType(opType);
				gmRequest.setAccount(account);
				gmRequest.setPassword(password);
				gmRequest.setArgs(args);
				return gmRequest;
	}

}