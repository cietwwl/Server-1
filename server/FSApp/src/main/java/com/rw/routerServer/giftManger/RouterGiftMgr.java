package com.rw.routerServer.giftManger;

import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.PlayerMgr;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.routerServer.data.ResultState;

public class RouterGiftMgr {
	
	private static RouterGiftMgr instance = new RouterGiftMgr();
	
	protected RouterGiftMgr(){	}
	
	public static RouterGiftMgr getInstance(){
		return instance;
	}
	
	public ResultState addGift(String userId, String giftId, String date){
		//TODO 判断时间的合法性，以及id的合法性
		RouterGiftDataHolder giftDataHolder = RouterGiftDataHolder.getInstance();
		RouterGiftDataItem giftItem = giftDataHolder.getItem(userId, Integer.valueOf(giftId));
		if(null == giftItem){	// || !StringUtils.equals(giftItem.getBelongTime(), date)){
			giftItem = new RouterGiftDataItem();
			giftItem.setUserId(userId);
			giftItem.setGiftId(Integer.valueOf(giftId));
			giftItem.setBelongTime(date);
			giftItem.setCount(1);
			giftDataHolder.addItem(userId, giftItem);
		}else if(!StringUtils.equals(giftItem.getBelongTime(), date)){
			giftItem.setBelongTime(date);
			giftItem.setCount(giftItem.getCount() + 1);
		}else{
			return ResultState.REPEAT_GET;
		}
		takeGift(userId);
		return ResultState.SUCCESS;
	}
	
	public void takeGift(String userId){
		if(null == PlayerMgr.getInstance().findPlayerFromMemory(userId)){
			return ;
		}
		RouterGiftDataHolder giftDataHolder = RouterGiftDataHolder.getInstance();
		RoleExtPropertyStore<RouterGiftDataItem> giftDatas = giftDataHolder.getItemStore(userId);
		if(null != giftDatas){
			Enumeration<RouterGiftDataItem> giftEnum = giftDatas.getExtPropertyEnumeration();
			while (giftEnum.hasMoreElements()) {
				RouterGiftDataItem routerGiftDataItem = giftEnum.nextElement();
				if(routerGiftDataItem.getCount() > 0){
					for(int i = 0; i < routerGiftDataItem.getCount(); i++){
						sendGift(userId, String.valueOf(routerGiftDataItem.getGiftId()));
					}
					routerGiftDataItem.setCount(0);
					giftDataHolder.updateItem(userId, routerGiftDataItem);
				}
			}
		}
	}
	
	private void sendGift(String userId, String giftId){
		//TODO 读取礼包配置表，通过邮件发送礼包
	}
}
