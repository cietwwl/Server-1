package com.rwbase.dao.skill.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.rwproto.SkillServiceProtos.TagSkillData;

public class SkillHelper {
	public static String getItemId(String ownerId, String skillCfgId){
		
		return ownerId+"_"+skillCfgId;
	}
	
	public static List<TagSkillData> getSkillProtoList(List<Skill> skillLIst)
	{
		if(skillLIst==null)
			return null;
		List<TagSkillData> list = new ArrayList<TagSkillData>();
		Collections.sort(skillLIst, new Comparator<Skill>() {
			public int compare(Skill o1, Skill o2) {
				return o1.getOrder() - o2.getOrder();
			}
		});
		for (Skill skill : skillLIst) {
			TagSkillData.Builder tagSkill = TagSkillData.newBuilder();
			tagSkill.setLevel(skill.getLevel());
			tagSkill.setSkillId(skill.getSkillId());
			tagSkill.setOrder(skill.getOrder());
			tagSkill.setExtraDamage(skill.getExtraDamage());
			tagSkill.setSkillRate(skill.getSkillRate());
			for (Integer buff : skill.getBuffId()) {
				tagSkill.addBuffId(buff);
			}

			list.add(tagSkill.build());
		}
		return list;
	}
}
