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
	public enum FashionType {
		// 翅膀
		Wing(0),
		// 宠物
		Pet(1),
		// 套装
		Suit(2);

		int type;

		FashionType(int ty) {
			type = ty;
		}
	}

	@Id
	private String id; // 时装唯一id
	private String userId; // 用户ID
	private int type; // 时装类型
	private int state; // 时装的状态
	private long buyTime;// 购买时间（续费时间）

	// private ConcurrentHashMap<eAttrIdDef, Double> addPercentAttr = new ConcurrentHashMap<eAttrIdDef, Double>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rwbase.dao.fashion.FashionItemIF#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rwbase.dao.fashion.FashionItemIF#getUserId()
	 */
	@Override
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rwbase.dao.fashion.FashionItemIF#getType()
	 */
	@Override
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rwbase.dao.fashion.FashionItemIF#getState()
	 */
	@Override
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rwbase.dao.fashion.FashionItemIF#getBuyTime()
	 */
	@Override
	public long getBuyTime() {
		return buyTime;
	}

	public void setBuyTime(long buyTime) {
		this.buyTime = buyTime;
	}
	// /* (non-Javadoc)
	// * @see com.rwbase.dao.fashion.FashionItemIF#getAddPercentAttr()
	// */
	// @Override
	// public ConcurrentHashMap<eAttrIdDef, Double> getAddPercentAttr() {
	// return addPercentAttr;
	// }
	//
	// public void setAddPercentAttr(ConcurrentHashMap<eAttrIdDef, Double> addPercentAttr) {
	// this.addPercentAttr = addPercentAttr;
	// }
	//
	// public void addPercentAttr(eAttrIdDef def, double value){
	// addPercentAttr.put(def, value);
	// }
}
