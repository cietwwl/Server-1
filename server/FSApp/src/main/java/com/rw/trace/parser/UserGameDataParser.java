package com.rw.trace.parser;

import com.rwbase.dao.user.UserGameExtendInfo;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.user.UserGameData;
import com.alibaba.fastjson.JSONObject;

public class UserGameDataParser implements DataValueParser<UserGameData> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public UserGameData copy(UserGameData entity) {
        UserGameData newData_ = new UserGameData();
        newData_.setUserId(entity.getUserId());
        newData_.setVersion(entity.getVersion());
        newData_.setIphone(entity.isIphone());
        newData_.setPower(entity.getPower());
        newData_.setMaxPower(entity.getMaxPower());
        newData_.setUpgradeExp(entity.getUpgradeExp());
        newData_.setBuyPowerTimes(entity.getBuyPowerTimes());
        newData_.setBuyCoinTimes(entity.getBuyCoinTimes());
        newData_.setBuySkillTimes(entity.getBuySkillTimes());
        newData_.setLastLoginTime(entity.getLastLoginTime());
        newData_.setRookieFlag(entity.getRookieFlag());
        newData_.setFreeChat(entity.getFreeChat());
        newData_.setLastAddPowerTime(entity.getLastAddPowerTime());
        newData_.setLastResetTime(entity.getLastResetTime());
        newData_.setLastResetTime5Clock(entity.getLastResetTime5Clock());
        newData_.setLastChangeInfoTime(entity.getLastChangeInfoTime());
        newData_.setHeadFrame(entity.getHeadFrame());
        newData_.setSkillPointCount(entity.getSkillPointCount());
        newData_.setLastRecoverSkillPointTime(entity.getLastRecoverSkillPointTime());
        newData_.setUnendingWarCoin(entity.getUnendingWarCoin());
        newData_.setTowerCoin(entity.getTowerCoin());
        newData_.setExpCoin(entity.getExpCoin());
        newData_.setStrenCoin(entity.getStrenCoin());
        newData_.setPeakArenaCoin(entity.getPeakArenaCoin());
        newData_.setArenaCoin(entity.getArenaCoin());
        newData_.setWakenPiece(entity.getWakenPiece());
        newData_.setWakenKey(entity.getWakenKey());
        newData_.setCarrerChangeTime(entity.getCarrerChangeTime());
        newData_.setLastWorshipTime(entity.getLastWorshipTime());
        newData_.setFightingAll(entity.getFightingAll());
        newData_.setStarAll(entity.getStarAll());
        return newData_;
    }

    @Override
    public JSONObject recordAndUpdate(UserGameData entity1, UserGameData entity2) {
        JSONObject jsonMap = null;
        String userId1 = entity1.getUserId();
        String userId2 = entity2.getUserId();
        if (!writer.equals(userId1, userId2)) {
            entity1.setUserId(userId2);
            jsonMap = writer.write(jsonMap, "userId", userId2);
        }
        long version1 = entity1.getVersion();
        long version2 = entity2.getVersion();
        if (version1 != version2) {
            entity1.setVersion(version2);
            jsonMap = writer.write(jsonMap, "version", version2);
        }
        boolean iphone1 = entity1.isIphone();
        boolean iphone2 = entity2.isIphone();
        if (iphone1 != iphone2) {
            entity1.setIphone(iphone2);
            jsonMap = writer.write(jsonMap, "iphone", iphone2);
        }
        int power1 = entity1.getPower();
        int power2 = entity2.getPower();
        if (power1 != power2) {
            entity1.setPower(power2);
            jsonMap = writer.write(jsonMap, "power", power2);
        }
        int maxPower1 = entity1.getMaxPower();
        int maxPower2 = entity2.getMaxPower();
        if (maxPower1 != maxPower2) {
            entity1.setMaxPower(maxPower2);
            jsonMap = writer.write(jsonMap, "maxPower", maxPower2);
        }
        long upgradeExp1 = entity1.getUpgradeExp();
        long upgradeExp2 = entity2.getUpgradeExp();
        if (upgradeExp1 != upgradeExp2) {
            entity1.setUpgradeExp(upgradeExp2);
            jsonMap = writer.write(jsonMap, "upgradeExp", upgradeExp2);
        }
        int buyPowerTimes1 = entity1.getBuyPowerTimes();
        int buyPowerTimes2 = entity2.getBuyPowerTimes();
        if (buyPowerTimes1 != buyPowerTimes2) {
            entity1.setBuyPowerTimes(buyPowerTimes2);
            jsonMap = writer.write(jsonMap, "buyPowerTimes", buyPowerTimes2);
        }
        int buyCoinTimes1 = entity1.getBuyCoinTimes();
        int buyCoinTimes2 = entity2.getBuyCoinTimes();
        if (buyCoinTimes1 != buyCoinTimes2) {
            entity1.setBuyCoinTimes(buyCoinTimes2);
            jsonMap = writer.write(jsonMap, "buyCoinTimes", buyCoinTimes2);
        }
        int buySkillTimes1 = entity1.getBuySkillTimes();
        int buySkillTimes2 = entity2.getBuySkillTimes();
        if (buySkillTimes1 != buySkillTimes2) {
            entity1.setBuySkillTimes(buySkillTimes2);
            jsonMap = writer.write(jsonMap, "buySkillTimes", buySkillTimes2);
        }
        long lastLoginTime1 = entity1.getLastLoginTime();
        long lastLoginTime2 = entity2.getLastLoginTime();
        if (lastLoginTime1 != lastLoginTime2) {
            entity1.setLastLoginTime(lastLoginTime2);
            jsonMap = writer.write(jsonMap, "lastLoginTime", lastLoginTime2);
        }
        int rookieFlag1 = entity1.getRookieFlag();
        int rookieFlag2 = entity2.getRookieFlag();
        if (rookieFlag1 != rookieFlag2) {
            entity1.setRookieFlag(rookieFlag2);
            jsonMap = writer.write(jsonMap, "rookieFlag", rookieFlag2);
        }
        int freeChat1 = entity1.getFreeChat();
        int freeChat2 = entity2.getFreeChat();
        if (freeChat1 != freeChat2) {
            entity1.setFreeChat(freeChat2);
            jsonMap = writer.write(jsonMap, "freeChat", freeChat2);
        }
        long lastAddPowerTime1 = entity1.getLastAddPowerTime();
        long lastAddPowerTime2 = entity2.getLastAddPowerTime();
        if (lastAddPowerTime1 != lastAddPowerTime2) {
            entity1.setLastAddPowerTime(lastAddPowerTime2);
            jsonMap = writer.write(jsonMap, "lastAddPowerTime", lastAddPowerTime2);
        }
        long lastResetTime1 = entity1.getLastResetTime();
        long lastResetTime2 = entity2.getLastResetTime();
        if (lastResetTime1 != lastResetTime2) {
            entity1.setLastResetTime(lastResetTime2);
            jsonMap = writer.write(jsonMap, "lastResetTime", lastResetTime2);
        }
        long lastResetTime5Clock1 = entity1.getLastResetTime5Clock();
        long lastResetTime5Clock2 = entity2.getLastResetTime5Clock();
        if (lastResetTime5Clock1 != lastResetTime5Clock2) {
            entity1.setLastResetTime5Clock(lastResetTime5Clock2);
            jsonMap = writer.write(jsonMap, "lastResetTime5Clock", lastResetTime5Clock2);
        }
        long lastChangeInfoTime1 = entity1.getLastChangeInfoTime();
        long lastChangeInfoTime2 = entity2.getLastChangeInfoTime();
        if (lastChangeInfoTime1 != lastChangeInfoTime2) {
            entity1.setLastChangeInfoTime(lastChangeInfoTime2);
            jsonMap = writer.write(jsonMap, "lastChangeInfoTime", lastChangeInfoTime2);
        }
        String headFrame1 = entity1.getHeadFrame();
        String headFrame2 = entity2.getHeadFrame();
        if (!writer.equals(headFrame1, headFrame2)) {
            entity1.setHeadFrame(headFrame2);
            jsonMap = writer.write(jsonMap, "headFrame", headFrame2);
        }
        int skillPointCount1 = entity1.getSkillPointCount();
        int skillPointCount2 = entity2.getSkillPointCount();
        if (skillPointCount1 != skillPointCount2) {
            entity1.setSkillPointCount(skillPointCount2);
            jsonMap = writer.write(jsonMap, "skillPointCount", skillPointCount2);
        }
        long lastRecoverSkillPointTime1 = entity1.getLastRecoverSkillPointTime();
        long lastRecoverSkillPointTime2 = entity2.getLastRecoverSkillPointTime();
        if (lastRecoverSkillPointTime1 != lastRecoverSkillPointTime2) {
            entity1.setLastRecoverSkillPointTime(lastRecoverSkillPointTime2);
            jsonMap = writer.write(jsonMap, "lastRecoverSkillPointTime", lastRecoverSkillPointTime2);
        }
        int unendingWarCoin1 = entity1.getUnendingWarCoin();
        int unendingWarCoin2 = entity2.getUnendingWarCoin();
        if (unendingWarCoin1 != unendingWarCoin2) {
            entity1.setUnendingWarCoin(unendingWarCoin2);
            jsonMap = writer.write(jsonMap, "unendingWarCoin", unendingWarCoin2);
        }
        int towerCoin1 = entity1.getTowerCoin();
        int towerCoin2 = entity2.getTowerCoin();
        if (towerCoin1 != towerCoin2) {
            entity1.setTowerCoin(towerCoin2);
            jsonMap = writer.write(jsonMap, "towerCoin", towerCoin2);
        }
        int expCoin1 = entity1.getExpCoin();
        int expCoin2 = entity2.getExpCoin();
        if (expCoin1 != expCoin2) {
            entity1.setExpCoin(expCoin2);
            jsonMap = writer.write(jsonMap, "expCoin", expCoin2);
        }
        int strenCoin1 = entity1.getStrenCoin();
        int strenCoin2 = entity2.getStrenCoin();
        if (strenCoin1 != strenCoin2) {
            entity1.setStrenCoin(strenCoin2);
            jsonMap = writer.write(jsonMap, "strenCoin", strenCoin2);
        }
        int peakArenaCoin1 = entity1.getPeakArenaCoin();
        int peakArenaCoin2 = entity2.getPeakArenaCoin();
        if (peakArenaCoin1 != peakArenaCoin2) {
            entity1.setPeakArenaCoin(peakArenaCoin2);
            jsonMap = writer.write(jsonMap, "peakArenaCoin", peakArenaCoin2);
        }
        int arenaCoin1 = entity1.getArenaCoin();
        int arenaCoin2 = entity2.getArenaCoin();
        if (arenaCoin1 != arenaCoin2) {
            entity1.setArenaCoin(arenaCoin2);
            jsonMap = writer.write(jsonMap, "arenaCoin", arenaCoin2);
        }
        int wakenPiece1 = entity1.getWakenPiece();
        int wakenPiece2 = entity2.getWakenPiece();
        if (wakenPiece1 != wakenPiece2) {
            entity1.setWakenPiece(wakenPiece2);
            jsonMap = writer.write(jsonMap, "wakenPiece", wakenPiece2);
        }
        int wakenKey1 = entity1.getWakenKey();
        int wakenKey2 = entity2.getWakenKey();
        if (wakenKey1 != wakenKey2) {
            entity1.setWakenKey(wakenKey2);
            jsonMap = writer.write(jsonMap, "wakenKey", wakenKey2);
        }
        long carrerChangeTime1 = entity1.getCarrerChangeTime();
        long carrerChangeTime2 = entity2.getCarrerChangeTime();
        if (carrerChangeTime1 != carrerChangeTime2) {
            entity1.setCarrerChangeTime(carrerChangeTime2);
            jsonMap = writer.write(jsonMap, "carrerChangeTime", carrerChangeTime2);
        }
        long lastWorshipTime1 = entity1.getLastWorshipTime();
        long lastWorshipTime2 = entity2.getLastWorshipTime();
        if (lastWorshipTime1 != lastWorshipTime2) {
            entity1.setLastWorshipTime(lastWorshipTime2);
            jsonMap = writer.write(jsonMap, "lastWorshipTime", lastWorshipTime2);
        }
        int fightingAll1 = entity1.getFightingAll();
        int fightingAll2 = entity2.getFightingAll();
        if (fightingAll1 != fightingAll2) {
            entity1.setFightingAll(fightingAll2);
            jsonMap = writer.write(jsonMap, "fightingAll", fightingAll2);
        }
        int starAll1 = entity1.getStarAll();
        int starAll2 = entity2.getStarAll();
        if (starAll1 != starAll2) {
            entity1.setStarAll(starAll2);
            jsonMap = writer.write(jsonMap, "starAll", starAll2);
        }
        UserGameExtendInfo extendInfo1 = entity1.getExtendInfo();
        UserGameExtendInfo extendInfo2 = entity2.getExtendInfo();
        Pair<UserGameExtendInfo, JSONObject> extendInfoPair = writer.checkObject(jsonMap, "extendInfo", extendInfo1, extendInfo2);
        if (extendInfoPair != null) {
            extendInfo1 = extendInfoPair.getT1();
            entity1.setExtendInfo(extendInfo1);
            jsonMap = extendInfoPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "extendInfo", extendInfo1, extendInfo2);
        }

        return jsonMap;
    }

    @Override
    public JSONObject toJson(UserGameData entity) {
        JSONObject json = new JSONObject(32);
        json.put("userId", entity.getUserId());
        json.put("version", entity.getVersion());
        json.put("iphone", entity.isIphone());
        json.put("power", entity.getPower());
        json.put("maxPower", entity.getMaxPower());
        json.put("upgradeExp", entity.getUpgradeExp());
        json.put("buyPowerTimes", entity.getBuyPowerTimes());
        json.put("buyCoinTimes", entity.getBuyCoinTimes());
        json.put("buySkillTimes", entity.getBuySkillTimes());
        json.put("lastLoginTime", entity.getLastLoginTime());
        json.put("rookieFlag", entity.getRookieFlag());
        json.put("freeChat", entity.getFreeChat());
        json.put("lastAddPowerTime", entity.getLastAddPowerTime());
        json.put("lastResetTime", entity.getLastResetTime());
        json.put("lastResetTime5Clock", entity.getLastResetTime5Clock());
        json.put("lastChangeInfoTime", entity.getLastChangeInfoTime());
        json.put("headFrame", entity.getHeadFrame());
        json.put("skillPointCount", entity.getSkillPointCount());
        json.put("lastRecoverSkillPointTime", entity.getLastRecoverSkillPointTime());
        json.put("unendingWarCoin", entity.getUnendingWarCoin());
        json.put("towerCoin", entity.getTowerCoin());
        json.put("expCoin", entity.getExpCoin());
        json.put("strenCoin", entity.getStrenCoin());
        json.put("peakArenaCoin", entity.getPeakArenaCoin());
        json.put("arenaCoin", entity.getArenaCoin());
        json.put("wakenPiece", entity.getWakenPiece());
        json.put("wakenKey", entity.getWakenKey());
        json.put("carrerChangeTime", entity.getCarrerChangeTime());
        json.put("lastWorshipTime", entity.getLastWorshipTime());
        json.put("fightingAll", entity.getFightingAll());
        json.put("starAll", entity.getStarAll());
        return json;
    }

}