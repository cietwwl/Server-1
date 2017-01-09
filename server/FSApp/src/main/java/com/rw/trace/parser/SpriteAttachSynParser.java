package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import java.util.List;
import com.rwbase.dao.spriteattach.SpriteAttachItem;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.spriteattach.SpriteAttachSyn;
import com.rw.fsutil.common.Pair;
import com.alibaba.fastjson.JSONObject;

public class SpriteAttachSynParser implements DataValueParser<SpriteAttachSyn> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public SpriteAttachSyn copy(SpriteAttachSyn entity) {
        SpriteAttachSyn spriteAttachSynCopy = new SpriteAttachSyn();
        spriteAttachSynCopy.setId(entity.getId());
        spriteAttachSynCopy.setOwnerId(entity.getOwnerId());
        spriteAttachSynCopy.setItems(writer.copyObject(entity.getItems()));
        return spriteAttachSynCopy;
    }

    @Override
    public JSONObject recordAndUpdate(SpriteAttachSyn entity1, SpriteAttachSyn entity2) {
        JSONObject jsonMap = null;
        Integer id1 = entity1.getId();
        Integer id2 = entity2.getId();
        if (!writer.equals(id1, id2)) {
            entity1.setId(id2);
            jsonMap = writer.write(jsonMap, "id", id2);
        }
        String ownerId1 = entity1.getOwnerId();
        String ownerId2 = entity2.getOwnerId();
        if (!writer.equals(ownerId1, ownerId2)) {
            entity1.setOwnerId(ownerId2);
            jsonMap = writer.write(jsonMap, "ownerId", ownerId2);
        }
        List<SpriteAttachItem> items1 = entity1.getItems();
        List<SpriteAttachItem> items2 = entity2.getItems();
        Pair<List<SpriteAttachItem>, JSONObject> itemsPair = writer.checkObject(jsonMap, "items", items1, items2);
        if (itemsPair != null) {
            items1 = itemsPair.getT1();
            entity1.setItems(items1);
            jsonMap = itemsPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "items", items1, items2);
        }

        return jsonMap;
    }

    @Override
    public boolean hasChanged(SpriteAttachSyn entity1, SpriteAttachSyn entity2) {
        if (!writer.equals(entity1.getId(), entity2.getId())) {
            return true;
        }
        if (!writer.equals(entity1.getOwnerId(), entity2.getOwnerId())) {
            return true;
        }
        if (writer.hasChanged(entity1.getItems(), entity2.getItems())) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(SpriteAttachSyn entity) {
        JSONObject json = new JSONObject(5);
        json.put("id", entity.getId());
        json.put("ownerId", entity.getOwnerId());
        Object itemsJson = writer.toJSON(entity.getItems());
        if (itemsJson != null) {
            json.put("items", itemsJson);
        }
        return json;
    }

}