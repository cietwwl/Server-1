package com.rwbase.common.attribute.param;

import java.util.List;

import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;

/*
 * @author HC
 * @date 2016年7月14日 下午12:22:39
 * @Description 
 */
public class FixExpEquipParam {
	private final String userId;
	private final String heroId;
	private final List<FixExpEquipDataItem> fixExpEquipList;

	public FixExpEquipParam(String userId, String heroId, List<FixExpEquipDataItem> fixExpEquipList) {
		this.userId = userId;
		this.heroId = heroId;
		this.fixExpEquipList = fixExpEquipList;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroId() {
		return heroId;
	}

	public List<FixExpEquipDataItem> getFixExpEquipList() {
		return fixExpEquipList;
	}

	public static class FixExpEquipBuilder {
		private String userId;
		private String heroId;
		private List<FixExpEquipDataItem> fixExpEquipList;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setHeroId(String heroId) {
			this.heroId = heroId;
		}

		public void setFixExpEquipList(List<FixExpEquipDataItem> fixExpEquipList) {
			this.fixExpEquipList = fixExpEquipList;
		}

		public FixExpEquipParam build() {
			return new FixExpEquipParam(userId, heroId, fixExpEquipList);
		}
	}
}