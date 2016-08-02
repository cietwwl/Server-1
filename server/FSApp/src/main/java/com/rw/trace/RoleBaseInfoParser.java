package com.rw.trace;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;

public class RoleBaseInfoParser implements DataValueParser<RoleBaseInfo>{

	@Override
	public RoleBaseInfo copy(RoleBaseInfo entity) {
		return null;
	}

	@Override
	public Map<String, ChangedRecord> compareDiff(RoleBaseInfo entity1, RoleBaseInfo entity2) {
		return null;
	}

	@Override
	public JSONObject toJson(RoleBaseInfo entity) {
		JSONObject json = new JSONObject();
		json.put("id", entity.getId());
		json.put("careerType", entity.getCareer());
		
		json.put("templateId", entity.getTemplateId());
		json.put("modeId", entity.getModeId());
		json.put("level", entity.getLevel());
		json.put("starLevel", entity.getStarLevel());
		json.put("qualityId", entity.getQualityId());
		json.put("exp", entity.getExp());
		return json;
	}

}
