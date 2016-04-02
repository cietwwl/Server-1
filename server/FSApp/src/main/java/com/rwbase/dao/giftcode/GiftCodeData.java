package com.rwbase.dao.giftcode;

import javax.persistence.Id;
import javax.persistence.Table;

/*
 * @author HC
 * @date 2016年3月18日 上午10:41:13
 * @Description 兑换码的记录
 */
@Table(name = "gift_code")
public class GiftCodeData {
	@Id
	private String code;// 兑换码数据
	private String userId;// 兑换的角色Id
	private long useTime;// 使用兑换码的时间
	private int giftId;// 获取到的礼品Id

	public void setCode(String code) {
		this.code = code;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUseTime(long useTime) {
		this.useTime = useTime;
	}

	public void setGiftId(int giftId) {
		this.giftId = giftId;
	}

	public String getCode() {
		return code;
	}

	public String getUserId() {
		return userId;
	}

	public long getUseTime() {
		return useTime;
	}

	public int getGiftId() {
		return giftId;
	}
}