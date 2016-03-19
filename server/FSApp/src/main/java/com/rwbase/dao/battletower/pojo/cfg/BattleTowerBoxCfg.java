package com.rwbase.dao.battletower.pojo.cfg;

import org.codehaus.jackson.annotate.JsonIgnore;

/*
 * @author HC
 * @date 2015年9月16日 上午10:54:10
 * @Description 试练塔宝匣配置
 */
public class BattleTowerBoxCfg {
	private int keyType;// 钥匙类型
	private String keyDropIds;// 钥匙掉落方案

	// ///////////////////////////////////////////////毋须转换成Json的区域
	@JsonIgnore
	private String[] keyDropIdArr;// 铜钥匙掉落

	public int getKeyType() {
		return keyType;
	}

	public void setKeyType(int keyType) {
		this.keyType = keyType;
	}

	public String getKeyDropIds() {
		return keyDropIds;
	}

	public void setKeyDropIds(String keyDropIds) {
		this.keyDropIds = keyDropIds;
	}

	public String[] getKeyDropIdArr() {
		if (this.keyDropIds != null) {
			this.keyDropIdArr = this.keyDropIds.split(",");
		}

		return this.keyDropIdArr;
	}
}