package com.rw.trace;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.majorDatas.pojo.MajorData;

public class MajorDataParser implements DataValueParser<MajorData> {

	@Override
	public MajorData copy(MajorData entity) {
		return null;
	}

	@Override
	public Map<String, ChangedRecord> compareDiff(MajorData entity1, MajorData entity2) {
		return null;
	}

	@Override
	public JSONObject toJson(MajorData entity) {
		JSONObject json = new JSONObject(8);
		json.put("id", entity.getId());
		json.put("ownerId", entity.getOwnerId());
		json.put("coin", entity.getCoin());
		json.put("gold", entity.getGold());
		json.put("giftGold", entity.getGiftGold());
		json.put("chargeGold", entity.getChargeGold());
		return json;
	}

}
