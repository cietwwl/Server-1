package com.rwbase.dao.battletower.pojo;

import java.util.ArrayList;
import java.util.List;

import com.rwbase.dao.battletower.pojo.readonly.BattleTowerRoleInfoIF;

/*
 * @author HC
 * @date 2015年9月2日 下午5:50:26
 * @Description 试练塔角色打到某个里程碑的详细信息
 */
public class BattleTowerRoleInfo implements BattleTowerRoleInfoIF {
	private String userId;// 角色Id
	private int level;// 等级
	private String headIcon;// 图标
	private String name;// 名字
	private int floor;// 挑战的最高层数
	private String magicIcon;// 使用法宝的Icon
	private List<BattleTowerHeroInfo> heroInfoList;// 试练塔挑战成功的阵容中的佣兵信息列表

	public BattleTowerRoleInfo() {
		this.heroInfoList = new ArrayList<BattleTowerHeroInfo>();
	}

	public BattleTowerRoleInfo(String userId) {
		this();
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public String getMagicIcon() {
		return magicIcon;
	}

	public void setMagicIcon(String magicIcon) {
		this.magicIcon = magicIcon;
	}

	public List<BattleTowerHeroInfo> getHeroInfoList() {
		return heroInfoList;
	}

	public void setHeroInfoList(List<BattleTowerHeroInfo> heroInfoList) {
		this.heroInfoList = heroInfoList;
	}
}