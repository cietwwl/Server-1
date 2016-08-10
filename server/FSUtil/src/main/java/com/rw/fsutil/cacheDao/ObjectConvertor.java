package com.rw.fsutil.cacheDao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.record.CacheRecordEvent;
import com.rw.fsutil.dao.cache.record.SingleRecordEvent;
import com.rw.fsutil.dao.cache.trace.CacheJsonConverter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;

public class ObjectConvertor<T> implements CacheJsonConverter<String, T, SingleRecordEvent> {

	private final DataValueParser<T> parser;

	public ObjectConvertor(DataValueParser<T> parser) {
		this.parser = parser;
	}

	@Override
	public SingleRecordEvent parseToRecordData(String key, T cacheValue) throws Exception {
		if (parser != null) {
			return new SingleRecordEvent(key, parser.toJson(cacheValue));
		} else {
			return new SingleRecordEvent(key, (JSONObject) JSON.toJSON(cacheValue));
		}
	}

	@Override
	public CacheRecordEvent parse(SingleRecordEvent oldRecord, SingleRecordEvent newRecord) {
		return newRecord.parse(oldRecord);
	}

}
