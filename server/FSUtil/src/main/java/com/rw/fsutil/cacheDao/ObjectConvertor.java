package com.rw.fsutil.cacheDao;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.record.DataLoggerRecord;
import com.rw.fsutil.dao.cache.record.SingleChangedRecord;
import com.rw.fsutil.dao.cache.trace.CacheJsonConverter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.dao.cache.trace.SignleChangedEvent;

public class ObjectConvertor<T> implements CacheJsonConverter<String, T, SignleChangedEvent<T>, SignleChangedEvent<T>> {

	private final DataValueParser<T> parser;

	public ObjectConvertor(DataValueParser<T> parser) {
		this.parser = parser;
	}

	@Override
	public DataLoggerRecord parseAndUpdate(Object key, SignleChangedEvent<T> oldRecord, T newRecord, SignleChangedEvent<T> event) {
		if (oldRecord == null) {
			return null;
		}
		JSONObject json = parser.recordAndUpdate(oldRecord.getOldRecord(), newRecord);
		if (json == null) {
			// 没有发生变化
			return null;
		}
		return new SingleChangedRecord(key, json, true);
	}

	@Override
	public DataLoggerRecord parse(String key, SignleChangedEvent<T> value) {
		if (value == null) {
			return null;
		}
		JSONObject json = parser.toJson(value.getOldRecord());
		if (json == null) {
			return null;
		}
		return new SingleChangedRecord(key, json, false);
	}

	@Override
	public SignleChangedEvent<T> copy(Object key, T value) {
		T copyValue = parser.copy(value);
		if (copyValue != null) {
			return new SignleChangedEvent<T>(copyValue, value);
		} else {
			return null;
		}
	}

	@Override
	public SignleChangedEvent<T> produceChangedEvent(Object key, SignleChangedEvent<T> oldRecord, T newRecord) {
		return oldRecord;
	}

}
