package com.rw.service.worship;

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
		if(!WorshipMgr.getInstance().isWorship(player.getUserId())){//不可膜拜
			response.setResultType(EWorshipResultType.FAIL);
			return response.build().toByteString();
		}
		CfgWorshipReward cfg = CfgWorshipRewardHelper.getInstance().getWorshipRewardCfg(WORSHIPPERS_KEY);
		if(cfg == null){
			response.setResultType(EWorshipResultType.FAIL);
			return response.build().toByteString();
		}
		String reward = cfg.getRewardType() + "~" + cfg.getRewardCount();
		WorshipItemData rewardData = WorshipUtils.getRandomRewardData(cfg.getRandomScheme());		
		if(rewardData != null && !StringUtils.isEmpty(rewardData.getItemId())){
			reward += "," + rewardData.getItemId() + "~" + rewardData.getCount();
		}else{
			GameLog.debug("");
		}
		
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
		response.setCanWorship(WorshipMgr.getInstance().isWorship(player.getUserId()));
		pushWorshipList(request.getWorshipCareer(), player);
		return response.build().toByteString();
	}
	
	/**推送膜拜者列表*/
	public void pushWorshipList(int career, Player player){
		WorshipResponse.Builder response = WorshipResponse.newBuilder();
		response.setRequestType(EWorshipRequestType.PUSH_WORSHIP_LIST);
		response.setResultType(EWorshipResultType.SUCCESS);
		response.setWorshipCareer(career);
		response.addAllWorshipList(WorshipMgr.getInstance().getWorshipList(ECareer.valueOf(career)));
		player.SendMsg(Command.MSG_Worship, response.build().toByteString());
	}
}
