package com.playerdata.groupFightOnline.cfg;
import java.util.List;

import com.common.BaseConfig;
import com.playerdata.groupFightOnline.bm.GFightHelper;
import com.rwbase.dao.copy.pojo.ItemInfo;

public class GFightBiddingCfg extends BaseConfig {
	private int key; //关键字段
	private int typeId; //竞标种类ID
	private int rate; //奖励倍率
	private int vip; //VIP限制
	private String cost; //花费
	private List<ItemInfo> list_cost;	//压标花费
	private String biddingReward; //压标奖励
	private List<ItemInfo> list_biddingReward;
	private String victoryReward; //被压标奖励
	private List<ItemInfo> list_victoryReward;
	private int victoryMaxRate; //被压上奖限倍率
	private int victoryRewardEmailId;	//被压标奖励的邮件id
	private int emailId; //压标成功邮件ID
	private int failEmailID; //压标失败对应邮件ID

	public int getKey() {
		return key;
	}
	
	public int getTypeId() {
		return typeId;
	}
	
	public int getRate() {
		return rate;
	}
	
	public int getVip() {
		return vip;
	}
	
	public String getCost() {
		return cost;
	}
	
	public String getCostCount() {
		return cost.split("_")[1];
	}
	
	public String getBiddingReward() {
		return biddingReward;
	}
	
	public String getVictoryReward() {
		return victoryReward;
	}
	
	public int getVictoryMaxRate() {
		return victoryMaxRate;
	}
	
	public int getVictoryRewardEmailId(){
		return victoryRewardEmailId;
	}
	
	public int getEmailId() {
		return emailId;
	}
	
	public int getFailEmailID() {
		return failEmailID;
	}
	
	public List<ItemInfo> getBiddingRewardItem(){
		return list_biddingReward;
	}
	
	public List<ItemInfo> getVictoryRewardItem(){
		return list_victoryReward;
	}
	
	public List<ItemInfo> getBidCost(){
		return list_cost;
	}
	
	@Override
	public void ExtraInitAfterLoad() {
		this.list_biddingReward = GFightHelper.stringToItemList(biddingReward, "~");
		this.list_victoryReward = GFightHelper.stringToItemList(victoryReward, "~");
		this.list_cost = GFightHelper.stringToItemList(cost, "_");
	}
}
