package com.rw.service.gamble.datamodel;

import java.util.List;
import java.util.Random;

import com.common.RandomIntGroups;
import com.common.RefInt;

public class TmpDropGamblePlan implements IDropGambleItemPlan {
	private DropGamblePlan proxy;
	private boolean usingGuarantee;

	public TmpDropGamblePlan(DropGamblePlan proxy) {
		this.proxy = proxy;
		tmpOrdinaryGroup = proxy.getOrdinaryGroup();
		usingGuarantee = false;
	}

	public int getMaxCheckNum() {
		return proxy.getMaxCheckNum();
	}

	@Override
	public int getExclusiveCount() {
		return proxy.getExclusiveCount();
	}

	@Override
	public int getCheckNum(int index) {
		return proxy.getCheckNum(index);
	}

	@Override
	public int getLastCheckIndex() {
		return proxy.getLastCheckIndex();
	}

	@Override
	public boolean checkInList(String itemModelId) {
		return proxy.checkInList(itemModelId);
	}

	@Override
	public int getGuaranteeGroup(Random r) {
		return proxy.getGuaranteeGroup(r);
	}

	@Override
	public int getGuaranteeGroup(Random r, RefInt planIndex) {
		return proxy.getGuaranteeGroup(r,planIndex);
	}
	
	@Override
	public GambleDropGroup getGuaranteeGroup(Random r, List<String> historyRecord,RefInt selectedIndex) {
		return proxy.getGuaranteeGroup(r, historyRecord,selectedIndex);
	}

	@Override
	public boolean isSingleGamble() {
		return proxy.isSingleGamble();
	}

	@Override
	public Iterable<ItemOrHeroGambleInfo> ReInitPreviewData() {
		return proxy.ReInitPreviewData();
	}

	@Override
	public Iterable<ItemOrHeroGambleInfo> getGamblePosibles() {
		return proxy.getGamblePosibles();
	}

	@Override
	public int getOrdinaryGroup(Random r) {
		return getOrdinaryGroup(r,(RefInt)null);
	}

	private RandomIntGroups tmpOrdinaryGroup;
	@Override
	public IDropGambleItemPlan removeHistoryFromOrdinaryGroup(int planIdIndex) {
		if (tmpOrdinaryGroup!= null){
			tmpOrdinaryGroup = tmpOrdinaryGroup.removeIndex(planIdIndex);
			if (tmpOrdinaryGroup == null){
				if (!usingGuarantee){
					//一般抽卡组失效，使用保底方案组
					tmpOrdinaryGroup = proxy.getGuaranteeGroup();
					usingGuarantee = true;
				}else{
					//保底方案组也失效，使用最后容错方案
					return null;
				}
			}
		}
		return this;
	}
	
	@Override
	public GambleDropGroup getOrdinaryGroup(Random r, List<String> historyRecord,RefInt selectedIndex) {
		if (tmpOrdinaryGroup!= null){
			return proxy.getGroup(r, historyRecord, tmpOrdinaryGroup,selectedIndex);
		}
		return proxy.getOrdinaryGroup(r, historyRecord,selectedIndex);
	}

	@Override
	public int getOrdinaryGroup(Random r, RefInt planIndex) {
		if (tmpOrdinaryGroup!= null){
			return tmpOrdinaryGroup.getRandomGroup(r,planIndex);
		}
		return proxy.getOrdinaryGroup(r,planIndex);
	}

}
