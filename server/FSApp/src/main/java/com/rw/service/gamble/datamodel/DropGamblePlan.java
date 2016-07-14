package com.rw.service.gamble.datamodel;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.common.ListParser;
import com.common.RandomIntGroups;
import com.common.RefInt;

public class DropGamblePlan implements IDropGambleItemPlan {
	private int[] checkList;
	private RandomIntGroups ordinaryGroup;
	private RandomIntGroups guaranteeGroup;
	private int[] guaranteeCheckNumList; // 收费保底检索次数
	private int maxCheckCount=0;
	private int exclusiveCount=0;
	private boolean isSignleGamble;
	
	public DropGamblePlan(String guaranteeCheckList,String ordinaryPlan,String guaranteePlan,String guaranteeCheckNum,int exclusiveCount,boolean isSignleGamble){
		this.isSignleGamble = isSignleGamble;
		checkList = ListParser.ParseIntList(guaranteeCheckList, ",", "钓鱼台", "", "解释保底检索物品组");
		ordinaryGroup = RandomIntGroups.Create("钓鱼台", "GamblePlanCfg.csv", ",", "_", ordinaryPlan);
		guaranteeGroup = RandomIntGroups.Create("钓鱼台", "GamblePlanCfg.csv", ",", "_", guaranteePlan);
		guaranteeCheckNumList = ListParser.ParseIntList(guaranteeCheckNum, "\\|", "钓鱼台", "", "收费保底检索次数");
		if (guaranteeCheckNumList.length <=0){
			throw new RuntimeException("钓鱼台 GamblePlanCfg.csv 没有配置 收费保底检索次数 guaranteeCheckNum");
		}
		for (int i = 0;i<guaranteeCheckNumList.length;i++){
			if (guaranteeCheckNumList[i]>maxCheckCount){
				maxCheckCount = guaranteeCheckNumList[i];
			}
		}
		this.exclusiveCount = exclusiveCount;
	}

	@Override
	public int getExclusiveCount() {
		return exclusiveCount;
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
	public int getLastCheckIndex(){
		return guaranteeCheckNumList.length-1;
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
	
	public int getOrdinaryGroup(Random r, RefInt planIndex) {
		return ordinaryGroup.getRandomGroup(r,planIndex);
	}
	
	public int getGuaranteeGroup(Random r, RefInt planIndex) {
		return guaranteeGroup.getRandomGroup(r,planIndex);
	}
	
	@Override
	public GambleDropGroup getGuaranteeGroup(Random r, List<String> historyRecord,RefInt selectedIndex) {
		return getGroup(r, historyRecord, guaranteeGroup,selectedIndex);
	}

	@Override
	public GambleDropGroup getOrdinaryGroup(Random r, List<String> historyRecord,RefInt selectedIndex) {
		return getGroup(r, historyRecord, ordinaryGroup,selectedIndex);
	}

	protected GambleDropGroup getGroup(Random r, List<String> historyRecord, RandomIntGroups startGroup,RefInt selectedIndex) {
		if (historyRecord == null || historyRecord.size() <= 0) {
			int selected = startGroup.getRandomGroup(r,selectedIndex);
			return GambleDropCfgHelper.getInstance().getGroup(selected);
		}

		RefInt selectedGroupIndex = new RefInt();
		RandomIntGroups tmpGroup = startGroup;
		GambleDropGroup result = null;
		boolean isFirst = true;
		while (result == null && tmpGroup != null && tmpGroup.size() > 0) {
			if (isFirst) {
				isFirst = false;
			} else {
				tmpGroup = startGroup.removeIndex(selectedGroupIndex.value);
			}
			result = findRandomGroup(r, historyRecord, tmpGroup, selectedGroupIndex);
		}
		if (selectedIndex!=null){
			selectedIndex.value = selectedGroupIndex.value;
		}
		return result;
	}
	
	private GambleDropGroup findRandomGroup(Random r, List<String> historyRecord,RandomIntGroups startGroup,RefInt selectedGroupIndex){
		int groupKey = startGroup.getRandomGroup(r, selectedGroupIndex);
		GambleDropGroup result =GambleDropCfgHelper.getInstance().getGroup(groupKey);
		if (result != null){
			result = result.removeHistory(historyRecord);
		}
		return result;
	}

	@Override
	public boolean isSingleGamble() {
		return isSignleGamble;
	}
	
	@Override
	public IDropGambleItemPlan removeHistoryFromOrdinaryGroup(int planId) {
		TmpDropGamblePlan tmp = new TmpDropGamblePlan(this);
		return tmp.removeHistoryFromOrdinaryGroup(planId);
	}
	
	protected RandomIntGroups getOrdinaryGroup(){
		return ordinaryGroup;
	}

	protected RandomIntGroups getGuaranteeGroup(){
		return guaranteeGroup;
	}
}
