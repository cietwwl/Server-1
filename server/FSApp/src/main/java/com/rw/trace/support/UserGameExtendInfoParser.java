package com.rw.trace.support;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rwbase.dao.user.UserGameExtendInfo;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.alibaba.fastjson.JSONObject;

public class UserGameExtendInfoParser implements DataValueParser<UserGameExtendInfo> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public UserGameExtendInfo copy(UserGameExtendInfo entity) {
        UserGameExtendInfo userGameExtendInfoCopy = new UserGameExtendInfo();
        userGameExtendInfoCopy.setSendGold(entity.getSendGold());
        userGameExtendInfoCopy.setChargedGold(entity.getChargedGold());
        return userGameExtendInfoCopy;
    }

    @Override
    public JSONObject recordAndUpdate(UserGameExtendInfo entity1, UserGameExtendInfo entity2) {
        JSONObject jsonMap = null;
        int sendGold1 = entity1.getSendGold();
        int sendGold2 = entity2.getSendGold();
        if (sendGold1 != sendGold2) {
            entity1.setSendGold(sendGold2);
            jsonMap = writer.write(jsonMap, "sendGold", sendGold2);
        }
        int chargedGold1 = entity1.getChargedGold();
        int chargedGold2 = entity2.getChargedGold();
        if (chargedGold1 != chargedGold2) {
            entity1.setChargedGold(chargedGold2);
            jsonMap = writer.write(jsonMap, "chargedGold", chargedGold2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(UserGameExtendInfo entity1, UserGameExtendInfo entity2) {
        if (entity1.getSendGold() != entity2.getSendGold()) {
            return true;
        }
        if (entity1.getChargedGold() != entity2.getChargedGold()) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(UserGameExtendInfo entity) {
        JSONObject json = new JSONObject(2);
        json.put("sendGold", entity.getSendGold());
        json.put("chargedGold", entity.getChargedGold());
        return json;
    }

}