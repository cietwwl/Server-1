package com.rwbase.dao.targetSell;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;

/**
 * 精准营销系统推送的一组物品
 * @author Alex
 * 2016年9月17日 下午3:26:51
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@SynClass
public class BenefitItems {

	/**物品组id*/
	private int itemGroupId;
	
	/**分类*/
	private int zlass;
	
	/**时间戳，秒*/
	private int finishTime;
	
	/**道具id字符串，格式：ID*NUM,ID*NUM,ID,ID*NUM*/
	private String itemIds;
	
	/**充值金额，如果actionName=reward时，没有此字段*/
	private int	recharge;
	
	/**标题*/
	private String title;

	
	
	
	public int getItemGroupId() {
		return itemGroupId;
	}

	public void setItemGroupId(int itemGroupId) {
		this.itemGroupId = itemGroupId;
	}

	public int getZlass() {
		return zlass;
	}

	public void setZlass(int zlass) {
		this.zlass = zlass;
	}

	public int getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}

	public String getItemIds() {
		return itemIds;
	}

	public void setItemIds(String itemIds) {
		this.itemIds = itemIds;
	}

	public int getRecharge() {
		return recharge;
	}

	public void setRecharge(int recharge) {
		this.recharge = recharge;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
	
}
