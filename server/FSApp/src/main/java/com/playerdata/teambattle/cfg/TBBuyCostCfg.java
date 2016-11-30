package com.playerdata.teambattle.cfg;
import java.util.List;

import com.common.BaseConfig;
import com.playerdata.groupFightOnline.bm.GFightHelper;
import com.rwbase.dao.copy.pojo.ItemInfo;

public class TBBuyCostCfg extends BaseConfig {
	private String Key; //关键字段
	private String id; //副本id
	private int times; //第X次购买
	private int numbers; //每次购买可获得挑战次数
	private String cost; //花费
	public List<ItemInfo> list_cost;

	public String getKey() {
		return Key;
	}
	
	public String getId() {
		return id;
	}
	
	public int getTimes() {
		return times;
	}
	
	public int getNumbers() {
		return numbers;
	}
	
	public String getCost() {
		return cost;
	}
	
	public List<ItemInfo> getBuyCost(){
		return this.list_cost;
	}
	
	@Override
 	public void ExtraInitAfterLoad() {
 		this.list_cost = GFightHelper.stringToItemList(cost, "_");
 	}
}
