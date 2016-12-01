package com.bm.worldBoss.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bm.worldBoss.WBMgr;
import com.bm.worldBoss.WBOnFightMgr;
import com.bm.worldBoss.WBUserMgr;
import com.bm.worldBoss.cfg.WBBuyBuffCfg;
import com.bm.worldBoss.cfg.WBBuyBuffCfgDAO;
import com.bm.worldBoss.cfg.WBSettingCfg;
import com.bm.worldBoss.cfg.WBSettingCfgDAO;
import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;
import com.bm.worldBoss.data.WBUserData;
import com.bm.worldBoss.data.WBUserDataDao;
import com.bm.worldBoss.data.WBUserDataHolder;
import com.bm.worldBoss.state.WBStateFSM;
import com.common.Utils;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.embattle.EmBattlePositionKey;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.rw.fsutil.common.Pair;
import com.rwbase.common.attribute.AttributeConst;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.WorldBossProtos.BuyBuffParam;
import com.rwproto.WorldBossProtos.CommonReqMsg;
import com.rwproto.WorldBossProtos.CommonRspMsg;
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
		
//		FightBeginParam fightBeginParam = commonReq.getFightBeginParam();
		EmbattlePositionInfo embattleInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(), eBattlePositionType.WorldBoss_VALUE, EmBattlePositionKey.posWorldBoss.getKey());
		
		Map<String, Integer> posMap = new HashMap<String, Integer>();
		List<String> heroIdsList = getHeroIdList(player, embattleInfo,posMap);	

		WBResult result = checkFightBegin(player);
		if(result.isSuccess()){
			//更新用户cd
			WBUserMgr.getInstance().fightBeginUpdate(player);
			
			ArmyInfo bossArmy = WBMgr.getInstance().getBossArmy();	
			
			ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(player.getUserId(), heroIdsList);
			armyInfo.setPos(posMap);

//			System.out.println("--------------------------role original attack value:" + armyInfo.getPlayer().getAttrData().getPhysiqueAttack());
			//这里还要加上角色的鼓舞buff
			int buffValue = getBuffValue(player.getUserId());
			if(buffValue != 0){
				ArmyInfoHelper.IncreaseArmyAttrack(armyInfo, buffValue);
			}
//			System.out.println("========================role increase attack value:" + armyInfo.getPlayer().getAttrData().getPhysiqueAttack());
			
			String bossJson = bossArmy.toJson();
			String armyJson = armyInfo.toJson();
			
			FightBeginRep beginRep = FightBeginRep.newBuilder().setBossArmy(bossJson).setSelfArmy(armyJson).build();	
			response.setFightBeginRep(beginRep);
			WBOnFightMgr.getInstance().enter(player.getUserId(), 0);
			
		}
		response.setIsSuccess(result.isSuccess());
		if(result.getReason() != null)
		response.setTipMsg(result.getReason());	
				
		return response.build().toByteString();
	}
	
	
	private int getBuffValue(String userID){
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(userID);
		List<String> list = wbUserData.getBuffCfgIdList();
		WBBuyBuffCfg cfg;
		int totalValue = 0;
		for (String cfgID : list) {
			cfg = WBBuyBuffCfgDAO.getInstance().getCfgById(cfgID);
			if(cfg != null){
				totalValue += cfg.getBuffValue();
			}
			
		}
		
		return totalValue;
	}
	
	private List<String> getHeroIdList(Player player, EmbattlePositionInfo embattleInfo, Map<String,Integer> posMap){
		List<String> heroIdList = new ArrayList<String>();
		if(embattleInfo == null){
			return heroIdList;
		}
		List<EmbattleHeroPosition> pos = embattleInfo.getPos();
		for (EmbattleHeroPosition heroPosition : pos) {
			String heroId = heroPosition.getId();
			if(!StringUtils.equals(player.getUserId(), heroId)){
				heroIdList.add(heroId);
			}
			posMap.put(heroId, heroPosition.getPos());
		}
		return heroIdList;
	}

	
	private WBResult checkFightBegin(Player player ){
		WBData data = WBDataHolder.getInstance().get();
		WBResult result = WBResult.newInstance(true);
		WBState state = WBStateFSM.getInstance().getState();
		if(state == WBState.PreStart){
			result.setSuccess(false);
			result.setReason("魔神尚未降临。");
		}else if(state == WBState.FightEnd || state == WBState.SendAward || state == WBState.Finish){
			result.setSuccess(false);
			result.setReason("活动已经结束。");
		}else if(data == null || !data.isOpen()){
			result.setSuccess(false);
			result.setReason("活动暂时不开放。");
		}
		if(result.isSuccess()){
			result = checkCD(player);
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
			if(updateHurt <= 0){
				//不扣血，只做同步
				result.setSuccess(true);
			}else{
				//检查一下伤害值  暂时不做伤害检查 后面再加  加的时候记得修改applicationcontent.xml
//				updateHurt = WBMgr.getInstance().checkHurt(player, updateHurt);
				updateHurt = WBMgr.getInstance().decrHp(player,updateHurt);
				if(updateHurt >= 0){				
					long totalHurt = WBUserMgr.getInstance().fightUpdate(player, updateHurt);
					WBOnFightMgr.getInstance().enter(player.getUserId(), totalHurt);
				}else{
					result.setSuccess(false);
					result.setReason("世界boss已被击杀。");
				}
			}
			
		}
		response.setIsSuccess(result.isSuccess());
		if(result.getReason() != null)
			response.setTipMsg(result.getReason());	
		
		return response.build().toByteString();
	}
	
	/**
	 * 战斗结束
	 * @param player
	 * @param commonReq
	 * @return
	 */
	public ByteString doFightEnd(Player player, CommonReqMsg commonReq) {
		WBUserMgr.getInstance().resetUserDataIfNeed(player);
		
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		//检查角色是否在战斗列表内
		Pair<String,Long> data = WBOnFightMgr.getInstance().getRoleBattleData(player.getUserId());
		if(data != null){
			WBUserMgr.getInstance().fightEndUpdate(player);
			WBOnFightMgr.getInstance().leave(player.getUserId());
		}
		
		response.setIsSuccess(true);
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
		
		WBResult result = WBResult.newInstance(true);
		WBState state = WBStateFSM.getInstance().getState();
		if(state != WBState.PreStart && state != WBState.FightStart){
			result.setSuccess(false);
			result.setReason("活动已经结束");
		}
		if(result.isSuccess()){
			result = checkBuyBuff(player);
			if(result.isSuccess()){
				WBBuyBuffCfg buyBuffCfg = WBBuyBuffCfgDAO.getInstance().getCfgById(bufBuffCfgId);
				if(buyBuffCfg == null){
					result.setReason("配置不存在。");
				}else{
					eSpecialItemId costType = buyBuffCfg.getCostTypeEnum();
					int costCount = buyBuffCfg.getCostCount();			
					result = WBHelper.takeCost(player, costType, costCount);
					if(result.isSuccess()){
						WBUserMgr.getInstance().addBuff(player, buyBuffCfg.getId());
					}
					
				}
			}
		}

		response.setIsSuccess(result.isSuccess());
		if(result.getReason() != null){
			response.setTipMsg(result.getReason());	
		}
				
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


	/**
	 * @param player
	 * @param commonReq
	 * @return
	 */
	public ByteString doBuyCD(Player player, CommonReqMsg commonReq) {
		WBUserMgr.getInstance().resetUserDataIfNeed(player);
		
		
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		WBResult result = checkBoss();
		if(result.isSuccess()){
			result = checkUser(player);
		}
		if(result.isSuccess()){			
			result = WBHelper.takeCost(player, eSpecialItemId.Gold, getBuyCDCost(player));
			if(result.isSuccess()){
				WBUserMgr.getInstance().cleanCD(player);
			}
		}

		response.setIsSuccess(result.isSuccess());
		if(result.getReason() != null)
		response.setTipMsg(result.getReason());	
				
		return response.build().toByteString();
	}
	
	private WBResult checkUser(Player player){
		WBResult result = WBResult.newInstance(true);
		//策划要求去掉复活次数
//		if(!WBUserMgr.getInstance().canBuyCd(player)){
//			result.setSuccess(false);
//			result.setReason("购买次数已满，升级vip可以获得更多购买次数。");
//		}
		
		//检查角色是否在复活时间内
		WBUserData userData = WBUserDataHolder.getInstance().get(player.getUserId());
		if(System.currentTimeMillis() >= userData.getFightCdTime()){
			//角色cd时间结束
			result.setSuccess(false);
			result.setReason("角色已经复活");
		}
		return result;
	}
	
	private int getBuyCDCost(Player player){
		WBUserData userData = WBUserDataHolder.getInstance().get(player.getUserId());
		int buyCount = userData == null ? 0 : userData.getCdBuyCount();
		String[] cdCost = WBSettingCfgDAO.getInstance().getCfg().getCdCost().split(";");
		int cost = 0;
		if(buyCount >= cdCost.length){
			cost = Integer.parseInt(cdCost[cdCost.length - 1]);
		}else{
			cost = Integer.parseInt(cdCost[buyCount]);
		}
		return cost;
	}


}
