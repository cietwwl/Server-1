package com.rwbase.dao.arena.pojo;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/*
 * @author HC
 * @date 2016年7月14日 下午4:26:34
 * @Description 竞技场机器人的属性
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class ArenaRobotData {
	private String userId;// 主角Id
	private int[] fixEquip;// 神器信息：按照顺序是--->等级,品质,星数
	private int[] taoist;// 道术信息：按照顺序是--->1阶道术等级,2阶道术等级,3级道术等级
	private String[] fashionId;// 时装信息：按照顺序是--->套装，翅膀，宠物
	private HeroFettersRobotInfo[] fetters;// 机器人羁绊信息
	private int extraAttrId;// 额外的属性Id

	// ----------------------------------------------Get区
	public String getUserId() {
		return userId;
	}

	public int[] getFixEquip() {
		return fixEquip;
	}

	public int[] getTaoist() {
		return taoist;
	}

	public String[] getFashionId() {
		return fashionId;
	}

	public HeroFettersRobotInfo[] getFetters() {
		return fetters;
	}

	public int getExtraAttrId() {
		return extraAttrId;
	}

	// ----------------------------------------------Set区
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setFixEquip(int[] fixEquip) {
		this.fixEquip = fixEquip;
	}

	public void setTaoist(int[] taoist) {
		this.taoist = taoist;
	}

	public void setFashionId(String[] fashionId) {
		this.fashionId = fashionId;
	}

	public void setFetters(HeroFettersRobotInfo[] fetters) {
		this.fetters = fetters;
	}

	public void setExtraAttrId(int extraAttrId) {
		this.extraAttrId = extraAttrId;
	}
}