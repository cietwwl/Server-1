package com.rw.service.store;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.common.stream.IStream;
import com.rw.fsutil.common.stream.StreamImpl;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.item.SpecialItemCfgDAO;
import com.rwbase.dao.store.CommodityCfgDAO;
import com.rwbase.dao.store.StoreCfgDAO;
import com.rwbase.dao.store.pojo.CommodityCfg;
import com.rwbase.dao.store.pojo.StoreCfg;
import com.rwproto.StoreProtos.StoreRequest;
import com.rwproto.StoreProtos.StoreResponse;
import com.rwproto.StoreProtos.eStoreRequestType;
import com.rwproto.StoreProtos.eStoreResultType;
import com.rwproto.StoreProtos.eWakenRewardDrawType;
import com.rwproto.StoreProtos.tagCommodity;


public class StoreHandler {
	private Player m_pPlayer;
	private static StoreHandler instance = new StoreHandler();	
	private StoreHandler(){		
	}
	
	public static StoreHandler getInstance(){
		return instance;
	}
	
	private StreamImpl<Pair<Player,Integer>> openStoreNotification = new StreamImpl<Pair<Player,Integer>>();
	/**
	 * 通知发送后会清除内部缓存的数据
	 * @return
	 */
	public IStream<Pair<Player,Integer>> getOpenStoreNotification(){
		return openStoreNotification;
	}

	public ByteString OpenStore(int storeType) {
		openStoreNotification.fire(Pair.Create(m_pPlayer, storeType));
		openStoreNotification.hold(null);//clear cache 
		StoreResponse.Builder resp =StoreResponse.newBuilder();
		m_pPlayer.getStoreMgr().OpenStore(storeType);
		m_pPlayer.getTempAttribute().setRefreshStore(false);
		return resp.build().toByteString();
	}

	public ByteString BuyCommodity(tagCommodity reqCommodity) {
		StoreResponse.Builder resp =StoreResponse.newBuilder();
		resp.setRequestType(eStoreRequestType.BuyCommodity);
		int result = m_pPlayer.getStoreMgr().BuyCommodity(reqCommodity.getId(), reqCommodity.getCount());
		resp.setReslutType(eStoreResultType.FAIL);
		CommodityCfg cfg = CommodityCfgDAO.getInstance().GetCommodityCfg(reqCommodity.getId());
		if(cfg == null){
			resp.setReslutValue("已经卖完了！");
			return resp.build().toByteString();
		}
		StoreCfg storeCfg = StoreCfgDAO.getInstance().getStoreCfgByID(cfg.getStoreId());
		if(result==1){
			resp.setReslutType(eStoreResultType.SUCCESS);
			tagCommodity.Builder respCommodity = tagCommodity.newBuilder();
			respCommodity.setId(reqCommodity.getId());
			respCommodity.setCount(0);
			resp.setStoreType(storeCfg.getType());			
			resp.setCommodity(respCommodity);
			if(eSpecialItemId.getDef(cfg.getCostType())==eSpecialItemId.BraveCoin){
				UserEventMgr.getInstance().buyInTowerShopVitality(m_pPlayer, 1);
			}			
		}else if(result==-2){
			resp.setCostType(cfg.getCostType());
			resp.setReslutValue(SpecialItemCfgDAO.getDAO().getCfgById(String.valueOf(cfg.getCostType())).getName() + "不足");
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
		}else if(result == -1){
			resp.setReslutValue("商店数据错误");
		}
		return resp.build().toByteString();
	}

	public void SetPlayer(Player player) {
		m_pPlayer = player;
	}
	
	public ByteString wakenRewardDraw(Player player, StoreRequest req){
		StoreResponse.Builder resp = StoreResponse.newBuilder();
		eWakenRewardDrawType drawType = req.getDrawType();
		int consumeType = req.getConsumeType();
		player.getStoreMgr().processWakenLottery(player, drawType, resp, consumeType);
		return resp.build().toByteString();
	}
	
	public ByteString exchangeWakenItem(Player player, StoreRequest req){
		tagCommodity reqCommodity = req.getCommodity();
		StoreResponse.Builder resp =StoreResponse.newBuilder();
		resp.setRequestType(eStoreRequestType.BuyCommodity);
		int result = m_pPlayer.getStoreMgr().exchangeItem(reqCommodity.getId());
		resp.setReslutType(eStoreResultType.FAIL);
		CommodityCfg cfg = CommodityCfgDAO.getInstance().GetCommodityCfg(reqCommodity.getId());
		if(cfg == null){
			resp.setReslutValue("已经兑换了！");
			return resp.build().toByteString();
		}
		StoreCfg storeCfg = StoreCfgDAO.getInstance().getStoreCfgByID(cfg.getStoreId());
		if(result > 0){
			resp.setReslutType(eStoreResultType.SUCCESS);
			tagCommodity.Builder respCommodity = tagCommodity.newBuilder();
			respCommodity.setId(reqCommodity.getId());
			respCommodity.setExchangeCount(result);
			resp.setStoreType(storeCfg.getType());			
			resp.setCommodity(respCommodity);
		}else if(result==-2){
			resp.setCostType(cfg.getCostType());
			resp.setReslutValue(SpecialItemCfgDAO.getDAO().getCfgById(String.valueOf(cfg.getCostType())).getName() + "不足");
		}else if(result==-3){
			resp.setReslutValue("已经兑换了！");
		}
		return resp.build().toByteString();
	}
}
