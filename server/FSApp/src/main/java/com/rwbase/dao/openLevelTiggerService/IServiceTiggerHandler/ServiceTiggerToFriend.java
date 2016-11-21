package com.rwbase.dao.openLevelTiggerService.IServiceTiggerHandler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.friend.TableFriend;
import com.rwbase.dao.friend.TableFriendDAO;
import com.rwbase.dao.openLevelLimit.pojo.CfgOpenLevelLimit;
import com.rwbase.dao.openLevelTiggerService.CfgOpenLevelTiggerServiceDAO;
import com.rwbase.dao.openLevelTiggerService.pojo.CfgOpenLevelTiggerService;
import com.rwbase.dao.openLevelTiggerService.pojo.OpenLevelTiggerServiceItem;
import com.rwbase.dao.openLevelTiggerService.pojo.OpenLevelTiggerServiceSubItem;

public class ServiceTiggerToFriend implements IServiceTiggerHandler{

	@Override
	public void openLevelToCreatItem(Long now,String userId,CfgOpenLevelLimit cfg,CfgOpenLevelTiggerServiceDAO cfgServiceDao) {
		TableFriend friendTable = TableFriendDAO.getInstance().get(userId);
		List<CfgOpenLevelTiggerService> tiggerCfgList = cfgServiceDao.getListByType(cfg.getType());
		if(tiggerCfgList == null){
			return;
		}
//		TableFriend friendTable = player.getFriendMgr().getTableFriend();
		OpenLevelTiggerServiceItem item = friendTable.getOpenLevelTiggerServiceItem();//store.get(cfg.getType());
		if(item.getCreatTime() != 0){
			return;
		}
		item = new OpenLevelTiggerServiceItem();
		item.setId(cfg.getType());
		item.setCreatTime(now);
		item.setUserId(userId);
		List<OpenLevelTiggerServiceSubItem> subItemList = new ArrayList<OpenLevelTiggerServiceSubItem>();
		for(CfgOpenLevelTiggerService serviceCfg : tiggerCfgList){
			OpenLevelTiggerServiceSubItem subItem = new OpenLevelTiggerServiceSubItem();
			subItem.setOver(false);
			subItem.setTriggerTime(serviceCfg.getTriggerTime());
			subItem.setTriggerNumber(serviceCfg.getTriggerNumber());
			subItem.setGivePower(serviceCfg.isGive());
			subItemList.add(subItem);
		}
		item.setSubItemList(subItemList);
//		itemList.add(item);
//		TableFriendDAO.getInstance().update(friendTable);
		friendTable.setOpenLevelTiggerServiceItem(item);
		TableFriendDAO.getInstance().update(userId);
	}

	@Override
	public void doActionByTimerManager(Player player) {
		Long currentTime = DateUtils.getSecondLevelMillis();
		TableFriend friendTable = player.getFriendMgr().getTableFriend();
		OpenLevelTiggerServiceItem item = friendTable.getOpenLevelTiggerServiceItem();
		if(item.getCreatTime() == 0){
			return;
		}
		List<OpenLevelTiggerServiceSubItem> subItemList = item.getSubItemList();
		boolean isSave = false;
		for(OpenLevelTiggerServiceSubItem subItem : subItemList){
			if(!StringUtils.isBlank(subItem.getUserId())){
				continue;
			}
			
			long timeBySecond = (currentTime - item.getCreatTime())/1000;
			if(timeBySecond > subItem.getTriggerTime()){
//				type.doAction();
				isSave = true;
				isSave = player.getFriendMgr().robotRequestAddPlayerToFriend(subItem,friendTable);				
			}
		}
		if(isSave){
			player.getFriendMgr().save();	
		}
				
	}

	
	
}
