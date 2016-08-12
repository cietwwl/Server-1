package com.rwbase.common.attribute.param;

import java.util.List;

import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;

/*
 * @author HC
 * @date 2016年7月14日 下午12:22:52
 * @Description 
 */
public class FixNormEquipParam {
	private final String userId;
	private final String heroId;
	private final List<FixNormEquipDataItem> fixNormEquipList;

	public FixNormEquipParam(String userId, String heroId, List<FixNormEquipDataItem> fixNormEquipList) {
		this.userId = userId;
		this.heroId = heroId;
		this.fixNormEquipList = fixNormEquipList;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroId() {
		return heroId;
	}

	public List<FixNormEquipDataItem> getFixNormEquipList() {
		return fixNormEquipList;
	}

	public static class FixNormEquipBuilder {
		private String userId;
		private String heroId;
		private List<FixNormEquipDataItem> fixNormEquipList;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setHeroId(String heroId) {
			this.heroId = heroId;
		}

		public void setFixNormEquipList(List<FixNormEquipDataItem> fixNormEquipList) {
			this.fixNormEquipList = fixNormEquipList;
		}

		public FixNormEquipParam build() {
			return new FixNormEquipParam(userId, heroId, fixNormEquipList);
		}
	}
}