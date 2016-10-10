package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.majorDatas.pojo.MajorData;
import com.alibaba.fastjson.JSONObject;

public class MajorDataParser implements DataValueParser<MajorData> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public MajorData copy(MajorData entity) {
        MajorData majorDataCopy = new MajorData();
        majorDataCopy.setId(entity.getId());
        majorDataCopy.setCoin(entity.getCoin());
        majorDataCopy.setGold(entity.getGold());
        majorDataCopy.setGiftGold(entity.getGiftGold());
        majorDataCopy.setChargeGold(entity.getChargeGold());
        return majorDataCopy;
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
    public boolean hasChanged(MajorData entity1, MajorData entity2) {
        if (!writer.equals(entity1.getId(), entity2.getId())) {
            return true;
        }
        if (entity1.getCoin() != entity2.getCoin()) {
            return true;
        }
        if (entity1.getGold() != entity2.getGold()) {
            return true;
        }
        if (entity1.getGiftGold() != entity2.getGiftGold()) {
            return true;
        }
        if (entity1.getChargeGold() != entity2.getChargeGold()) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(MajorData entity) {
        JSONObject json = new JSONObject(6);
        json.put("id", entity.getId());
        json.put("coin", entity.getCoin());
        json.put("gold", entity.getGold());
        json.put("giftGold", entity.getGiftGold());
        json.put("chargeGold", entity.getChargeGold());
        return json;
    }

}