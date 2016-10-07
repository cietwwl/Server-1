package com.playerdata.activity.rankType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bm.rank.RankType;
import com.bm.rank.arena.ArenaRankingComparable;
import com.bm.rank.fightingAll.FightingComparable;
import com.common.RefParam;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.RankingMgr;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfg;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfgDAO;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeSubCfg;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeSubCfgDAO;
import com.playerdata.activity.rankType.cfg.SendRewardRecord;
import com.playerdata.activity.rankType.data.ActivityRankTypeEntry;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.playerdata.activity.rankType.data.ActivityRankTypeItemHolder;
import com.playerdata.activity.rankType.data.ActivityRankTypeUserInfo;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.dataaccess.mapitem.MapItemValidateParam;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.ranking.RankingUtils;
import com.rwbase.dao.ranking.RankingSort.RankFight;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwproto.RankServiceProtos.RankInfo;

public class ActivityRankTypeMgr implements ActivityRedPointUpdate {

	private static ActivityRankTypeMgr instance = new ActivityRankTypeMgr();

	private static Map<String, SendRewardRecord> sendMap = new HashMap<String, SendRewardRecord>();



	public static ActivityRankTypeMgr getInstance() {
		return instance;
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

	public List<ActivityRankTypeItem> creatItems(String userId,boolean isHasPlayer) {
		RoleExtPropertyStoreCache<ActivityRankTypeItem> storeCach = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_RANK, ActivityRankTypeItem.class);
		PlayerExtPropertyStore<ActivityRankTypeItem> store = null;
		
		List<ActivityRankTypeItem> addItemList = null;
		List<ActivityRankTypeCfg> allCfgList = ActivityRankTypeCfgDAO
				.getInstance().getAllCfg();
		for (ActivityRankTypeCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(cfg)) {
				continue;
			}
			ActivityRankTypeEnum RankTypeEnum = ActivityRankTypeEnum
					.getById(cfg.getEnumId());
			if (RankTypeEnum == null) {
				continue;
			}
//			String itemId = ActivityRankTypeHelper.getItemId(userId,
//					RankTypeEnum);
			int id = Integer.parseInt(RankTypeEnum.getCfgId());
			if(isHasPlayer){
				try {
					store = storeCach.getStore(userId);
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
			ActivityRankTypeItem item = new ActivityRankTypeItem();
			item.setId(id);
			item.setUserId(userId);
			item.setCfgId(cfg.getId());
			item.setEnumId(cfg.getEnumId());
			item.setVersion(cfg.getVersion());
			if (addItemList == null) {
				addItemList = new ArrayList<ActivityRankTypeItem>();
			}
			addItemList.add(item);
		}
		if(isHasPlayer&&addItemList != null){
			try {
				store.addItem(addItemList);
			} catch (DuplicatedKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return addItemList;
	}

	public boolean isOpen(ActivityRankTypeCfg activityRankTypeCfg) {

		long startTime = activityRankTypeCfg.getStartTime();
		long endTime = activityRankTypeCfg.getEndTime();
		long currentTime = System.currentTimeMillis();

		return currentTime < endTime && currentTime >= startTime;
	}

	private void checkCfgVersion(Player player) {
		ActivityRankTypeItemHolder dataHolder = ActivityRankTypeItemHolder
				.getInstance();
		ActivityRankTypeCfgDAO dao = ActivityRankTypeCfgDAO.getInstance();
		List<ActivityRankTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		List<ActivityRankTypeCfg> cfgList = dao.getAllCfg();
		for(ActivityRankTypeCfg cfg : cfgList){
			if(!isOpen(cfg)){
				continue;
			}
			if(itemList == null){
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityRankTypeItem freshItem = null;
			for(ActivityRankTypeItem item : itemList){
				if(StringUtils.equals(item.getEnumId(), cfg.getEnumId())&&!StringUtils.equals(item.getVersion(), cfg.getVersion())){
					freshItem = item;
				}
			}
			if(freshItem == null){
				continue;
			}
			freshItem.reset(cfg);
			dataHolder.updateItem(player, freshItem);
		}
	}

	private void checkClose(Player player) {
		ActivityRankTypeItemHolder dataHolder = ActivityRankTypeItemHolder.getInstance();
		ComGiftMgr comGiftMgr = ComGiftMgr.getInstance();
		List<ActivityRankTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		ActivityRankTypeCfgDAO dao = ActivityRankTypeCfgDAO.getInstance();
		List<ActivityRankTypeCfg> cfgList = dao.getAllCfg();
		long createTime = player.getUserDataMgr().getCreateTime();
		long currentTime = DateUtils.getSecondLevelMillis();
		for(ActivityRankTypeCfg cfg : cfgList){
			if(isOpen(cfg)){//配置开启
				continue;
			}
			if(createTime>cfg.getEndTime()){//配置过旧
				continue;
			}
			if(currentTime < cfg.getStartTime()){//配置过新
				continue;
			}
			if(itemList == null){
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityRankTypeItem closeItem = null;
			for(ActivityRankTypeItem item : itemList){
				if(StringUtils.equals(item.getVersion(), cfg.getVersion())&&StringUtils.equals(item.getEnumId(), cfg.getEnumId())){
					closeItem = item;
					break;
				}			
			}
			if(closeItem == null){
				continue;
			}			
			if (closeItem.getReward() != null) {// 有奖励的进这里
				// 派发；结算时没入榜，结算后不登陆更不会入榜，所以会在此处排除
				closeItem.setTaken(true);
				closeItem.setClosed(true);
				dataHolder.updateItem(player, closeItem);
				comGiftMgr.addtagInfoTOEmail(player,closeItem.getReward(),
				closeItem.getEmailId(), null);
				continue;
			}
			// 没奖的酱油进下边设置关闭
			SendRewardRecord record = sendMap.get(closeItem.getEnumId());
			if (record == null) {
				continue;
			}
			long sendtime = record.getLasttime();
			if (sendtime == 0) {
				continue;
			}
			long nowtime = System.currentTimeMillis();
			if (sendtime > nowtime) {
				long tmp = sendtime;
				sendtime = nowtime;
				nowtime = tmp;
			}
			if (DateUtils.getAbsoluteHourDistance(sendtime, nowtime) > 1) {// 设置固定时间后，再生成的奖励也不触发，防止当机；此限制应加在服务器数据表里，现在临时加在内存的静态变量中；
				closeItem.setClosed(true);
				dataHolder.updateItem(player, closeItem);
			}
		}	
	}

	public boolean isClose(ActivityRankTypeItem activityRankTypeItem) {
		ActivityRankTypeCfg cfgById = ActivityRankTypeCfgDAO.getInstance()
				.getCfgById(activityRankTypeItem.getCfgId());
		if (cfgById == null) {
			return false;
		}
		long endTime = cfgById.getEndTime();
		long currentTime = System.currentTimeMillis();
		return currentTime > endTime;
	}

	public boolean isCanGift(ActivityRankTypeCfg cfg, SendRewardRecord record) {
		if (isOpen(cfg)) {
			return false;
		}
		if (!StringUtils.equals(record.getId(), cfg.getId())) {
			return false;
		}
		return true;
	}

	public List<ActivityRankTypeEntry> getRankList(
			ActivityRankTypeEnum rankType, int offset, int limit) {
		return new ArrayList<ActivityRankTypeEntry>();
	}

	public ActivityRankTypeUserInfo getUserInfo(Player player,
			ActivityRankTypeEnum rankType) {
		return null;
	}

	/** 定时核查一遍，将排行奖励派发到用户数据库 */
	public void sendGift() {
		if (sendMap.size() == 0) {
			creatMap();//
		}
		RankingMgr rankingMgr = RankingMgr.getInstance();
		ActivityRankTypeItemHolder activityRankTypeItemHolder = ActivityRankTypeItemHolder
				.getInstance();
		ActivityRankTypeSubCfgDAO activityRankTypeSubCfgDAO = ActivityRankTypeSubCfgDAO
				.getInstance();
		List<ActivityRankTypeCfg> cfgList = ActivityRankTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityRankTypeCfg cfg : cfgList) {// 所有的配表活动
			RefParam<ActivityRankTypeEnum> enumValue = new RefParam<ActivityRankTypeEnum>();
			List<ActivityRankTypeSubCfg> subCfgList = new ArrayList<ActivityRankTypeSubCfg>();
			boolean isCan = checkCfgIsCanSend(cfg,activityRankTypeSubCfgDAO,enumValue,subCfgList);
			if(!isCan){
				continue;
			}
			ActivityRankTypeEnum activityRankTypeEnum = enumValue.value;
			for (Integer ranktype : activityRankTypeEnum.getRankTypes()) {// 该配表对应的所有排行榜，比如竞技场就分4个职业
				List<RankInfo> rankList = new ArrayList<RankInfo>();
				for(ActivityRankTypeSubCfg subCfg : subCfgList){//根据子表去除对应的排行榜数据
					getRankListByRankTypeAndsubCfgNum(subCfg,ranktype,rankList,rankingMgr,activityRankTypeEnum);					
					for (RankInfo rankInfo : rankList) {// 所有的该段榜上榜名单对应匹配用户数据						
						if (rankInfo.getLevel() < cfg.getLevelLimit()) {
							// 虽让上了榜，但级别不够不能触发榜对应的活动
							continue;
						}	
						sendGifgSingel(rankInfo,activityRankTypeItemHolder,activityRankTypeEnum,subCfg);						
					}						
				}			
			}
		}
	}

	/**
	 * 
	 * @param cfg
	 * @param activityRankTypeSubCfgDAO
	 * @return 核实该配置对应的排行榜活动是否需要派发奖励
	 */
	private boolean checkCfgIsCanSend(ActivityRankTypeCfg cfg,
			ActivityRankTypeSubCfgDAO activityRankTypeSubCfgDAO,RefParam<ActivityRankTypeEnum> rankEnum,List<ActivityRankTypeSubCfg> subList) {
		
		rankEnum.value = ActivityRankTypeEnum.getById(cfg.getEnumId());
		
		if (rankEnum.value == null) {
			// 代码没定义配置表里要的活动
			return false;
		}
		
		
		SendRewardRecord record = sendMap.get(cfg.getEnumId());
		if (record.isSend()) {
			// 已经派发过，避免多次触发
			// System.out.println("activityrank.已经拍法过奖励。。。。。。。。。。。。。。。。"+
			// cfg.getId());
			return false;
		}
		if (!isCanGift(cfg, record)) {
			// 是否为同类型活动里处于激活派发状态的一个
			// System.out.println("activityrank.活动还未结束。。。。。。。。。。。。。。。。");
			return false;
		}
		subList.addAll(activityRankTypeSubCfgDAO.getByParentCfgId(cfg.getId()));
		
		if (subList == null||subList.isEmpty()) {
			// 父表没在子表找到对应的list
			return false;
		}
		record.setLasttime(System.currentTimeMillis());
		record.setSend(true);
		return true;
	}

	/**
	 * 
	 * @param subCfg
	 * @param ranktype
	 * @param rankList   根据子表，传入子表偏移和数量，获取对应排行榜数据
	 */
	private void getRankListByRankTypeAndsubCfgNum(
			ActivityRankTypeSubCfg subCfg, Integer ranktype,
			List<RankInfo> rankList, RankingMgr rankingMgr,
			ActivityRankTypeEnum enumType) {
		int size = subCfg.getRankRanges()[1];
		int offset = subCfg.getRankRanges()[0];

		RankType rankType2 = RankType.getRankType(ranktype, 1);
		Ranking<?, RankingLevelData> ranking = RankingFactory
				.getRanking(rankType2.getType());
		EnumerateList<? extends MomentRankingEntry<?, RankingLevelData>> it = ranking
				.getEntriesEnumeration(offset, size);
		for (; it.hasMoreElements();) {
			MomentRankingEntry<?, RankingLevelData> entry = it.nextElement();
			RankingLevelData rankData = entry.getExtendedAttribute();
			RankInfo rankInfo = RankingUtils.createOneRankInfo(rankData,
					rankData.getRankLevel());
			rankList.add(rankInfo);
		}
	}
	
	/**
	 * 挨个派发奖励
	 * @param rankInfo
	 * @param activityRankTypeItemHolder
	 * @param activityRankTypeEnum
	 * @param subCfg
	 */
	private void sendGifgSingel(RankInfo rankInfo,ActivityRankTypeItemHolder activityRankTypeItemHolder,
			ActivityRankTypeEnum activityRankTypeEnum,ActivityRankTypeSubCfg subCfg) {
		if (rankInfo.getRankingLevel() > subCfg.getRankRanges()[1]) {
			// 奖励活动有效位数小于当前榜上用户的排名
			return;
		}
		String userId = rankInfo.getHeroUUID();
		PlayerExtPropertyStore<ActivityRankTypeItem> itemStore = activityRankTypeItemHolder
				.getItemStore(userId);
		if (itemStore == null) {
			return;
		}
		ActivityRankTypeItem targetItem = activityRankTypeItemHolder
				.getItem(userId,activityRankTypeEnum);
		if (targetItem == null) {
			// 有排行无登录时生成的排行榜活动奖励数据，说明是机器人或活动期间没登陆过
			return;
		}							
		System.out.println(userId);
		String tmpReward = subCfg.getReward();
		String emaiId = subCfg.getEmailId();//
		targetItem.setReward(tmpReward);
		targetItem.setEmailId(emaiId);
		itemStore.update(targetItem.getId());	
	}
	
	/** 开服第一次触发时，初始化排行榜派奖的id-版本号；后续核实活动过期后，初始化是否派发和派发时间 */
	public void creatMap() {
		ActivityRankTypeCfgDAO activityRankTypeCfgDAO = ActivityRankTypeCfgDAO
				.getInstance();
		List<ActivityRankTypeCfg> cfgList = activityRankTypeCfgDAO.getAllCfg();
		for (ActivityRankTypeCfg cfg : cfgList) {
			if (sendMap.get(cfg.getEnumId()) != null) {
				continue;
			}
			ActivityRankTypeCfg modelCfg = activityRankTypeCfgDAO
					.getCfgByModleCfgEnumId(cfg.getEnumId());

			SendRewardRecord record = new SendRewardRecord();
			record.setId(modelCfg.getId());
			record.setVersion(modelCfg.getVersion());
			record.setSend(false);
			record.setEnumId(modelCfg.getEnumId());
			sendMap.put(cfg.getEnumId(), record);
		}
	}

	/** 每一个小时检查一遍配置文件对应的cfglist；如果有新增id或者更高的活动版本号，推进map;此方法可支持热更新，暂时不用 */
	public void changeMap() {
		List<ActivityRankTypeCfg> cfgList = ActivityRankTypeCfgDAO
				.getInstance().getAllCfg();
		for (ActivityRankTypeCfg cfg : cfgList) {
			if (sendMap.get(cfg.getEnumId()) == null) {
				continue;
			}
			if (!isOpen(cfg)) {
				continue;
			}
			if (!StringUtils.equals(sendMap.get(cfg.getEnumId()).getVersion(),
					cfg.getVersion())) {
				SendRewardRecord record = new SendRewardRecord();
				record.setId(cfg.getId());
				record.setVersion(cfg.getVersion());
				record.setSend(false);
				record.setEnumId(cfg.getEnumId());
				sendMap.put(cfg.getEnumId(), record);
			}
		}
	}

	@Override
	public void updateRedPoint(Player player, String eNum) {
		ActivityRankTypeItemHolder activityCountTypeItemHolder = new ActivityRankTypeItemHolder();

		ActivityRankTypeCfg cfg = ActivityRankTypeCfgDAO.getInstance()
				.getCfgById(eNum);
		if (cfg == null) {
			return;
		}
		ActivityRankTypeEnum rankEnum = ActivityRankTypeEnum.getById(cfg
				.getEnumId());
		if (rankEnum == null) {
			return;
		}
		ActivityRankTypeItem dataItem = activityCountTypeItemHolder.getItem(
				player.getUserId(), rankEnum);
		if (dataItem == null) {
			return;
		}
		if (!dataItem.isTouchRedPoint()) {
			dataItem.setTouchRedPoint(true);
			activityCountTypeItemHolder.updateItem(player, dataItem);
		}

	}

	public boolean isOpen(long param) {
		List<ActivityRankTypeCfg> list = ActivityRankTypeCfgDAO.getInstance()
				.getAllCfg();
		for (ActivityRankTypeCfg cfg : list) {
			if (isOpen(cfg, param)) {
				return true;
			}
		}
		return false;
	}

	private boolean isOpen(ActivityRankTypeCfg cfg, long param) {
		if (cfg != null) {
			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = param;
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}

	public List<String> haveRedPoint(Player player) {
		List<String> redPointList = new ArrayList<String>();
		ActivityRankTypeItemHolder rankHolder = ActivityRankTypeItemHolder.getInstance();
		List<ActivityRankTypeItem> rankItemList = null;
		List<ActivityRankTypeCfg> cfgList = ActivityRankTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityRankTypeCfg cfg : cfgList){
			if(!isOpen(cfg)){
				continue;
			}
			if(rankItemList == null){
				rankItemList = rankHolder.getItemList(player.getUserId());
			}
			ActivityRankTypeItem item = null;//rankItemList.get(Integer.parseInt(cfg.getEnumId()));
			for(ActivityRankTypeItem temp : rankItemList){
				if(StringUtils.equals(temp.getEnumId(), cfg.getEnumId())){
					item = temp ;
					break;
				}
			}
			
			if(item == null){
				continue;
			}
			if (!item.isTouchRedPoint()) {
				redPointList.add(item.getCfgId());
				continue;
			}
		}
		return redPointList;
	}

}
