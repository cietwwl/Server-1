package com.rw.trace.support;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.alibaba.fastjson.JSONObject;

public class ZoneLoginInfoParser implements DataValueParser<ZoneLoginInfo> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public ZoneLoginInfo copy(ZoneLoginInfo entity) {
        ZoneLoginInfo zoneLoginInfoCopy = new ZoneLoginInfo();
        zoneLoginInfoCopy.setLoginZoneId(entity.getLoginZoneId());
        zoneLoginInfoCopy.setLoginZoneTime(entity.getLoginZoneTime());
        zoneLoginInfoCopy.setLoginChannelId(entity.getLoginChannelId());
        zoneLoginInfoCopy.setLoginSubChannelId(entity.getLoginSubChannelId());
        zoneLoginInfoCopy.setLoginClientPlatForm(entity.getLoginClientPlatForm());
        zoneLoginInfoCopy.setLoginNetType(entity.getLoginNetType());
        zoneLoginInfoCopy.setClientVersion(entity.getClientVersion());
        zoneLoginInfoCopy.setLoginPhoneType(entity.getLoginPhoneType());
        zoneLoginInfoCopy.setLoginClientIp(entity.getLoginClientIp());
        zoneLoginInfoCopy.setLoginImei(entity.getLoginImei());
        zoneLoginInfoCopy.setLoginImac(entity.getLoginImac());
        zoneLoginInfoCopy.setLoginsdkVersion(entity.getLoginsdkVersion());
        zoneLoginInfoCopy.setLoginsystemVersion(entity.getLoginsystemVersion());
        zoneLoginInfoCopy.setLoginadLinkId(entity.getLoginadLinkId());
        return zoneLoginInfoCopy;
    }

    @Override
    public JSONObject recordAndUpdate(ZoneLoginInfo entity1, ZoneLoginInfo entity2) {
        JSONObject jsonMap = null;
        Integer loginZoneId1 = entity1.getLoginZoneId();
        Integer loginZoneId2 = entity2.getLoginZoneId();
        if (!writer.equals(loginZoneId1, loginZoneId2)) {
            entity1.setLoginZoneId(loginZoneId2);
            jsonMap = writer.write(jsonMap, "loginZoneId", loginZoneId2);
        }
        Long loginZoneTime1 = entity1.getLoginZoneTime();
        Long loginZoneTime2 = entity2.getLoginZoneTime();
        if (!writer.equals(loginZoneTime1, loginZoneTime2)) {
            entity1.setLoginZoneTime(loginZoneTime2);
            jsonMap = writer.write(jsonMap, "loginZoneTime", loginZoneTime2);
        }
        String loginChannelId1 = entity1.getLoginChannelId();
        String loginChannelId2 = entity2.getLoginChannelId();
        if (!writer.equals(loginChannelId1, loginChannelId2)) {
            entity1.setLoginChannelId(loginChannelId2);
            jsonMap = writer.write(jsonMap, "loginChannelId", loginChannelId2);
        }
        String loginSubChannelId1 = entity1.getLoginSubChannelId();
        String loginSubChannelId2 = entity2.getLoginSubChannelId();
        if (!writer.equals(loginSubChannelId1, loginSubChannelId2)) {
            entity1.setLoginSubChannelId(loginSubChannelId2);
            jsonMap = writer.write(jsonMap, "loginSubChannelId", loginSubChannelId2);
        }
        String loginClientPlatForm1 = entity1.getLoginClientPlatForm();
        String loginClientPlatForm2 = entity2.getLoginClientPlatForm();
        if (!writer.equals(loginClientPlatForm1, loginClientPlatForm2)) {
            entity1.setLoginClientPlatForm(loginClientPlatForm2);
            jsonMap = writer.write(jsonMap, "loginClientPlatForm", loginClientPlatForm2);
        }
        String loginNetType1 = entity1.getLoginNetType();
        String loginNetType2 = entity2.getLoginNetType();
        if (!writer.equals(loginNetType1, loginNetType2)) {
            entity1.setLoginNetType(loginNetType2);
            jsonMap = writer.write(jsonMap, "loginNetType", loginNetType2);
        }
        String clientVersion1 = entity1.getClientVersion();
        String clientVersion2 = entity2.getClientVersion();
        if (!writer.equals(clientVersion1, clientVersion2)) {
            entity1.setClientVersion(clientVersion2);
            jsonMap = writer.write(jsonMap, "clientVersion", clientVersion2);
        }
        String loginPhoneType1 = entity1.getLoginPhoneType();
        String loginPhoneType2 = entity2.getLoginPhoneType();
        if (!writer.equals(loginPhoneType1, loginPhoneType2)) {
            entity1.setLoginPhoneType(loginPhoneType2);
            jsonMap = writer.write(jsonMap, "loginPhoneType", loginPhoneType2);
        }
        String loginClientIp1 = entity1.getLoginClientIp();
        String loginClientIp2 = entity2.getLoginClientIp();
        if (!writer.equals(loginClientIp1, loginClientIp2)) {
            entity1.setLoginClientIp(loginClientIp2);
            jsonMap = writer.write(jsonMap, "loginClientIp", loginClientIp2);
        }
        String loginImei1 = entity1.getLoginImei();
        String loginImei2 = entity2.getLoginImei();
        if (!writer.equals(loginImei1, loginImei2)) {
            entity1.setLoginImei(loginImei2);
            jsonMap = writer.write(jsonMap, "loginImei", loginImei2);
        }
        String loginImac1 = entity1.getLoginImac();
        String loginImac2 = entity2.getLoginImac();
        if (!writer.equals(loginImac1, loginImac2)) {
            entity1.setLoginImac(loginImac2);
            jsonMap = writer.write(jsonMap, "loginImac", loginImac2);
        }
        String loginsdkVersion1 = entity1.getLoginsdkVersion();
        String loginsdkVersion2 = entity2.getLoginsdkVersion();
        if (!writer.equals(loginsdkVersion1, loginsdkVersion2)) {
            entity1.setLoginsdkVersion(loginsdkVersion2);
            jsonMap = writer.write(jsonMap, "loginsdkVersion", loginsdkVersion2);
        }
        String loginsystemVersion1 = entity1.getLoginsystemVersion();
        String loginsystemVersion2 = entity2.getLoginsystemVersion();
        if (!writer.equals(loginsystemVersion1, loginsystemVersion2)) {
            entity1.setLoginsystemVersion(loginsystemVersion2);
            jsonMap = writer.write(jsonMap, "loginsystemVersion", loginsystemVersion2);
        }
        String loginadLinkId1 = entity1.getLoginadLinkId();
        String loginadLinkId2 = entity2.getLoginadLinkId();
        if (!writer.equals(loginadLinkId1, loginadLinkId2)) {
            entity1.setLoginadLinkId(loginadLinkId2);
            jsonMap = writer.write(jsonMap, "loginadLinkId", loginadLinkId2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(ZoneLoginInfo entity1, ZoneLoginInfo entity2) {
        if (!writer.equals(entity1.getLoginZoneId(), entity2.getLoginZoneId())) {
            return true;
        }
        if (!writer.equals(entity1.getLoginZoneTime(), entity2.getLoginZoneTime())) {
            return true;
        }
        if (!writer.equals(entity1.getLoginChannelId(), entity2.getLoginChannelId())) {
            return true;
        }
        if (!writer.equals(entity1.getLoginSubChannelId(), entity2.getLoginSubChannelId())) {
            return true;
        }
        if (!writer.equals(entity1.getLoginClientPlatForm(), entity2.getLoginClientPlatForm())) {
            return true;
        }
        if (!writer.equals(entity1.getLoginNetType(), entity2.getLoginNetType())) {
            return true;
        }
        if (!writer.equals(entity1.getClientVersion(), entity2.getClientVersion())) {
            return true;
        }
        if (!writer.equals(entity1.getLoginPhoneType(), entity2.getLoginPhoneType())) {
            return true;
        }
        if (!writer.equals(entity1.getLoginClientIp(), entity2.getLoginClientIp())) {
            return true;
        }
        if (!writer.equals(entity1.getLoginImei(), entity2.getLoginImei())) {
            return true;
        }
        if (!writer.equals(entity1.getLoginImac(), entity2.getLoginImac())) {
            return true;
        }
        if (!writer.equals(entity1.getLoginsdkVersion(), entity2.getLoginsdkVersion())) {
            return true;
        }
        if (!writer.equals(entity1.getLoginsystemVersion(), entity2.getLoginsystemVersion())) {
            return true;
        }
        if (!writer.equals(entity1.getLoginadLinkId(), entity2.getLoginadLinkId())) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(ZoneLoginInfo entity) {
        JSONObject json = new JSONObject(19);
        json.put("loginZoneId", entity.getLoginZoneId());
        json.put("loginZoneTime", entity.getLoginZoneTime());
        json.put("loginChannelId", entity.getLoginChannelId());
        json.put("loginSubChannelId", entity.getLoginSubChannelId());
        json.put("loginClientPlatForm", entity.getLoginClientPlatForm());
        json.put("loginNetType", entity.getLoginNetType());
        json.put("clientVersion", entity.getClientVersion());
        json.put("loginPhoneType", entity.getLoginPhoneType());
        json.put("loginClientIp", entity.getLoginClientIp());
        json.put("loginImei", entity.getLoginImei());
        json.put("loginImac", entity.getLoginImac());
        json.put("loginsdkVersion", entity.getLoginsdkVersion());
        json.put("loginsystemVersion", entity.getLoginsystemVersion());
        json.put("loginadLinkId", entity.getLoginadLinkId());
        return json;
    }

}