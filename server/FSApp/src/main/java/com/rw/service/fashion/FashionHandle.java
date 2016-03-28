package com.rw.service.fashion;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.FashionMgr;
import com.playerdata.Player;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.fashion.FashState;
import com.rwbase.dao.fashion.FashionBuyRenewCfgDao;
import com.rwbase.dao.fashion.FashionCfg;
import com.rwbase.dao.fashion.FashionCfgDao;
import com.rwbase.dao.fashion.FashionItem;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.FashionServiceProtos.FashionCommon;
import com.rwproto.FashionServiceProtos.FashionEventType;
import com.rwproto.FashionServiceProtos.FashionRequest;
import com.rwproto.FashionServiceProtos.FashionResponse;
import com.rwproto.FashionServiceProtos.FashionResponse.Builder;
import com.rwproto.FashionServiceProtos.FashionUsed;



public class FashionHandle {
	private static FashionHandle instance = new FashionHandle();
	
	private FashionHandle(){};

	public static FashionHandle getInstance() {
		return instance;
	}

	public ByteString buyFash(Player player, int fashionId) {
		FashionResponse.Builder response = FashionResponse.newBuilder();
		FashionCfg cfg = FashionCfgDao.getInstance().getConfig(fashionId);
		if(cfg == null){
			response.setError(ErrorType.CONFIG_ERROR);
			return response.build().toByteString();
		}
		int cost = 0;
		FashionItem item = player.getFashionMgr().getItem(fashionId);
		if(item!= null && item.getState() == FashState.EXPIRED.ordinal()){
			cost = cfg.getRenewCost();
		}else{
			cost = cfg.getBuyCost();
		}
		if(cost > player.getUserGameDataMgr().getGold()){
			response.setError(ErrorType.NOT_ENOUGH_GOLD);
			return response.build().toByteString();
		}
		player.getUserGameDataMgr().addGold(-cost);
		response.setFashionId(fashionId);
		response.setError(ErrorType.SUCCESS);
		player.getFashionMgr().buyFash(fashionId);
		return response.build().toByteString();
	}

	public ByteString offFash(Player player, int fashionId) {
		FashionResponse.Builder response = FashionResponse.newBuilder();
		player.getFashionMgr().changeFashState(fashionId, FashState.OFF);
		response.setError(ErrorType.SUCCESS);
		response.setFashionId(fashionId);
		return response.build().toByteString();
	}

	public ByteString onFash(Player player, int fashionId) {
		FashionResponse.Builder response = FashionResponse.newBuilder();
		FashionCfg cfg = FashionCfgDao.getInstance().getConfig(fashionId);
		if(cfg.getSex() != player.getSex() && cfg.getSex() != -1){
			response.setError(ErrorType.NOT_CONFORM_CONDITIONS);
			return response.build().toByteString();
		}
		response.setError(ErrorType.SUCCESS);
		response.setFashionId(fashionId);
		player.getFashionMgr().changeFashState(fashionId, FashState.ON);
		return response.build().toByteString();
	}

	/**
	 * 获取时装配置和穿戴状态
	 * @param player
	 * @return
	 */
	public ByteString getFashionData(Player player) {
		FashionResponse.Builder response = FashionResponse.newBuilder();
		response.setEventType(FashionEventType.getFashiondata);
		FashionCommon.Builder common = FashionCommon.newBuilder();
		FashionBuyRenewCfgDao cfgHelper = FashionBuyRenewCfgDao.getInstance();
		common.setBuyRenewCfg(cfgHelper.getConfigProto());
		
		FashionUsed.Builder fashion = player.getFashionMgr().getFashionUsedBuilder(player.getUserId());
		common.setUsedFashion(fashion);
		response.setFashionCommon(common);
		response.setError(ErrorType.SUCCESS);
		return response.build().toByteString();
	}

	/**
	 * 续费
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString renewFashion(Player player, FashionRequest req) {
		FashionResponse.Builder response = FashionResponse.newBuilder();
		response.setEventType(req.getEventType());
		//必须已购买才能续费
		int renewFashionId = req.getFashionId();
		FashionMgr fashionMgr = player.getFashionMgr();
		FashionItem item = fashionMgr.getItem(renewFashionId);
		if(item == null || item.getBuyTime() <= 0){
			return setErrorResponse(response,player,",FashionID="+String.valueOf(renewFashionId),"没有对应时装");
		}
		
		//检查配置是否正确
		int planId = req.getBuyRenewPlanId();
		FashionBuyRenewCfgDao cfgHelper = FashionBuyRenewCfgDao.getInstance();
		com.rwbase.dao.fashion.FashionBuyRenewCfg renewCfg = cfgHelper.getRenewConfig(renewFashionId,planId);
		if (renewCfg == null){
			return setErrorResponse(response,player,",ID="+String.valueOf(planId),"没有对应续费方案");
		}
		int cost = renewCfg.getNum();
		eSpecialItemId currencyType = renewCfg.getCoinType();
		int renewDay = renewCfg.getDay();
		if (renewDay <= 0 || cost <=0 || currencyType.geteAttrId() == null){
			return setErrorResponse(response,player,",有效期或货币类型或货币值错误,ID="+String.valueOf(planId),"续费方案配置错误");
		}

		//扣费
		if (!player.getUserGameDataMgr().deductCurrency(currencyType, cost)) {
			return setErrorResponse(response,player,null, "货币不足！");
		}
		
		fashionMgr.renewFashion(item, renewDay);
		
		response.setError(ErrorType.SUCCESS);
		return response.build().toByteString();
	}

	private ByteString setErrorResponse(Builder response, Player player, String addedLog, String reason) {
		if (addedLog != null){
			String errorReason = reason + addedLog;
			GameLog.error("时装", player.getUserId(), errorReason);
		}
		
		if (!StringUtils.isBlank(reason)) {
			response.setTips(reason);
		}
		response.setError(ErrorType.FAIL);
		return response.build().toByteString();
	}		
}
