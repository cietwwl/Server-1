package com.rw.trace;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.skill.pojo.Skill;

public class SkillParser implements DataValueParser<Skill>{

	@Override
	public Skill copy(Skill entity) {
		return null;
	}

	@Override
	public Map<String, ChangedRecord> compareDiff(Skill entity1, Skill entity2) {
		return null;
	}

	@Override
	public JSONObject toJson(Skill entity) {
		JSONObject json = new JSONObject(8);
		json.put("id", entity.getId());
		json.put("ownerId", entity.getOwnerId());
		json.put("skillId", entity.getSkillId());
		json.put("level", entity.getLevel());
		json.put("order", entity.getOrder());
		return json;
	}

}
