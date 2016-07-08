package com.rw.service.gamble.datamodel;

import org.apache.commons.lang3.StringUtils;

import com.common.BaseConfig;
import com.common.PairParser;
import com.common.RefInt;
import com.log.GameLog;
import com.rwbase.common.enu.eSpecialItemId;

public class GamblePlanCfg extends BaseConfig {
	private int key;
	private int dropType;//抽卡分类
	private String levelSegment;//等级分段
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

	//等级分段的起始值
	private int levelStart;
	private int levelEnd;
	
	//开放等级和vip等级（两个条件为并关系）
	private int openLevel;
	private int openVipLevel;
	
	private int hotCount;//本次抽取热点个数
	private String hotCheckRange;//热点必掉英雄次数
	
	private int hotCheckMin;
	private int hotCheckMax;
	
	//免费组
	private String guaranteeFreeCheckNum; // 免费保底检索次数
	private DropGamblePlan freePlan;
	private int freeExclusiveCount;//唯一性检查次数（免费组）
	
	//收费组
	private String guaranteeCheckNum; // 收费保底检索次数
	private DropGamblePlan chargePlan;
	private int chargeExclusiveCount;//唯一性检查次数（收费组）

	private int maxCheckCount;//免费组和收费组的最高需要保存的历史记录值
	
	@Override
	public void ExtraInitAfterLoad() {
		String[] cond = openCondition.split(",");
		openLevel = Integer.parseInt(cond[0]);
		openVipLevel=Integer.parseInt(cond[1]);
		freePlan = new DropGamblePlan(guaranteeFreeCheckList, ordinaryFreePlan, guaranteeFreePlan, guaranteeFreeCheckNum,freeExclusiveCount,dropItemCount == 1);
		chargePlan = new DropGamblePlan(guaranteeCheckList, ordinaryPlan, guaranteePlan, guaranteeCheckNum,chargeExclusiveCount,dropItemCount == 1);

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
		
		if (StringUtils.isBlank(levelSegment)){
			GameLog.error("钓鱼台", "key="+key, "没有配置等级分段");
		}else{
			String[] seg = levelSegment.split("~");
			levelStart = Integer.parseInt(seg[0]);
			levelEnd = Integer.parseInt(seg[1]);
			if (levelStart > levelEnd){
				GameLog.error("钓鱼台", "key="+key, "等级分段配置有误");
			}
		}
		
		maxCheckCount = Math.max(freePlan.getMaxCheckNum(),chargePlan.getMaxCheckNum());
		int distinctCount = Math.max(freeExclusiveCount,chargeExclusiveCount);
		maxCheckCount = Math.max(distinctCount, maxCheckCount);
	}

	public int getMaxCheckCount() {
		return maxCheckCount;
	}

	public int getFreeExclusiveCount() {
		return freeExclusiveCount;
	}

	public int getChargeExclusiveCount() {
		return chargeExclusiveCount;
	}

	public int getDropType() {
		return dropType;
	}

	public int getLevelStart() {
		return levelStart;
	}

	public int getLevelEnd() {
		return levelEnd;
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

	public boolean inLevelSegment(int level) {
		return (levelStart <= level && level <= levelEnd);
	}
	
	public int obtainSpecialGuaranteeGroupId() {
		return chargePlan.getGuaranteeGroup().getPlanList()[0];
	}
}