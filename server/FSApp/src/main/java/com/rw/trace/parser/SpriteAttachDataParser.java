package com.rw.trace.parser;

import java.util.List;

import sun.net.www.content.image.jpeg;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.spriteattach.SpriteAttachItem;
import com.rwbase.dao.spriteattach.SpriteAttachSyn;

public class SpriteAttachDataParser implements DataValueParser<SpriteAttachSyn>{

	private JsonValueWriter writer = JsonValueWriter.getInstance();
	
	@Override
	public SpriteAttachSyn copy(SpriteAttachSyn entity) {
		SpriteAttachSyn copy = new SpriteAttachSyn();
		copy.setId(entity.getId());
		copy.setOwnerId(entity.getOwnerId());
		copy.setItems(writer.copyObject(entity.getItems()));
		return copy;
	}

	@Override
	public JSONObject toJson(SpriteAttachSyn entity) {
		JSONObject json = new JSONObject(5);
        json.put("id", entity.getId());
        json.put("ownerId", entity.getOwnerId());
        Object object = writer.toJSON(entity.getItems());
        if(object != null){
        	json.put("items", object);
        }
		return json;
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
        List<SpriteAttachItem> items2 = entity1.getItems();
        Pair<List<SpriteAttachItem>,JSONObject> pair = writer.checkObject(jsonMap, "items", items1, items2);
        if(pair != null){
        	items1 = pair.getT1();
        	entity1.setItems(items1);
        	jsonMap = pair.getT2();
        }else{
        	jsonMap = writer.compareSetDiff(jsonMap, "items", items1, items2);
        }
		return jsonMap;
	}

	@Override
	public boolean hasChanged(SpriteAttachSyn entity1, SpriteAttachSyn entity2) {
		if (!writer.equals(entity1.getOwnerId(), entity2.getOwnerId())) {
            return true;
        }
        if (writer.hasChanged(entity1.getItems(), entity2.getItems())) {
            return true;
        }
        return false;
	}

}
