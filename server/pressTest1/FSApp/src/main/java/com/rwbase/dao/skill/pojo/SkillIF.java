package com.rwbase.dao.skill.pojo;

import java.util.List;


public interface SkillIF  {

	public String getId() ;

	public int getLevel();

	public int getOrder();

	public float getSkillRate();

	public int getExtraDamage() ;

	public List<Integer> getBuffId() ;

	public String getOwnerId() ;

	public String getSkillId();

}
