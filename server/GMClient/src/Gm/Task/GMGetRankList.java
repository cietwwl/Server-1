package Gm.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Gm.AGMHandler;
import Gm.GMManager;
import Gm.GmRequest;

public class GMGetRankList extends AGMHandler{

	public final static Map<Integer, String> RankTypeMap = new HashMap<Integer, String>();
	
	static{
		RankTypeMap.put(1, "FIGHTING");      	//战力
		RankTypeMap.put(2, "LEVEL");         	//等级
		RankTypeMap.put(3, "PRIEST_ARENA");  	//牧师
		RankTypeMap.put(4, "SWORDMAN_ARENA"); 	//剑士
		RankTypeMap.put(5, "WARRIOR_ARENA"); 	//力士
		RankTypeMap.put(6, "MAGICAN_ARENA"); 	//法师
		RankTypeMap.put(7, "GROUP");         	//帮派排行榜
	}
	private String rankType;
	
	public void setParams(String[] args) {
		if (args != null && args.length > 0) {
			rankType = args[0];
		}
	}
	
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
		if(rankType == null){
			rankType = "GROUP";
		}
		
		args.put("rankInfoType", rankType);
		args.put("offset", 1);
		args.put("limit", 100);
		
		GmRequest gmRequest = new GmRequest();
		gmRequest.setOpType(opType);
		gmRequest.setAccount(account);
		gmRequest.setPassword(password);
		gmRequest.setArgs(args);
		return gmRequest;
	}

}
