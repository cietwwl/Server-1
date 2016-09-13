package com.bm.worldBoss.service;

import java.util.List;

import com.bm.worldBoss.WBMgr;
import com.bm.worldBoss.WBUserMgr;
import com.bm.worldBoss.cfg.WBAwardCfg;
import com.bm.worldBoss.cfg.WBAwardCfgDAO;
import com.bm.worldBoss.cfg.WBBuyBuffCfg;
import com.bm.worldBoss.cfg.WBBuyBuffCfgDAO;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwproto.WorldBossProtos.BuyBuffParam;
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


	public ByteString getSuccessRep(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());		
		
		response.setIsSuccess(true);	
				
		return response.build().toByteString();
	}

	public ByteString doFightBegin(Player player, CommonReqMsg commonReq) {
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
		WBResult result = checkBoss();
		if(result.isSuccess()){
			result = checkUser(player);
		}
		if(result.isSuccess()){			
			List<String> heroIdsList = fightBeginParam.getHeroIdsList();	
			if(heroIdsList.size() > 4){
				result.setSuccess(false);
				result.setReason("上阵人数不能大于5.");
			}
		}
		return result;
	}

	public ByteString doFightEnd(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		FightEndParam fightEndParam = commonReq.getFightEndParam();	

		WBResult result = checkFightEnd(player, fightEndParam);
		if(result.isSuccess()){
			long totalHurt = fightEndParam.getTotalHurt();
			int awardCoin = addAwardCoin(player, totalHurt);
			boolean success = WBMgr.getInstance().decrHp(player,totalHurt);
			if(success){
				WBUserMgr.getInstance().fightEndUpdate(player, totalHurt, awardCoin);
			}else{
				result.setSuccess(false);
				result.setReason("世界boss已被击杀。");
			}
			
			
		}
		response.setIsSuccess(result.isSuccess());
		response.setTipMsg(result.getReason());	
				
		return response.build().toByteString();
	}


	private int addAwardCoin(Player player, long totalHurt) {
		int level = player.getLevel();
		WBAwardCfg awardCfg = WBAwardCfgDAO.getInstance().getCfgById(String.valueOf(level));
		int awardCoin = 0;
		if(awardCfg!=null){
			float factor = awardCfg.getFactor();
			awardCoin = (int)(factor*totalHurt);
		}

		boolean success = WBHelper.addCoin(player, awardCoin);
		
		return success?awardCoin:0;
	}


	private WBResult checkFightEnd(Player player, FightEndParam fightEndParam){
		WBResult result = checkBoss();
		if(result.isSuccess()){
			result = checkUser(player);
		}
		
		return result;
	}


	private WBResult checkBoss() {
		WBResult result = WBResult.newInstance(true);
		if(WBMgr.getInstance().isBossDie()){
			result.setSuccess(false);
			result.setReason("世界boss已被击杀。");				
		}else if(!WBMgr.getInstance().isAfterBossStartTime()){
			result.setSuccess(false);
			result.setReason("世界boss尚未抵达。");			
		}else if(!WBMgr.getInstance().isBeginBossEndTime()){
			result.setSuccess(false);
			result.setReason("世界boss已离开。");			
		}
		return result;
	}
	
	private WBResult checkUser(Player player) {
		WBResult result = WBResult.newInstance(true);
		if(WBUserMgr.getInstance().isInCD(player)){
			result.setSuccess(false);
			result.setReason("间隔CD中，请等待。。。");				
		}
		return result;
	}
	

	public ByteString doBuyBuff(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		BuyBuffParam buyBuffParam = commonReq.getBuyBuffParam();
		String bufBuffCfgId = buyBuffParam.getCfgId();
		WBBuyBuffCfg buyBuffCfg = WBBuyBuffCfgDAO.getInstance().getCfgById(bufBuffCfgId);
		
		WBResult result = WBResult.newInstance(false);
		if(buyBuffCfg == null){
			result.setReason("配置不存在。");
		}else{
			eSpecialItemId costType = buyBuffCfg.getCostType();
			int costCount = buyBuffCfg.getCostCount();			
			result = WBHelper.takeCost(player, costType, costCount);
			if(result.isSuccess()){
				WBUserMgr.getInstance().addBuff(player, buyBuffCfg.getId());
			}
			
		}

		response.setIsSuccess(result.isSuccess());
		response.setTipMsg(result.getReason());	
				
		return response.build().toByteString();
	}

	public ByteString doBuyCD(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		WBResult result = WBHelper.takeCost(player, eSpecialItemId.Gold, getBuyCDCost());
		if(result.isSuccess()){
			WBUserMgr.getInstance().cleanCD(player);
		}

		response.setIsSuccess(result.isSuccess());
		response.setTipMsg(result.getReason());	
				
		return response.build().toByteString();
	}
	
	private int getBuyCDCost(){
		return 30;
	}


}
