package com.rwbase.dao.skill.pojo;

public interface SkillIF {

	public Integer getId();

	public int getLevel();

	public int getOrder();

	public float getSkillRate();

	public int getExtraDamage();

	// public List<Integer> getBuffId() ;

	public String getOwnerId();

	public String getSkillId();

}
