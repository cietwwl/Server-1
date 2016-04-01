package com.rwbase.dao.fashion;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.NonSave;

@Table(name = "fashion_brought_items")
@SynClass
public class FashionItem implements IMapItem, FashionItemIF {
	@Id
	private String id; //数据库存取id，用userId+"_"+fanshionId并起来,客户端不需要使用，因为每一件时装只允许购买一次
	
	private int fashionId;// 时装模型id
	
	@IgnoreSynField
	private String userId; // 用户ID，客户端用当前用户
	
	@IgnoreSynField
	private long buyTime;// 购买时间（或者续费时间）
	
	@NonSave
	private boolean isBrought = true;//以后可能需要保存到数据库
	
	private int type; // 时装类型
	private long expiredTime;//到期时间（每次续费或者第一次购买会修改）
	private String specialIncrPlanId;//特殊效果方案ID
	
	@Override
	public String getId() {
		return id;
	}

	public void InitStoreId(){
		if (id == null && userId != null && fashionId != 0){
			id = userId + "_" + fashionId;
		}
	}
	
	@Override
	public int getFashionId() {
		return fashionId;
	}
	
	public void setFashionId(int fashionId){
		this.fashionId = fashionId;
	}

	@Override
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public long getBuyTime() {
		return buyTime;
	}

	public void setBuyTime(long buyTime) {
		this.buyTime = buyTime;
	}
	
	public long getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(long expiredTime) {
		this.expiredTime = expiredTime;
	}

	public String getSpecialIncrPlanId() {
		return specialIncrPlanId;
	}

	public void setSpecialIncrPlanId(String specialIncrPlanId) {
		this.specialIncrPlanId = specialIncrPlanId;
	}
}
