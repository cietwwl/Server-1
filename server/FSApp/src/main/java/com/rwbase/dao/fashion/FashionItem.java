package com.rwbase.dao.fashion;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;

/**
 * 时装信息
 * 
 * @author allen
 *
 */
@Table(name = "fashion_item")
@SynClass
public class FashionItem implements IMapItem, FashionItemIF {
	@Id
	private int fashionId; // 时装唯一id
	private String userId; // 用户ID
	private int type; // 时装类型
	private long buyTime;// 购买时间（或者续费时间）
	private long expiredTime;//到期时间（每次续费或者第一次购买会修改）
	private int specialIncrPlanId;//特殊效果方案ID

	@Override
	public String getId() {
		return String.valueOf(fashionId);
	}

	public void setId(String id) {
		this.fashionId = Integer.parseInt(id);
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

	public int getSpecialIncrPlanId() {
		return specialIncrPlanId;
	}

	public void setSpecialIncrPlanId(int specialIncrPlanId) {
		this.specialIncrPlanId = specialIncrPlanId;
	}
}
