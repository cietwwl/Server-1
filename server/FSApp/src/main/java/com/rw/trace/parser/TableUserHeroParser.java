package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import java.util.List;
import com.rwbase.dao.hero.pojo.TableUserHero;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.common.Pair;
import com.alibaba.fastjson.JSONObject;

public class TableUserHeroParser implements DataValueParser<TableUserHero> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public TableUserHero copy(TableUserHero entity) {
        TableUserHero tableUserHeroCopy = new TableUserHero();
        tableUserHeroCopy.setUserId(entity.getUserId());
        tableUserHeroCopy.setHeroIds(writer.copyObject(entity.getHeroIds()));
        return tableUserHeroCopy;
    }

    @Override
    public JSONObject recordAndUpdate(TableUserHero entity1, TableUserHero entity2) {
        JSONObject jsonMap = null;
        String userId1 = entity1.getUserId();
        String userId2 = entity2.getUserId();
        if (!writer.equals(userId1, userId2)) {
            entity1.setUserId(userId2);
            jsonMap = writer.write(jsonMap, "userId", userId2);
        }
        List<String> heroIds1 = entity1.getHeroIds();
        List<String> heroIds2 = entity2.getHeroIds();
        Pair<List<String>, JSONObject> heroIdsPair = writer.checkObject(jsonMap, "heroIds", heroIds1, heroIds2);
        if (heroIdsPair != null) {
            heroIds1 = heroIdsPair.getT1();
            entity1.setHeroIds(heroIds1);
            jsonMap = heroIdsPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "heroIds", heroIds1, heroIds2);
        }

        return jsonMap;
    }

    @Override
    public boolean hasChanged(TableUserHero entity1, TableUserHero entity2) {
        if (!writer.equals(entity1.getUserId(), entity2.getUserId())) {
            return true;
        }
        if (writer.hasChanged(entity1.getHeroIds(), entity2.getHeroIds())) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(TableUserHero entity) {
        JSONObject json = new JSONObject(2);
        json.put("userId", entity.getUserId());
        Object heroIdsJson = writer.toJSON(entity.getHeroIds());
        if (heroIdsJson != null) {
            json.put("heroIds", heroIdsJson);
        }
        return json;
    }

}