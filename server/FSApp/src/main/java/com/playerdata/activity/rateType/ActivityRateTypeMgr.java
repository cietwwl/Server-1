package com.playerdata.activity.rateType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfg;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfgDAO;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeStartAndEndHourHelper;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.playerdata.activity.rateType.data.ActivityRateTypeItemHolder;
import com.playerdata.activityCommon.activityType.IndexRankJudgeIF;
import com.playerdata.fightinggrowth.FSuserFightingGrowthMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.itemPrivilege.PrivilegeDescItem;

public class ActivityRateTypeMgr implements ActivityRedPointUpdate, IndexRankJudgeIF{
	
	private static final int ACTIVITY_INDEX_BEGIN = 20000;
	private static final int ACTIVITY_INDEX_END = 30000;

	private static ActivityRateTypeMgr instance = new ActivityRateTypeMgr();

	public static ActivityRateTypeMgr getInstance() {
		return instance;
	}

	public void synData(Player player) {
		if(isOpen(System.currentTimeMillis())){
			ActivityRateTypeItemHolder.getInstance().synAllData(player);
		}
		
	}

	public boolean isLevelEnough(Player player,ActivityRateTypeCfg cfg){
		boolean iscan = false;
		iscan = player.getLevel() >= cfg.getLevelLimit() ? true : false;	
		return iscan;
		
	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkVersion(player);
		checkClose(player);

	}
	
	

	/**
	 * 
	 * @param player  同个类型活动同时开启两个会导致add不了新的活动，风险低，需检测
	 */
	private void checkNewOpen(Player player) {	
		String userId = player.getUserId();
//		List<ActivityRateTypeItem> addList = null;
//		RoleExtPropertyStoreCache<ActivityRateTypeItem> cach = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_RATE, ActivityRateTypeItem.class);//
//		PlayerExtPropertyStore<ActivityRateTypeItem> store = null;
//		try {
//			store = cach.getStore(userId);
		creatItems(userId, true);
//			if(addList != null){
//				store.addItem(addList);
//			}
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
	}
	
	public List<ActivityRateTypeItem> creatItems(String userId ,boolean isHasPlayer ){
		RoleExtPropertyStoreCache<ActivityRateTypeItem> cach = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_RATE, ActivityRateTypeItem.class);//
		RoleExtPropertyStore<ActivityRateTypeItem> store = null;
		List<ActivityRateTypeItem> addItemList = null;
		List<ActivityRateTypeCfg> allCfgList = ActivityRateTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityRateTypeCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(cfg)) {
				// 活动未开启
				continue;
			}
			ActivityRateTypeEnum typeEnum = ActivityRateTypeEnum.getById(String.valueOf(cfg.getEnumId()));
			if (typeEnum == null) {
				// 枚举没有配置
				continue;
			}
//			String itemId = ActivityRateTypeHelper.getItemId(userId, typeEnum);
			int id = Integer.parseInt(typeEnum.getCfgId());
			if(isHasPlayer){
				try {
					store = cach.getStore(userId);
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
			ActivityRateTypeItem item = new ActivityRateTypeItem();
			item.setId(id);
			item.setCfgId(String.valueOf(cfg.getId()));
			item.setEnumId(String.valueOf(cfg.getEnumId()));
			item.setUserId(userId);
			item.setVersion(String.valueOf(cfg.getVersion()));
			item.setMultiple(cfg.getMultiple());
			if (addItemList == null) {
				addItemList = new ArrayList<ActivityRateTypeItem>();
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
	
	
	
	private void checkVersion(Player player) {
		ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder.getInstance();
		ActivityRateTypeCfgDAO dao = ActivityRateTypeCfgDAO.getInstance();
		List<ActivityRateTypeItem> itemList = null;//dataHolder.getItemList(player.getUserId());
		List<ActivityRateTypeCfg> cfgList = dao.getAllCfg();
		for(ActivityRateTypeCfg cfg : cfgList){
			if(!isOpen(cfg)){
				continue;
			}
			if(itemList == null){
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityRateTypeItem freshItem = null;
			for(ActivityRateTypeItem item : itemList){
				if(!StringUtils.equals(item.getVersion(), String.valueOf(cfg.getVersion()))){
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
	
	
	public boolean isOpen(ActivityRateTypeCfg ActivityRateTypeCfg) {
		boolean isopen = false;
		long startTime = ActivityRateTypeCfg.getStartTime();
		long endTime = ActivityRateTypeCfg.getEndTime();
		long currentTime = System.currentTimeMillis();
		isopen = currentTime < endTime && currentTime >= startTime ? true
				: false;
		if(!isopen){
			return isopen;
		}
		
		int currentHHMMSS = Integer.parseInt(DateUtils.getTimeOfDayFomrateTips(currentTime));
		for (ActivityRateTypeStartAndEndHourHelper timebyhour : ActivityRateTypeCfg.getStartAndEnd()) {
			isopen = currentHHMMSS >= timebyhour.getStarthour()&& currentHHMMSS < timebyhour.getEndhour() ? true : false;
			if (isopen) {
				return isopen;
			}
		}
		return isopen;
	}
	
	private boolean isOpenNotActivie(ActivityRateTypeCfg cfg) {
		long startTime = cfg.getStartTime();
		long endTime = cfg.getEndTime();
		long currentTime = System.currentTimeMillis();
		return currentTime < endTime && currentTime >= startTime ? true
				: false;
	}
	
	private void checkClose(Player player) {
		ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder
				.getInstance();
		List<ActivityRateTypeItem> itemList = null;//dataHolder.getItemList(player.getUserId());
		ActivityRateTypeCfgDAO dao = ActivityRateTypeCfgDAO.getInstance();
		List<ActivityRateTypeCfg> cfgList = dao.getAllCfg();
		long createTime = player.getUserDataMgr().getCreateTime();
		long currentTime = DateUtils.getSecondLevelMillis();
		for(ActivityRateTypeCfg cfg : cfgList){
			if(isOpenNotActivie(cfg)){//配置开启
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
			ActivityRateTypeItem closeItem = null;
			for(ActivityRateTypeItem item : itemList){
				if(StringUtils.equals(item.getEnumId(), String.valueOf(cfg.getEnumId()))&&StringUtils.equals(item.getVersion(), String.valueOf(cfg.getVersion()))){
					closeItem = item;
					break;
				}			
			}
			if(closeItem == null){
				continue;
			}			
			if (!closeItem.isClosed()) {
				closeItem.setClosed(true);
				closeItem.setTouchRedPoint(true);
				dataHolder.updateItem(player, closeItem);
			}			
		}
	}

	/**
	 * 
	 * @param copyCfg  副本
	 * @param player
	 * @param eSpecialItemIDUserInfo  传入的战斗结果数据对象
	 * 此方法用于站前将结算双倍金币经验等信息发给客户端显示
	 */
	public void setEspecialItemidlis(CopyCfg copyCfg,Player player,eSpecialItemIDUserInfo eSpecialItemIDUserInfo){
		Map<Integer, Integer> map = ActivityRateTypeMgr.getInstance().getEspecialItemtypeAndEspecialWithTime(player, copyCfg.getLevelType());
	
		float multiplePlayerExp = 1 + ActivityRateTypeMgr.getInstance().getMultiple(map, eSpecialItemId.PlayerExp.getValue());
		float multipleCoin = 1 + ActivityRateTypeMgr.getInstance().getMultiple(map, eSpecialItemId.Coin.getValue());

		List<? extends PrivilegeDescItem> privList = FSuserFightingGrowthMgr.getInstance().getPrivilegeDescItem(player);
		if(privList!=null && !privList.isEmpty()){
			for(PrivilegeDescItem iteminfo : privList){
				if(iteminfo.getItemID() == eSpecialItemId.PlayerExp.getValue()){
					multiplePlayerExp += iteminfo.getValue();
				}
				if(iteminfo.getItemID() == eSpecialItemId.Coin.getValue()){
					multipleCoin += iteminfo.getValue();
				}
			}
		}
		getesESpecialItemIDUserInfo(eSpecialItemIDUserInfo,(int)(copyCfg.getPlayerExp()*multiplePlayerExp),0);		
		getesESpecialItemIDUserInfo(eSpecialItemIDUserInfo,0,(int)(copyCfg.getCoin()*multipleCoin));
	}
	
	/**
	 * 传入副本类型，根据类型，是否开启获得当前对应副本的“产出类型→产出倍数”的映射返回；
	 */
	public Map<Integer, Integer> getEspecialItemtypeAndEspecialWithTime(Player player,int copyType){
		Map<Integer, Integer> especialItemtypeAndEspecialWithTime = new HashMap<Integer, Integer>();
		List<ActivityRateTypeCfg> cfgList = ActivityRateTypeCfgDAO.getInstance().getAllCfg();
		ActivityRateTypeMgr activityRateTypeMgr = ActivityRateTypeMgr.getInstance();
		
		
		for(ActivityRateTypeCfg cfg : cfgList){
			if(!activityRateTypeMgr.isActivityOnGoing(player, cfg)){
				continue;
			}
			Map<Integer, List<Integer>> map = cfg.getCopyTypeMap();
			if(map.get(copyType)== null){
				continue;
			}
			//当前玩家通关的副本类型在这个活动里有对应的双倍奖励
			ActivityRateTypeEnum eNum = ActivityRateTypeEnum.getById(String.valueOf(cfg.getEnumId()));
			if(eNum == null){
				continue;
			}
			
			List<Integer> especials = map.get(copyType);
			for(Integer especial : especials){
				if(especialItemtypeAndEspecialWithTime.containsKey(especial)){
					int old = especialItemtypeAndEspecialWithTime.get(especial);
					especialItemtypeAndEspecialWithTime.put(especial, old + cfg.getMultiple()-1);
				}else{
					especialItemtypeAndEspecialWithTime.put(especial, cfg.getMultiple()-1);//一个活动对一个特定副本的某种产出的若干倍翻倍
				}
			}			
		}		
		return especialItemtypeAndEspecialWithTime;
	}
	
	/**判断是否活动开启以及玩家是否满足活动需求等级*/
	public boolean isActivityOnGoing(Player player,
			ActivityRateTypeCfg cfg) {
				ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder
				.getInstance();		
		if(cfg == null){			
			return false;
		}				
		{
			ActivityRateTypeItem targetItem = dataHolder.getItem(player.getUserId(), ActivityRateTypeEnum.getById(String.valueOf(cfg.getEnumId())));// 已在之前生成数据的活动
			if(targetItem == null){
				checkNewOpen(player);				
				return false;
			}
			return ActivityRateTypeMgr.getInstance().isOpen(cfg)&&player.getLevel() >= cfg.getLevelLimit();			
		}
	}
	
	public int getMultiple(Map<Integer, Integer> map ,int especial){
		int multiple = 0;
		if(map.containsKey(especial)){
			multiple = map.get(especial);
		}
		return multiple;
	}
	
	/** 通用活动三可能扩展的双倍需要发送给客户端显示的在此处理;只能存在一种枚举,需要双加的额外添组合 */
	public eSpecialItemIDUserInfo getesESpecialItemIDUserInfo(eSpecialItemIDUserInfo eSpecialItemIDUserInfo, int expvalue,int coinvalue) {
		if(expvalue != 0){
			eSpecialItemIDUserInfo.setPlayerExp(expvalue);
		}
		if(coinvalue != 0){
			eSpecialItemIDUserInfo.setCoin(coinvalue);
		}
		return eSpecialItemIDUserInfo;
	}
	
	@Override
	public void updateRedPoint(Player player, String eNum) {
		ActivityRateTypeItemHolder activityCountTypeItemHolder = new ActivityRateTypeItemHolder();
		ActivityRateTypeCfg cfg = ActivityRateTypeCfgDAO.getInstance().getCfgById(eNum);
		if(cfg == null ){
			return;
		}
		ActivityRateTypeEnum rateEnum = ActivityRateTypeEnum.getById(String.valueOf(cfg.getEnumId()));
		if(rateEnum == null){
			return;
		}
		ActivityRateTypeItem dataItem = activityCountTypeItemHolder.getItem(player.getUserId(),rateEnum);
		if(dataItem == null){
			return;
		}
		if(!dataItem.isTouchRedPoint()){
			dataItem.setTouchRedPoint(true);
			activityCountTypeItemHolder.updateItem(player, dataItem);
		}		
	}

	public boolean isOpen(long param) {
		List<ActivityRateTypeCfg> list = ActivityRateTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityRateTypeCfg cfg : list){
			if(isOpen(cfg,param)){
				return true;
			}			
		}
		return false;
	}

	private boolean isOpen(ActivityRateTypeCfg cfg, long param) {
		boolean isopen = false;
		if (cfg != null) {			
			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = param;
			isopen =  currentTime < endTime && currentTime >= startTime;
			if(!isopen){
				return isopen;
			}
			//int hour = DateUtils.getCurrentHour();
			int currentHHMMSS = Integer.parseInt(DateUtils.getTimeOfDayFomrateTips(currentTime));
			for (ActivityRateTypeStartAndEndHourHelper timebyhour : cfg.getStartAndEnd()) {
				isopen = currentHHMMSS >= timebyhour.getStarthour()&& currentHHMMSS < timebyhour.getEndhour() ? true : false;
				if (isopen) {
					return isopen;
				}
			}
		}
		return isopen;
	}
	
	@Override
	public boolean isThisActivityIndex(int index) {
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
