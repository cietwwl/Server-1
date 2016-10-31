package com.rw.trace.support;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rwbase.dao.user.UserExtendInfo;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.alibaba.fastjson.JSONObject;

public class UserExtendInfoParser implements DataValueParser<UserExtendInfo> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public UserExtendInfo copy(UserExtendInfo entity) {
        UserExtendInfo userExtendInfoCopy = new UserExtendInfo();
        userExtendInfoCopy.setBlockReason(entity.getBlockReason());
        userExtendInfoCopy.setBlockCoolTime(entity.getBlockCoolTime());
        userExtendInfoCopy.setChatBanReason(entity.getChatBanReason());
        userExtendInfoCopy.setChatBanCoolTime(entity.getChatBanCoolTime());
        userExtendInfoCopy.setFeedbackId(entity.getFeedbackId());
        return userExtendInfoCopy;
    }

    @Override
    public JSONObject recordAndUpdate(UserExtendInfo entity1, UserExtendInfo entity2) {
        JSONObject jsonMap = null;
        String blockReason1 = entity1.getBlockReason();
        String blockReason2 = entity2.getBlockReason();
        if (!writer.equals(blockReason1, blockReason2)) {
            entity1.setBlockReason(blockReason2);
            jsonMap = writer.write(jsonMap, "blockReason", blockReason2);
        }
        long blockCoolTime1 = entity1.getBlockCoolTime();
        long blockCoolTime2 = entity2.getBlockCoolTime();
        if (blockCoolTime1 != blockCoolTime2) {
            entity1.setBlockCoolTime(blockCoolTime2);
            jsonMap = writer.write(jsonMap, "blockCoolTime", blockCoolTime2);
        }
        String chatBanReason1 = entity1.getChatBanReason();
        String chatBanReason2 = entity2.getChatBanReason();
        if (!writer.equals(chatBanReason1, chatBanReason2)) {
            entity1.setChatBanReason(chatBanReason2);
            jsonMap = writer.write(jsonMap, "chatBanReason", chatBanReason2);
        }
        long chatBanCoolTime1 = entity1.getChatBanCoolTime();
        long chatBanCoolTime2 = entity2.getChatBanCoolTime();
        if (chatBanCoolTime1 != chatBanCoolTime2) {
            entity1.setChatBanCoolTime(chatBanCoolTime2);
            jsonMap = writer.write(jsonMap, "chatBanCoolTime", chatBanCoolTime2);
        }
        int feedbackId1 = entity1.getFeedbackId();
        int feedbackId2 = entity2.getFeedbackId();
        if (feedbackId1 != feedbackId2) {
            entity1.setFeedbackId(feedbackId2);
            jsonMap = writer.write(jsonMap, "feedbackId", feedbackId2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(UserExtendInfo entity1, UserExtendInfo entity2) {
        if (!writer.equals(entity1.getBlockReason(), entity2.getBlockReason())) {
            return true;
        }
        if (entity1.getBlockCoolTime() != entity2.getBlockCoolTime()) {
            return true;
        }
        if (!writer.equals(entity1.getChatBanReason(), entity2.getChatBanReason())) {
            return true;
        }
        if (entity1.getChatBanCoolTime() != entity2.getChatBanCoolTime()) {
            return true;
        }
        if (entity1.getFeedbackId() != entity2.getFeedbackId()) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(UserExtendInfo entity) {
        JSONObject json = new JSONObject(7);
        json.put("blockReason", entity.getBlockReason());
        json.put("blockCoolTime", entity.getBlockCoolTime());
        json.put("chatBanReason", entity.getChatBanReason());
        json.put("chatBanCoolTime", entity.getChatBanCoolTime());
        json.put("feedbackId", entity.getFeedbackId());
        return json;
    }

}