package com.playerdata;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.common.PlayerEventListener;
import com.playerdata.readonly.StoreMgrIF;
import com.rwbase.common.RandomUtil;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.enu.eStoreConditionType;
import com.rwbase.common.enu.eStoreExistType;
import com.rwbase.common.enu.eStoreType;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.store.CommodityCfgDAO;
import com.rwbase.dao.store.StoreCfgDAO;
import com.rwbase.dao.store.TableStoreDao;
import com.rwbase.dao.store.pojo.CommodityCfg;
import com.rwbase.dao.store.pojo.CommodityData;
import com.rwbase.dao.store.pojo.StoreCfg;
import com.rwbase.dao.store.pojo.StoreData;
import com.rwbase.dao.store.pojo.StoreDataHolder;
import com.rwbase.dao.store.pojo.TableStore;
import com.rwproto.MsgDef.Command;
import com.rwproto.StoreProtos.StoreResponse;
import com.rwproto.StoreProtos.eProbType;
import com.rwproto.StoreProtos.eStoreRequestType;
import com.rwproto.StoreProtos.eStoreResultType;

public class StoreMgr implements StoreMgrIF, PlayerEventListener {

	private StoreDataHolder storeDataHolder;
	private int m_nRandom;

	protected Player m_pPlayer = null;
	private String userId;

	// 初始化
	public void init(Player pOwner) {

		m_pPlayer = pOwner;
		this.userId = pOwner.getUserId();
		storeDataHolder = new StoreDataHolder(pOwner.getUserId());
		// m_StoreTable = storeDataHolder.get();
	}

	@Override
	public void notifyPlayerCreated(Player player) {
	}

	@Override
	public void notifyPlayerLogin(Player player) {
	}

	/**
	 * vip升级，主角升级新开商店
	 */
	public void AddStore() {
		StoreData pStoreData;
		int type = 0;
		List<StoreCfg> allStore = StoreCfgDAO.getInstance().getAllCfg();
		ConcurrentHashMap<Integer, StoreData> m_StoreData = storeDataHolder.get().getStoreDataMap();
		for (StoreCfg cfg : allStore) {
			type = cfg.getType();
			if (m_pPlayer.getLevel() >= cfg.getLevelLimit() && m_pPlayer.getVip() >= cfg.getVipLimit()) {
				// boolean hasGuild =
				// StringUtils.isNotBlank(m_pPlayer.getGuildUserMgr().getGuildId());
				// boolean hasGuild =false;
				UserGroupAttributeDataIF groupData = m_pPlayer.getUserGroupAttributeDataMgr().getUserGroupAttributeData();

				boolean hasGroup = StringUtils.isNotBlank(groupData.getGroupId());
				if (type == eStoreType.Union.getOrder() && !hasGroup) {
					continue;
				}
				if (!m_StoreData.containsKey(type)) {
					pStoreData = new StoreData();
					pStoreData.setId(getStoreId(type, m_pPlayer));
					pStoreData.setVersion(cfg.getVersion());
					pStoreData.setCommodity(RandomList(type));
					pStoreData.setLastRefreshTime(System.currentTimeMillis());
					pStoreData.setRefreshNum(0);
					pStoreData.setExistType(eStoreExistType.Always);
					pStoreData.setType(eStoreType.getDef(type));
					m_StoreData.put(type, pStoreData);
				} else if (m_StoreData.containsKey(type) && cfg.getVersion() != getStore(type).getVersion()) {

					pStoreData = getStore(type);
					pStoreData.setVersion(cfg.getVersion());
					pStoreData.setCommodity(RandomList(type));
					pStoreData.setLastRefreshTime(System.currentTimeMillis());
					pStoreData.setRefreshNum(0);
					pStoreData.setExistType(eStoreExistType.Always);
				}
				storeDataHolder.add(m_pPlayer, type);
			}
		}
	}

	/** 每分钟执行 */
	public void onMinutes() {
		TableStore m_StoreTable = storeDataHolder.get();
		if (m_StoreTable == null) {
			GameLog.error("StoreMgr", "#syncAllStore()", "find TableStore fail:" + userId);
			return;
		}
		Enumeration<StoreData> StoreEnumeration = getStoreEnumeration();
		ConcurrentHashMap<Integer, StoreData> m_StoreData = storeDataHolder.get().getStoreDataMap();
		while (StoreEnumeration.hasMoreElements()) {
			StoreData data = StoreEnumeration.nextElement();
			if (data == null) {
				continue;
			}
			refreshStoreInfo(data.getType().getOrder());
			if (data.getExistType() == eStoreExistType.Interval) {
				StoreCfg cfg = StoreCfgDAO.getInstance().getStoreCfg(data.getType().getOrder());
				double temp = (double) (System.currentTimeMillis() - data.getLastRefreshTime()) / 1000 / 60;
				if (temp > cfg.getExistMin()) {
					// syncProbStore(data.getType().getOrder(),
					// eProbType.Close,false);
					// author:lida 移除关闭的商店
					m_StoreData.remove(data.getType().getOrder());
					storeDataHolder.remove(m_pPlayer, data);

				}
			}
		}
	}

	/**
	 * 概率刷出商店
	 * 
	 * @param type
	 */
	public void ProbStore(eStoreConditionType type) {
		switch (type) {
		case WarCopy:
			List<Integer> storeTypes = new ArrayList<Integer>();
			// 暂时不刷新黑市商人和神秘商人
			storeTypes.add(eStoreType.Secret.getOrder());// 概率
			storeTypes.add(eStoreType.Blackmark.getOrder());
			ConcurrentHashMap<Integer, StoreData> m_StoreData = storeDataHolder.get().getStoreDataMap();
			for (Integer storetype : storeTypes) {
				if (m_StoreData.containsKey(storetype)) {
					continue;
				}
				StoreCfg cfg = StoreCfgDAO.getInstance().getStoreCfg(storetype);
				if (cfg == null || cfg.getLevelLimit() > m_pPlayer.getLevel()) {
					return;
				}
				int prob = RandomUtil.nextInt(100);
				// author:lida 2015-09-28 解决黑市商人和神秘商人出现的概率过高
				if (cfg.getProb() >= prob) {
					StoreData pStoreData = new StoreData();
					pStoreData.setId(getStoreId(storetype, m_pPlayer));
					pStoreData.setVersion(cfg.getVersion());
					pStoreData.setCommodity(RandomList(storetype));
					pStoreData.setLastRefreshTime(System.currentTimeMillis());
					pStoreData.setRefreshNum(0);
					pStoreData.setType(eStoreType.getDef(storetype));
					pStoreData.setExistType(eStoreExistType.Interval);
					m_StoreData.put(storetype, pStoreData);
					syncProbStore(storetype, eProbType.Open, true);
					storeDataHolder.add(m_pPlayer, storetype);
					break;
				}
			}
			break;
		default:
			break;
		}
	}

	private void syncProbStore(int storeType, eProbType type, boolean btip) {
		StoreResponse.Builder resp = StoreResponse.newBuilder();
		resp.setRequestType(eStoreRequestType.ProbStore);
		resp.setStoreType(storeType);
		resp.setProbType(type);
		if (btip) {
			resp.setReslutType(eStoreResultType.HasTip);
		} else {
			resp.setReslutType(eStoreResultType.NoTip);
		}
		m_pPlayer.SendMsg(Command.MSG_STORE, resp.build().toByteString());
	}

	public void save() {
		storeDataHolder.flush();
	}

	/**
	 * 随机获得商品列表
	 * 
	 * @param index
	 * @return
	 */
	private List<CommodityData> RandomList(int index) {
		List<CommodityData> list = new ArrayList<CommodityData>();
		StoreCfg cfg = StoreCfgDAO.getInstance().getStoreCfg(index);
		if (cfg == null) {
			GameLog.info("store", m_pPlayer.getUserId(), "配置表错误：store表没有类型为" + index + "的数据", null);
			return list;
		}
		String[] colList = cfg.getColType().split(",");
		HashMap<Integer, List<CommodityCfg>> commoMap = new HashMap<Integer, List<CommodityCfg>>();

		for (int i = 0; i < colList.length; i++) {
			int type = Integer.parseInt(colList[i]);
			if (!commoMap.containsKey(type)) {
				List<CommodityCfg> Commodity = CommodityCfgDAO.getInstance().GetCommdity(cfg.getId(), type, m_pPlayer.getLevel());
				commoMap.put(type, Commodity);
			}
		}
		for (int i = 0; i < colList.length; i++) {
			int type = Integer.parseInt(colList[i]);
			List<CommodityCfg> commcfgs = commoMap.get(type);
			if (commcfgs.size() <= 0) {
				GameLog.info("store", m_pPlayer.getUserId(),
						"配置表错误：" + cfg.getName() + "没有类型为" + type + "的商品", null);
				continue;
			}
			m_nRandom = 0;
			CommodityCfg commcfg = getRandomCommondity(commcfgs);
			if (commcfg != null) {
				CommodityData pCommodityCell = new CommodityData();
				pCommodityCell.setId(commcfg.getId());
				pCommodityCell.setCount(1);
				pCommodityCell.setSolt(i);
				list.add(pCommodityCell);
				commcfgs.remove(commcfg);
			}
		}
		return list;
	}
	
	private int getStoreCommodityListLength(int index){
		StoreCfg cfg = StoreCfgDAO.getInstance().getStoreCfg(index);
		if (cfg == null) {
			GameLog.info("store", m_pPlayer.getUserId(), "配置表错误：store表没有类型为" + index + "的数据", null);
			return 0;
		}
		String[] colList = cfg.getColType().split(",");
		return colList.length;
	}

	/**
	 * 随机获取单个商品
	 * 
	 * @param Commodity
	 */
	private CommodityCfg getRandomCommondity(List<CommodityCfg> Commodity) {
		if(Commodity == null || Commodity.isEmpty()){
			return null;
		}
		int size = Commodity.size();
		int total = 0;
		for (int i = 0; i < size; i++) {
			CommodityCfg cfg = Commodity.get(i);
			total += cfg.getProb();
		}
		if (total <= 0) {
			return null;
		}
		int prob = RandomUtil.nextInt(total);
		for (int i = 0; i < size; i++) {
			CommodityCfg cfg = Commodity.get(i);
			int rate = cfg.getProb();
			if (rate > prob) {
				return cfg;
			}
			prob -= rate;
		}
		GameLog.error("StoreMgr", "#getRandomCommondity()", "随机商店物品失败：" + total);
		return size > 0 ? Commodity.get(size - 1) : null;
	}

	/**
	 * 获得商店（时间刷新后）
	 * 
	 * @param type
	 * @return
	 */
	public StoreData refreshStoreInfo(int type) {
		StoreCfg cfg = StoreCfgDAO.getInstance().getStoreCfg(type);
		if (cfg == null) {
			GameLog.info("store", m_pPlayer.getUserId(), "配置表错误：store表没有类型为" + type + "的数据", null);
			return null;
		}
		StoreData pStoreCell = getStore(type);
		if (pStoreCell == null) {
			return null;
		}
		switch (pStoreCell.getExistType()) {
		case Interval:
			int temp = (int) (System.currentTimeMillis() - pStoreCell.getLastRefreshTime()) / 1000 / 60;
			if (temp > cfg.getExistMin()) {
				return null;
			}
			break;
		case Always:
			List<CommodityData> commodity = pStoreCell.getCommodity();
			if (pStoreCell.getVersion() != cfg.getVersion() || checkCommodityDataExpire(commodity)) {
				List<CommodityData> randomList = RandomList(type);
				int rightSize = getStoreCommodityListLength(type);
				if(randomList.size() != rightSize){
					return null;
				}
				pStoreCell.setCommodity(randomList);
				pStoreCell.setLastRefreshTime(System.currentTimeMillis());
				pStoreCell.setVersion(cfg.getVersion());
				return pStoreCell;
			}
			pStoreCell = getAllwaysStore(pStoreCell);
			break;
		default:
			break;
		}
		return pStoreCell;
	}
	
	private boolean checkCommodityDataExpire(List<CommodityData> commodity){
		
		for (CommodityData commodityData : commodity) {
			CommodityCfg cfgById = CommodityCfgDAO.getInstance().getCfgById(String.valueOf(commodityData.getId()));
			if(cfgById == null){
				return true;
			}
		}
		return false;
	}

	private StoreData getAllwaysStore(StoreData vo) {
		StoreCfg cfg = StoreCfgDAO.getInstance().getStoreCfg(vo.getType().getOrder());
		if (StringUtils.isBlank(cfg.getAutoRetime())) {
			return vo;
		}
		Date today = new Date();
		Date lastDay = new Date(vo.getLastRefreshTime());
		int todaySec = today.getHours() * 3600 + today.getMinutes() * 60 + today.getSeconds();
		int lastSec = lastDay.getHours() * 3600 + lastDay.getMinutes() * 60 + lastDay.getSeconds();
		if (cfg.getAutoRetime().length() < 5) {
			m_pPlayer.NotifyCommonMsg("StoreCfg表id为" + cfg.getId() + "的项AutoRetime配置错误 “00:00”");
			return vo;
		}

		String[] timeArr = cfg.getAutoRetime().split("_");
		for (String time : timeArr) {
			int hour = Integer.parseInt(time.substring(0, 2));
			int min = Integer.parseInt(time.substring(3, 5));
			int sec = hour * 3600 + min * 60;
			if ((today.getDate() != lastDay.getDate() && todaySec >= sec) || (today.getDate() == lastDay.getDate() && todaySec >= sec && lastSec < sec)) {
				// 刷新
				vo.setCommodity(RandomList(vo.getType().getOrder()));
				vo.setLastRefreshTime(System.currentTimeMillis());
				storeDataHolder.add(this.m_pPlayer, vo.getType().getOrder());
				m_pPlayer.getTempAttribute().setRefreshStore(true);
				break;
			}
		}
		return vo;
	}

	/**
	 * 花钱刷新商店
	 * 
	 * @param storeType
	 * @return
	 */
	public int ResqRefresh(int storeType) {
		StoreCfg cfg = StoreCfgDAO.getInstance().getStoreCfg(storeType);
		if (cfg == null) {
			GameLog.info("store", m_pPlayer.getUserId(), "配置表错误：store表没有类型为" + storeType + "的数据", null);
			return -1;
		}
		StoreData pStoreData = getStore(storeType);
		eSpecialItemId etype = eSpecialItemId.getDef(cfg.getCostType());
		int refreshnum = pStoreData.getRefreshNum();
		int cost = Integer.parseInt(cfg.getRefreshCost().split("_")[refreshnum]);
		if (m_pPlayer.getReward(etype) < cost) {
			return -2;
		}
		if (pStoreData.getRefreshNum() > cfg.getRefreshCount()) {
			return -3;
		}
		List<CommodityData> randomList = RandomList(storeType);
		int rightSize = getStoreCommodityListLength(storeType);
		if(rightSize == 0 || rightSize != randomList.size()){
			return -1;
		}
		m_pPlayer.getItemBagMgr().addItem(cfg.getCostType(), -cost);
		refreshnum++;
		pStoreData.setRefreshNum(refreshnum);
		
		pStoreData.setCommodity(randomList);
		storeDataHolder.add(m_pPlayer, storeType);
		return 1;
	}

	/**
	 * 购买商品
	 * 
	 * @param storeType
	 * @param commodityId
	 * @param count
	 * @return
	 */
	public int BuyCommodity(int commodityId, int count) {
		CommodityCfg cfg = CommodityCfgDAO.getInstance().GetCommodityCfg(commodityId);
		if (cfg == null) {
			GameLog.info("store", m_pPlayer.getUserId(), "配置表错误：commodity表没有id为" + commodityId + "的商品", null);
			return -1;
		}
		StoreCfg storeCfg = StoreCfgDAO.getInstance().getStoreCfgByID(cfg.getStoreId());
		StoreData pStoreData = refreshStoreInfo(storeCfg.getType());
		if (pStoreData == null) {
			return -4;
		}
		List<CommodityData> list = pStoreData.getCommodity();
		for (CommodityData pCommodityData : list) {
			if (pCommodityData.getId() == commodityId) {
				if (pCommodityData.getCount() >= count) {
					eSpecialItemId etype = eSpecialItemId.getDef(cfg.getCostType());
					if (m_pPlayer.getReward(etype) < cfg.getCost()) {
						return -2;
					}
					m_pPlayer.getItemBagMgr().addItem(cfg.getGoodsId(), cfg.getCount());
					m_pPlayer.getItemBagMgr().addItem(cfg.getCostType(), -cfg.getCost());
					pCommodityData.setCount(0);
					storeDataHolder.update(m_pPlayer, storeCfg.getType());
					return 1;
				}
				return -3;
			}
		}
		return 0;
	}

	public void syncAllStore() {
		TableStore m_StoreTable = storeDataHolder.get();
		if (m_StoreTable == null) {
			GameLog.error("StoreMgr", "#syncAllStore()", "find TableStore fail:" + userId);
			return;
		}
		ConcurrentHashMap<Integer, StoreData> m_StoreData = storeDataHolder.get().getStoreDataMap();
		if (m_StoreTable != null && m_StoreData != null) {

			List<Integer> removeKey = new ArrayList<Integer>();
			for (Iterator<Entry<Integer, StoreData>> iterator = m_StoreData.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry<Integer, StoreData> dic = iterator.next();
				StoreData pStoreData = refreshStoreInfo(dic.getKey());
				if (pStoreData == null) {
					removeKey.add(dic.getKey());
					continue;
				}
				dic.setValue(pStoreData);
				if (pStoreData.getExistType() == eStoreExistType.Interval) {

					StoreCfg cfg = StoreCfgDAO.getInstance().getStoreCfg(pStoreData.getType().getOrder());
					double temp = (double) (System.currentTimeMillis() - pStoreData.getLastRefreshTime()) / 1000 / 60;
					if (temp > cfg.getExistMin()) {
						iterator.remove();
						storeDataHolder.remove(m_pPlayer, pStoreData);
						continue;
					}
					storeDataHolder.add(m_pPlayer, dic.getKey());
					// syncProbStore(dic.getKey(),eProbType.Open,false);
				}
			}
			for (Integer key : removeKey) {
				m_StoreData.remove(key);
			}
		}
		if (m_pPlayer.getLevel() > 1) {
			AddStore();
		}
	}

	public void OpenStore(int storeType) {
		refreshStoreInfo(storeType);
	}

	public Enumeration<StoreData> getStoreEnumeration() {
		ConcurrentHashMap<Integer, StoreData> m_StoreData = storeDataHolder.get().getStoreDataMap();
		return m_StoreData.elements();
	}

	public StoreData getStore(eStoreType type) {
		int etype = type.getOrder();
		return getStore(etype);
	}

	public StoreData getStore(int type) {
		ConcurrentHashMap<Integer, StoreData> m_StoreData = storeDataHolder.get().getStoreDataMap();
		return m_StoreData.get(type);
	}

	public String getStoreId(int type, Player m_Player) {
		return m_Player.getUserId() + "_" + type;
	}

	/**
	 * 重置刷新次数
	 */
	public void resetRefreshNum() {
		for (Iterator<Entry<Integer, StoreData>> iterator = storeDataHolder.get().getStoreDataMap().entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, StoreData> entry = iterator.next();
			StoreData storeData = entry.getValue();
			storeData.setRefreshNum(0);
		}
	}

}
