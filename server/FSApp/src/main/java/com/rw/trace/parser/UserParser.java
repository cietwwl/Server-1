package com.rw.trace.parser;

import com.rwbase.dao.user.UserExtendInfo;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rwbase.dao.user.User;
import com.rw.service.log.infoPojo.ZoneRegInfo;
import com.rw.fsutil.common.Pair;
import com.alibaba.fastjson.JSONObject;

public class UserParser implements DataValueParser<User> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public User copy(User entity) {
        User userCopy = new User();
        userCopy.setUserId(entity.getUserId());
        userCopy.setZoneId(entity.getZoneId());
        userCopy.setVip(entity.getVip());
        userCopy.setSex(entity.getSex());
        userCopy.setAccount(entity.getAccount());
        userCopy.setUserName(entity.getUserName());
        userCopy.setHeadImage(entity.getHeadImage());
        userCopy.setCreateTime(entity.getCreateTime());
        userCopy.setOpenAccount(entity.getOpenAccount());
        userCopy.setLastLoginTime(entity.getLastLoginTime());
        userCopy.setKickOffCoolTime(entity.getKickOffCoolTime());
        userCopy.setZoneRegInfo(writer.copyObject(entity.getZoneRegInfo()));
        userCopy.setExtendInfo(writer.copyObject(entity.getExtendInfo()));
        userCopy.setZoneLoginInfo(writer.copyObject(entity.getZoneLoginInfo()));
        userCopy.setExp(entity.getExp());
        userCopy.setLevel(entity.getLevel());
        return userCopy;
    }

    @Override
    public JSONObject recordAndUpdate(User entity1, User entity2) {
        JSONObject jsonMap = null;
        String userId1 = entity1.getUserId();
        String userId2 = entity2.getUserId();
        if (!writer.equals(userId1, userId2)) {
            entity1.setUserId(userId2);
            jsonMap = writer.write(jsonMap, "userId", userId2);
        }
        int zoneId1 = entity1.getZoneId();
        int zoneId2 = entity2.getZoneId();
        if (zoneId1 != zoneId2) {
            entity1.setZoneId(zoneId2);
            jsonMap = writer.write(jsonMap, "zoneId", zoneId2);
        }
        int vip1 = entity1.getVip();
        int vip2 = entity2.getVip();
        if (vip1 != vip2) {
            entity1.setVip(vip2);
            jsonMap = writer.write(jsonMap, "vip", vip2);
        }
        int sex1 = entity1.getSex();
        int sex2 = entity2.getSex();
        if (sex1 != sex2) {
            entity1.setSex(sex2);
            jsonMap = writer.write(jsonMap, "sex", sex2);
        }
        String account1 = entity1.getAccount();
        String account2 = entity2.getAccount();
        if (!writer.equals(account1, account2)) {
            entity1.setAccount(account2);
            jsonMap = writer.write(jsonMap, "account", account2);
        }
        String userName1 = entity1.getUserName();
        String userName2 = entity2.getUserName();
        if (!writer.equals(userName1, userName2)) {
            entity1.setUserName(userName2);
            jsonMap = writer.write(jsonMap, "userName", userName2);
        }
        String headImage1 = entity1.getHeadImage();
        String headImage2 = entity2.getHeadImage();
        if (!writer.equals(headImage1, headImage2)) {
            entity1.setHeadImage(headImage2);
            jsonMap = writer.write(jsonMap, "headImage", headImage2);
        }
        long createTime1 = entity1.getCreateTime();
        long createTime2 = entity2.getCreateTime();
        if (createTime1 != createTime2) {
            entity1.setCreateTime(createTime2);
            jsonMap = writer.write(jsonMap, "createTime", createTime2);
        }
        String openAccount1 = entity1.getOpenAccount();
        String openAccount2 = entity2.getOpenAccount();
        if (!writer.equals(openAccount1, openAccount2)) {
            entity1.setOpenAccount(openAccount2);
            jsonMap = writer.write(jsonMap, "openAccount", openAccount2);
        }
        long lastLoginTime1 = entity1.getLastLoginTime();
        long lastLoginTime2 = entity2.getLastLoginTime();
        if (lastLoginTime1 != lastLoginTime2) {
            entity1.setLastLoginTime(lastLoginTime2);
            jsonMap = writer.write(jsonMap, "lastLoginTime", lastLoginTime2);
        }
        long kickOffCoolTime1 = entity1.getKickOffCoolTime();
        long kickOffCoolTime2 = entity2.getKickOffCoolTime();
        if (kickOffCoolTime1 != kickOffCoolTime2) {
            entity1.setKickOffCoolTime(kickOffCoolTime2);
            jsonMap = writer.write(jsonMap, "kickOffCoolTime", kickOffCoolTime2);
        }
        ZoneRegInfo zoneRegInfo1 = entity1.getZoneRegInfo();
        ZoneRegInfo zoneRegInfo2 = entity2.getZoneRegInfo();
        Pair<ZoneRegInfo, JSONObject> zoneRegInfoPair = writer.checkObject(jsonMap, "zoneRegInfo", zoneRegInfo1, zoneRegInfo2);
        if (zoneRegInfoPair != null) {
            zoneRegInfo1 = zoneRegInfoPair.getT1();
            entity1.setZoneRegInfo(zoneRegInfo1);
            jsonMap = zoneRegInfoPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "zoneRegInfo", zoneRegInfo1, zoneRegInfo2);
        }

        UserExtendInfo extendInfo1 = entity1.getExtendInfo();
        UserExtendInfo extendInfo2 = entity2.getExtendInfo();
        Pair<UserExtendInfo, JSONObject> extendInfoPair = writer.checkObject(jsonMap, "extendInfo", extendInfo1, extendInfo2);
        if (extendInfoPair != null) {
            extendInfo1 = extendInfoPair.getT1();
            entity1.setExtendInfo(extendInfo1);
            jsonMap = extendInfoPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "extendInfo", extendInfo1, extendInfo2);
        }

        ZoneLoginInfo zoneLoginInfo1 = entity1.getZoneLoginInfo();
        ZoneLoginInfo zoneLoginInfo2 = entity2.getZoneLoginInfo();
        Pair<ZoneLoginInfo, JSONObject> zoneLoginInfoPair = writer.checkObject(jsonMap, "zoneLoginInfo", zoneLoginInfo1, zoneLoginInfo2);
        if (zoneLoginInfoPair != null) {
            zoneLoginInfo1 = zoneLoginInfoPair.getT1();
            entity1.setZoneLoginInfo(zoneLoginInfo1);
            jsonMap = zoneLoginInfoPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "zoneLoginInfo", zoneLoginInfo1, zoneLoginInfo2);
        }

        long exp1 = entity1.getExp();
        long exp2 = entity2.getExp();
        if (exp1 != exp2) {
            entity1.setExp(exp2);
            jsonMap = writer.write(jsonMap, "exp", exp2);
        }
        int level1 = entity1.getLevel();
        int level2 = entity2.getLevel();
        if (level1 != level2) {
            entity1.setLevel(level2);
            jsonMap = writer.write(jsonMap, "level", level2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(User entity1, User entity2) {
        if (!writer.equals(entity1.getUserId(), entity2.getUserId())) {
            return true;
        }
        if (entity1.getZoneId() != entity2.getZoneId()) {
            return true;
        }
        if (entity1.getVip() != entity2.getVip()) {
            return true;
        }
        if (entity1.getSex() != entity2.getSex()) {
            return true;
        }
        if (!writer.equals(entity1.getAccount(), entity2.getAccount())) {
            return true;
        }
        if (!writer.equals(entity1.getUserName(), entity2.getUserName())) {
            return true;
        }
        if (!writer.equals(entity1.getHeadImage(), entity2.getHeadImage())) {
            return true;
        }
        if (entity1.getCreateTime() != entity2.getCreateTime()) {
            return true;
        }
        if (!writer.equals(entity1.getOpenAccount(), entity2.getOpenAccount())) {
            return true;
        }
        if (entity1.getLastLoginTime() != entity2.getLastLoginTime()) {
            return true;
        }
        if (entity1.getKickOffCoolTime() != entity2.getKickOffCoolTime()) {
            return true;
        }
        if (writer.hasChanged(entity1.getZoneRegInfo(), entity2.getZoneRegInfo())) {
            return true;
        }
        if (writer.hasChanged(entity1.getExtendInfo(), entity2.getExtendInfo())) {
            return true;
        }
        if (writer.hasChanged(entity1.getZoneLoginInfo(), entity2.getZoneLoginInfo())) {
            return true;
        }
        if (entity1.getExp() != entity2.getExp()) {
            return true;
        }
        if (entity1.getLevel() != entity2.getLevel()) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(User entity) {
        JSONObject json = new JSONObject(22);
        json.put("userId", entity.getUserId());
        json.put("zoneId", entity.getZoneId());
        json.put("vip", entity.getVip());
        json.put("sex", entity.getSex());
        json.put("account", entity.getAccount());
        json.put("userName", entity.getUserName());
        json.put("headImage", entity.getHeadImage());
        json.put("createTime", entity.getCreateTime());
        json.put("openAccount", entity.getOpenAccount());
        json.put("lastLoginTime", entity.getLastLoginTime());
        json.put("kickOffCoolTime", entity.getKickOffCoolTime());
        Object zoneRegInfoJson = writer.toJSON(entity.getZoneRegInfo());
        if (zoneRegInfoJson != null) {
            json.put("zoneRegInfo", zoneRegInfoJson);
        }
        Object extendInfoJson = writer.toJSON(entity.getExtendInfo());
        if (extendInfoJson != null) {
            json.put("extendInfo", extendInfoJson);
        }
        Object zoneLoginInfoJson = writer.toJSON(entity.getZoneLoginInfo());
        if (zoneLoginInfoJson != null) {
            json.put("zoneLoginInfo", zoneLoginInfoJson);
        }
        json.put("exp", entity.getExp());
        json.put("level", entity.getLevel());
        return json;
    }

}