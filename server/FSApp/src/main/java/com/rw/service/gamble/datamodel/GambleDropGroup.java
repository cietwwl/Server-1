package com.rw.service.gamble.datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
	
	protected GambleDropGroup(List<Pair<String, Integer>> pairList,int[] slotCountArr) {
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
		RefInt planIndex = new RefInt();
		String result = super.getRandomGroup(r, planIndex,weight);//use slotCount to get plan index
		slotCount.value = slotCountArr[planIndex.value];
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
	
	public static GambleDropGroup Create(List<Pair<String, Integer>> pairList,int[] slotCountArr){
		return new GambleDropGroup(pairList,slotCountArr);
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

	@JsonIgnore
	public GambleDropGroup removeHistory(List<String> historyRecord) {
		HashSet<String> tmp = new HashSet<String>(historyRecord);
		return removeHistory(tmp);
	}
	
	@JsonIgnore
	private GambleDropGroup removeHistory(Collection<String> tmp){
		List<Pair<String,Integer>> pair = new ArrayList<Pair<String,Integer>>();
		List<Integer> tmpCount = new ArrayList<Integer>();
		
		boolean decreased = false;
		int count =distributions.length;
		for (int i = 0; i < count; i++) {
			int delta;
			if (i > 0) {
				delta = distributions[i] - distributions[i - 1];
			} else {
				delta = distributions[i];
			}
			boolean isDuplicate = tmp.contains(plans[i]);
			if (!isDuplicate){
				pair.add(Pair.Create(plans[i], delta));
				tmpCount.add(this.slotCountArr[i]);
			}else{
				decreased = true;
			}
		}
		
		if (!decreased){
			return this;
		}
		
		if (tmpCount.size() <= 0){
			return null;
		}
		
		int[] array = new int[tmpCount.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = tmpCount.get(i);
		}
		
		return new GambleDropGroup(pair, array);
	}

	/**
	 * 连续生成N个热点
	 * 避免重复，如果热电组人数不够才允许重复
	 * @param r
	 * @param hotCount
	 * @param guanrateeHero
	 * @return
	 */
	public List<Pair<String, Integer>> getHotRandomGroup(Random r, int hotCount,String guanrateeHero) {
		List<String> historyRecord=new ArrayList<String>(1);
		List<Pair<String, Integer>> result = new ArrayList<Pair<String, Integer>>(hotCount);
		RefInt slotCount=new RefInt();
		RefInt weight=new RefInt();
		GambleDropGroup tmpGroup = this;
		String heroId = guanrateeHero;
		
		while (result.size() < hotCount){
			if (historyRecord.size() <= 0){
				historyRecord.add(heroId);
			}else{
				historyRecord.set(0, heroId);
			}
			tmpGroup = tmpGroup.removeHistory(historyRecord);
			heroId = tmpGroup.getRandomGroup(r, slotCount, weight);
			result.add(Pair.Create(heroId, weight.value));
		}
		
		return result;
	}
}
