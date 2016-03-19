package com.rwbase.dao.skill.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
@Table(name = "skill_item")
public class Skill implements IMapItem,SkillIF {

	@Id
	private String id;
	private String ownerId;
	@CombineSave
	private String skillId;
	@CombineSave
	private int level;//如果0级说明没开放
	@CombineSave
	private int order;//第几个技能
	@CombineSave
	private List<Integer> buffId = new ArrayList<Integer>();
	@CombineSave
	private float skillRate;
	@CombineSave
	private int extraDamage;
	@CombineSave
	private int skillDamage;
	@CombineSave
	private List<Integer> selfBuffId = new ArrayList<Integer>();
	
	public Skill() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public float getSkillRate() {
		return skillRate;
	}
	public void setSkillRate(float skillRate) {
		this.skillRate = skillRate;
	}
	public int getExtraDamage() {
		return extraDamage;
	}
	public void setExtraDamage(int extraDamage) {
		this.extraDamage = extraDamage;
	}
	public List<Integer> getBuffId() {
		return buffId;
	}
	public void setBuffId(List<Integer> buffId) {
		this.buffId = buffId;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getSkillId() {
		return skillId;
	}
	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}
	public int getSkillDamage() {
		return skillDamage;
	}
	public void setSkillDamage(int skillDamage) {
		this.skillDamage = skillDamage;
	}
	public List<Integer> getSelfBuffId() {
		return selfBuffId;
	}
	public void setSelfBuffId(List<Integer> selfBuffId) {
		this.selfBuffId = selfBuffId;
	}
	

}
