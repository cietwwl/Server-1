package com.rw.service.worship;

import java.util.List;

import com.alibaba.druid.util.StringUtils;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.WorshipMgr;
import com.rwbase.common.enu.ECareer;
import com.rwbase.dao.worship.CfgWorshipRewardHelper;
import com.rwbase.dao.worship.WorshipUtils;
import com.rwbase.dao.worship.pojo.CfgWorshipReward;
import com.rwbase.dao.worship.pojo.WorshipItemData;
import com.rwproto.MsgDef.Command;
import com.rwproto.WorshipServiceProtos.EWorshipRequestType;
import com.rwproto.WorshipServiceProtos.EWorshipResultType;
import com.rwproto.WorshipServiceProtos.WorshipInfo;
import com.rwproto.WorshipServiceProtos.WorshipRequest;
import com.rwproto.WorshipServiceProtos.WorshipResponse;

public class WorshipHandler {
private static WorshipHandler instance = new WorshipHandler();

	public static final String WORSHIPPERS_KEY = "worshippers";
	public static final String BY_WORSHIPPERS_KEY = "byWorshippers";
	
	private WorshipHandler(){
		
	}
	
	public static WorshipHandler getInstance(){
		return instance;
	}
	
	/**膜拜*/
	public ByteString worship(WorshipRequest request, Player player){
		WorshipResponse.Builder response = WorshipResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		if(!WorshipMgr.getInstance().isWorship(player)){//不可膜拜
			response.setResultType(EWorshipResultType.FAIL);
			return response.build().toByteString();
		}
		CfgWorshipReward cfg = CfgWorshipRewardHelper.getInstance().getWorshipRewardCfg();
		if(cfg == null){
			response.setResultType(EWorshipResultType.FAIL);
			return response.build().toByteString();
		}
		String reward = cfg.getRewardStr();
		WorshipItemData rewardData = WorshipUtils.getWorshipDataFromStr(reward);
//		WorshipItemData rewardData = WorshipUtils.getRandomRewardData(cfg.getRandomScheme());		
//		if(rewardData != null && !StringUtils.isEmpty(rewardData.getItemId())){
//			reward += "," + rewardData.getItemId() + "~" + rewardData.getCount();
//		}else{
//			GameLog.debug("");
//		}
		//设置膜拜时间
		player.getUserGameDataMgr().setLastWorshipTime(System.currentTimeMillis());
		player.getItemBagMgr().addItemByPrizeStr(reward);
		
		WorshipMgr.getInstance().addWorshippers(ECareer.valueOf(request.getWorshipCareer()), player, rewardData);
		
		response.setResultType(EWorshipResultType.SUCCESS);
		response.setRewardList(reward);
		response.setCanWorship(false);
		pushWorshipList(request.getWorshipCareer(), player);
		return response.build().toByteString();
	}
	
	/**查看膜拜状态*/
	public ByteString getWorshipState(WorshipRequest request, Player player){
		WorshipResponse.Builder response = WorshipResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.setResultType(EWorshipResultType.SUCCESS);
		response.setCanWorship(WorshipMgr.getInstance().isWorship(player));
		pushWorshipList(request.getWorshipCareer(), player);
		return response.build().toByteString();
	}
	
	/**推送膜拜者列表*/
	public void pushWorshipList(int career, Player player){
		WorshipResponse.Builder response = WorshipResponse.newBuilder();
		response.setRequestType(EWorshipRequestType.PUSH_WORSHIP_LIST);
		response.setResultType(EWorshipResultType.SUCCESS);
		response.setWorshipCareer(career);
		List<WorshipInfo> worshipList = WorshipMgr.getInstance().getWorshipList(ECareer.valueOf(career));
		int num = Math.min(WorshipUtils.UpperWorshipNum, worshipList.size());
		for (int i = 0; i < num; i++) {//修改为20个上限
			response.addWorshipList(worshipList.get(i));
		}
//		response.addAllWorshipList();
		player.SendMsg(Command.MSG_Worship, response.build().toByteString());
		System.out.println("puch worship list, list size:" + num);
	}
}
