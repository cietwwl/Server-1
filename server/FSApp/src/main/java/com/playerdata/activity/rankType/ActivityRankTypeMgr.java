package com.playerdata.activity.rankType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bm.rank.ListRankingType;
import com.bm.rank.RankType;
import com.bm.rank.arena.ArenaExtAttribute;
import com.common.RefParam;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.activity.ActivityRedPointUpdate;
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
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.ranking.ListRanking;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerPredecessor;

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
		RoleExtPropertyStore<ActivityRankTypeItem> store = null;
		List<ActivityRankTypeItem> addItemList = null;
		List<ActivityRankTypeCfg> allCfgList = ActivityRankTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityRankTypeCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(cfg)) {
				continue;
			}
			ActivityRankTypeEnum RankTypeEnum = ActivityRankTypeEnum.getById(cfg.getEnumId());
			if (RankTypeEnum == null) {
				continue;
			}
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
		ActivityRankTypeItemHolder dataHolder = ActivityRankTypeItemHolder.getInstance();
		ActivityRankTypeCfgDAO dao = ActivityRankTypeCfgDAO.getInstance();
		List<ActivityRankTypeItem> itemList = null;
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
		//ComGiftMgr comGiftMgr = ComGiftMgr.getInstance();
		List<ActivityRankTypeItem> itemList = null;//dataHolder.getItemList(player.getUserId());
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
			if(closeItem == null||closeItem.isTaken()||closeItem.isClosed()){
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
	
	/** 定时核查一遍，将排行奖励派发到用户邮箱 */
	public void sendGift() {
		if (sendMap.size() == 0) {
			creatMap();//
		}
		final ActivityRankTypeItemHolder activityRankTypeItemHolder = ActivityRankTypeItemHolder.getInstance();
		ActivityRankTypeSubCfgDAO activityRankTypeSubCfgDAO = ActivityRankTypeSubCfgDAO.getInstance();
		List<ActivityRankTypeCfg> cfgList = ActivityRankTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityRankTypeCfg cfg : cfgList) {
			// 这一层循环，是因为该活动有多个子活动（战力大比拼，竞技之王）
			RefParam<ActivityRankTypeEnum> enumValue = new RefParam<ActivityRankTypeEnum>();
			List<ActivityRankTypeSubCfg> subCfgList = new ArrayList<ActivityRankTypeSubCfg>();
			if(!checkCfgIsCanSend(cfg, activityRankTypeSubCfgDAO, enumValue, subCfgList)){
				//多数时候，循环到这里就终止了，只有活动结束，需要发奖励的时候，才会继续
				continue;
			}
			final ActivityRankTypeEnum activityRankTypeEnum = enumValue.value;
			for (Integer ranktype : activityRankTypeEnum.getRankTypes()) {
				// 该配表对应的所有排行榜，比如竞技场就分4个职业（该层循环是因为一个活动需要依赖多个排行榜，后边排行榜合并成一个之后，这一层可以去掉）
				List<String> rankList = getRankListByRankTypeAndsubCfgNum(cfg.getLevelLimit(), ranktype);
				for(final ActivityRankTypeSubCfg subCfg : subCfgList){
					//根据配置表的名次发放奖励
					int size = rankList.size();
					for(int i = subCfg.getRankRanges()[0]; i <= subCfg.getRankRanges()[1] && i > 0 && i <= size; i++){
						final String userId = rankList.get(i-1);
						if(StringUtils.isBlank(userId)){
							continue;
						}
						User user = UserDataDao.getInstance().getByUserId(userId);
						if (user == null) {
							GameLog.error("ActivityRankTypeMgr, sendGift", userId, "找不到玩家信息id:" + userId);
						} else {
							if (user.isRobot() || user.getLastLoginTime() < cfg.getStartTime()) {
								// 机器人，或者最后次登陆时间不在活动时间内；
								continue;
							}
							final int rank = i;
							GameLog.info("ActivityRankTypeMgr, sendGift", userId, String.format("发放玩家的%s奖励，排名[%s]", activityRankTypeEnum, rank));
							GameWorldFactory.getGameWorld().asyncExecute(userId, new PlayerPredecessor() {
								@Override
								public void run(String e) {
									sendGifgSingel(userId, activityRankTypeItemHolder, activityRankTypeEnum, subCfg, rank);
								}
							});
						}
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
	private boolean checkCfgIsCanSend(ActivityRankTypeCfg cfg, ActivityRankTypeSubCfgDAO activityRankTypeSubCfgDAO,
			RefParam<ActivityRankTypeEnum> rankEnum, List<ActivityRankTypeSubCfg> subList) {
		
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
			// System.out.println("activityrank.活动还未结束或者还未开始。。。。。。。。。。。。。。。。");
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
	@SuppressWarnings("unchecked")
	private List<String> getRankListByRankTypeAndsubCfgNum(int levelLimit, Integer ranktype) {
		List<String> rankList = new ArrayList<String>();
		RankType rankEnum = RankType.getRankType(ranktype, 1);
		ListRankingType sType = ListRankingType.getListRankingType(rankEnum);
		ListRanking<String, ArenaExtAttribute> sr = RankingFactory.getSRanking(sType);
		if(null == sr){
			Ranking<?, RankingLevelData> ranking = RankingFactory.getRanking(rankEnum);
			if(null == ranking){
				return rankList;
			}
			EnumerateList<? extends MomentRankingEntry<?, RankingLevelData>> enumList = ranking.getEntriesEnumeration();
			for (; enumList.hasMoreElements();) {
				MomentRankingEntry<?, RankingLevelData> entry = enumList.nextElement();
				RankingLevelData data = entry.getExtendedAttribute();
				if(data.getLevel() < levelLimit){
					rankList.add(null);
				}else{
					rankList.add(data.getUserId());
				}
			}
		}else{
			Iterator<? extends ListRankingEntry<String, ArenaExtAttribute>> it = sr.getEntrysCopy().iterator();
			for (; it.hasNext();) {
				ListRankingEntry<String, ArenaExtAttribute> entry = it.next();
				ArenaExtAttribute data = entry.getExtension();
				if(data.getLevel() < levelLimit){
					rankList.add(null);
				}else{
					rankList.add(entry.getKey());
				}
			}
		}
		return rankList;
	}
	
	/**
	 * 挨个派发奖励
	 * @param userId
	 * @param activityRankTypeItemHolder
	 * @param activityRankTypeEnum
	 * @param subCfg
	 */
	private void sendGifgSingel(String userId, ActivityRankTypeItemHolder activityRankTypeItemHolder,
			ActivityRankTypeEnum activityRankTypeEnum, ActivityRankTypeSubCfg subCfg, int rank) {
		RoleExtPropertyStore<ActivityRankTypeItem> itemStore = activityRankTypeItemHolder.getItemStore(userId);
		if (itemStore == null) {
			GameLog.error("ActivityRankTypeMgr, sendGifgSingel", userId, String.format("玩家的%s的%s活动数据为空", userId, activityRankTypeEnum));
			return;
		}
		ActivityRankTypeItem targetItem = null;
		targetItem = itemStore.get(Integer.parseInt(activityRankTypeEnum.getCfgId()));
		if (targetItem == null) {
			// 有排行无登录时生成的排行榜活动奖励数据，说明是机器人或活动期间没登陆过
			GameLog.error("ActivityRankTypeMgr, sendGifgSingel", userId, String.format("玩家%s%s活动期间没有登录过，数据为空，不发放奖励", userId, activityRankTypeEnum));
			return;
		}
		if(!targetItem.isTaken()){
			GameLog.info("ActivityRankTypeMgr, sendGift", userId, String.format("发放玩家的%s奖励，排名[%s]，邮件id[%s]，物品[%s]", activityRankTypeEnum, rank, subCfg.getEmailId(), subCfg.getReward()));
			EmailUtils.sendEmail(userId, subCfg.getEmailId(), subCfg.getReward());
			targetItem.setTaken(true);
			itemStore.update(targetItem.getId());
		}else{
			GameLog.info("ActivityRankTypeMgr, sendGifgSingel", userId, String.format("玩家%s%s活动的奖励已经发放过，不能重复发放", userId, activityRankTypeEnum));
		}
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
			if(isOpen(modelCfg)){
				record.setSend(false);
			}else{
				record.setSend(true);
			}
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
			if(cfg.getLevelLimit()> player.getLevel()){
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
