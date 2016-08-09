package com.rw.trace;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.DataValueParser;

public class FixExpEquipDataItemParser implements DataValueParser<FixExpEquipDataItem> {

	@Override
	public FixExpEquipDataItem copy(FixExpEquipDataItem entity) {
		return null;
	}

	@Override
	public Map<String, ChangedRecord> compareDiff(FixExpEquipDataItem entity1, FixExpEquipDataItem entity2) {
		return null;
	}

	@Override
	public JSONObject toJson(FixExpEquipDataItem entity) {
		JSONObject json = new JSONObject();
		json.put("id", entity.getId());
		json.put("ownerId", entity.getOwnerId());
		json.put("cfgId", entity.getCfgId());
		json.put("level", entity.getLevel());
		json.put("quality", entity.getQuality());
		json.put("star", entity.getStar());
		json.put("slot", entity.getSlot());
		return json;
	}

}
