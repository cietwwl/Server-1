package com.rwbase.dao.openLevelTiggerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.pojo.CfgOpenLevelLimit;
import com.rwbase.dao.openLevelTiggerService.IServiceTiggerHandler.ServiceTiggerToFriend;
import com.rwbase.dao.openLevelTiggerService.pojo.OpenLevelTiggerServiceItem;
import com.rwbase.dao.openLevelTiggerService.pojo.OpenLevelTiggerServiceSubItem;
import com.rwbase.dao.user.User;

public class OpenLevelTiggerServiceMgr {

	private static OpenLevelTiggerServiceMgr instance = new OpenLevelTiggerServiceMgr();

	public static OpenLevelTiggerServiceMgr getInstance() {
		return instance;
	}

	private final ServiceTiggerToFriend friendTigger;

	protected OpenLevelTiggerServiceMgr() {
		this.friendTigger = new ServiceTiggerToFriend();
	}

	public void tiggerServiceByLevel(Player player, User oldRecord, User currentRecord) {
		String userId = player.getUserId();
		Long currentTime = DateUtils.getSecondLevelMillis();
		CfgOpenLevelLimitDAO cfgLimitDao = CfgOpenLevelLimitDAO.getInstance();
		CfgOpenLevelTiggerServiceDAO cfgServiceDao = CfgOpenLevelTiggerServiceDAO.getInstance();
		for (int i = (oldRecord.getLevel() + 1); i < (currentRecord.getLevel() + 1); i++) {// 很少连升多级；先取引导的配置，再用引导配置取对应等级的功能服务配置；防止出现两份表不统一的情况出现
			List<CfgOpenLevelLimit> cfgList = cfgLimitDao.getOpenByLevel(i);
			if (cfgList == null) {
				continue;
			}
			for (CfgOpenLevelLimit cfg : cfgList) {// 很少一个级别配置多个引导；获得该等级各激活的功能的对应辅助配置
				friendTigger.openLevelToCreatItem(player, currentTime, userId, cfg, cfgServiceDao);
			}
		}
	}

	/**
	 * 加好友赠送体力，功能开启5秒后就要触发，使用秒时效
	 * 
	 * @param player
	 */
	public void oneSecondAction(Player player) {
		friendTigger.doActionByTimerManager(player);
	}

	/**
	 * 登陆时会读取并把已开启未完成的数据注册；目前模式不采用;维护的话应该移到接口去
	 * 
	 * @param player
	 */
	public void regeditByLogin(Player player) {
		OpenLevelTiggerServiceRegeditInfo newInfo = new OpenLevelTiggerServiceRegeditInfo();
		String userId = player.getUserId();
		OpenLevelTiggerServiceHolder dataHolder = OpenLevelTiggerServiceHolder.getInstance();
		List<OpenLevelTiggerServiceItem> itemList = dataHolder.getItemList(userId);
		Map<Integer, List<Integer>> typeOfEventList = new HashMap<Integer, List<Integer>>();
		for (OpenLevelTiggerServiceItem item : itemList) {
			List<Integer> eventList = new ArrayList<Integer>();
			for (OpenLevelTiggerServiceSubItem subItem : item.getSubItemList()) {
				if (subItem.isOver()) {
					continue;
				}
				eventList.add(subItem.getTriggerNumber());
			}
			if (eventList.isEmpty()) {
				continue;
			}
			typeOfEventList.put(item.getId(), eventList);
		}
		newInfo.setEventListByType(typeOfEventList);
		player.setOpenLevelTiggerServiceRegeditInfo(newInfo);
	}

	/**
	 * 升级时会把已开启未完成的数据注册；目前模式不采用;维护的话应该移到接口去
	 * 
	 * @param player
	 */
	public void regeditByLevelUp(Player player, List<OpenLevelTiggerServiceItem> itemList) {
		OpenLevelTiggerServiceRegeditInfo newInfo = new OpenLevelTiggerServiceRegeditInfo();

		Map<Integer, List<Integer>> typeOfEventList = newInfo.getEventListByType();
		for (OpenLevelTiggerServiceItem item : itemList) {
			List<Integer> eventList = new ArrayList<Integer>();
			for (OpenLevelTiggerServiceSubItem subItem : item.getSubItemList()) {
				if (subItem.isOver()) {
					continue;
				}
				eventList.add(subItem.getTriggerNumber());
			}
			if (eventList.isEmpty()) {
				continue;
			}
			typeOfEventList.put(item.getId(), eventList);
		}
		newInfo.setEventListByType(typeOfEventList);
		player.setOpenLevelTiggerServiceRegeditInfo(newInfo);
	}

}
