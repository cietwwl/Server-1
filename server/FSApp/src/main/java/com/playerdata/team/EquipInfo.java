package com.playerdata.team;

/*
 * @author HC
 * @date 2016年4月15日 下午4:31:17
 * @Description 
 */
public class EquipInfo {
	private String tId;// 装备模版Id
	private int eLevel;// 附灵等级

	public String gettId() {
		return tId;
	}

	public void settId(String tId) {
		this.tId = tId;
	}

	public int geteLevel() {
		return eLevel;
	}

	public void seteLevel(int eLevel) {
		this.eLevel = eLevel;
	}
}