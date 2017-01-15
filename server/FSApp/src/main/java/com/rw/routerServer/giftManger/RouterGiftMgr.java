package com.rw.routerServer.giftManger;

import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.routerServer.cfg.UCGiftCfg;
import com.rw.routerServer.cfg.UCGiftCfgDAO;
import com.rw.routerServer.data.ResultState;
import com.rw.service.Email.EmailUtils;

public class RouterGiftMgr {
	
	private static RouterGiftMgr instance = new RouterGiftMgr();
	
	protected RouterGiftMgr(){	}
	
	public static RouterGiftMgr getInstance(){
		return instance;
	}
	
	public ResultState addGift(String userId, String giftId, String date){
		//TODO 判断时间的合法性，以及id的合法性
		UCGiftCfg cfg = UCGiftCfgDAO.getInstance().getCfgById(giftId);
		if(cfg == null){
			System.out.println("------------------无法找到配置表~");
			GameLog.error(LogModule.RouterServer, "RounterGiftMgr[addGift]", "无法找到目标配置表，配置表id:" + giftId, null);
			return ResultState.GIFT_ID_ERROR;
		}
		RouterGiftDataHolder giftDataHolder = RouterGiftDataHolder.getInstance();
		RouterGiftDataItem giftItem = giftDataHolder.getItem(userId, Integer.valueOf(giftId));
		if(null == giftItem){	// || !StringUtils.equals(giftItem.getBelongTime(), date)){
			giftItem = new RouterGiftDataItem();
			giftItem.setUserId(userId);
			giftItem.setGiftId(Integer.valueOf(giftId));
			giftItem.setBelongTime(date);
			giftItem.setCount(1);
			giftDataHolder.addItem(userId, giftItem);
			System.out.println("--------------添加成功~");
		}else if(!StringUtils.equals(giftItem.getBelongTime(), date)){
			giftItem.setBelongTime(date);
			giftItem.setCount(giftItem.getCount() + 1);
			System.out.println("--------------添加成功~");
		}else{
			System.out.println("-------------领取重复~");
			return ResultState.REPEAT_GET;
		}
		takeGift(userId);
		return ResultState.SUCCESS;
	}
	
	public void takeGift(String userId){
		try {
			
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendGift(String userId, String giftId){
		//TODO 读取礼包配置表，通过邮件发送礼包
		UCGiftCfg cfg = UCGiftCfgDAO.getInstance().getCfgById(giftId);
		if(cfg == null){
			GameLog.error(LogModule.RouterServer.getName(), "RounterGiftMgr[sendGift]", "直通车请求发送礼包，无法找到目标配置,giftID："+ giftId, null);
			return;
		}
		EmailUtils.sendEmail(userId, giftId, cfg.getContent());
		
	}
}
