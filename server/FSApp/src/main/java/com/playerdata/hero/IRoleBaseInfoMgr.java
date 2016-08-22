package com.playerdata.hero;

import com.playerdata.Hero;

public interface IRoleBaseInfoMgr {
	
	public void setQualityId(Hero hero, String qualityId);
	
	public void setCareerType(Hero hero, int career);
	
	public void setModelId(Hero hero, int modelId);
	
	public void setTemplateId(Hero hero, String templateId);
	
	public void setStarLevel(Hero hero, int starLevel);
	
	public void setLevel(Hero hero, int level);
	
	public void setExp(Hero hero, long exp);
	
	public void setLevelAndExp(Hero hero, int level, long exp);
}
