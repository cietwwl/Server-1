package com.rw.trace;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.skill.pojo.SkillItem;

public class SkillParser implements DataValueParser<SkillItem>{

	@Override
	public SkillItem copy(SkillItem entity) {
		return null;
	}

	@Override
	public Map<String, ChangedRecord> compareDiff(SkillItem entity1, SkillItem entity2) {
		return null;
	}

	@Override
	public JSONObject toJson(SkillItem entity) {
		JSONObject json = new JSONObject(8);
		json.put("id", entity.getId());
		json.put("ownerId", entity.getOwnerId());
		json.put("skillId", entity.getSkillId());
		json.put("level", entity.getLevel());
		json.put("order", entity.getOrder());
		return json;
	}

}
