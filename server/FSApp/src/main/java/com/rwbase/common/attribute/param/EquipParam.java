package com.rwbase.common.attribute.param;

import java.util.List;

import com.playerdata.team.EquipInfo;

/*
 * @author HC
 * @date 2016年5月14日 下午6:13:01
 * @Description 
 */
public class EquipParam {
	private final String userId;
	private final String heroId;
	private final List<EquipInfo> equipList;

	private EquipParam(String userId, String heroId, List<EquipInfo> equipList) {
		this.userId = userId;
		this.heroId = heroId;
		this.equipList = equipList;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroId() {
		return heroId;
	}

	public List<EquipInfo> getEquipList() {
		return equipList;
	}

	public static class EquipBuilder {
		private String userId;
		private String heroId;
		private List<EquipInfo> equipList;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setHeroId(String heroId) {
			this.heroId = heroId;
		}

		public void setEquipList(List<EquipInfo> equipList) {
			this.equipList = equipList;
		}

		public EquipParam build() {
			return new EquipParam(userId, heroId, equipList);
		}
	}
}