package com.rw.service.store;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwbase.dao.store.CommodityCfgDAO;
import com.rwbase.dao.store.StoreCfgDAO;
import com.rwbase.dao.store.pojo.CommodityCfg;
import com.rwbase.dao.store.pojo.StoreCfg;
import com.rwproto.StoreProtos.StoreResponse;
import com.rwproto.StoreProtos.eStoreRequestType;
import com.rwproto.StoreProtos.eStoreResultType;
import com.rwproto.StoreProtos.tagCommodity;


public class StoreHandler {
	private Player m_pPlayer;
	private static StoreHandler instance = new StoreHandler();	
	private StoreHandler(){		
	}
	
	public static StoreHandler getInstance(){
		return instance;
	}

	public ByteString OpenStore(int storeType) {
		StoreResponse.Builder resp =StoreResponse.newBuilder();
		m_pPlayer.getStoreMgr().OpenStore(storeType);
		return resp.build().toByteString();
	}

	public ByteString BuyCommodity(tagCommodity reqCommodity) {
		StoreResponse.Builder resp =StoreResponse.newBuilder();
		resp.setRequestType(eStoreRequestType.BuyCommodity);
		int result = m_pPlayer.getStoreMgr().BuyCommodity(reqCommodity.getId(), reqCommodity.getCount());
		resp.setReslutType(eStoreResultType.FAIL);
		CommodityCfg cfg = CommodityCfgDAO.getInstance().GetCommodityCfg(reqCommodity.getId());
		StoreCfg storeCfg = StoreCfgDAO.getInstance().getStoreCfgByID(cfg.getStoreId());
		if(result==1){
			resp.setReslutType(eStoreResultType.SUCCESS);
			tagCommodity.Builder respCommodity = tagCommodity.newBuilder();
			respCommodity.setId(reqCommodity.getId());
			respCommodity.setCount(0);
			resp.setStoreType(storeCfg.getType());
			
			resp.setCommodity(respCommodity);
		}else if(result==-2){
			resp.setCostType(cfg.getCostType());
			resp.setReslutValue(String.valueOf(cfg.getCostType()) + "不足");
		}else if(result==-3){
			resp.setReslutValue("已经卖完了！");
		}
		return resp.build().toByteString();
	}

	public ByteString RefreshStore(int storeType) {
		StoreResponse.Builder resp =StoreResponse.newBuilder();
		resp.setRequestType(eStoreRequestType.RefreshStore);
		int result = m_pPlayer.getStoreMgr().ResqRefresh(storeType);
		resp.setReslutType(eStoreResultType.FAIL);
		resp.setStoreType(storeType);
		StoreCfg storeCfg = StoreCfgDAO.getInstance().getStoreCfg(storeType);
		
		if(result==1){
			resp.setReslutType(eStoreResultType.SUCCESS);
		}else if(result==-2){
			resp.setCostType(storeCfg.getCostType());
			resp.setReslutValue(storeCfg.getColType() + "余额不足");
		}else if(result==-3){
			resp.setReslutValue("商店刷新次数已上限");
		}
		return resp.build().toByteString();
	}

	public void SetPlayer(Player player) {
		m_pPlayer = player;
	}
}
