package com.rw.service.gamble.datamodel;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.common.ListParser;
import com.common.RandomIntGroups;

public class DropGamblePlan implements IDropGambleItemPlan {
	private int[] checkList;
	private RandomIntGroups ordinaryGroup;
	private RandomIntGroups guaranteeGroup;
	private int[] guaranteeCheckNumList; // 收费保底检索次数
	private int maxCheckCount=0;
	
	public DropGamblePlan(String guaranteeCheckList,String ordinaryPlan,String guaranteePlan,String guaranteeCheckNum){
		checkList = ListParser.ParseIntList(guaranteeCheckList, ",", "钓鱼台", "", "解释保底检索物品组");
		ordinaryGroup = RandomIntGroups.Create("钓鱼台", "GamblePlanCfg.csv", ",", "_", ordinaryPlan);
		guaranteeGroup = RandomIntGroups.Create("钓鱼台", "GamblePlanCfg.csv", ",", "_", guaranteePlan);
		guaranteeCheckNumList = ListParser.ParseIntList(guaranteeCheckNum, "|", "钓鱼台", "", "收费保底检索次数");
		if (guaranteeCheckNumList.length <=0){
			throw new RuntimeException("钓鱼台 GamblePlanCfg.csv 没有配置 收费保底检索次数 guaranteeCheckNum");
		}
		for (int i = 0;i<guaranteeCheckNumList.length;i++){
			if (guaranteeCheckNumList[i]>maxCheckCount){
				maxCheckCount = guaranteeCheckNumList[i];
			}
		}
	}

	public int getMaxCheckNum() {
		return maxCheckCount;
	}
	
	@Override
	public int getCheckNum(int index){
		int length = guaranteeCheckNumList.length;
		int ind = index < 0 ? 0 : index >= length ? length -1: index; 
		return guaranteeCheckNumList[ind];
	}

	@Override
	public boolean checkInList(String itemModelId) {
		if (StringUtils.isBlank(itemModelId))
			return false;
		GambleDropCfgHelper helper = GambleDropCfgHelper.getInstance();
		for (int i = 0; i < checkList.length; i++) {
			if (helper.checkInGroup(checkList[i], itemModelId)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getOrdinaryGroup(Random r) {
		return ordinaryGroup.getRandomGroup(r);
	}

	@Override
	public int getGuaranteeGroup(Random r) {
		return guaranteeGroup.getRandomGroup(r);
	}

}
