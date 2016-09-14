package com.rw.trace.support;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.service.log.infoPojo.ZoneRegInfo;
import com.alibaba.fastjson.JSONObject;

public class ZoneRegInfoParser implements DataValueParser<ZoneRegInfo> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public ZoneRegInfo copy(ZoneRegInfo entity) {
        ZoneRegInfo zoneRegInfoCopy = new ZoneRegInfo();
        zoneRegInfoCopy.setZoneCreatedTime(entity.getZoneCreatedTime());
        zoneRegInfoCopy.setRegZoneId(entity.getRegZoneId());
        zoneRegInfoCopy.setRegZoneTime(entity.getRegZoneTime());
        zoneRegInfoCopy.setRegChannelId(entity.getRegChannelId());
        zoneRegInfoCopy.setRegChannelId_uid(entity.getRegChannelId_uid());
        zoneRegInfoCopy.setRegSubChannelId(entity.getRegSubChannelId());
        zoneRegInfoCopy.setRegClientPlatForm(entity.getRegClientPlatForm());
        zoneRegInfoCopy.setPhoneOp(entity.getPhoneOp());
        return zoneRegInfoCopy;
    }

    @Override
    public JSONObject recordAndUpdate(ZoneRegInfo entity1, ZoneRegInfo entity2) {
        JSONObject jsonMap = null;
        String zoneCreatedTime1 = entity1.getZoneCreatedTime();
        String zoneCreatedTime2 = entity2.getZoneCreatedTime();
        if (!writer.equals(zoneCreatedTime1, zoneCreatedTime2)) {
            entity1.setZoneCreatedTime(zoneCreatedTime2);
            jsonMap = writer.write(jsonMap, "zoneCreatedTime", zoneCreatedTime2);
        }
        Integer regZoneId1 = entity1.getRegZoneId();
        Integer regZoneId2 = entity2.getRegZoneId();
        if (!writer.equals(regZoneId1, regZoneId2)) {
            entity1.setRegZoneId(regZoneId2);
            jsonMap = writer.write(jsonMap, "regZoneId", regZoneId2);
        }
        Long regZoneTime1 = entity1.getRegZoneTime();
        Long regZoneTime2 = entity2.getRegZoneTime();
        if (!writer.equals(regZoneTime1, regZoneTime2)) {
            entity1.setRegZoneTime(regZoneTime2);
            jsonMap = writer.write(jsonMap, "regZoneTime", regZoneTime2);
        }
        String regChannelId1 = entity1.getRegChannelId();
        String regChannelId2 = entity2.getRegChannelId();
        if (!writer.equals(regChannelId1, regChannelId2)) {
            entity1.setRegChannelId(regChannelId2);
            jsonMap = writer.write(jsonMap, "regChannelId", regChannelId2);
        }
        String regChannelId_uid1 = entity1.getRegChannelId_uid();
        String regChannelId_uid2 = entity2.getRegChannelId_uid();
        if (!writer.equals(regChannelId_uid1, regChannelId_uid2)) {
            entity1.setRegChannelId_uid(regChannelId_uid2);
            jsonMap = writer.write(jsonMap, "regChannelId_uid", regChannelId_uid2);
        }
        String regSubChannelId1 = entity1.getRegSubChannelId();
        String regSubChannelId2 = entity2.getRegSubChannelId();
        if (!writer.equals(regSubChannelId1, regSubChannelId2)) {
            entity1.setRegSubChannelId(regSubChannelId2);
            jsonMap = writer.write(jsonMap, "regSubChannelId", regSubChannelId2);
        }
        String regClientPlatForm1 = entity1.getRegClientPlatForm();
        String regClientPlatForm2 = entity2.getRegClientPlatForm();
        if (!writer.equals(regClientPlatForm1, regClientPlatForm2)) {
            entity1.setRegClientPlatForm(regClientPlatForm2);
            jsonMap = writer.write(jsonMap, "regClientPlatForm", regClientPlatForm2);
        }
        String phoneOp1 = entity1.getPhoneOp();
        String phoneOp2 = entity2.getPhoneOp();
        if (!writer.equals(phoneOp1, phoneOp2)) {
            entity1.setPhoneOp(phoneOp2);
            jsonMap = writer.write(jsonMap, "phoneOp", phoneOp2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(ZoneRegInfo entity1, ZoneRegInfo entity2) {
        if (!writer.equals(entity1.getZoneCreatedTime(), entity2.getZoneCreatedTime())) {
            return true;
        }
        if (!writer.equals(entity1.getRegZoneId(), entity2.getRegZoneId())) {
            return true;
        }
        if (!writer.equals(entity1.getRegZoneTime(), entity2.getRegZoneTime())) {
            return true;
        }
        if (!writer.equals(entity1.getRegChannelId(), entity2.getRegChannelId())) {
            return true;
        }
        if (!writer.equals(entity1.getRegChannelId_uid(), entity2.getRegChannelId_uid())) {
            return true;
        }
        if (!writer.equals(entity1.getRegSubChannelId(), entity2.getRegSubChannelId())) {
            return true;
        }
        if (!writer.equals(entity1.getRegClientPlatForm(), entity2.getRegClientPlatForm())) {
            return true;
        }
        if (!writer.equals(entity1.getPhoneOp(), entity2.getPhoneOp())) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(ZoneRegInfo entity) {
        JSONObject json = new JSONObject(8);
        json.put("zoneCreatedTime", entity.getZoneCreatedTime());
        json.put("regZoneId", entity.getRegZoneId());
        json.put("regZoneTime", entity.getRegZoneTime());
        json.put("regChannelId", entity.getRegChannelId());
        json.put("regChannelId_uid", entity.getRegChannelId_uid());
        json.put("regSubChannelId", entity.getRegSubChannelId());
        json.put("regClientPlatForm", entity.getRegClientPlatForm());
        json.put("phoneOp", entity.getPhoneOp());
        return json;
    }

}