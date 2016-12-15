package com.playerdata.activity.limitHeroType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.common.HPCUtil;
import com.common.RefInt;
import com.common.serverdata.ServerCommonData;
import com.common.serverdata.ServerCommonDataHolder;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitGambleDropCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitGambleDropCfgDAO;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitGamblePlanCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitGamblePlanCfgDAO;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroBoxCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroBoxCfgDAO;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfgDAO;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroRankCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroRankCfgDAO;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItemHolder;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeSubItem;
import com.playerdata.activity.limitHeroType.gamble.FreeGamble;
import com.playerdata.activity.limitHeroType.gamble.Gamble;
import com.playerdata.activity.limitHeroType.gamble.SingelGamble;
import com.playerdata.activity.limitHeroType.gamble.TenGamble;
import com.playerdata.activityCommon.activityType.IndexRankJudgeIF;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.gamble.datamodel.DropMissingCfg;
import com.rw.service.gamble.datamodel.DropMissingCfgHelper;
import com.rw.service.gamble.datamodel.DropMissingLogic;
import com.rw.service.role.MainMsgHandler;
import com.rwproto.ActivityLimitHeroTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityLimitHeroTypeProto.ActivityCommonRspMsg.Builder;
import com.rwproto.ActivityLimitHeroTypeProto.GambleType;
import com.rwproto.ActivityLimitHeroTypeProto.GamebleReward;
import com.rwproto.ActivityLimitHeroTypeProto.RankRecord;

public class ActivityLimitHeroTypeMgr implements ActivityRedPointUpdate, IndexRankJudgeIF{
	
	private static final int ACTIVITY_INDEX_BEGIN = 120000;
	private static final int ACTIVITY_INDEX_END = 130000;
	
	public final static int TYPE_FREE_GAMBLE = 0;// 免费单抽
	public final static int TYPE_SINGAL_GAMBLE = 1;// 单抽
	public final static int TYPE_TEN_GAMBLE = 2;// 十连抽

	private final static HashMap<Integer, Gamble> ActivityLimitGambleMap = new HashMap<Integer, Gamble>();
	static {
		ActivityLimitGambleMap.put(TYPE_FREE_GAMBLE, new FreeGamble());
		ActivityLimitGambleMap.put(TYPE_SINGAL_GAMBLE, new SingelGamble());
		ActivityLimitGambleMap.put(TYPE_TEN_GAMBLE, new TenGamble());
	}

	private static ActivityLimitHeroTypeMgr instance = new ActivityLimitHeroTypeMgr();

	public static ActivityLimitHeroTypeMgr getInstance() {
		return instance;
	}

	private final static int MAKEUPEMAIL = 10055;

	public void synCountTypeData(Player player) {
		if (isOpen(System.currentTimeMillis())) {
			ActivityLimitHeroTypeItemHolder.getInstance().synAllData(player);
		}

	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);
		checkClose(player);
	}

	private void checkNewOpen(Player player) {

		String userId = player.getUserId();
		creatItems(userId, true);

	}

	public List<ActivityLimitHeroTypeItem> creatItems(String userid, boolean isHasPlayer) {
		RoleExtPropertyStoreCache<ActivityLimitHeroTypeItem> storeCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_LIMITHERO, ActivityLimitHeroTypeItem.class);
		RoleExtPropertyStore<ActivityLimitHeroTypeItem> store = null;
		List<ActivityLimitHeroTypeItem> addItemList = null;
		ActivityLimitHeroBoxCfgDAO dao = ActivityLimitHeroBoxCfgDAO.getInstance();
		List<ActivityLimitHeroCfg> allCfgList = ActivityLimitHeroCfgDAO.getInstance().getAllCfg();
		int id = Integer.parseInt(ActivityLimitHeroEnum.LimitHero.getCfgId());
		for (ActivityLimitHeroCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据

			if (!isOpen(cfg)) {
				// 活动未开启
				continue;
			}
			if (isHasPlayer) {
				try {
					store = storeCache.getStore(userid);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (store != null) {
					if (store.get(id) != null) {
						continue;
					}
				}
			}

			ActivityLimitHeroTypeItem item = new ActivityLimitHeroTypeItem();

			item.setId(id);
			item.setCfgId(String.valueOf(cfg.getId()));
			item.setUserId(userid);
			item.setVersion(String.valueOf(cfg.getVersion()));
			item.setLastSingleTime(0);
			item.setIntegral(0);
			List<ActivityLimitHeroBoxCfg> boxCfgList = dao.getCfgListByParentID(String.valueOf(cfg.getId()));
			List<ActivityLimitHeroTypeSubItem> subItemList = new ArrayList<ActivityLimitHeroTypeSubItem>();
			if (boxCfgList == null) {
				boxCfgList = new ArrayList<ActivityLimitHeroBoxCfg>();
			}
			for (ActivityLimitHeroBoxCfg boxCfg : boxCfgList) {
				ActivityLimitHeroTypeSubItem subItem = new ActivityLimitHeroTypeSubItem();
				subItem.setCfgId(boxCfg.getId());
				subItem.setIntegral(boxCfg.getIntegral());
				subItem.setRewards(boxCfg.getRewards());
				subItemList.add(subItem);
			}
			item.setSubList(subItemList);
			if (addItemList == null) {
				addItemList = new ArrayList<ActivityLimitHeroTypeItem>();
			}
			addItemList.add(item);
		}
		if (isHasPlayer && addItemList != null) {
			try {
				store.addItem(addItemList);
			} catch (DuplicatedKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return addItemList;
	}

	public boolean isOpen(ActivityLimitHeroCfg cfg) {
		if (cfg != null) {
			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}

	private void checkCfgVersion(Player player) {
		ActivityLimitHeroTypeItemHolder dataHolder = ActivityLimitHeroTypeItemHolder.getInstance();
		List<ActivityLimitHeroTypeItem> itemList = null;// dataHolder.getItemList(player.getUserId());
		ActivityLimitHeroCfgDAO dao = ActivityLimitHeroCfgDAO.getInstance();
		ServerCommonDataHolder scdhDataHolder = ServerCommonDataHolder.getInstance();
		List<ActivityLimitHeroCfg> cfgList = dao.getAllCfg();
		for (ActivityLimitHeroCfg cfg : cfgList) {
			if (!isOpen(cfg)) {
				continue;
			}
			if (itemList == null) {
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityLimitHeroTypeItem freshItem = null;
			for (ActivityLimitHeroTypeItem item : itemList) {
				if (!StringUtils.equals(item.getVersion(), String.valueOf(cfg.getVersion()))) {
					freshItem = item;
				}
			}
			if (freshItem == null) {
				continue;
			}
			freshItem.reset(cfg, dao.newSubItemList(cfg));
			dataHolder.updateItem(player, freshItem);
			ServerCommonData scdData = ServerCommonDataHolder.getInstance().get();
			if (scdData == null) {
				continue;
			}
			List<ActivityLimitHeroRankRecord> list = scdData.getActivityLimitHeroRankRecord();
			if (reFreshRankByVersion(list, cfg)) {
				scdhDataHolder.update(scdData);
			}
		}
	}

	private void checkClose(Player player) {
		ActivityLimitHeroTypeItemHolder dataHolder = ActivityLimitHeroTypeItemHolder.getInstance();
		List<ActivityLimitHeroTypeItem> itemList = null;// dataHolder.getItemList(player.getUserId());
		ActivityLimitHeroCfgDAO dao = ActivityLimitHeroCfgDAO.getInstance();
		List<ActivityLimitHeroCfg> cfgList = dao.getAllCfg();
		long createTime = player.getUserDataMgr().getCreateTime();
		long currentTime = DateUtils.getSecondLevelMillis();
		for (ActivityLimitHeroCfg cfg : cfgList) {
			if (isOpen(cfg)) {// 配置开启
				continue;
			}
			if (createTime > cfg.getEndTime()) {// 配置过旧
				continue;
			}
			if (currentTime < cfg.getStartTime()) {// 配置过新
				continue;
			}
			if (itemList == null) {
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityLimitHeroTypeItem closeItem = null;
			for (ActivityLimitHeroTypeItem item : itemList) {
				if (StringUtils.equals(item.getVersion(), String.valueOf(cfg.getVersion()))) {
					closeItem = item;
					break;
				}
			}
			if (closeItem == null) {
				continue;
			}
			if (!closeItem.isClosed()) {
				checkRankRewards(player, closeItem);// 邮件派发排行奖励
				sendEmailIfGiftNotTaken(player, closeItem);
				closeItem.setClosed(true);
				closeItem.setTouchRedPoint(true);
				dataHolder.updateItem(player, closeItem);
			}
		}

	}

	private void sendEmailIfGiftNotTaken(Player player, ActivityLimitHeroTypeItem item) {
		List<ActivityLimitHeroTypeSubItem> subList = item.getSubList();
		ActivityLimitHeroCfg cfg = ActivityLimitHeroCfgDAO.getInstance().getCfgById(item.getCfgId());
		if (cfg == null) {
			GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "cfg删除早了", null);
			return;
		}
		ComGiftMgr comGiftMgr = ComGiftMgr.getInstance();
		for (ActivityLimitHeroTypeSubItem subItem : subList) {// 配置表里的每种奖励

			if (item.getIntegral() >= subItem.getIntegral() && !subItem.isTanken()) {
				boolean isAdd = comGiftMgr.addGiftTOEmailById(player, subItem.getRewards(), MAKEUPEMAIL + "", cfg.getEmailTitle());
				subItem.setTanken(true);
				if (!isAdd) {
					GameLog.error(LogModule.ComActivityVitality, player.getUserId(), "通用活动关闭后未领取奖励获取邮件内容失败", null);
				}
			}
		}

	}

	private void checkRankRewards(Player player, ActivityLimitHeroTypeItem item) {
		ServerCommonData scdData = ServerCommonDataHolder.getInstance().get();
		ActivityLimitHeroCfgDAO activityLimitHeroCfgDAO = ActivityLimitHeroCfgDAO.getInstance();
		ActivityLimitHeroRankCfgDAO activityLimitHeroRankCfgDAO = ActivityLimitHeroRankCfgDAO.getInstance();
		if (scdData == null) {
			return;
		}
		List<ActivityLimitHeroRankRecord> list = scdData.getActivityLimitHeroRankRecord();
		boolean isHas = false;
		int num = 0;
		for (ActivityLimitHeroRankRecord record : list) {
			if (StringUtils.equals(record.getUid(), player.getUserId())) {
				isHas = true;
				num++;
				break;
			}
			num++;
		}
		/** 有记录，刷新下 */
		if (!isHas) {
			return;
		}
		ActivityLimitHeroCfg cfg = activityLimitHeroCfgDAO.getCfgById(item.getCfgId());
		if (cfg == null) {
			return;
		}

		List<ActivityLimitHeroRankCfg> subCfgList = activityLimitHeroRankCfgDAO.getByParentCfgId(String.valueOf(cfg.getId()));
		String tmpReward = null;
		for (ActivityLimitHeroRankCfg subCfg : subCfgList) {
			if (num >= subCfg.getRankRanges()[0] && num <= subCfg.getRankRanges()[1]) {
				tmpReward = subCfg.getRewards();
				break;
			}
		}
		item.setRankRewards(tmpReward);
		ComGiftMgr.getInstance().addGiftTOEmailById(player, item.getRankRewards(), MAKEUPEMAIL + "", cfg.getEmailTitle());

	}

	public ActivityComResult getRewards(Player player, int boxId) {
		ActivityComResult result = ActivityComResult.newInstance(false);
		result.setReason("");

		ActivityLimitHeroTypeItemHolder dataHolder = ActivityLimitHeroTypeItemHolder.getInstance();
		ActivityLimitHeroTypeItem dataItem = dataHolder.getItem(player.getUserId());
		List<ActivityLimitHeroTypeSubItem> subList = dataItem.getSubList();
		ActivityLimitHeroTypeSubItem tagratItem = null;
		for (ActivityLimitHeroTypeSubItem subItem : subList) {
			if (!StringUtils.equals(subItem.getCfgId(), boxId + "")) {
				continue;
			}
			if (subItem.isTanken()) {
				continue;
			}
			tagratItem = subItem;
			break;
		}
		if (tagratItem == null) {
			result.setReason("没有对应的奖励箱子");
			return result;
		}
		tagratItem.setTanken(true);
		ComGiftMgr.getInstance().addGiftById(player, tagratItem.getRewards());
		dataHolder.updateItem(player, dataItem);
		result.setReason("恭喜你获得奖励");
		result.setSuccess(true);
		return result;
	}

	public ActivityComResult gamble(Player player, ActivityCommonReqMsg commonReq, Builder response) {
		ActivityComResult result = ActivityComResult.newInstance(false);
		result.setReason("");
		ActivityLimitHeroTypeItemHolder dataHolder = new ActivityLimitHeroTypeItemHolder();
		ActivityLimitHeroTypeItem dataItem = dataHolder.getItem(player.getUserId());
		if (dataItem == null) {
			GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "没有数据的用户发来了抽卡申请", null);
			result.setReason("数据异常");
			return result;
		}
		ActivityLimitHeroCfg cfg = ActivityLimitHeroCfgDAO.getInstance().getCfgById(dataItem.getCfgId());
		if (cfg == null) {
			GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "有数据的用户发来了抽卡申请,没找到配置活动表", null);
			result.setReason("数据异常");
			return result;
		}
		ActivityLimitGamblePlanCfg planCfg = ActivityLimitGamblePlanCfgDAO.getInstance().getCfgByType(commonReq.getGambleType().getNumber(), player.getLevel());
		if (planCfg == null) {
			GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "有数据的用户发来了抽卡申请,没找到配置抽卡方案表", null);
			result.setReason("数据异常");
			return result;
		}
		boolean isFree = false;
		if (!isEnoughGold(player, commonReq, planCfg, cfg, dataItem, isFree)) {
			result.setReason("钻石不足");
			return result;
		}
		RefInt guatanteeTimes = new RefInt();
		int type = getType(dataItem, planCfg, commonReq, isFree, guatanteeTimes);
		Gamble handler = ActivityLimitGambleMap.get(type);
		String map = handler.gamble(player, dataHolder, planCfg, guatanteeTimes.value);
		dataHolder.updateItem(player, dataItem);
		doDropList(player, response, map);
		result.setSuccess(true);
		reFreshIntegralRank(player, dataItem, cfg);
		return result;
	}

	/**
	 * 是否钻石足够；如果是免费次数，则将消耗置0
	 * 
	 * @param isFree
	 */
	private boolean isEnoughGold(Player player, ActivityCommonReqMsg commonReq, ActivityLimitGamblePlanCfg planCfg, ActivityLimitHeroCfg cfg, ActivityLimitHeroTypeItem item, boolean isFree) {
		int spendNeed = planCfg.getMoneyNum();
		if (commonReq.getGambleType() == GambleType.SINGLE) {
			long now = System.currentTimeMillis();
			long lastTime = item.getLastSingleTime();
			if ((now - lastTime) > planCfg.getRecoverTime() * 1000) {
				item.setLastSingleTime(now);
				spendNeed = 0;
				isFree = true;
			}
			if (player.getUserGameDataMgr().getGold() < spendNeed) {
				return false;
			} else {
				player.getUserGameDataMgr().addGold(-spendNeed);
				item.setIntegral(item.getIntegral() + cfg.getSingleintegral());
				return true;
			}
		} else if (commonReq.getGambleType() == GambleType.TEN) {
			if (player.getUserGameDataMgr().getGold() < spendNeed) {
				return false;
			} else {
				player.getUserGameDataMgr().addGold(-spendNeed);
				item.setIntegral(item.getIntegral() + cfg.getTenintegral());
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取方案，以及触发的保底次数
	 * 
	 * @param guatanteeTimes
	 */
	private int getType(ActivityLimitHeroTypeItem dataItem, ActivityLimitGamblePlanCfg planCfg, ActivityCommonReqMsg commonReq, boolean isFree, RefInt guatanteeTimes) {
		int type = TYPE_SINGAL_GAMBLE;
		int guatanteeTime = 0;
		type = getBaseType(commonReq, isFree, type);
		int count = planCfg.getDropItemCount();
		// 根据单十抽来判断是否触发保底
		if (commonReq.getGambleType() == GambleType.SINGLE) {

			for (Integer time : planCfg.getGuaranteeList()) {
				if (time == (dataItem.getGuarantee() + count)) {
					guatanteeTime++;
					break;
				}
			}
			if (dataItem.getGuarantee() != 0 && (dataItem.getGuarantee() + count) % planCfg.getMaxGuarantee() == 0) {
				guatanteeTime++;
			}
			dataItem.setGuarantee(dataItem.getGuarantee() + count);
		} else if (commonReq.getGambleType() == GambleType.TEN) {
			int lastGuarantee = dataItem.getGuarantee() + count;
			for (Integer time : planCfg.getGuaranteeList()) {
				if (lastGuarantee >= time && dataItem.getGuarantee() < time) {
					guatanteeTime++;
				}
			}
			for (int i = dataItem.getGuarantee() + 1; i < lastGuarantee + 1; i++) {
				if (i % planCfg.getMaxGuarantee() == 0 && dataItem.getGuarantee() != 0) {
					guatanteeTime++;
				}
			}
			dataItem.setGuarantee(dataItem.getGuarantee() + count);
		}
		guatanteeTimes.value = guatanteeTime;
		return type;
	}

	/** 在不计算保底的基础上简单分类 */
	private int getBaseType(ActivityCommonReqMsg commonReq, boolean isFree, int type) {
		if (commonReq.getGambleType() == GambleType.SINGLE) {
			if (isFree) {
				type = TYPE_FREE_GAMBLE;
			} else {
				type = TYPE_SINGAL_GAMBLE;
			}
		} else if (commonReq.getGambleType() == GambleType.TEN) {
			type = TYPE_TEN_GAMBLE;
		}
		return type;
	}

	public String getGambleRewards(Player player, Map<Integer, Integer> planList) {
		StringBuilder strbuild = new StringBuilder();
		int randomGroup = HPCUtil.getRandom().nextInt(10000);
		int groupId = getRandomGroup(planList, randomGroup);
		List<ActivityLimitGambleDropCfg> cfgList = ActivityLimitGambleDropCfgDAO.getInstance().getActivityLimitGambleDropCfgByPoolId(groupId);
		int sumWeight = ActivityLimitGambleDropCfgDAO.getInstance().getSumWeightByPoolId(groupId);
		int result = HPCUtil.getRandom().nextInt(sumWeight);
		DropMissingCfg cfg = DropMissingCfgHelper.getInstance().getCfgById(cfgList.get(0).getItemID());
		if (cfgList.size() == 1 && cfg != null) {
			// 从某个指定的道具组里随机一个，比如蓝装，绿装，紫装
			String id = DropMissingLogic.getInstance().searchMissingItem(player, cfg);

			if (id == null) {
				planList.remove(planList.get(groupId));
				if (planList.isEmpty()) {
					return null;
				}
				return getGambleRewards(player, planList);
			}

			return strbuild.append(id).append("~").append(1).toString();
		}

		ActivityLimitGambleDropCfg resultCfg = getRandomCfg(cfgList, result);
		strbuild.append(resultCfg.getItemID()).append("~").append(resultCfg.getSlotCount());
		return strbuild.toString();
	}

	private ActivityLimitGambleDropCfg getRandomCfg(List<ActivityLimitGambleDropCfg> list, int result) {
		int value = 0;
		for (ActivityLimitGambleDropCfg cfg : list) {
			value += cfg.getWeight();
			if (result < value) {
				return cfg;
			}
		}
		return list.size() > 0 ? list.get(0) : null;
	}

	private int getRandomGroup(Map<Integer, Integer> planList, int result) {
		int value = 0;
		int firstPlan = 0;
		int i = 0;
		for (Map.Entry<Integer, Integer> entry : planList.entrySet()) {
			if (i == 0)
				firstPlan = entry.getKey();
			i++;
			value += entry.getValue();
			if (result < value) {
				return entry.getKey();
			}
		}
		return planList.size() > 0 ? planList.get(firstPlan) : null;
	}

	// id~num,id2~num2格式奖励加入response和背包;如果是英雄，则为id_starlevel~num;
	private void doDropList(Player player, Builder response, String map) {
		ArrayList<GamebleReward> dropList = new ArrayList<GamebleReward>();
		MainMsgHandler mainMsgHandler = MainMsgHandler.getInstance();
		String reward = "";
		if (map == null) {
			return;
		}
		String[] splitList = map.split(",");
		for (String str : splitList) {
			String[] idAndNum = str.split("~");
			Integer modelId = -1;
			Integer count = Integer.parseInt(idAndNum[1]);
			// player.getItemBagMgr().addItem(modelId, count);
			if (idAndNum[0].indexOf("_") != -1) {// 佣兵
				player.getHeroMgr().addHero(player, idAndNum[0]);// 自动转碎片
				mainMsgHandler.sendPmdJtYb(player, idAndNum[0]);
			} else {
				modelId = Integer.parseInt(idAndNum[0]);
				reward += "," + modelId + "~" + count;
				mainMsgHandler.sendPmdJtGoods(player, idAndNum[0]);
			}

			GamebleReward.Builder data = GamebleReward.newBuilder();
			data.setRewardId(idAndNum[0]);
			data.setRewardNum(count);
			dropList.add(data.build());
		}
		ItemBagMgr.getInstance().addItemByPrizeStr(player, reward);
		response.addAllGamebleReward(dropList);
	}

	/** 保存积分榜前xx名的玩家抽卡信息 */
	private void reFreshIntegralRank(Player player, ActivityLimitHeroTypeItem dataItem, ActivityLimitHeroCfg cfg) {
		ServerCommonDataHolder serverCommonDataHolder = ServerCommonDataHolder.getInstance();
		ServerCommonData scdData = serverCommonDataHolder.get();
		if (scdData == null) {
			return;
		}

		List<ActivityLimitHeroRankRecord> list = scdData.getActivityLimitHeroRankRecord();

		if (reFreshRankByVersion(list, cfg)) {
			serverCommonDataHolder.update(scdData);
		}
		boolean isHas = false;
		for (ActivityLimitHeroRankRecord record : list) {
			if (StringUtils.equals(record.getUid(), player.getUserId())) {
				record.setIntegral(dataItem.getIntegral());
				record.setRegditTime(System.currentTimeMillis());
				isHas = true;
				break;
			}
		}
		/** 有记录，刷新下 */
		if (isHas) {
			reSort(list);
			serverCommonDataHolder.update(scdData);
			return;
		}

		ActivityLimitHeroRankRecord record = new ActivityLimitHeroRankRecord();
		record.setIntegral(dataItem.getIntegral());
		record.setPlayerName(player.getUserName());
		record.setUid(player.getUserId());
		record.setRegditTime(System.currentTimeMillis());
		record.setVersion(String.valueOf(cfg.getVersion()));

		/** 没记录，但不用抢 */
		if (list.size() < cfg.getRankNumer()) {
			list.add(record);
			reSort(list);
			serverCommonDataHolder.update(scdData);
			return;
		}
		/** 抢 */
		ActivityLimitHeroRankRecord recordTmp = list.get(list.size() - 1);
		if (record.getIntegral() <= recordTmp.getIntegral()) {
			// **没抢过
			return;
		}

		list.remove(list.size() - 1);
		list.add(record);
		reSort(list);
		serverCommonDataHolder.update(scdData);
	}

	/**
	 * 降序排序，相同积分时先到先上
	 * 
	 * @param response
	 */
	private void reSort(List<ActivityLimitHeroRankRecord> list) {
		Collections.sort(list, new Comparator<ActivityLimitHeroRankRecord>() {
			@Override
			public int compare(ActivityLimitHeroRankRecord o1, ActivityLimitHeroRankRecord o2) {
				// TODO Auto-generated method stub
				if (o1.getIntegral() > o2.getIntegral()) {
					return -1;
				} else if (o1.getIntegral() == o2.getIntegral()) {
					if (o1.getRegditTime() < o2.getRegditTime()) {
						return -1;
					}
					return 1;
				} else {
					return 1;
				}
			}
		});
	}

	/**
	 * 
	 * @param list 活动换版本时清空数据
	 * @param cfg
	 */
	private boolean reFreshRankByVersion(List<ActivityLimitHeroRankRecord> list, ActivityLimitHeroCfg cfg) {
		for (ActivityLimitHeroRankRecord record : list) {
			if (!StringUtils.equals(record.getVersion(), String.valueOf(cfg.getVersion()))) {
				list.clear();
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param player
	 * @param commonReq 排行榜数据在此处推给客户端；同时触发一次holder推送免费时间
	 * @param response 要保证发过去的每条记录里的number数据时对应的排名，就必须确保插入更新时都有做排序操作
	 * @return
	 */
	public ActivityComResult viewRank(Player player, ActivityCommonReqMsg commonReq, Builder response) {
		ActivityComResult result = ActivityComResult.newInstance(false);
		result.setReason("");
		ServerCommonData scdData = ServerCommonDataHolder.getInstance().get();
		if (scdData == null) {
			result.setSuccess(true);
			result.setReason("排行榜是空的");
			return result;
		}
		List<ActivityLimitHeroRankRecord> list = scdData.getActivityLimitHeroRankRecord();
		if (list == null || list.isEmpty()) {
			result.setSuccess(true);
			result.setReason("排行榜是空的");
			return result;
		}
		// compareAndAddResponse(map,response);
		addResponse(list, response);
		result.setSuccess(true);
		result.setReason("成功获得");

		ActivityLimitHeroTypeItemHolder dataHolder = new ActivityLimitHeroTypeItemHolder();
		ActivityLimitHeroTypeItem dataItem = dataHolder.getItem(player.getUserId());
		dataHolder.synData(player, dataItem);
		return result;
	}

	private void addResponse(List<ActivityLimitHeroRankRecord> list, Builder response) {
		int num = 0;
		for (ActivityLimitHeroRankRecord listRecord : list) {
			RankRecord.Builder record = RankRecord.newBuilder();
			record.setNumber(num);
			record.setName(listRecord.getPlayerName());
			record.setGetIntegral(listRecord.getIntegral());
			record.setUid(listRecord.getUid());
			response.addRecord(record.build());
			num++;
		}

	}

	@Override
	public void updateRedPoint(Player player, String enumStr) {
		ActivityLimitHeroTypeItemHolder activityLimitHeroItemHolder = new ActivityLimitHeroTypeItemHolder();
		ActivityLimitHeroCfg cfg = ActivityLimitHeroCfgDAO.getInstance().getCfgById(enumStr);
		if (cfg == null) {
			return;
		}

		ActivityLimitHeroTypeItem dataItem = activityLimitHeroItemHolder.getItem(player.getUserId());
		if (dataItem == null) {
			GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动数据", null);
			return;
		}
		if (!dataItem.isTouchRedPoint()) {
			dataItem.setTouchRedPoint(true);
			activityLimitHeroItemHolder.updateItem(player, dataItem);
		}
	}

	public boolean isOpen(long param) {
		List<ActivityLimitHeroCfg> allCfgList = ActivityLimitHeroCfgDAO.getInstance().getAllCfg();
		for (ActivityLimitHeroCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (isOpen(cfg, param)) {
				return true;
			}
		}
		return false;
	}

	public boolean isOpen(ActivityLimitHeroCfg cfg, long param) {
		if (cfg != null) {
			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = param;
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}

	@Override
	public boolean isThisActivityIndex(int index) {
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
