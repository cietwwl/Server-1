package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.alibaba.fastjson.JSONObject;
import com.rwbase.dao.majorDatas.pojo.MajorData;

public class MajorDataParser implements DataValueParser<MajorData> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public MajorData copy(MajorData entity) {
        MajorData newData_ = new MajorData();
        newData_.setId(entity.getId());
        newData_.setOwnerId(entity.getOwnerId());
        newData_.setCoin(entity.getCoin());
        newData_.setGold(entity.getGold());
        newData_.setGiftGold(entity.getGiftGold());
        newData_.setChargeGold(entity.getChargeGold());
        return newData_;
    }

    @Override
    public JSONObject recordAndUpdate(MajorData entity1, MajorData entity2) {
        JSONObject jsonMap = null;
        String id1 = entity1.getId();
        String id2 = entity2.getId();
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
        long coin1 = entity1.getCoin();
        long coin2 = entity2.getCoin();
        if (coin1 != coin2) {
            entity1.setCoin(coin2);
            jsonMap = writer.write(jsonMap, "coin", coin2);
        }
        int gold1 = entity1.getGold();
        int gold2 = entity2.getGold();
        if (gold1 != gold2) {
            entity1.setGold(gold2);
            jsonMap = writer.write(jsonMap, "gold", gold2);
        }
        int giftGold1 = entity1.getGiftGold();
        int giftGold2 = entity2.getGiftGold();
        if (giftGold1 != giftGold2) {
            entity1.setGiftGold(giftGold2);
            jsonMap = writer.write(jsonMap, "giftGold", giftGold2);
        }
        int chargeGold1 = entity1.getChargeGold();
        int chargeGold2 = entity2.getChargeGold();
        if (chargeGold1 != chargeGold2) {
            entity1.setChargeGold(chargeGold2);
            jsonMap = writer.write(jsonMap, "chargeGold", chargeGold2);
        }
        return jsonMap;
    }

    @Override
    public JSONObject toJson(MajorData entity) {
        JSONObject json = new JSONObject(6);
        json.put("id", entity.getId());
        json.put("ownerId", entity.getOwnerId());
        json.put("coin", entity.getCoin());
        json.put("gold", entity.getGold());
        json.put("giftGold", entity.getGiftGold());
        json.put("chargeGold", entity.getChargeGold());
        return json;
    }

}