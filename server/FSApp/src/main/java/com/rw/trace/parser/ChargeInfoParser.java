package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import java.util.List;
import com.playerdata.charge.dao.ChargeInfoSubRecording;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.playerdata.charge.dao.ChargeOrder;
import com.playerdata.charge.dao.ChargeInfo;
import com.rw.fsutil.common.Pair;
import com.alibaba.fastjson.JSONObject;

public class ChargeInfoParser implements DataValueParser<ChargeInfo> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public ChargeInfo copy(ChargeInfo entity) {
        ChargeInfo chargeInfoCopy = new ChargeInfo();
        chargeInfoCopy.setUserId(entity.getUserId());
        chargeInfoCopy.setPayTimesList(writer.copyObject(entity.getPayTimesList()));
        chargeInfoCopy.setCount(entity.getCount());
        chargeInfoCopy.setLastCharge(entity.getLastCharge());
        chargeInfoCopy.setLastChargeTime(entity.getLastChargeTime());
        chargeInfoCopy.setTotalChargeMoney(entity.getTotalChargeMoney());
        chargeInfoCopy.setTotalChargeGold(entity.getTotalChargeGold());
        chargeInfoCopy.setChargeOrderList(writer.copyObject(entity.getChargeOrderList()));
        return chargeInfoCopy;
    }

    @Override
    public JSONObject recordAndUpdate(ChargeInfo entity1, ChargeInfo entity2) {
        JSONObject jsonMap = null;
        String userId1 = entity1.getUserId();
        String userId2 = entity2.getUserId();
        if (!writer.equals(userId1, userId2)) {
            entity1.setUserId(userId2);
            jsonMap = writer.write(jsonMap, "userId", userId2);
        }
        List<ChargeInfoSubRecording> payTimesList1 = entity1.getPayTimesList();
        List<ChargeInfoSubRecording> payTimesList2 = entity2.getPayTimesList();
        Pair<List<ChargeInfoSubRecording>, JSONObject> payTimesListPair = writer.checkObject(jsonMap, "payTimesList", payTimesList1, payTimesList2);
        if (payTimesListPair != null) {
            payTimesList1 = payTimesListPair.getT1();
            entity1.setPayTimesList(payTimesList1);
            jsonMap = payTimesListPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "payTimesList", payTimesList1, payTimesList2);
        }

        int count1 = entity1.getCount();
        int count2 = entity2.getCount();
        if (count1 != count2) {
            entity1.setCount(count2);
            jsonMap = writer.write(jsonMap, "count", count2);
        }
        int lastCharge1 = entity1.getLastCharge();
        int lastCharge2 = entity2.getLastCharge();
        if (lastCharge1 != lastCharge2) {
            entity1.setLastCharge(lastCharge2);
            jsonMap = writer.write(jsonMap, "lastCharge", lastCharge2);
        }
        long lastChargeTime1 = entity1.getLastChargeTime();
        long lastChargeTime2 = entity2.getLastChargeTime();
        if (lastChargeTime1 != lastChargeTime2) {
            entity1.setLastChargeTime(lastChargeTime2);
            jsonMap = writer.write(jsonMap, "lastChargeTime", lastChargeTime2);
        }
        int totalChargeMoney1 = entity1.getTotalChargeMoney();
        int totalChargeMoney2 = entity2.getTotalChargeMoney();
        if (totalChargeMoney1 != totalChargeMoney2) {
            entity1.setTotalChargeMoney(totalChargeMoney2);
            jsonMap = writer.write(jsonMap, "totalChargeMoney", totalChargeMoney2);
        }
        int totalChargeGold1 = entity1.getTotalChargeGold();
        int totalChargeGold2 = entity2.getTotalChargeGold();
        if (totalChargeGold1 != totalChargeGold2) {
            entity1.setTotalChargeGold(totalChargeGold2);
            jsonMap = writer.write(jsonMap, "totalChargeGold", totalChargeGold2);
        }
        List<ChargeOrder> chargeOrderList1 = entity1.getChargeOrderList();
        List<ChargeOrder> chargeOrderList2 = entity2.getChargeOrderList();
        Pair<List<ChargeOrder>, JSONObject> chargeOrderListPair = writer.checkObject(jsonMap, "chargeOrderList", chargeOrderList1, chargeOrderList2);
        if (chargeOrderListPair != null) {
            chargeOrderList1 = chargeOrderListPair.getT1();
            entity1.setChargeOrderList(chargeOrderList1);
            jsonMap = chargeOrderListPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "chargeOrderList", chargeOrderList1, chargeOrderList2);
        }

        return jsonMap;
    }

    @Override
    public boolean hasChanged(ChargeInfo entity1, ChargeInfo entity2) {
        if (!writer.equals(entity1.getUserId(), entity2.getUserId())) {
            return true;
        }
        if (writer.hasChanged(entity1.getPayTimesList(), entity2.getPayTimesList())) {
            return true;
        }
        if (entity1.getCount() != entity2.getCount()) {
            return true;
        }
        if (entity1.getLastCharge() != entity2.getLastCharge()) {
            return true;
        }
        if (entity1.getLastChargeTime() != entity2.getLastChargeTime()) {
            return true;
        }
        if (entity1.getTotalChargeMoney() != entity2.getTotalChargeMoney()) {
            return true;
        }
        if (entity1.getTotalChargeGold() != entity2.getTotalChargeGold()) {
            return true;
        }
        if (writer.hasChanged(entity1.getChargeOrderList(), entity2.getChargeOrderList())) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(ChargeInfo entity) {
        JSONObject json = new JSONObject(8);
        json.put("userId", entity.getUserId());
        Object payTimesListJson = writer.toJSON(entity.getPayTimesList());
        if (payTimesListJson != null) {
            json.put("payTimesList", payTimesListJson);
        }
        json.put("count", entity.getCount());
        json.put("lastCharge", entity.getLastCharge());
        json.put("lastChargeTime", entity.getLastChargeTime());
        json.put("totalChargeMoney", entity.getTotalChargeMoney());
        json.put("totalChargeGold", entity.getTotalChargeGold());
        Object chargeOrderListJson = writer.toJSON(entity.getChargeOrderList());
        if (chargeOrderListJson != null) {
            json.put("chargeOrderList", chargeOrderListJson);
        }
        return json;
    }

}