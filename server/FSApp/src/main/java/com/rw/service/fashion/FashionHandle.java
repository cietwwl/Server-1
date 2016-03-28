package com.rw.service.fashion;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwbase.dao.fashion.FashState;
import com.rwbase.dao.fashion.FashionCfg;
import com.rwbase.dao.fashion.FashionCfgDao;
import com.rwbase.dao.fashion.FashionItem;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.FashionServiceProtos.FashionResponse;



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
}
