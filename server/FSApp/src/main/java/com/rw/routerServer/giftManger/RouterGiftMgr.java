package com.rw.routerServer.giftManger;

import java.util.Calendar;
import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.PlayerMgr;
import com.playerdata.charge.ChargeMgr;
import com.playerdata.charge.dao.ChargeInfo;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.util.DateUtils;
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
			GameLog.error(LogModule.RouterServer, "RounterGiftMgr[addGift]", "无法找到目标配置表，配置表id:" + giftId, null);
			return ResultState.GIFT_ID_ERROR;
		}
		RouterGiftDataHolder giftDataHolder = RouterGiftDataHolder.getInstance();
		RouterGiftDataItem giftItem = giftDataHolder.getItem(userId, Integer.valueOf(giftId));
		if(null == giftItem){	
			giftItem = new RouterGiftDataItem();
			giftItem.setUserId(userId);
			giftItem.setId(Integer.valueOf(giftId));
			giftItem.setBelongTime(date);
			giftItem.setCount(1);
			giftDataHolder.addItem(userId, giftItem);
		}else {
			//已经有的记录，要判断一下类型
			ResultState state = checkGiftType(giftItem, cfg, date);
			if(state != ResultState.SUCCESS){
				return state;
			}
			giftDataHolder.updateItem(userId, giftItem);

		}
		takeGift(userId);
		return ResultState.SUCCESS;
	}
	
	/**
	 * 检查是否可以领取奖励 这个后面要重构
	 * @param giftItem
	 * @param cfg
	 */
	private ResultState checkGiftType(RouterGiftDataItem giftItem, UCGiftCfg cfg, String date) {
		if(cfg.getType() == RounterGiftType.TYPE_DAILY.getType() && StringUtils.equals(giftItem.getBelongTime(), date)){
			return ResultState.REPEAT_GET;
		}else if(cfg.getType() == RounterGiftType.TYPE_CHARGE.getType() ||
				cfg.getType() == RounterGiftType.TYPE_LEVEL.getType()){
			return ResultState.REPEAT_GET;
		}else if(cfg.getType() == RounterGiftType.TYPE_WEEK.getType()){
			//检查当周有没有记录
			boolean sameWeek = checkWeek(giftItem.getBelongTime());
			if(sameWeek){
				return ResultState.REPEAT_GET;
			}
		}
		//其他情况直接加1
		giftItem.setBelongTime(date);
		giftItem.setCount(giftItem.getCount() + 1);
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
							sendGift(userId, String.valueOf(routerGiftDataItem.getId()));
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
		boolean sendEmail = EmailUtils.sendEmail(userId, cfg.getMailId(), cfg.getContent());
		if(!sendEmail){
			GameLog.error(LogModule.RouterServer.getName(), "RounterGiftMgr[sendGift]", "发送直通车礼包不成功，角色id:"
					+userId + ",礼包id:" + giftId);
		}
	}
	
	
	private  boolean checkWeek(String dayOfYear){
		int w = DateUtils.getCurrent().get(Calendar.WEEK_OF_YEAR);
		long time = DateUtils.getTime(dayOfYear);
		Calendar calendar = DateUtils.getCalendar();
		calendar.setTimeInMillis(time);
		int w2 = calendar.get(Calendar.WEEK_OF_YEAR);
		return w == w2;
	}
	
	
}
