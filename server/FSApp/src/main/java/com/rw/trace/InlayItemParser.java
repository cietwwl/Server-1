package com.rw.trace;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.inlay.InlayItem;

public class InlayItemParser implements DataValueParser<InlayItem> {

	@Override
	public InlayItem copy(InlayItem entity) {
		return null;
	}

	@Override
	public Map<String, ChangedRecord> compareDiff(InlayItem entity1, InlayItem entity2) {
		return null;
	}

	@Override
	public JSONObject toJson(InlayItem entity) {
		JSONObject json = new JSONObject(8);
		json.put("id", entity.getId());
		json.put("ownerId", entity.getOwnerId());
		json.put("slotId", entity.getSlotId());
		json.put("modelId", entity.getModelId());
		return json;
	}

}
