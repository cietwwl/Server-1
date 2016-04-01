package com.rw.service.fashion;

import org.apache.commons.lang3.StringUtils;

import com.common.OutString;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.FashionMgr;
import com.playerdata.Player;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.fashion.FashionBuyRenewCfg;
import com.rwbase.dao.fashion.FashionBuyRenewCfgDao;
import com.rwbase.dao.fashion.FashionCommonCfg;
import com.rwbase.dao.fashion.FashionCommonCfgDao;
import com.rwbase.dao.fashion.FashionItem;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.FashionServiceProtos.FashionCommon;
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

	/**
	 * 购买时装，不做续费处理，如果已经购买了，会返回错误
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString buyFash(Player player, FashionRequest req) {
		FashionResponse.Builder response = getResponse(req);
		int fashionId = req.getFashionId();
		FashionItem item = player.getFashionMgr().getItem(fashionId);
		if (item != null){
			return setErrorResponse(response, player, ",ID="+item.getId(), "时装已经购买，请续费");
		}
		
		FashionCommonCfg fashionCfg = FashionCommonCfgDao.getInstance().getConfig(fashionId);
		if (fashionCfg == null){
			return setErrorResponse(response,player,",ID="+fashionId,"没有相应时装配置");
		}

		//检查购买配置
		String buyPlanId = req.getBuyRenewPlanId();
		FashionBuyRenewCfgDao cfgHelper = FashionBuyRenewCfgDao.getInstance();
		FashionBuyRenewCfg cfg = cfgHelper.getBuyConfig(fashionId, buyPlanId);
		if (cfg == null){
			return setErrorResponse(response,player,",ID="+buyPlanId,"没有对应购买方案");
		}
		int cost = cfg.getNum();
		eSpecialItemId currencyType = cfg.getCoinType();
		int renewDay = cfg.getDay();
		if (renewDay <= 0 || cost <=0 || currencyType.geteAttrId() == null){
			return setErrorResponse(response,player,",有效期或货币类型或货币值错误,ID="+buyPlanId,"购买方案配置错误");
		}
		
		//扣费
		if (!player.getUserGameDataMgr().deductCurrency(currencyType, cost)) {
			return setErrorResponse(response,player,null, "货币不足！",currencyType);
		}
		
		//更新时装数据
		if (!player.getFashionMgr().buyFashionItemNotCheck(fashionCfg,cfg)){
			return setErrorResponse(response,player,"严重错误：无法创建FashionItem,fashionId="+fashionId, "购买失败");
		}
		
		response.setFashionId(fashionId);
		return SetSuccessResponse(response,player);
	}

	public ByteString offFash(Player player, FashionRequest req) {
		FashionResponse.Builder response = getResponse(req);
		int fashionId = req.getFashionId();
		FashionMgr fashionMgr = player.getFashionMgr();
		if (!fashionMgr.takeOffFashion(fashionId)){
			return setErrorResponse(response,player,",脱不了时装，fashionId="+fashionId, "无效时装");
		}

		response.setFashionId(fashionId);
		return SetSuccessResponse(response,player);
	}

	public ByteString onFash(Player player,FashionRequest req) {
		FashionResponse.Builder response = getResponse(req);
		int fashionId = req.getFashionId();
		FashionMgr fashionMgr = player.getFashionMgr();
		//检查是否过期
		OutString tip = new OutString();
		if (fashionMgr.isExpired(fashionId,tip)){
			return setErrorResponse(response, player, null, tip.str==null?"时装已过期":tip.str);
		}
		
		if (!fashionMgr.putOnFashion(fashionId, tip)){
			return setErrorResponse(response, player, null, tip.str);
		}
		
		response.setFashionId(fashionId);
		return SetSuccessResponse(response,player);
	}

	/**
	 * 获取时装配置和穿戴状态
	 * @param player
	 * @param req 
	 * @return
	 */
	public ByteString getFashionData(Player player, FashionRequest req) {
		FashionResponse.Builder response = getResponse(req);
		FashionCommon.Builder common = FashionCommon.newBuilder();
		FashionBuyRenewCfgDao cfgHelper = FashionBuyRenewCfgDao.getInstance();
		common.setBuyRenewCfg(cfgHelper.getConfigProto());
		
		FashionUsed.Builder fashion = player.getFashionMgr().getFashionUsedBuilder(player.getUserId());
		common.setUsedFashion(fashion);
		response.setFashionCommon(common);
		return SetSuccessResponse(response);
	}

	/**
	 * 续费
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString renewFashion(Player player, FashionRequest req) {
		FashionResponse.Builder response = getResponse(req);
		//必须已购买才能续费
		int renewFashionId = req.getFashionId();
		FashionMgr fashionMgr = player.getFashionMgr();
		FashionItem item = fashionMgr.getItem(renewFashionId);
		if(item == null || item.getBuyTime() <= 0){
			return setErrorResponse(response,player,",FashionID="+String.valueOf(renewFashionId),"没有对应时装");
		}
		
		//检查配置是否正确
		String planId = req.getBuyRenewPlanId();
		FashionBuyRenewCfgDao cfgHelper = FashionBuyRenewCfgDao.getInstance();
		FashionBuyRenewCfg renewCfg = cfgHelper.getRenewConfig(renewFashionId,planId);
		if (renewCfg == null){
			return setErrorResponse(response,player,",ID="+planId,"没有对应续费方案");
		}
		int cost = renewCfg.getNum();
		eSpecialItemId currencyType = renewCfg.getCoinType();
		int renewDay = renewCfg.getDay();
		if (renewDay <= 0 || cost <=0 || currencyType.geteAttrId() == null){
			return setErrorResponse(response,player,",有效期或货币类型或货币值错误,ID="+planId,"续费方案配置错误");
		}

		//扣费
		if (!player.getUserGameDataMgr().deductCurrency(currencyType, cost)) {
			return setErrorResponse(response,player,null, "货币不足！",currencyType);
		}
		
		fashionMgr.renewFashion(item, renewDay);
		response.setFashionId(renewFashionId);
		return SetSuccessResponse(response,player);
	}

	private ByteString setErrorResponse(Builder response, Player player, String addedLog, String reason,
			ErrorType err) {
		if (addedLog != null){
			String errorReason = reason + addedLog;
			GameLog.error("时装", player.getUserId(), errorReason);
		}
		
		if (!StringUtils.isBlank(reason)) {
			response.setTips(reason);
		}
		response.setError(err);
		return response.build().toByteString();
	}

	private ByteString setErrorResponse(Builder response, Player player, String addedLog, String reason) {
		return setErrorResponse(response,player,addedLog,reason,ErrorType.FAIL);
	}
	
	private ByteString setErrorResponse(Builder response, Player player, String addedLog, String reason,eSpecialItemId currencyType) {
		ErrorType err = ErrorType.FAIL;
		if (currencyType == null) {
			err = ErrorType.FAIL;
		}else if (currencyType == eSpecialItemId.Gold){
			err = ErrorType.NOT_ENOUGH_GOLD;
		}else if (currencyType == eSpecialItemId.Coin){
			err = ErrorType.NOT_ENOUGH_COIN;
		}
		return setErrorResponse(response,player,addedLog,reason,err);
	}
	
	private FashionResponse.Builder getResponse(FashionRequest req) {
		FashionResponse.Builder response = FashionResponse.newBuilder();
		response.setEventType(req.getEventType());
		return response;
	}

	/**
	 * 当逻辑可能修改时装购买列表或穿戴数据的时候，执行这个方法进行刷新
	 * @param response
	 * @param player
	 * @return
	 */
	private ByteString SetSuccessResponse(Builder response, Player player) {
		player.getFashionMgr().OnLogicEnd();
		return SetSuccessResponse(response);
	}

	private ByteString SetSuccessResponse(FashionResponse.Builder response) {
		response.setError(ErrorType.SUCCESS);
		return response.build().toByteString();
	}
}
