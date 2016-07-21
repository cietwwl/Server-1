package com.playerdata.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.army.ArmyMagic;
import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年4月15日 下午4:30:10
 * @Description 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class TeamInfo {
	private List<HeroInfo> hero;// 英雄列表，包含主角
	// 法宝信息
	private ArmyMagic magic;
	// 主角基础信息
	private int vip;// Vip等级
	private String name;// 佣兵名字
	private int career;// 职业
	private String headId;// 头像Id
	private String groupName;// 帮派名字
	// 道术
	private Map<Integer, Integer> taoist;// 道术
	// 帮派技能
	private Map<Integer, Integer> gs;// 帮派技能
	// 时装
	private FashionInfo fashion;// 时装
	// 指定的额外属性Id(仅用于机器人类)
	private int extraId;// 指定的额外属性Id(仅用于机器人类)
	// 阵容的战力
	private int teamFighting;// 阵容战力
	// 等级
	private int level;
	private String uuid;// 角色的唯一Id

	public TeamInfo() {
		hero = new ArrayList<HeroInfo>();
		taoist = new HashMap<Integer, Integer>();
		gs = new HashMap<Integer, Integer>();
	}

	public List<HeroInfo> getHero() {
		return hero;
	}

	public void setHero(List<HeroInfo> hero) {
		this.hero = hero;
	}

	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}

	public ArmyMagic getMagic() {
		return magic;
	}

	public void setMagic(ArmyMagic magic) {
		this.magic = magic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCareer() {
		return career;
	}

	public void setCareer(int career) {
		this.career = career;
	}

	public String getHeadId() {
		return headId;
	}

	public void setHeadId(String headId) {
		this.headId = headId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getTeamFighting() {
		return teamFighting;
	}

	public void setTeamFighting(int teamFighting) {
		this.teamFighting = teamFighting;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Map<Integer, Integer> getTaoist() {
		return taoist;
	}

	public void setTaoist(Map<Integer, Integer> taoist) {
		this.taoist = taoist;
	}

	public Map<Integer, Integer> getGs() {
		return gs;
	}

	public void setGs(Map<Integer, Integer> gs) {
		this.gs = gs;
	}

	public FashionInfo getFashion() {
		return fashion;
	}

	public void setFashion(FashionInfo fashion) {
		this.fashion = fashion;
	}

	public int getExtraId() {
		return extraId;
	}

	public void setExtraId(int extraId) {
		this.extraId = extraId;
	}
}