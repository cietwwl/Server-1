package com.playerdata.hero.core;

import javax.persistence.Id;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.hero.pojo.RoleBaseInfoIF;

@SynClass
public class RoleBaseInfo implements RoleBaseInfoIF {

	@Id
	private String id;
	@IgnoreSynField
	private String templateId;
	private int level;
	private int starLevel;
	private String qualityId;
	private long exp;
	private int modeId;
	private int careerType;
	
	public RoleBaseInfo() {}
	
	public RoleBaseInfo(RoleBaseInfoIF target) {
		this.id = target.getId();
		this.templateId = target.getTemplateId();
		this.level = target.getLevel();
		this.starLevel = target.getStarLevel();
		this.qualityId = target.getQualityId();
		this.exp = target.getExp();
		this.modeId = target.getModeId();
		this.careerType = target.getCareerType();
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setStarLevel(int starLevel) {
		this.starLevel = starLevel;
	}

	public void setQualityId(String qualityId) {
		this.qualityId = qualityId;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public void setModeId(int modeId) {
		this.modeId = modeId;
	}
	
	public void setCareerType(int careerType) {
		this.careerType = careerType;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTemplateId() {
		return templateId;
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public int getStarLevel() {
		return starLevel;
	}

	@Override
	public String getQualityId() {
		return qualityId;
	}

	@Override
	public long getExp() {
		return exp;
	}

	@Override
	public int getModeId() {
		return modeId;
	}

	public int getCareerType() {
		return careerType;
	}
	
	
}
