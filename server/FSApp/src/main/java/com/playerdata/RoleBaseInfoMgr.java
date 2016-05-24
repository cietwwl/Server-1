package com.playerdata;

import com.common.Action;
import com.rwbase.dao.fetters.FettersBM;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.hero.pojo.RoleBaseInfoHolder;

public class RoleBaseInfoMgr extends IDataMgr {

	private RoleBaseInfoHolder roleBaseInfoHolder;

	public void init(Hero pRole, RoleBaseInfo roleBaseItemP) {
		initPlayer(pRole);
		roleBaseInfoHolder = new RoleBaseInfoHolder(pRole.getUUId());
		if (roleBaseItemP != null) {
			roleBaseInfoHolder.setBaseInfo(roleBaseItemP);
		}
	}

	public void regChangeCallBack(Action callBack) {
		roleBaseInfoHolder.regChangeCallBack(callBack);
	}

	public void syn(int version) {
		roleBaseInfoHolder.syn(m_pPlayer, version);
	}

	public RoleBaseInfo getBaseInfo() {
		return roleBaseInfoHolder.get();
	}

	public boolean save() {
		roleBaseInfoHolder.flush();
		return false;
	}

	// public AttrData getTotalBaseAttrData() {
	// return roleBaseInfoHolder.toAttrData();
	// }
	//
	// public AttrData getTotalQualityAttrDataForLog() {
	// return roleBaseInfoHolder.toQualityAttrDataForLog();
	// }

	public void setQualityId(String id) {
		RoleBaseInfo roleBaseInfo = roleBaseInfoHolder.get();

		String oldQuality = roleBaseInfo.getQualityId();

		roleBaseInfo.setQualityId(id);
		roleBaseInfoHolder.update(m_pPlayer);

		// 品质修改
		if (!oldQuality.equals(id)) {
			FettersBM.whenHeroChange(m_pPlayer, m_pOwner.getModelId());
		}
	}

	public void setCareerType(int career) {
		RoleBaseInfo roleBaseInfo = roleBaseInfoHolder.get();
		roleBaseInfo.setCareerType(career);
		roleBaseInfoHolder.update(m_pPlayer);
	}

	public void setModelId(int modelId) {
		RoleBaseInfo roleBaseInfo = roleBaseInfoHolder.get();
		roleBaseInfo.setModeId(modelId);
		roleBaseInfoHolder.update(m_pPlayer);
	}

	public void setTemplateId(String templateId) {
		RoleBaseInfo roleBaseInfo = roleBaseInfoHolder.get();
		roleBaseInfo.setTemplateId(templateId);
		roleBaseInfoHolder.update(m_pPlayer);
	}

	public void setStarLevel(int starLevel) {
		RoleBaseInfo roleBaseInfo = roleBaseInfoHolder.get();

		int oldStar = roleBaseInfo.getStarLevel();

		roleBaseInfo.setStarLevel(starLevel);
		roleBaseInfoHolder.update(m_pPlayer);
		// 星级修改
		if (oldStar != starLevel) {
			FettersBM.whenHeroChange(m_pPlayer, m_pOwner.getModelId());
		}
	}

	public void setLevel(int level) {
		RoleBaseInfo roleBaseInfo = roleBaseInfoHolder.get();

		int oldLevel = roleBaseInfo.getLevel();

		roleBaseInfo.setLevel(level);
		roleBaseInfoHolder.update(m_pPlayer);
		// 等级修改
		if (oldLevel != level) {
			FettersBM.whenHeroChange(m_pPlayer, m_pOwner.getModelId());
		}
	}

	public void setExp(long exp) {
		RoleBaseInfo roleBaseInfo = roleBaseInfoHolder.get();
		roleBaseInfo.setExp(exp);
		roleBaseInfoHolder.update(m_pPlayer);

	}

	/**
	 * 增加同时设置等级和经验的方法
	 * 
	 * @param level
	 * @param exp
	 */
	public void setLevelAndExp(int level, int exp) {
		RoleBaseInfo roleBaseInfo = roleBaseInfoHolder.get();

		int oldLevel = roleBaseInfo.getLevel();

		roleBaseInfo.setLevel(level);
		roleBaseInfo.setExp(exp);
		roleBaseInfoHolder.update(m_pPlayer);

		// 等级修改
		if (oldLevel != level) {
			FettersBM.whenHeroChange(m_pPlayer, m_pOwner.getModelId());
		}
	}
}