package com.playerdata.charge;

import com.log.GameLog;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.cfg.ChargeCfgDao;
import com.playerdata.charge.cfg.ChargeTypeEnum;
import com.playerdata.charge.cfg.FirstChargeCfg;
import com.playerdata.charge.cfg.FirstChargeCfgDao;
import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.charge.dao.ChargeInfoHolder;
import com.playerdata.charge.dao.ChargeOrder;
import com.rw.chargeServer.ChargeContentPojo;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;

public class ChargeMgr {

	private static ChargeMgr instance = new ChargeMgr();
	
	public static ChargeMgr getInstance(){
		return instance;
	}
	
	public void syn(Player player, int version){		
		ChargeInfoHolder.getInstance().syn(player,version);
	}
	
	public ChargeInfo getChargeInfo(String userId){
		return ChargeInfoHolder.getInstance().get(userId);
	}
	
	public ChargeResult chargeAndTakeGift(Player player, String itemId){
		
		ChargeResult result = ChargeResult.newResult(false);
		
		ChargeCfg target = ChargeCfgDao.getInstance().getConfig(itemId);
		
		if(target!=null){
			boolean success = doCharge(player, target);
			if(success){
				success = takeChargeGift(target);
			}
			
			result.setSuccess(success);
		}else{
			result.setTips("充值类型错误.");
		}
		return result;
	}
	
	private boolean takeChargeGift(ChargeCfg target) {int extraGiftId = target.getExtraGiftId();
		// TODO takeGift logic
		return true;
	}
	
	public boolean charge(ChargeContentPojo chargeContentPojo){
		boolean success=false;
		//TODO: 充值，保存订单，返回结果
		Player player = get(chargeContentPojo);
		if(player!=null){
			ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
			if(!chargeInfo.isOrderExist(chargeContentPojo.getCpTradeNo())){
				ChargeOrder chargeOrder = ChargeOrder.fromReq(chargeContentPojo);
				success = ChargeInfoHolder.getInstance().addChargeOrder(player,chargeOrder);
			}			
		}
		if(success){
			chargeType(player,chargeContentPojo);			
		}
		
		return success;
	}
	
	private Player get(ChargeContentPojo chargeContentPojo) {
		Player player = PlayerMgr.getInstance().find(chargeContentPojo.getUserId());		
		return player;
	}

	private void chargeType(Player player, ChargeContentPojo chargeContentPojo) {
		ChargeCfg target = ChargeCfgDao.getInstance().getConfig(chargeContentPojo.getItemId());

		
		
		if(target!=null){
			boolean success = false;
			if(target.getChargeType() == ChargeTypeEnum.Normal){
				success = doCharge(player, target);
			}
			if(target.getChargeType() == ChargeTypeEnum.MonthCard || target.getChargeType() == ChargeTypeEnum.VipMonthCard){
				//月卡购买逻辑；需合入月卡分支后处理
			}
			
			if(success){
				GameLog.error("chargemgr", "sdk-充值", "充值成功" + chargeContentPojo.getMoney() + "元"+ "充值类型 =" + target.getChargeType());
			}else{
				GameLog.error("chargemgr", "sdk-充值", "充值失败" + chargeContentPojo.getMoney() + "元"+ "充值类型 =" + target.getChargeType());
			}
		}	
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
		player.getUserGameDataMgr().addGold(addGold);
		ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
		chargeInfo.addTotalChargeGold(addGold).addTotalChargeMoney(money).addCount(1);
		
		//限购派发奖励
		
		
		
		//派发账号首冲额外奖励-------------------------------
		if(chargeInfo.getCount()==1){
			FirstChargeCfg cfg = FirstChargeCfgDao.getInstance().getAllCfg().get(0);
			int addgoldfirstcharge =  addGold*cfg.getAwardTimes() < cfg.getAwardMax() ?  addGold*cfg.getAwardTimes() : cfg.getAwardMax();
			player.getUserGameDataMgr().addReCharge(addgoldfirstcharge);			
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
		while (cfg.getRechargeCount() <= totalChargeGold) {
			player.AddVip(1);
			totalChargeGold -= cfg.getRechargeCount();
			cfg = PrivilegeCfgDAO.getInstance().getCfg(player.getVip() + 1);
		}
	}

	public ChargeResult gerReward(Player player) {
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
	
	
	
	
}
