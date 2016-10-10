package com.rw.trace.support;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.playerdata.charge.dao.ChargeOrder;
import com.alibaba.fastjson.JSONObject;

public class ChargeOrderParser implements DataValueParser<ChargeOrder> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public ChargeOrder copy(ChargeOrder entity) {
        ChargeOrder chargeOrderCopy = new ChargeOrder();
        chargeOrderCopy.setCpTradeNo(entity.getCpTradeNo());
        chargeOrderCopy.setReceiveTime(entity.getReceiveTime());
        return chargeOrderCopy;
    }

    @Override
    public JSONObject recordAndUpdate(ChargeOrder entity1, ChargeOrder entity2) {
        JSONObject jsonMap = null;
        String cpTradeNo1 = entity1.getCpTradeNo();
        String cpTradeNo2 = entity2.getCpTradeNo();
        if (!writer.equals(cpTradeNo1, cpTradeNo2)) {
            entity1.setCpTradeNo(cpTradeNo2);
            jsonMap = writer.write(jsonMap, "cpTradeNo", cpTradeNo2);
        }
        long receiveTime1 = entity1.getReceiveTime();
        long receiveTime2 = entity2.getReceiveTime();
        if (receiveTime1 != receiveTime2) {
            entity1.setReceiveTime(receiveTime2);
            jsonMap = writer.write(jsonMap, "receiveTime", receiveTime2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(ChargeOrder entity1, ChargeOrder entity2) {
        if (!writer.equals(entity1.getCpTradeNo(), entity2.getCpTradeNo())) {
            return true;
        }
        if (entity1.getReceiveTime() != entity2.getReceiveTime()) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(ChargeOrder entity) {
        JSONObject json = new JSONObject(2);
        json.put("cpTradeNo", entity.getCpTradeNo());
        json.put("receiveTime", entity.getReceiveTime());
        return json;
    }

}