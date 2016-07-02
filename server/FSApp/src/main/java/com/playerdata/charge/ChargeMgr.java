package com.playerdata.charge;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeEnum;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeCfgDAO;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeSubCfg;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeSubCfgDAO;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItemHolder;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeSubItem;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.cfg.ChargeCfgDao;
import com.playerdata.charge.cfg.ChargeTypeEnum;
import com.playerdata.charge.cfg.FirstChargeCfg;
import com.playerdata.charge.cfg.FirstChargeCfgDao;
import com.playerdata.charge.cfg.VipGiftCfg;
import com.playerdata.charge.cfg.VipGiftCfgDao;
import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.charge.dao.ChargeInfoHolder;
import com.playerdata.charge.dao.ChargeInfoSubRecording;
import com.playerdata.charge.dao.ChargeOrder;
import com.rw.chargeServer.ChargeContentPojo;
import com.rw.service.Privilege.MonthCardPrivilegeMgr;
import com.rw.service.dailyActivity.DailyActivityHandler;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;

public class ChargeMgr {

	private static ChargeMgr instance = new ChargeMgr();
	
	
	public static ChargeMgr getInstance(){
		return instance;
	}
	
	public boolean isValid(Player player,ChargeTypeEnum monthCardType){
		ActivityTimeCardTypeItemHolder dataHolder = ActivityTimeCardTypeItemHolder.getInstance();		
		ActivityTimeCardTypeItem dataItem = dataHolder.getItem(player.getUserId(),ActivityTimeCardTypeEnum.Month);
		if(dataItem == null){
			GameLog.error("chargemgr", player.getUserId(), "数据库没数据就设置月卡特权");
			return false;
		}
		List<ActivityTimeCardTypeSubItem>  monthCardList = dataItem.getSubItemList();
		ActivityTimeCardTypeSubItem targetItem = null;
		String cardtype= monthCardType.getCfgId();
		for (ActivityTimeCardTypeSubItem itemTmp : monthCardList) {
			if(StringUtils.equals(itemTmp.getChargetype(), cardtype)){
				targetItem = itemTmp;
				break;
			}
		}
		if(targetItem!=null){
			return targetItem.getDayLeft() > 0?true:false;
		}		
		return false;
	}
	
	public void syn(Player player, int version){		
		ChargeInfoHolder.getInstance().syn(player,version);
	}
	
	public ChargeInfo getChargeInfo(String userId){
		return ChargeInfoHolder.getInstance().get(userId);
	}
	
	public ChargeResult buyAndTakeVipGift(Player player, String itemId){		
		ChargeResult result = ChargeResult.newResult(false);		
		VipGiftCfg target = VipGiftCfgDao.getInstance().getCfgById(itemId);
		int viplevel = Integer.parseInt(itemId);
		
		if(target!=null && !player.getVipMgr().isVipGiftTaken(viplevel)){			
			boolean success = buyVipGift(player, target,result);
			if(success){
				player.getVipMgr().setVipGiftTaken(viplevel);
				success = takeVipGift(player,target);
				result.setTips("购买成功");
				result.setSuccess(success);	
				
			}else{
				result.setTips("等级不足或货币不足");
				result.setSuccess(false);	
				player.getVipMgr().failToBuyVipGift(viplevel);
			}			
		}else{
			result.setSuccess(false);
			result.setTips("购买失败！");
		}
		return result;
	}
	
	private boolean takeVipGift(Player player,VipGiftCfg target) {
		ComGiftMgr.getInstance().addGiftById(player,target.getGift());			
		return true;
	}

	private boolean buyVipGift(Player player, VipGiftCfg target,ChargeResult result) {
		if(player.getVip() < Integer.parseInt(target.getVipLv())){
			result.setTips("Vip等级低于购买礼包等级");
			GameLog.error("chargeMgr", player.getUserId(), "Vip等级低于购买礼包等级");
			return false;
		}	
		
		if(player.getUserGameDataMgr().getGold() < Integer.parseInt(target.getCurCost())){
			result.setTips("货币不够");
			GameLog.error("chargeMgr", player.getUserId(), "货币不够");
			return false;
		}
		player.getUserGameDataMgr().addGold(-Integer.parseInt(target.getCurCost()));
		
		return true;
	}

	public boolean charge(ChargeContentPojo chargeContentPojo){
		boolean success=false;
		// 充值，保存订单，返回结果
		Player player = get(chargeContentPojo);
		if(player!=null){
			ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
			if(!chargeInfo.isOrderExist(chargeContentPojo.getCpTradeNo())){
				ChargeOrder chargeOrder = ChargeOrder.fromReq(chargeContentPojo);
				success = ChargeInfoHolder.getInstance().addChargeOrder(player,chargeOrder);
			}else{
				GameLog.error("chargemgr", "sdk-充值", "充值失败,订单号异常！面额" + chargeContentPojo.getMoney() + "元"+ " ； uid ="  + chargeContentPojo.getUserId() + " 订单号 = " + chargeContentPojo.getCpTradeNo());
			}
		}
		if(success){
			success = chargeType(player,chargeContentPojo);			
		}
		
		return success;
	}
	
	private Player get(ChargeContentPojo chargeContentPojo) {
		Player player = null;		
		String uid = chargeContentPojo.getRoleId();
		if(StringUtils.isBlank(uid)){
			GameLog.error("chargemgr", "sdk-充值", "uid异常，无法获取uid，订单号为" + chargeContentPojo.getCpTradeNo());
			return player;
		}
		player = PlayerMgr.getInstance().find(uid);
		return player;
	}

	private boolean chargeType(Player player, ChargeContentPojo chargeContentPojo) {
		String itemId = chargeContentPojo.getItemId();//ios包没有 itemId字段
		ChargeCfg target = ChargeCfgDao.getInstance().getConfig(itemId);
		if(target == null){//ios
			itemId= chargeContentPojo.getPrivateField();
			target = ChargeCfgDao.getInstance().getConfig(itemId);
		}
		
		
		if(target!=null){
//			if(chargeContentPojo.getMoney() == 1){//合入的时候需注释
//				GameLog.error("chargemgr", "sdk-充值", "充值测试,价格为1分； 商品价格 =" + target.getMoneyCount() + " 订单金额 =" + chargeContentPojo.getMoney()+" 商品id="+ chargeContentPojo.getItemId() + " 订单号=" + chargeContentPojo.getCpTradeNo());
//			}else
			if(chargeContentPojo.getMoney()/100 != target.getMoneyCount()){
				GameLog.error("chargemgr", "sdk-充值", "充值失败,价格不匹配； 商品价格 =" + target.getMoneyCount() + " 订单金额 =" + chargeContentPojo.getMoney()+" 商品id="+ chargeContentPojo.getItemId() + " 订单号=" + chargeContentPojo.getCpTradeNo());
				return false;
			}
			
			boolean success = false;
			if(target.getChargeType() == ChargeTypeEnum.Normal){
				success = doCharge(player, target);
			}
			if(target.getChargeType() == ChargeTypeEnum.MonthCard || target.getChargeType() == ChargeTypeEnum.VipMonthCard){
				List<ActivityTimeCardTypeSubCfg>  timeCardList = ActivityTimeCardTypeSubCfgDAO.getInstance().getAllCfg();
				for(ActivityTimeCardTypeSubCfg timecardcfg : timeCardList){
					if(timecardcfg.getChargeType() == target.getChargeType()){
						success = buyMonthCard(player, timecardcfg.getId()).isSuccess();
						break;
					}
				}
			}
			UserEventMgr.getInstance().charge(player, chargeContentPojo.getMoney()/100);
			
			if(success){
				GameLog.error("chargemgr", "sdk-充值", "充值成功;  " + chargeContentPojo.getMoney() + "分"+ ",充值类型 =" + target.getChargeType() + " 订单号 =" + chargeContentPojo.getCpTradeNo());
			}else{
				GameLog.error("chargemgr", "sdk-充值", "充值失败,商品价值;  " + chargeContentPojo.getMoney() + "元"+ ",充值类型 =" + target.getChargeType() + " 商品id =" + chargeContentPojo.getItemId()+ " 订单号 =" + chargeContentPojo.getCpTradeNo());
			}
		}else{
			GameLog.error("chargemgr", "sdk-充值", "充值失败,未找到商品  ； 商品id =" + chargeContentPojo.getItemId()+ " 订单号 =" + chargeContentPojo.getCpTradeNo());
		}		
		return true;
	}

	
	
	



	public ChargeResult charge(Player player, String itemId){
		
		ChargeResult result = ChargeResult.newResult(false);
		
		ChargeCfg target = ChargeCfgDao.getInstance().getConfig(itemId);
		if(target!=null){
			boolean success = doCharge(player, target);
			result.setSuccess(success);
			result.setTips("充值成功.");
		}else{
			result.setTips("充值类型错误.");
		}
		return result;
	}

	private boolean doCharge(Player player, ChargeCfg target) {

		int addGold = target.getGoldCount();
		int money = target.getMoneyCount();
		
		player.getUserGameDataMgr().addReCharge(addGold);
		ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
		chargeInfo.addTotalChargeGold(addGold).addTotalChargeMoney(money).addCount(1);
		
		//派发限购的钻石奖励
		ChargeInfoSubRecording sublist = null;
		Boolean isextragive = false;
		Boolean ishasrecording = false;
		for(ChargeInfoSubRecording sub :chargeInfo.getPayTimesList()){
			if(Integer.parseInt(target.getId())==Integer.parseInt(sub.getId())){//有该道具的购买记录
				ishasrecording = true;
				if(target.getGiveCount()>sub.getCount()){//还有多余的限购次数
					isextragive = true;
					sublist = sub;
				}
			}
		}
		if(!ishasrecording&&target.getGiveCount() > 0){//可限购且没记录
			isextragive = true;
		}
		if(isextragive){
			if(sublist != null){//只有限购次数大于1才会有数据而进入；；
				
			}else{
				ChargeInfoSubRecording sub = ChargeCfgDao.getInstance().newSubItem(target.getId());
				sub.setCount(sub.getCount() + 1);
				chargeInfo.getPayTimesList().add(sub);
				player.getUserGameDataMgr().addGold(target.getExtraGive());//派出额外的钻石				
			}
		}
		
		
		
		//派发首冲的额外奖励-------------------------------
		if(chargeInfo.getCount()==1){
			FirstChargeCfg cfg = FirstChargeCfgDao.getInstance().getAllCfg().get(0);
			int addgoldfirstcharge =  addGold*cfg.getAwardTimes() < cfg.getAwardMax() ?  addGold*cfg.getAwardTimes() : cfg.getAwardMax();
			player.getUserGameDataMgr().addGold(addgoldfirstcharge);			
		}
		
		ChargeInfoHolder.getInstance().update(player);		
		
		player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Recharge);
		
		//升级vip，如果达到条件
		upgradeVip(player, chargeInfo);
		// 设置界面更新vip
		player.getSettingMgr().checkOpen();
		
		return true;
		
	}

	private void upgradeVip(Player player, ChargeInfo chargeInfo) {
		int totalChargeGold = chargeInfo.getTotalChargeGold();
		PrivilegeCfg cfg = PrivilegeCfgDAO.getInstance().getCfg(player.getVip() + 1);
		if(cfg == null){
			return;
		}
		while (cfg.getRechargeCount() <= totalChargeGold) {
			player.AddVip(1);
//			totalChargeGold -= cfg.getRechargeCount();
			cfg = PrivilegeCfgDAO.getInstance().getCfg(player.getVip() + 1);
		}
	}

	public ChargeResult gerRewardForFirstPay(Player player) {
		ChargeResult result = ChargeResult.newResult(false);
		ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
		if(chargeInfo == null){
			
		}else{
			if(!chargeInfo.isFirstAwardTaken()&&chargeInfo.getCount()>0){
				chargeInfo.setFirstAwardTaken(true);
				ComGiftMgr.getInstance().addGiftById(player,FirstChargeCfgDao.getInstance().getAllCfg().get(0).getReward());	
				
				
				
				
				
//				FirstChargeCfg cfg = FirstChargeCfgDao.getInstance().getAllCfg().get(0);
//				Set<String> keySet = cfg.getGiftMap().keySet();
//	
//				Iterator<String> iterable = keySet.iterator();
//				while(iterable.hasNext()){
//					String giftid = iterable.next();
//					int count = cfg.getGiftMap().get(giftid);
//					player.getItemBagMgr().addItem(Integer.parseInt(giftid),count);
//				}
				
				
				ChargeInfoHolder.getInstance().update(player);
				result.setSuccess(true);
			}else{
				result.setTips("数据异常 没有首冲奖励");
			}		
		}		
		return result;
	}

	public ChargeResult buyMonthCard(Player player, String timeCardSubCfgId) {
		UserEventMgr.getInstance().charge(player, 30);//模拟充值的充值活动传入，测试用，正式服需注释
		ChargeResult result = ChargeResult.newResult(false);
		ActivityTimeCardTypeItemHolder dataHolder = ActivityTimeCardTypeItemHolder.getInstance();
		
		ActivityTimeCardTypeItem dataItem = dataHolder.getItem(player.getUserId(),ActivityTimeCardTypeEnum.Month);
		if(dataItem == null){//首次读取创建记录
			dataItem = ActivityTimeCardTypeCfgDAO.getInstance().newItem(player,ActivityTimeCardTypeEnum.Month);
			if(dataItem != null){
				dataHolder.addItem(player, dataItem);
				}
		}
		
		List<ActivityTimeCardTypeSubItem>  monthCardList = dataItem.getSubItemList();
		ActivityTimeCardTypeSubItem targetItem = null;
		ChargeTypeEnum cardtypenume = ActivityTimeCardTypeSubCfgDAO.getInstance().getById(timeCardSubCfgId).getChargeType();
		
		
		
		String cardtype= cardtypenume.getCfgId();
		for (ActivityTimeCardTypeSubItem itemTmp : monthCardList) {
			if(StringUtils.equals(itemTmp.getChargetype(), cardtype)){
				targetItem = itemTmp;
				break;
			}
		}
		
		if(targetItem == null){//newitem已添加list，不会null
			GameLog.error("chargemgr", "买月卡", "chargeMgr.list里没有该项月卡类型！！"+player);
			result.setTips("购买月卡异常");
		}else{
			int tempdayleft = targetItem.getDayLeft();
			targetItem.setDayLeft(targetItem.getDayLeft() + ActivityTimeCardTypeSubCfgDAO.getInstance().getBynume(cardtypenume).getDays());
			dataHolder.updateItem(player, dataItem);
			result.setSuccess(true);
			
			DailyActivityHandler.getInstance().sendTaskList(player);
			
			
			
			if(tempdayleft < ActivityTimeCardTypeSubCfgDAO.getInstance().getById(timeCardSubCfgId).getDaysLimit()){				
				result.setTips("购买月卡成功");				
			}else{				
				result.setTips("剩余日期超过5天但依然冲了钱。。。");
				GameLog.error("chargemgr", "买月卡", "没到期也能付费,玩家名 ="+player.getUserName()+" 月卡cfgid =" + timeCardSubCfgId);
			}
		}
		if (result.isSuccess()){
			String orderStr = targetItem.getChargetype();
			try {
				if (StringUtils.isNotBlank(orderStr)){
					int order = Integer.parseInt(orderStr);
					ChargeTypeEnum[] values = ChargeTypeEnum.values();
					if (0 <= order && order < values.length){
						ChargeTypeEnum type = values[order];
						MonthCardPrivilegeMgr.getShareInstance().signalMonthCardChange(player, type, true);
					}
				}
			} catch (Exception e) {
				GameLog.info("特权", player.getUserId(), "无法获取充值类型:"+orderStr, e);
			}
		}
		return result;
	}

	
	
	
	
}
