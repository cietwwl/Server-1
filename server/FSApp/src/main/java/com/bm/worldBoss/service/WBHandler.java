package com.bm.worldBoss.service;

import java.util.List;

import com.bm.worldBoss.WBMgr;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.rwproto.WorldBossProtos.CommonReqMsg;
import com.rwproto.WorldBossProtos.CommonRspMsg;
import com.rwproto.WorldBossProtos.FightBeginParam;
import com.rwproto.WorldBossProtos.FightBeginRep;
import com.rwproto.WorldBossProtos.FightEndParam;

public class WBHandler {
	
	private static WBHandler instance = new WBHandler();
	
	public static WBHandler getInstance(){
		return instance;
	}	

	public ByteString DoEnter(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());		
		
		response.setIsSuccess(true);	
				
		return response.build().toByteString();
	}

	public ByteString DoFightBegin(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		FightBeginParam fightBeginParam = commonReq.getFightBeginParam();
		List<String> heroIdsList = fightBeginParam.getHeroIdsList();	

		WBResult result = checkFightBegin(player, fightBeginParam);
		if(result.isSuccess()){
			
			ArmyInfo bossArmy = WBMgr.getInstance().getBossArmy();	
			
			ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(player.getUserId(), heroIdsList);		
			
			String bossJson = bossArmy.toJson();
			String armyJson = armyInfo.toJson();
			
			FightBeginRep beginRep = FightBeginRep.newBuilder().setBossArmy(bossJson).setSelfArmy(armyJson).build();	
			response.setFightBeginRep(beginRep);
			
		}
		response.setIsSuccess(result.isSuccess());
		response.setTipMsg(result.getReason());	
				
		return response.build().toByteString();
	}
	
	private WBResult checkFightBegin(Player player, FightBeginParam fightBeginParam){
		WBResult result = WBResult.newInstance(true);
		List<String> heroIdsList = fightBeginParam.getHeroIdsList();	
		if(heroIdsList.size() > 4){
			result.setSuccess(false);
			result.setReason("上阵人数不能大于5.");
		}
		return result;
	}

	public ByteString DoFightEnd(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		FightEndParam fightEndParam = commonReq.getFightEndParam();
	

		WBResult result = checkFightEnd(player, fightEndParam);
		if(result.isSuccess()){
			long totalHurt = fightEndParam.getTotalHurt();
			WBMgr.getInstance().decrHp(player,totalHurt);
			
		}
		response.setIsSuccess(result.isSuccess());
		response.setTipMsg(result.getReason());	
				
		return response.build().toByteString();
	}


	private WBResult checkFightEnd(Player player, FightEndParam fightEndParam){
		WBResult result = WBResult.newInstance(true);
	
		return result;
	}


}
