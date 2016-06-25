package com.rwbase.dao.majorDatas.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;

/**
 * 重要数据
 * 
 * @author lida
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "majordata")
@SynClass
public class MajorData implements IMapItem {
	@Id
	private String id;
	private String ownerId;

	private long coin;// 铜钱

	private int gold;// 赠送金钱,展示用
	private int giftGold;// 赠送金钱
	private int chargeGold;// 充值金钱

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getGiftGold() {
		return giftGold;
	}

	public void setGiftGold(int giftGold) {
		this.giftGold = giftGold;
	}

	public int getChargeGold() {
		return chargeGold;
	}

	public void setChargeGold(int chargeGold) {
		this.chargeGold = chargeGold;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getCoin() {
		return coin;
	}

	public void setCoin(long coin) {
		this.coin = coin;
	}

	public void updateGold() {
		this.gold = this.giftGold + this.chargeGold;
	}
}
