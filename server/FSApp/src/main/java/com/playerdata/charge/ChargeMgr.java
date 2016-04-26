package com.playerdata.charge;

import java.util.Iterator;



import java.util.Set;

import com.log.GameLog;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.cfg.ChargeCfgDao;
import com.playerdata.charge.cfg.FirstChargeCfg;
import com.playerdata.charge.cfg.FirstChargeCfgDao;
import com.playerdata.charge.cfg.VipGiftCfg;
import com.playerdata.charge.cfg.VipGiftCfgDao;
import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.charge.dao.ChargeInfoHolder;
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
	
	public ChargeResult buyAndTakeVipGift(Player player, String itemId){		
		ChargeResult result = ChargeResult.newResult(false);		
		VipGiftCfg target = VipGiftCfgDao.getInstance().getCfgById(itemId);
		int viplevel = Integer.parseInt(itemId);
		
		if(target!=null && !player.getVipMgr().isVipGiftTaken(viplevel)){
			player.getVipMgr().setVipGiftTaken(viplevel);
			boolean success = buyVipGift(player, target,result);
			if(success){
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
			GameLog.error("chargeMgr.Vip等级低于购买礼包等级");
			return false;
		}	
		
		if(player.getUserGameDataMgr().getGold() < Integer.parseInt(target.getCurCost())){
			result.setTips("货币不够");
			GameLog.error("chargeMgr.货币不够");
			return false;
		}
		player.getUserGameDataMgr().addGold(-Integer.parseInt(target.getCurCost()));
		
		
		// TODO Auto-generated method stub
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
		player.getUserGameDataMgr().addGold(target.getExtraGive());
		ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
		chargeInfo.addTotalChargeGold(addGold).addTotalChargeMoney(money).addCount(1);
		
		//派发额外-------------------------------
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
	
}
