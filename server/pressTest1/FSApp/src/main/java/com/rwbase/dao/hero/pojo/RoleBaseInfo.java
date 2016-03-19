package com.rwbase.dao.hero.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.SynClass;

/**
 * 人物基本属性
 * @author allen
 *
 */
@Table(name = "rolebaseinfo")
@SynClass
public class RoleBaseInfo implements RoleBaseInfoIF{

	@Id
	private String id;    //英雄uuid
	
	private int careerType;//职业
	private String templateId;//英雄模板Id，佣兵属性配置id
	private int modeId;//英雄模型Id
	private int level;//等级
	private int starLevel;//星级
	private String qualityId;//品阶Id
	private long exp ;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	
	public int getModeId() {
		return modeId;
	}
	public void setModeId(int modeId) {
		this.modeId = modeId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getStarLevel() {
		return starLevel;
	}
	public void setStarLevel(int starLevel) {
		this.starLevel = starLevel;
	}
	public String getQualityId() {
		return qualityId;
	}
	public void setQualityId(String qualityId) {
		this.qualityId = qualityId;
	}
	public long getExp() {
		return exp;
	}
	public void setExp(long exp) {
		this.exp = exp;
	}
	public int getCareerType() {
		return careerType;
	}
	public void setCareerType(int careerType) {
		this.careerType = careerType;
	}

	

	
	
}
