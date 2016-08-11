package com.rw.trace;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.hero.pojo.TableUserHero;

public class TableUserHeroParser implements DataValueParser<TableUserHero> {

	@Override
	public TableUserHero copy(TableUserHero entity) {
		return null;
	}

	@Override
	public Map<String, ChangedRecord> compareDiff(TableUserHero entity1, TableUserHero entity2) {
		return null;
	}

	@Override
	public JSONObject toJson(TableUserHero entity) {
		JSONObject json = new JSONObject(4);
		json.put("userId", entity.getUserId());
		List<String> heroIds = entity.getHeroIds();
		int heroIdsSize = heroIds.size();
		JSONArray heroIdsArray = new JSONArray(heroIdsSize);
		for (int i = 0; i < heroIdsSize; i++) {
			heroIdsArray.add(heroIds.get(i));
		}
		json.put("heroIds", heroIdsArray);
		return json;
	}

}
