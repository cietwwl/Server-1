package com.rwbase.dao.fashion;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.NonSave;

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
	private int id; //数据库存储id,时装唯一id
	
	@NonSave @IgnoreSynField
	private String fashionId;//逻辑层需要将int的id值转换为string类型
	
	@IgnoreSynField
	private String userId; // 用户ID
	
	@IgnoreSynField
	private long buyTime;// 购买时间（或者续费时间）
	
	@NonSave
	private boolean isBrought = true;
	
	private int type; // 时装类型
	private long expiredTime;//到期时间（每次续费或者第一次购买会修改）
	private int specialIncrPlanId;//特殊效果方案ID
	
	@Override
	public String getId() {
		if (fashionId == null){
			fashionId = String.valueOf(id);
		}
		return fashionId;
	}

	@Override
	public int getFashionId() {
		return id;
	}
	
	public void setFashionId(int fashionId){
		this.id = fashionId;
		this.fashionId = String.valueOf(id);
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
