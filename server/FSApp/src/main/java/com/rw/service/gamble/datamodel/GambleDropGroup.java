package com.rw.service.gamble.datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.RuntimeIoException;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.common.RandomStringGroups;
import com.common.RefInt;
import com.rw.fsutil.common.Pair;

public class GambleDropGroup extends RandomStringGroups {
	private int[] slotCountArr;

	//set 方法是Json库专用，其他类不要调用！
	public int[] getSlotCountArr() {
		return slotCountArr;
	}

	public void setSlotCountArr(int[] slotCountArr) {
		this.slotCountArr = slotCountArr;
	}

	public String[] getPlans() {
		return plans;
	}

	public void setPlans(String[] plans) {
		this.plans = plans;
	}

	public int[] getDistributions() {
		return distributions;
	}

	public void setDistributions(int[] distributions) {
		this.distributions = distributions;
	}

	public int getAccumulation() {
		return accumulation;
	}

	public void setAccumulation(int accumulation) {
		this.accumulation = accumulation;
	}
	
	protected GambleDropGroup(){super();}
	
	public GambleDropGroup(List<Pair<String, Integer>> pairList,int[] slotCountArr) {
		super(pairList);
		if (pairList.size() != slotCountArr.length) throw new RuntimeIoException("无效参数，两个数组长度不一致");
		this.slotCountArr=slotCountArr;
	}

	@JsonIgnore
	public String getRandomGroup(Random r, RefInt slotCount) {
		return getRandomGroup(r,slotCount,null);
	}
	
	@JsonIgnore
	public String getRandomGroup(Random r, RefInt slotCount,RefInt weight) {
		String result = super.getRandomGroup(r, slotCount,weight);//use slotCount to get plan index
		slotCount.value = slotCountArr[slotCount.value];
		return result;
	}

	public boolean checkInGroup(String itemModelId) {
		if (StringUtils.isBlank(itemModelId))
			return false;
		for (int i = 0; i < plans.length; i++) {
			if (plans[i].equals(itemModelId)) {
				return true;
			}
		}
		return false;
	}

	public static GambleDropGroup Create(LinkedList<GambleDropCfg> value) {
		ArrayList<Pair<String, Integer>> pairList = new ArrayList<Pair<String, Integer>>(value.size());
		int groupCount = value.size();
		int[] slotCountArr = new int[groupCount];
		for (int i = 0; i < groupCount; i++) {
			GambleDropCfg cfg = value.get(i);
			Pair<String, Integer> element = Pair.Create(cfg.getItemID(), cfg.getWeight());
			pairList.add(element);
			slotCountArr[i] = cfg.getSlotCount();
		}
		GambleDropGroup result = new GambleDropGroup(pairList,slotCountArr);
		return result;
	}

	@JsonIgnore
	public List<String> getStringList() {
		ArrayList<String> result = new ArrayList<String>(plans.length);
		for (int i = 0; i < plans.length; i++) {
			result.add(plans[i]);
		}
		return result;
	}

	@JsonIgnore
	public Collection<String> getHeroIdListWith(String guanrateeHero) {
		ArrayList<String> result = new ArrayList<String>(plans.length+1);
		//客户端假设了保底英雄作为本周热点放在最顶！
		result.add(guanrateeHero);
		for (int i = 0; i < plans.length; i++) {
			result.add(plans[i]);
		}
		return result;
	}
}