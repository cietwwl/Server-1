package com.rw.service.gamble.datamodel;

import com.common.BaseConfig;
import com.common.PairParser;
import com.common.RefInt;
import com.log.GameLog;
import com.rwbase.common.enu.eSpecialItemId;

public class GamblePlanCfg extends BaseConfig {
	private int key;
	private int goods; // 购买道具(一个经验丹)
	private int freeFirstDrop; // 免费首抽必掉方案(gambleDropCfg组ID)
	private int chargeFirstDrop; // 首抽必掉方案(gambleDropCfg组ID)
	private eSpecialItemId moneyType; // 货币类型
	private int moneyNum; // 货币数量
	private String openCondition; // 开放条件
	private int recoverTime; // 免费时间间隔
	private int freeCountPerDay; // 日免费次数
	private int dropItemCount; // 掉落物品数量
	private String ordinaryFreePlan; // 免费掉落方案
	private String guaranteeFreeCheckList; // 免费保底检索物品组
	private String guaranteeFreePlan; // 免费保底掉落方案
	private String ordinaryPlan; // 收费掉落方案
	private String guaranteeCheckList; // 收费保底检索物品组
	private String guaranteePlan; // 收费保底掉落方案

	//开放等级和vip等级（两个条件为并关系）
	private int openLevel;
	private int openVipLevel;
	
	private int hotCount;//本次抽取热点个数
	private String hotCheckRange;//热点必掉英雄次数
	
	private int hotCheckMin;
	private int hotCheckMax;
	
	//免费组
	private int guaranteeFreeCheckNum; // 免费保底检索次数
	private DropGamblePlan freePlan;
	
	//收费组
	private int guaranteeCheckNum; // 收费保底检索次数
	private DropGamblePlan chargePlan;

	@Override
	public void ExtraInitAfterLoad() {
		String[] cond = openCondition.split(",");
		openLevel = Integer.parseInt(cond[0]);
		openVipLevel=Integer.parseInt(cond[1]);
		freePlan = new DropGamblePlan(guaranteeFreeCheckList, ordinaryFreePlan, guaranteeFreePlan, guaranteeFreeCheckNum);
		chargePlan = new DropGamblePlan(guaranteeCheckList, ordinaryPlan, guaranteePlan, guaranteeCheckNum);

		if (hotCount > 0){
			RefInt i1=new RefInt();
			RefInt i2=new RefInt();
			if (!PairParser.ParseTwoInt(hotCheckRange, "~", "钓鱼台", "GamblePlanCfg.csv,Id="+key, i1, i2)){
				GameLog.error("钓鱼台", "GamblePlanCfg.csv,Id="+key, "没有设置 热点必掉英雄次数");
			}
			hotCheckMin = i1.value;
			hotCheckMax = i2.value;
		}
		
		if (moneyNum <= 0){
			GameLog.error("钓鱼台", "GamblePlanCfg.csv,Id="+key, "收费数量无效");
		}
		if (moneyType == null){
			GameLog.error("钓鱼台", "GamblePlanCfg.csv,Id="+key, "收费类型无效");
		}
		if (dropItemCount <= 0){
			GameLog.error("钓鱼台", "GamblePlanCfg.csv,Id="+key, "掉落物品数量无效");
		}
	}

	public int getHotCheckMin() {
		return hotCheckMin;
	}

	public int getHotCheckMax() {
		return hotCheckMax;
	}

	public int getHotCount() {
		return hotCount;
	}

	public IDropGambleItemPlan getFreePlan(){
		return freePlan;
	}
	
	public IDropGambleItemPlan getChargePlan(){
		return chargePlan;
	}
	
	public int getOpenLevel() {
		return openLevel;
	}

	public int getOpenVipLevel() {
		return openVipLevel;
	}

	public int getGoods() {
		return goods;
	}

	public eSpecialItemId getMoneyType() {
		return moneyType;
	}

	public int getMoneyNum() {
		return moneyNum;
	}

	public int getRecoverTime() {
		return recoverTime;
	}

	public int getFreeCountPerDay() {
		return freeCountPerDay;
	}

	public int getDropItemCount() {
		return dropItemCount;
	}

	public int getFreeFirstDrop() {
		return freeFirstDrop;
	}

	public int getChargeFirstDrop() {
		return chargeFirstDrop;
	}

	public int getKey() {
		return key;
	}
}