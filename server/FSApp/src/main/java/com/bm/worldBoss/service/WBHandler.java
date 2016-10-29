package com.bm.worldBoss.service;

import java.util.List;

import com.bm.worldBoss.WBMgr;
import com.bm.worldBoss.WBOnFightMgr;
import com.bm.worldBoss.WBUserMgr;
import com.bm.worldBoss.cfg.WBBuyBuffCfg;
import com.bm.worldBoss.cfg.WBBuyBuffCfgDAO;
import com.bm.worldBoss.cfg.WBSettingCfg;
import com.bm.worldBoss.cfg.WBSettingCfgDAO;
import com.bm.worldBoss.data.WBState;
import com.bm.worldBoss.data.WBUserData;
import com.bm.worldBoss.data.WBUserDataHolder;
import com.bm.worldBoss.state.WBStateFSM;
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
import com.rwproto.WorldBossProtos.FightUpdateParam;

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
		WBUserMgr.getInstance().resetUserDataIfNeed(player);
		
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		FightBeginParam fightBeginParam = commonReq.getFightBeginParam();
		List<String> heroIdsList = fightBeginParam.getHeroIdsList();	

		WBResult result = checkFightBegin(player, fightBeginParam);
		if(result.isSuccess()){
			//更新用户cd
			WBUserMgr.getInstance().fightBeginUpdate(player);
			
			ArmyInfo bossArmy = WBMgr.getInstance().getBossArmy();	
			
			ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(player.getUserId(), heroIdsList);		
			
			String bossJson = bossArmy.toJson();
			String armyJson = armyInfo.toJson();
			
			FightBeginRep beginRep = FightBeginRep.newBuilder().setBossArmy(bossJson).setSelfArmy(armyJson).build();	
			response.setFightBeginRep(beginRep);
			WBOnFightMgr.getInstance().enter(player.getUserId());
			
		}
		response.setIsSuccess(result.isSuccess());
		if(result.getReason() != null)
		response.setTipMsg(result.getReason());	
				
		return response.build().toByteString();
	}
	
	private WBResult checkFightBegin(Player player, FightBeginParam fightBeginParam){
		WBResult result = checkBoss();
		if(result.isSuccess()){
			result = checkCD(player);
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

	public ByteString doFightUpdate(Player player, CommonReqMsg commonReq) {
		WBUserMgr.getInstance().resetUserDataIfNeed(player);
		
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		FightUpdateParam fightParam = commonReq.getFightUpdateParam();	
		
		WBResult result = checkFightEnd(player);
		if(result.isSuccess()){
			long updateHurt = fightParam.getHurt();
			boolean success = WBMgr.getInstance().decrHp(player,updateHurt);
			if(success){				
				WBUserMgr.getInstance().fightUpdate(player, updateHurt);
				WBOnFightMgr.getInstance().enter(player.getUserId());
			}else{
				result.setSuccess(false);
				result.setReason("世界boss已被击杀。");
			}
			
		}
		response.setIsSuccess(result.isSuccess());
		if(result.getReason() != null)
			response.setTipMsg(result.getReason());	
		
		return response.build().toByteString();
	}
	public ByteString doFightEnd(Player player, CommonReqMsg commonReq) {
		WBUserMgr.getInstance().resetUserDataIfNeed(player);
		
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		FightUpdateParam fightParam = commonReq.getFightUpdateParam();;	

		WBResult result = checkFightEnd(player);
		if(result.isSuccess()){
			long updateHurt = fightParam.getHurt();
			boolean success = WBMgr.getInstance().decrHp(player,updateHurt);
			if(success){				
				WBUserMgr.getInstance().fightEndUpdate(player, updateHurt);
			}else{
				result.setSuccess(false);
				result.setReason("世界boss已被击杀。");
			}
			
		}
		
		WBOnFightMgr.getInstance().leave(player.getUserId());
		
		response.setIsSuccess(result.isSuccess());
		if(result.getReason() != null)
		response.setTipMsg(result.getReason());	
				
		return response.build().toByteString();
	}





	private WBResult checkFightEnd(Player player){
		WBResult result = checkBoss();
		
		return result;
	}


	private WBResult checkBoss() {
		WBResult result = WBResult.newInstance(true);
		WBState state = WBStateFSM.getInstance().getState();
		if(state!=WBState.FightStart){
			result.setSuccess(false);
			result.setReason("世界boss尚未开始。");	
		}else if(WBMgr.getInstance().isBossDie()){
			result.setSuccess(false);
			result.setReason("世界boss已被击杀。");				
		}else if(!WBMgr.getInstance().isAfterBossStartTime()){
			result.setSuccess(false);
			result.setReason("世界boss尚未抵达。");			
		}else if(!WBMgr.getInstance().isBeforeBossEndTime()){
			result.setSuccess(false);
			result.setReason("世界boss已离开。");			
		}
		return result;
	}
	
	private WBResult checkCD(Player player) {
		WBResult result = WBResult.newInstance(true);
		if(WBUserMgr.getInstance().isInCD(player)){
			result.setSuccess(false);
			result.setReason("间隔CD中，请等待。。。");				
		}
		return result;
	}
	
	

	public ByteString doBuyBuff(Player player, CommonReqMsg commonReq) {
		WBUserMgr.getInstance().resetUserDataIfNeed(player);
		
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		BuyBuffParam buyBuffParam = commonReq.getBuyBuffParam();
		String bufBuffCfgId = buyBuffParam.getCfgId();
		
		WBResult result = checkBoss();
		if(result.isSuccess()){
			result = checkBuyBuff(player);
			if(result.isSuccess()){
				WBBuyBuffCfg buyBuffCfg = WBBuyBuffCfgDAO.getInstance().getCfgById(bufBuffCfgId);
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
			}
		}

		response.setIsSuccess(result.isSuccess());
		response.setTipMsg(result.getReason());	
				
		return response.build().toByteString();
	}

	private WBResult checkBuyBuff(Player player) {
		WBResult r = new WBResult();
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());
		WBSettingCfg setting = WBSettingCfgDAO.getInstance().getCfg();
		if(wbUserData.getBuffCfgIdList().size() >= setting.getBuyBuffLimit()){
			r.setSuccess(false);
			r.setReason("已经达到可鼓舞次数上限!");
		}else{
			r.setSuccess(true);
		}
		return r;
	}


	public ByteString doBuyCD(Player player, CommonReqMsg commonReq) {
		WBUserMgr.getInstance().resetUserDataIfNeed(player);
		
		
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		WBResult result = checkBoss();
		if(result.isSuccess()){
			result = checkUser(player);
		}
		if(result.isSuccess()){			
			result = WBHelper.takeCost(player, eSpecialItemId.Gold, getBuyCDCost());
			if(result.isSuccess()){
				WBUserMgr.getInstance().cleanCD(player);
			}
		}

		response.setIsSuccess(result.isSuccess());
		response.setTipMsg(result.getReason());	
				
		return response.build().toByteString();
	}
	
	private WBResult checkUser(Player player){
		WBResult result = WBResult.newInstance(true);
		if(!WBUserMgr.getInstance().canBuyCd(player)){
			result.setSuccess(false);
			result.setReason("购买次数已满，升级vip可以获得更多购买次数。");
		}
		return result;
	}
	
	private int getBuyCDCost(){
		int cdCost = WBSettingCfgDAO.getInstance().getCfg().getCdCost();		
		return cdCost;
	}


}
