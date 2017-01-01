package com.playerdata.charge;

import com.log.GameLog;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.charge.cfg.FirstChargeCfgDao;
import com.playerdata.charge.cfg.VipGiftCfg;
import com.playerdata.charge.cfg.VipGiftCfgDao;
import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.charge.dao.ChargeInfoHolder;

public class ChargeGiftMgr {
	
	private static ChargeGiftMgr _instance = new ChargeGiftMgr();
	
	public static ChargeGiftMgr getInstance() {
		return _instance;
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
	
	public ChargeResult gerRewardForFirstPay(Player player) {
		ChargeResult result = ChargeResult.newResult(false);
		ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
		if (chargeInfo != null) {
			if (!chargeInfo.isFirstAwardTaken() && chargeInfo.getCount() > 0) {
				chargeInfo.setFirstAwardTaken(true);
				ComGiftMgr.getInstance().addGiftById(player, FirstChargeCfgDao.getInstance().getAllCfg().get(0).getReward());

				ChargeInfoHolder.getInstance().update(player);
				result.setSuccess(true);
			} else {
				result.setTips("数据异常 没有首冲奖励");
			}
		}
		return result;
	}
}
