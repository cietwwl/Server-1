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
import com.playerdata.charge.dao.ChargeInfoSubRecording;
import com.rw.chargeServer.ChargeContentPojo;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.VipDataHolder;
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

	public boolean charge(ChargeContentPojo chargeContentPojo){
		//TODO: 充值，保存订单，返回结果
		
		return false;
	}

	public ChargeResult charge(Player player, String itemId){
		
		ChargeResult result = ChargeResult.newResult(false);
		
		ChargeCfg target = ChargeCfgDao.getInstance().getConfig(itemId);
		System.out.println("  target.getitemid" +   itemId  );
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
	
}
