package com.playerdata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.common.RefParam;
import com.log.GameLog;
import com.playerdata.common.PlayerEventListener;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.playerdata.readonly.StoreMgrIF;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.RandomUtil;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.enu.eStoreConditionType;
import com.rwbase.common.enu.eStoreExistType;
import com.rwbase.common.enu.eStoreType;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.store.CommodityCfgDAO;
import com.rwbase.dao.store.StoreCfgDAO;
import com.rwbase.dao.store.WakenLotteryDrawCfgDAO;
import com.rwbase.dao.store.pojo.CommodityCfg;
import com.rwbase.dao.store.pojo.CommodityData;
import com.rwbase.dao.store.pojo.StoreCfg;
import com.rwbase.dao.store.pojo.StoreData;
import com.rwbase.dao.store.pojo.StoreDataHolder;
import com.rwbase.dao.store.pojo.TableStore;
import com.rwbase.dao.store.pojo.WakenLotteryDrawCfg;
import com.rwbase.dao.store.wakenlotterydraw.WakenLotteryProcesser;
import com.rwproto.MsgDef.Command;
import com.rwproto.PrivilegeProtos.StorePrivilegeNames;
import com.rwproto.StoreProtos.StoreResponse;
import com.rwproto.StoreProtos.eProbType;
import com.rwproto.StoreProtos.eStoreRequestType;
import com.rwproto.StoreProtos.eStoreResultType;
import com.rwproto.StoreProtos.eWakenRewardDrawType;

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
			// by franky 升级vip的时候会先更新特权，然后再调用AddStore!
			StorePrivilegeNames pname = null;
			eStoreType storeType = eStoreType.getDef(type);
			switch (storeType) {
			case Secret:
				pname = StorePrivilegeNames.isOpenMysteryStore;
				break;
			case Blackmark:
				pname = StorePrivilegeNames.isOpenBlackmarketStore;
				break;
			default:
				break;
			}
			if (pname != null) {
				boolean isOpen = m_pPlayer.getPrivilegeMgr().getBoolPrivilege(pname);
				if (isOpen) {
					if (!m_StoreData.containsKey(type)) {
						pStoreData = new StoreData();
						pStoreData.setId(getStoreId(type, m_pPlayer));
						pStoreData.setVersion(cfg.getVersion());
						pStoreData.setCommodity(RandomList(type));
						pStoreData.setLastRefreshTime(System.currentTimeMillis());
						pStoreData.setRefreshNum(0);
						pStoreData.setFreeRefreshNum(0);
						pStoreData.setExistType(eStoreExistType.Always);
						pStoreData.setType(storeType);
						m_StoreData.put(type, pStoreData);
					} else if (m_StoreData.containsKey(type) && cfg.getVersion() != getStore(type).getVersion()) {
						pStoreData = getStore(type);
						pStoreData.setVersion(cfg.getVersion());
						pStoreData.setCommodity(RandomList(type));
						pStoreData.setLastRefreshTime(System.currentTimeMillis());
						pStoreData.setRefreshNum(0);
						pStoreData.setFreeRefreshNum(0);
						pStoreData.setExistType(eStoreExistType.Always);
					}
					storeDataHolder.add(m_pPlayer, type);
				}
				continue;
			}

			eOpenLevelType openLevelType = storeType.getType();
			RefParam<String> outTip = new RefParam<String>();
			if (CfgOpenLevelLimitDAO.getInstance().isOpen(openLevelType, m_pPlayer, outTip)) {
				// if (m_pPlayer.getLevel() >= cfg.getLevelLimit() && m_pPlayer.getVip() >= cfg.getVipLimit()) {
				UserGroupAttributeDataIF groupData = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(m_pPlayer.getUserId());

				boolean hasGroup = groupData == null ? false : StringUtils.isNotBlank(groupData.getGroupId());
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
					pStoreData.setType(storeType);
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

	public void removeStore(int type) {
		StoreData store = getStore(type);
		if (store != null) {
			ConcurrentHashMap<Integer, StoreData> m_StoreData = storeDataHolder.get().getStoreDataMap();
			m_StoreData.remove(store.getType().getOrder());
			storeDataHolder.remove(m_pPlayer, store);
		}
	}

	/**
	 * 概率刷出商店
	 * 
	 * @param type
	 */
	public void ProbStore(eStoreConditionType type) {
		AddStore();
		switch (type) {
		case WarCopy:
			List<Integer> storeTypes = new ArrayList<Integer>();
			CfgOpenLevelLimitDAO helper = CfgOpenLevelLimitDAO.getInstance();
			// 暂时不刷新黑市商人和神秘商人
			if (helper.isOpen(eOpenLevelType.SECRET_SHOP, m_pPlayer)) {
				storeTypes.add(eStoreType.Secret.getOrder());// 概率
			}
			if (helper.isOpen(eOpenLevelType.Blackmark_SHOP, m_pPlayer)) {
				storeTypes.add(eStoreType.Blackmark.getOrder());
			}
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
					pStoreData.setFreeRefreshNum(0);
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
		List<Integer> modelIDList = new ArrayList<Integer>();
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
				GameLog.info("store", m_pPlayer.getUserId(), "配置表错误：" + cfg.getName() + "没有类型为" + type + "的商品", null);
				continue;
			}
			m_nRandom = 0;
			CommodityCfg commcfg = getRandomCommondity(commcfgs, modelIDList);
			if (commcfg != null) {
				// if(modelIDList.contains(commcfg.getGoodsId())){
				// continue;
				// }
				CommodityData pCommodityCell = new CommodityData();
				pCommodityCell.setId(commcfg.getId());
				pCommodityCell.setCount(1);
				pCommodityCell.setExchangeCount(0);
				pCommodityCell.setSolt(i);
				list.add(pCommodityCell);
				modelIDList.add(commcfg.getGoodsId());
				commcfgs.remove(commcfg);
			}
		}
		return list;
	}

	public CommodityCfg getRandomCommondity(List<CommodityCfg> Commodity, List<Integer> modelIDList) {
		CommodityCfg cfg = getRandomCommondity(Commodity);
		while (modelIDList.contains(cfg.getGoodsId()) && Commodity.size() > 0) {
			cfg = getRandomCommondity(Commodity);
		}
		return cfg;
	}

	private int getStoreCommodityListLength(int index) {
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
		if (Commodity == null || Commodity.isEmpty()) {
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
			if (pStoreCell.getVersion() != cfg.getVersion() || checkCommodityDataExpire(commodity, cfg)) {
				List<CommodityData> randomList = RandomList(type);
				int rightSize = getStoreCommodityListLength(type);
				if (randomList.size() != rightSize) {
					return null;
				}
				pStoreCell.setCommodity(randomList);
				pStoreCell.setLastRefreshTime(System.currentTimeMillis());
				pStoreCell.setVersion(cfg.getVersion());
				return pStoreCell;
			}
			getAllwaysStore(pStoreCell);
			break;
		default:
			break;
		}
		return pStoreCell;
	}

	private boolean checkCommodityDataExpire(List<CommodityData> commodity, StoreCfg storeCfg) {

		for (CommodityData commodityData : commodity) {
			CommodityCfg cfgById = CommodityCfgDAO.getInstance().getCfgById(String.valueOf(commodityData.getId()));
			if (cfgById == null) {
				return true;
			}
			if (cfgById.getStoreId() != storeCfg.getId()) {
				return true;
			}
		}
		return false;
	}

	private void getAllwaysStore(StoreData vo) {
		StoreCfg cfg = StoreCfgDAO.getInstance().getStoreCfg(vo.getType().getOrder());
		if (StringUtils.isBlank(cfg.getAutoRetime())) {
			return;
		}
		Date today = new Date();
		Date lastDay = new Date(vo.getLastRefreshTime());
		long lastDayTime = lastDay.getTime();
		long todayTime = today.getTime();
		if (cfg.getAutoRetime().length() < 5) {
			m_pPlayer.NotifyCommonMsg("StoreCfg表id为" + cfg.getId() + "的项AutoRetime配置错误 “00:00”");
			return;
		}

		List<Integer> refreshDayList = cfg.getRefreshDayList();

		if (refreshDayList.size() > 0) {
			checkRefreshByWeekDay(vo, cfg, lastDayTime, todayTime);
		} else {
			checkRefreshByDay(vo, cfg, lastDayTime, todayTime);
		}

		if (vo.getType() == eStoreType.Waken) {
			WakenLotteryDrawCfg wakenLotteryDrawCfg = WakenLotteryDrawCfgDAO.getInstance().getCfgById("1");
			WakenLotteryProcesser.getInstantce().checkDrawReset(m_pPlayer, storeDataHolder, vo, wakenLotteryDrawCfg);
		}
	}

	private void checkRefreshByWeekDay(StoreData vo, StoreCfg cfg, long lastDayTime, long todayTime) {

		Calendar ctoday = DateUtils.getDayZeroCalendar(todayTime);
		Calendar clastDay = DateUtils.getDayZeroCalendar(lastDayTime);

		List<Integer> refreshDayList = cfg.getRefreshDayList();
		for (Integer weekDay : refreshDayList) {
			// 特殊处理 java 星期天是1
			weekDay++;
			if (weekDay > 7) {
				weekDay = 1;
			}

			String[] timeArr = cfg.getAutoRetime().split("_");
			for (String time : timeArr) {
				int hour = Integer.parseInt(time.substring(0, 2));
				int min = Integer.parseInt(time.substring(3, 5));
				if (DateUtils.getDayDistance(lastDayTime, todayTime) >= 7) {
					refreshCommodity(vo);
					return;
				} else {
					clastDay.set(Calendar.DAY_OF_WEEK, weekDay);
					clastDay.set(Calendar.HOUR_OF_DAY, hour);
					clastDay.set(Calendar.MINUTE, min);

					ctoday.set(Calendar.DAY_OF_WEEK, weekDay);
					ctoday.set(Calendar.HOUR_OF_DAY, hour);
					ctoday.set(Calendar.MINUTE, min);

					if ((lastDayTime < clastDay.getTimeInMillis() && todayTime >= clastDay.getTimeInMillis()) || lastDayTime < ctoday.getTimeInMillis() && todayTime >= ctoday.getTimeInMillis()) {
						refreshCommodity(vo);
						return;
					}
				}
			}
		}
	}

	public void checkRefreshByDay(StoreData vo, StoreCfg cfg, long lastDayTime, long todayTime) {
		Calendar ctoday = DateUtils.getDayZeroCalendar(todayTime);
		Calendar clastDay = DateUtils.getDayZeroCalendar(lastDayTime);

		String[] timeArr = cfg.getAutoRetime().split("_");
		for (String time : timeArr) {
			int hour = Integer.parseInt(time.substring(0, 2));
			int min = Integer.parseInt(time.substring(3, 5));
			ctoday.set(Calendar.HOUR_OF_DAY, hour);
			ctoday.set(Calendar.MINUTE, min);
			long todayUpdateTime = ctoday.getTimeInMillis();
			clastDay.set(Calendar.HOUR_OF_DAY, hour);
			clastDay.set(Calendar.MINUTE, min);
			long lastUpdateTime = clastDay.getTimeInMillis();

			if ((lastDayTime < lastUpdateTime && todayTime >= lastUpdateTime) || (lastDayTime < todayUpdateTime && todayTime >= todayUpdateTime)) {
				vo.setCommodity(RandomList(vo.getType().getOrder()));
				vo.setLastRefreshTime(System.currentTimeMillis());
				vo.setRefresh(true);
				storeDataHolder.add(this.m_pPlayer, vo.getType().getOrder());
				return;
			}
		}
	}

	private void refreshCommodity(StoreData vo) {
		vo.setCommodity(RandomList(vo.getType().getOrder()));
		vo.setLastRefreshTime(System.currentTimeMillis());
		vo.setRefresh(true);
		storeDataHolder.add(this.m_pPlayer, vo.getType().getOrder());

	}

	/**
	 * 花钱刷新商店
	 * 
	 * @param storeType
	 * @return
	 */
	public int ResqRefresh(Player player, int storeType) {
		StoreCfg cfg = StoreCfgDAO.getInstance().getStoreCfg(storeType);
		if (cfg == null) {
			GameLog.info("store", m_pPlayer.getUserId(), "配置表错误：store表没有类型为" + storeType + "的数据", null);
			return -1;
		}
		// by franky
		StorePrivilegeNames pname = null;
		eStoreType stype = eStoreType.getDef(storeType);
		switch (stype) {
		case General:
			pname = StorePrivilegeNames.storeFreeRefreshCnt;
			break;
		case Secret:
			pname = StorePrivilegeNames.mysteryStoreFreeRefreshCnt;
			break;
		case Blackmark:
			pname = StorePrivilegeNames.bmstoreFreeRefreshCnt;
			break;
		default:
			break;
		}
		int freeRefreshCount = pname != null ? m_pPlayer.getPrivilegeMgr().getIntPrivilege(pname) : 0;

		StoreData pStoreData = getStore(storeType);
		if (pStoreData == null) {
			return -1;
		}
		boolean blnFree = false;
		int cost = 0;
		int refreshnum = 0;
		int freeRefreshNum = pStoreData.getFreeRefreshNum();
		if (freeRefreshNum >= freeRefreshCount) {
			blnFree = false;
			eSpecialItemId etype = eSpecialItemId.getDef(cfg.getCostType());
			refreshnum = pStoreData.getRefreshNum();
			String[] split = cfg.getRefreshCost().split("_");
			if (refreshnum >= split.length) {
				return -3;
			}
			cost = Integer.parseInt(split[refreshnum]);
			if (m_pPlayer.getReward(etype) < cost) {
				return -2;
			}
		} else {
			blnFree = true;
		}

		List<CommodityData> randomList = RandomList(storeType);
		int rightSize = getStoreCommodityListLength(storeType);
		if (rightSize == 0 || rightSize != randomList.size()) {
			return -1;
		}
		if (!blnFree) {
			ItemBagMgr.getInstance().addItem(player, cfg.getCostType(), -cost);
			refreshnum++;
			pStoreData.setRefreshNum(refreshnum);
		} else {
			freeRefreshNum++;
			pStoreData.setFreeRefreshNum(freeRefreshNum);
		}

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
	public int BuyCommodity(Player player, int commodityId, int count) {
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
					ItemBagMgr.getInstance().addItem(player, cfg.getGoodsId(), cfg.getCount());
					ItemBagMgr.getInstance().addItem(player, cfg.getCostType(), -cfg.getCost());
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
		StoreData storeData = getStore(storeType);
		storeData.setRefresh(false);
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
			storeData.setFreeRefreshNum(0);
		}

		save();
	}

	public void notifyVipUpgrade() {
		ConcurrentHashMap<Integer, StoreData> m_StoreData = storeDataHolder.get().getStoreDataMap();
		for (Iterator<Entry<Integer, StoreData>> iterator = m_StoreData.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, StoreData> entry = iterator.next();
			StoreData data = entry.getValue();
			if (data != null) {
				data.setFreeRefreshNum(0);
			}
		}
		m_pPlayer.getSettingMgr().notifyVipUpgrade();
	}

	/**
	 * 觉醒抽箱
	 * 
	 * @param player
	 * @param type
	 * @param resp
	 */
	public void processWakenLottery(Player player, eWakenRewardDrawType type, StoreResponse.Builder resp, int consumeType) {
		WakenLotteryProcesser.getInstantce().processWakenLottery(player, storeDataHolder, type, resp, consumeType);
	}

	/**
	 * 兑换物品
	 * 
	 * @param commodityId
	 * @param count
	 * @return
	 */
	public int exchangeItem(Player player, int commodityId, int time) {
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
				int exchangeCount = pCommodityData.getExchangeCount();
				exchangeCount += time;
				if (cfg.getExchangeTime() == 0 || exchangeCount <= cfg.getExchangeTime()) {
					eSpecialItemId etype = eSpecialItemId.getDef(cfg.getCostType());
					if (m_pPlayer.getReward(etype) < cfg.getCost() * time) {
						return -2;
					}
					ItemBagMgr.getInstance().addItem(player, cfg.getGoodsId(), cfg.getCount() * time);
					ItemBagMgr.getInstance().addItem(player, cfg.getCostType(), -(cfg.getCost() * time));
					pCommodityData.setExchangeCount(exchangeCount);
					storeDataHolder.update(m_pPlayer, storeCfg.getType());
					return exchangeCount;
				}
				return -3;
			}
		}
		return 0;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isWakenStoreRedPoint() {
		StoreData store = getStore(eStoreType.Waken);
		if (store != null && store.getDrawTime() <= 0) {
			return true;
		}
		return false;
	}

	public boolean isStoreRefresh() {
		Enumeration<StoreData> storeEnumeration = getStoreEnumeration();
		while (storeEnumeration.hasMoreElements()) {
			StoreData storeData = (StoreData) storeEnumeration.nextElement();
			if (storeData.isRefresh()) {
				return true;
			}
		}
		return false;
	}

	public void viewStore(int type) {
		eStoreType storeType = eStoreType.getDef(type);
		StoreData store = getStore(storeType);
		store.setRefresh(false);
		storeDataHolder.update(m_pPlayer, type);
	}
}
