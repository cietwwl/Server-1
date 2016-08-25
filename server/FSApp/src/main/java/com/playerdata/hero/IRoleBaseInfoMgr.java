package com.playerdata.hero;

import com.rwbase.dao.hero.pojo.RoleBaseInfo;

public interface IRoleBaseInfoMgr {
	
	public RoleBaseInfo getBaseInfo();

	public void setQualityId(String qualityId);
	
	public void setCareerType(int career);
	
	public void setModelId(int modelId);
	
	public void setTemplateId(String templateId);
	
	public void setStarLevel(int starLevel);
	
	public void setLevel(int level);
	
	public void setExp(long exp);
	
	public void setLevelAndExp(int level, int exp);
}
