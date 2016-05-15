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
	private final List<EquipInfo> equipList;

	private EquipParam(String userId, List<EquipInfo> equipList) {
		this.userId = userId;
		this.equipList = equipList;
	}

	public String getUserId() {
		return userId;
	}

	public List<EquipInfo> getEquipList() {
		return equipList;
	}

	public static class EquipBuilder {
		private String userId;
		private List<EquipInfo> equipList;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setEquipList(List<EquipInfo> equipList) {
			this.equipList = equipList;
		}

		public EquipParam build() {
			return new EquipParam(userId, equipList);
		}
	}
}