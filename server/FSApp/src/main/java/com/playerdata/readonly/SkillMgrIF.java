package com.playerdata.readonly;

import java.util.List;

import com.rwbase.dao.skill.pojo.SkillIF;
import com.rwproto.SkillServiceProtos.TagSkillData;

public interface SkillMgrIF {
//	// /**
//	// * 根据order获取技能
//	// * @param order
//	// * @return
//	// */
//	// public SkillIF getSkill(int order);
//
//	public List<? extends SkillIF> getSkillList();
//
//	public List<TagSkillData> getSkillProtoList();
	
	public List<? extends SkillIF> getSkillList(String key);
	
	public List<TagSkillData> getSkillProtoList(String key);

}
