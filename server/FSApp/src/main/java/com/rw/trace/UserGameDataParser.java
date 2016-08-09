package com.rw.trace;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.user.UserGameData;
import com.rwbase.dao.user.UserGameExtendInfo;

public class UserGameDataParser implements DataValueParser<UserGameData> {

	@Override
	public UserGameData copy(UserGameData entity) {
		return null;
	}

	@Override
	public Map<String, ChangedRecord> compareDiff(UserGameData entity1, UserGameData entity2) {
		return null;
	}

	@Override
	public JSONObject toJson(UserGameData entity) {
		JSONObject json = new JSONObject(64);
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
		UserGameExtendInfo extInfo = entity.getExtendInfo();
		JSONObject ext = new JSONObject(true);
		ext.put("sendGold", extInfo.getSendGold());
		ext.put("chargedGold", extInfo.getChargedGold());
		json.put("extendInfo", ext);
		return json;
	}

	public static void main(String[] args) {
		UserGameData data = new UserGameData();
		data.setUserId("100100008010");
		data.setVersion(12345);
		data.setIphone(false);
		data.setPower(120);
		data.setMaxPower(180);
		data.setUpgradeExp(180000);
		data.setBuyPowerTimes(3);
		data.setBuyCoinTimes(1);
		data.setBuySkillTimes(2);
		data.setLastLoginTime(System.currentTimeMillis());
		data.setRookieFlag(1);
		data.setFreeChat(10);
		data.setLastAddPowerTime(System.currentTimeMillis());
		data.setLastResetTime(System.currentTimeMillis());
		data.setLastResetTime5Clock(System.currentTimeMillis());
		data.setLastChangeInfoTime(System.currentTimeMillis());
		data.setHeadFrame("10010023");
		data.setSkillPointCount(20);
		data.setLastRecoverSkillPointTime(System.currentTimeMillis());
		data.setUnendingWarCoin(11000);
		data.setTowerCoin(20403);
		data.setExpCoin(5230);
		data.setStrenCoin(3230);
		data.setPeakArenaCoin(5632);
		data.setArenaCoin(12345);
		data.setWakenPiece(345);
		data.setWakenKey(123);
		data.setCarrerChangeTime(System.currentTimeMillis());
		UserGameExtendInfo info = new UserGameExtendInfo();
		info.setChargedGold(1000);
		data.setExtendInfo(info);
		UserGameDataParser parser = new UserGameDataParser();
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			JSON.toJSON(data);
			// parser.toJson(data);
		}
		System.out.println(System.currentTimeMillis() - start);
	}

}
