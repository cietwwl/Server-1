package com.rw.trace;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.equipment.EquipItem;

public class EquipItemParser implements DataValueParser<EquipItem>{

	@Override
	public EquipItem copy(EquipItem entity) {
		return null;
	}

	@Override
	public Map<String, ChangedRecord> compareDiff(EquipItem entity1, EquipItem entity2) {
		return null;
	}

	@Override
	public JSONObject toJson(EquipItem entity) {
		JSONObject json = new JSONObject();
		json.put("id", entity.getId());
		json.put("ownerId", entity.getOwnerId());
		json.put("equipIndex", entity.getEquipIndex());
		json.put("type", entity.getType());
		json.put("modelId", entity.getModelId());
		json.put("level", entity.getLevel());
		json.put("exp", entity.getExp());
		return json;
	}

}
