package com.rw.trace.parser;

import com.rwbase.dao.user.UserGameData;
import com.rwbase.dao.user.UserGameExtendInfo;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import java.util.List;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.common.Pair;
import com.alibaba.fastjson.JSONObject;

public class UserGameDataParser implements DataValueParser<UserGameData> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public UserGameData copy(UserGameData entity) {
        UserGameData userGameDataCopy = new UserGameData();
        userGameDataCopy.setUserId(entity.getUserId());
        userGameDataCopy.setVersion(entity.getVersion());
        userGameDataCopy.setIphone(entity.isIphone());
        userGameDataCopy.setPower(entity.getPower());
        userGameDataCopy.setMaxPower(entity.getMaxPower());
        userGameDataCopy.setUpgradeExp(entity.getUpgradeExp());
        userGameDataCopy.setBuyPowerTimes(entity.getBuyPowerTimes());
        userGameDataCopy.setBuyCoinTimes(entity.getBuyCoinTimes());
        userGameDataCopy.setBuySkillTimes(entity.getBuySkillTimes());
        userGameDataCopy.setLastLoginTime(entity.getLastLoginTime());
        userGameDataCopy.setRookieFlag(entity.getRookieFlag());
        userGameDataCopy.setFreeChat(entity.getFreeChat());
        userGameDataCopy.setLastAddPowerTime(entity.getLastAddPowerTime());
        userGameDataCopy.setLastResetTime(entity.getLastResetTime());
        userGameDataCopy.setLastResetTime5Clock(entity.getLastResetTime5Clock());
        userGameDataCopy.setLastChangeInfoTime(entity.getLastChangeInfoTime());
        userGameDataCopy.setHeadFrame(entity.getHeadFrame());
        userGameDataCopy.setSkillPointCount(entity.getSkillPointCount());
        userGameDataCopy.setLastRecoverSkillPointTime(entity.getLastRecoverSkillPointTime());
        userGameDataCopy.setUnendingWarCoin(entity.getUnendingWarCoin());
        userGameDataCopy.setTowerCoin(entity.getTowerCoin());
        userGameDataCopy.setExpCoin(entity.getExpCoin());
        userGameDataCopy.setStrenCoin(entity.getStrenCoin());
        userGameDataCopy.setPeakArenaCoin(entity.getPeakArenaCoin());
        userGameDataCopy.setArenaCoin(entity.getArenaCoin());
        userGameDataCopy.setWakenPiece(entity.getWakenPiece());
        userGameDataCopy.setWakenKey(entity.getWakenKey());
        userGameDataCopy.setCarrerChangeTime(entity.getCarrerChangeTime());
        userGameDataCopy.setLastWorshipTime(entity.getLastWorshipTime());
        userGameDataCopy.setRandomBossIds(writer.copyObject(entity.getRandomBossIds()));
        userGameDataCopy.setRandomBossFightCount(entity.getRandomBossFightCount());
        userGameDataCopy.setKillBossRewardCount(entity.getKillBossRewardCount());
        userGameDataCopy.setCreateBossCount(entity.getCreateBossCount());
        return userGameDataCopy;
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

        List<String> randomBossIds1 = entity1.getRandomBossIds();
        List<String> randomBossIds2 = entity2.getRandomBossIds();
        Pair<List<String>, JSONObject> randomBossIdsPair = writer.checkObject(jsonMap, "randomBossIds", randomBossIds1, randomBossIds2);
        if (randomBossIdsPair != null) {
            randomBossIds1 = randomBossIdsPair.getT1();
            entity1.setRandomBossIds(randomBossIds1);
            jsonMap = randomBossIdsPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "randomBossIds", randomBossIds1, randomBossIds2);
        }

        int randomBossFightCount1 = entity1.getRandomBossFightCount();
        int randomBossFightCount2 = entity2.getRandomBossFightCount();
        if (randomBossFightCount1 != randomBossFightCount2) {
            entity1.setRandomBossFightCount(randomBossFightCount2);
            jsonMap = writer.write(jsonMap, "randomBossFightCount", randomBossFightCount2);
        }
        int killBossRewardCount1 = entity1.getKillBossRewardCount();
        int killBossRewardCount2 = entity2.getKillBossRewardCount();
        if (killBossRewardCount1 != killBossRewardCount2) {
            entity1.setKillBossRewardCount(killBossRewardCount2);
            jsonMap = writer.write(jsonMap, "killBossRewardCount", killBossRewardCount2);
        }
        int createBossCount1 = entity1.getCreateBossCount();
        int createBossCount2 = entity2.getCreateBossCount();
        if (createBossCount1 != createBossCount2) {
            entity1.setCreateBossCount(createBossCount2);
            jsonMap = writer.write(jsonMap, "createBossCount", createBossCount2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(UserGameData entity1, UserGameData entity2) {
        if (!writer.equals(entity1.getUserId(), entity2.getUserId())) {
            return true;
        }
        if (entity1.getVersion() != entity2.getVersion()) {
            return true;
        }
        if (entity1.isIphone() != entity2.isIphone()) {
            return true;
        }
        if (entity1.getPower() != entity2.getPower()) {
            return true;
        }
        if (entity1.getMaxPower() != entity2.getMaxPower()) {
            return true;
        }
        if (entity1.getUpgradeExp() != entity2.getUpgradeExp()) {
            return true;
        }
        if (entity1.getBuyPowerTimes() != entity2.getBuyPowerTimes()) {
            return true;
        }
        if (entity1.getBuyCoinTimes() != entity2.getBuyCoinTimes()) {
            return true;
        }
        if (entity1.getBuySkillTimes() != entity2.getBuySkillTimes()) {
            return true;
        }
        if (entity1.getLastLoginTime() != entity2.getLastLoginTime()) {
            return true;
        }
        if (entity1.getRookieFlag() != entity2.getRookieFlag()) {
            return true;
        }
        if (entity1.getFreeChat() != entity2.getFreeChat()) {
            return true;
        }
        if (entity1.getLastAddPowerTime() != entity2.getLastAddPowerTime()) {
            return true;
        }
        if (entity1.getLastResetTime() != entity2.getLastResetTime()) {
            return true;
        }
        if (entity1.getLastResetTime5Clock() != entity2.getLastResetTime5Clock()) {
            return true;
        }
        if (entity1.getLastChangeInfoTime() != entity2.getLastChangeInfoTime()) {
            return true;
        }
        if (!writer.equals(entity1.getHeadFrame(), entity2.getHeadFrame())) {
            return true;
        }
        if (entity1.getSkillPointCount() != entity2.getSkillPointCount()) {
            return true;
        }
        if (entity1.getLastRecoverSkillPointTime() != entity2.getLastRecoverSkillPointTime()) {
            return true;
        }
        if (entity1.getUnendingWarCoin() != entity2.getUnendingWarCoin()) {
            return true;
        }
        if (entity1.getTowerCoin() != entity2.getTowerCoin()) {
            return true;
        }
        if (entity1.getExpCoin() != entity2.getExpCoin()) {
            return true;
        }
        if (entity1.getStrenCoin() != entity2.getStrenCoin()) {
            return true;
        }
        if (entity1.getPeakArenaCoin() != entity2.getPeakArenaCoin()) {
            return true;
        }
        if (entity1.getArenaCoin() != entity2.getArenaCoin()) {
            return true;
        }
        if (entity1.getWakenPiece() != entity2.getWakenPiece()) {
            return true;
        }
        if (entity1.getWakenKey() != entity2.getWakenKey()) {
            return true;
        }
        if (entity1.getCarrerChangeTime() != entity2.getCarrerChangeTime()) {
            return true;
        }
        if (entity1.getLastWorshipTime() != entity2.getLastWorshipTime()) {
            return true;
        }
        if (writer.hasChanged(entity1.getExtendInfo(), entity2.getExtendInfo())) {
            return true;
        }
        if (writer.hasChanged(entity1.getRandomBossIds(), entity2.getRandomBossIds())) {
            return true;
        }
        if (entity1.getRandomBossFightCount() != entity2.getRandomBossFightCount()) {
            return true;
        }
        if (entity1.getKillBossRewardCount() != entity2.getKillBossRewardCount()) {
            return true;
        }
        if (entity1.getCreateBossCount() != entity2.getCreateBossCount()) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(UserGameData entity) {
        JSONObject json = new JSONObject(34);
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
        Object randomBossIdsJson = writer.toJSON(entity.getRandomBossIds());
        if (randomBossIdsJson != null) {
            json.put("randomBossIds", randomBossIdsJson);
        }
        json.put("randomBossFightCount", entity.getRandomBossFightCount());
        json.put("killBossRewardCount", entity.getKillBossRewardCount());
        json.put("createBossCount", entity.getCreateBossCount());
        return json;
    }

}