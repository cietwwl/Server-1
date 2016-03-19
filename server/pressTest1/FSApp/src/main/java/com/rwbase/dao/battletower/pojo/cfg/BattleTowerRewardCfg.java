package com.rwbase.dao.battletower.pojo.cfg;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.util.StringUtils;

import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwproto.BattleTowerServiceProtos.RewardInfoMsg;

/*
 * @author HC
 * @date 2015年9月7日 上午11:01:40
 * @Description 
 */
public class BattleTowerRewardCfg {
	private int groupId;// 试练塔的里程碑Id
	private String firstReward;// 首次奖励的数据
	private String dropIds;// 掉落方案的Id列表
	private String unfirstReward;// 不是首次掉落的奖励数据（仅用于界面显示）
	private int copyId;// 每个组对应的战斗Id

	// /////////////////////////////////////////////////////////不序列化字段
	@JsonIgnore
	private List<ItemInfo> firstRewardList;
	@JsonIgnore
	private String[] dropIdArr;
	@JsonIgnore
	private List<RewardInfoMsg> unfirstRewardInfoMsg;

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getFirstReward() {
		return firstReward;
	}

	public void setFirstReward(String firstReward) {
		this.firstReward = firstReward;
	}

	public String getDropIds() {
		return dropIds;
	}

	public void setDropIds(String dropIds) {
		this.dropIds = dropIds;
	}

	public List<ItemInfo> getFirstRewardList() {
		// 转换成列表
		if (!StringUtils.isEmpty(this.firstReward)) {
			String[] temp = this.firstReward.split(",");
			this.firstRewardList = new ArrayList<ItemInfo>(temp.length);
			for (String temp0 : temp) {
				String[] temp1 = temp0.split(":");
				ItemInfo itemInfo = new ItemInfo();
				itemInfo.setItemID(Integer.parseInt(temp1[0]));
				itemInfo.setItemNum(Integer.parseInt(temp1[1]));
				this.firstRewardList.add(itemInfo);
			}
		}

		return firstRewardList == null ? new ArrayList<ItemInfo>() : firstRewardList;
	}

	public String[] getDropIdArr() {
		// 转换成列表
		if (!StringUtils.isEmpty(this.dropIds)) {
			this.dropIdArr = this.dropIds.split(",");
		}

		return dropIdArr == null ? new String[0] : dropIdArr;
	}

	public String getUnfirstReward() {
		return unfirstReward;
	}

	public void setUnfirstReward(String unfirstReward) {
		this.unfirstReward = unfirstReward;
	}

	public List<RewardInfoMsg> getUnfirstRewardInfoMsg() {
		// 非第一次奖励的信息，转换成奖励的信息
		if (this.unfirstReward != null && !this.unfirstReward.isEmpty()) {
			String[] temp = this.unfirstReward.split(",");
			int len = temp.length;
			this.unfirstRewardInfoMsg = new ArrayList<RewardInfoMsg>(len);
			for (String temp0 : temp) {
				String[] temp1 = temp0.split(":");
				RewardInfoMsg.Builder rewardInfoMsg = RewardInfoMsg.newBuilder();
				rewardInfoMsg.setType(Integer.parseInt(temp1[0]));
				rewardInfoMsg.setCount(Integer.parseInt(temp1[1]));
				this.unfirstRewardInfoMsg.add(rewardInfoMsg.build());
			}
		}
		return unfirstRewardInfoMsg == null ? new ArrayList<RewardInfoMsg>() : unfirstRewardInfoMsg;
	}

	public int getCopyId() {
		return copyId;
	}
}