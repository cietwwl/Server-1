package com.rw.trace;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.copypve.pojo.CopyData;
import com.rwbase.dao.copypve.pojo.TableCopyData;

public class TableCopyDataParser implements DataValueParser<TableCopyData> {

	@Override
	public TableCopyData copy(TableCopyData entity) {
		return null;
	}

	@Override
	public Map<String, ChangedRecord> compareDiff(TableCopyData entity1, TableCopyData entity2) {
		return null;
	}

	@Override
	public JSONObject toJson(TableCopyData entity) {
		JSONObject json = new JSONObject(4);
		json.put("userId", entity.getUserId());
		List<CopyData> copyList = entity.getCopyList();
		int copyListSize = copyList.size();
		JSONArray copyListArray = new JSONArray(copyListSize);
		for (int i = 0; i < copyListSize; i++) {
			CopyData copyData = copyList.get(i);
			JSONObject copyDataJson = new JSONObject(true);
			copyDataJson.put("infoId", copyData.getInfoId());
			copyDataJson.put("copyType", copyData.getCopyType());
			copyDataJson.put("copyCount", copyData.getCopyType());
			copyDataJson.put("resetCount", copyData.getResetCount());
			copyDataJson.put("lastFreeResetTime", copyData.getLastFreeResetTime());
			copyDataJson.put("lastChallengeTime", copyData.getLastChallengeTime());
			copyListArray.add(copyDataJson);
		}
		json.put("copyList", copyListArray);
		return json;
	}

}
