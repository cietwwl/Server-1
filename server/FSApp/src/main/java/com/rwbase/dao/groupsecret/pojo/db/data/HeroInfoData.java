package com.rwbase.dao.groupsecret.pojo.db.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rwbase.common.teamsyn.HeroLeftInfoSynData;

/*
 * @author HC
 * @date 2016年7月15日 下午10:06:54
 * @Description 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HeroInfoData {
	private int pos;// 站位
	private HeroLeftInfoSynData left;

	public HeroInfoData() {
	}

	public HeroInfoData(int pos, HeroLeftInfoSynData left) {
		this.pos = pos;
		this.left = left;
	}

	public int getPos() {
		return pos;
	}

	public HeroLeftInfoSynData getLeft() {
		return left;
	}
}