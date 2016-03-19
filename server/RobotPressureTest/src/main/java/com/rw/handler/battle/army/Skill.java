package com.rw.handler.battle.army;

import java.util.ArrayList;
import java.util.List;

public class Skill {

	private String id;
	private String ownerId;
	private String skillId;
	private int level;//如果0级说明没开放
	private int order;//第几个技能
	private List<Integer> buffId = new ArrayList<Integer>();
	private float skillRate;
	private int extraDamage;
	private int skillDamage;
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
