package com.playerdata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.log.GameLog;
import com.playerdata.common.PlayerEventListener;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.sign.ReSignCfgDAO;
import com.rwbase.dao.sign.SignCfgDAO;
import com.rwbase.dao.sign.TableSignDataDAO;
import com.rwbase.dao.sign.pojo.ReSignCfg;
import com.rwbase.dao.sign.pojo.SignCfg;
import com.rwbase.dao.sign.pojo.SignData;
import com.rwbase.dao.sign.pojo.SignDataHolder;
import com.rwbase.dao.sign.pojo.TableSignData;
import com.rwproto.MsgDef.Command;
import com.rwproto.SignServiceProtos.EResultType;
import com.rwproto.SignServiceProtos.MsgSignResponse;

public class SignMgr implements PlayerEventListener {
	private SignDataHolder signDataHolder;
	private Player player;

	/*
	 * 初始化
	 */
	public void init(Player pOwner) {
		player = pOwner;
		signDataHolder = new SignDataHolder(pOwner.getUserId());
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		TableSignData pTableSignData = new TableSignData();
		pTableSignData.setUserId(player.getUserId());
		TreeMap<String,SignData> treeMap = new TreeMap<String, SignData>();
		pTableSignData.setSignDataMap(treeMap);
		Calendar calendar = Calendar.getInstance();
		// 判断是否跨年
		if (calendar.get(Calendar.DAY_OF_MONTH) == 0) {
			if (calendar.get(Calendar.HOUR) < 5) {
				calendar.roll(Calendar.MONTH, -1);
				pTableSignData.setLastUpate(calendar);
			} else {
				pTableSignData.setLastUpate(calendar);
			}
		} else {
			pTableSignData.setLastUpate(calendar);
		}

		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		String signID = year + "_" + month + "_" + 1; // 保证每天都有...
		SignData newOpenSignData = new SignData();
		newOpenSignData.setOpen(true); // 能否签到...
		newOpenSignData.setOpenSignDate(Calendar.getInstance()); // 开放时间...
		newOpenSignData.setDouble(false); // 是否有剩余的VIP双倍可用状态...
		newOpenSignData.setResign(false);
		newOpenSignData.setLastSignDate(null); // 有签到时间的话才可能有双倍...
		treeMap.put(signID, newOpenSignData);
		getStringRecordFromData(signID, newOpenSignData);
		
		TableSignDataDAO.getInstance().update(pTableSignData);

		//this.signDataHolder.update(player);
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		TableSignData pTableSignData = signDataHolder.getTableSignData();
		if (pTableSignData.getLastUpate() == null) {
			Calendar calendar = Calendar.getInstance();
			pTableSignData.setLastUpate(calendar);
			this.signDataHolder.update(player);
		}
		if (checkRefreshTime()) // 检查是否需要更换月份
		{
			refreshData();
			this.save();
		}
	}

	public void save() {
		this.signDataHolder.flush();
	}

	/*
	 * 登陆消息回传...
	 */
	public void onLogin() {
		Calendar lastUpdate = signDataHolder.getLastUpdate();
		MsgSignResponse.Builder msgSignResponse = MsgSignResponse.newBuilder();
		msgSignResponse.setResultype(EResultType.INIT_DATA);
		msgSignResponse.setMonth(lastUpdate.get(Calendar.MONTH) + 1);
		msgSignResponse.setYear(Calendar.getInstance().get(Calendar.YEAR));
		msgSignResponse.setReSignCount(getResignCount());
		msgSignResponse.addAllTagSignData(getAllSignRecord());
		player.SendMsg(Command.MSG_SIGN, msgSignResponse.build().toByteString());
	}

	/*
	 * 数据库添加新签到记录，故次数必定是已签到数+1，默认状态是未签到已开放...
	 */
	private String addRecord(boolean isReSign) {
		Calendar lastUpdate = signDataHolder.getLastUpdate();
		int month = lastUpdate.get(Calendar.MONTH) + 1;
		int year = lastUpdate.get(Calendar.YEAR);
		String strRecord = null;
		TreeMap<String, SignData> map = signDataHolder.getSignDataMap();
		int count = map.size() + 1;
		String signID = year + "_" + month + "_" + count; // 保证每天都有...
		SignData newOpenSignData = new SignData();
		newOpenSignData.setOpen(true); // 能否签到...
		newOpenSignData.setOpenSignDate(Calendar.getInstance()); // 开放时间...
		newOpenSignData.setDouble(false); // 是否有剩余的VIP双倍可用状态...
		if (isReSign) {
			newOpenSignData.setResign(true);
		} else {
			newOpenSignData.setResign(false);
		}
		newOpenSignData.setLastSignDate(null); // 有签到时间的话才可能有双倍...
		map.put(signID, newOpenSignData);
		signDataHolder.setLastUpdate(Calendar.getInstance());
		this.signDataHolder.update(player);
		strRecord = getStringRecordFromData(signID, newOpenSignData);
		return strRecord;
	}

	/*
	 * 月份更换更新数据
	 */
	public void refreshData() {
		signDataHolder.refreshData();
		addRecord(false);
	}

	/*
	 * 请求的签到记录更改,注意，是更改，服务端没有记录的数据不能进行更改,包括补签和双倍...
	 */
	@SuppressWarnings("deprecation")
	public void changeSignData(String signId, Player player, MsgSignResponse.Builder response) {
		SignCfg signCfg = (SignCfg) SignCfgDAO.getInstance().getCfgById(signId);
		SignData signData = getSignData(signId); // 获取签到记录...
		if (signData.isOpen()) {
			if (signData.getLastSignDate() == null) // 开放并且没有签到过,有可能是补签有可能是正常签到...
			{
				if (signData.isResign()) // 如果是补签则需要扣除钻石...
				{
					ReSignCfg reSignCfg = (ReSignCfg) ReSignCfgDAO.getInstance().getCfgById(String.valueOf(signDataHolder.getCurrentResignCount() + 1));
					if (player.getUserGameDataMgr().getGold() < reSignCfg.getDiamondNum()) {
						GameLog.debug("钻石不足");
						response.setResultype(EResultType.NOT_ENOUGH_DIAMOND);
						return;
					} else {
						player.getUserGameDataMgr().addGold(-reSignCfg.getDiamondNum());
						signData.setResign(false);

						signDataHolder.setCurrentResignCount(signDataHolder.getCurrentResignCount() + 1);
						response.setReSignCount(signDataHolder.getCurrentResignCount());
					}
				}
				addItemData(signId, player);
				response.setResultype(EResultType.SUCCESS);
			} else if (signData.getLastSignDate() != null) // 已经签到过，只可能剩下双倍...
			{
				if (signData.isDouble()) {
					if (player.getVip() >= signCfg.getVipLimit()) {
						addItemData(signId, player);
						response.setResultype(EResultType.SUCCESS);
					} else {
						GameLog.debug("Vip等级不足");
					}
				} else {
					GameLog.debug("非法请求");
				}
			}
		} else {
			GameLog.debug("未开放");
		}
	}

	private List<String> addItemData(String signId, Player player) {
		List<String> list = new ArrayList<String>();
		SignCfg signCfg = (SignCfg) SignCfgDAO.getInstance().getCfgById(signId);
		SignData signData = getSignData(signId);

		int days = signDataHolder.getLastUpdate().get(Calendar.DAY_OF_MONTH);
		if (days == 1) // 首天的话需要考虑时间...
		{
			Calendar calendar = Calendar.getInstance();
			if (calendar.get(Calendar.HOUR_OF_DAY) < 5) {
				calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
				days = calendar.getActualMaximum(Calendar.DATE);
			} else {
				days = 1;
			}
		}
		TreeMap<String, SignData> signDataMap = signDataHolder.getSignDataMap();
		if (signDataMap.size() < days) // 如果签到次数少于当前天数并且当前签到的数据不为双倍可用则为其添加补签的下一个数据...
		{
			if (signData.getLastSignDate() == null || signData.equals(signDataMap.lastEntry()))
				list.add(addRecord(true));
		}

		// if (isNumberic(signCfg.getItemID())) // 检查奖励的类型...
		// {
		if (signCfg.getVipLimit() > 0) // 有双倍...
		{
			if (player.getVip() >= signCfg.getVipLimit()) {
				if (signData.getLastSignDate() == null) {// 如果没有领过就双倍
					// player.getItemBagMgr().addItem(Integer.valueOf(signCfg.getItemID()),
					// signCfg.getItemNum() * 2);
					sendReward(signCfg.getItemID(), signCfg.getItemNum() * 2);
				} else {
					// player.getItemBagMgr().addItem(Integer.valueOf(signCfg.getItemID()),
					// signCfg.getItemNum());
					sendReward(signCfg.getItemID(), signCfg.getItemNum());
				}
				signData.setDouble(false);
			} else {
				// player.getItemBagMgr().addItem(Integer.valueOf(signCfg.getItemID()),
				// signCfg.getItemNum());
				sendReward(signCfg.getItemID(), signCfg.getItemNum());
				signData.setDouble(true);
			}
		} else {
			// player.getItemBagMgr().addItem(Integer.valueOf(signCfg.getItemID()),
			// signCfg.getItemNum());
			sendReward(signCfg.getItemID(), signCfg.getItemNum());
		}
		// } else
		// // 英雄整卡...
		// {
		// player.getHeroMgr().addHero(signCfg.getItemID());
		// }

		signData.setResign(false);
		signData.setLastSignDate(Calendar.getInstance());
		this.signDataHolder.update(player);
		list.add(getStringRecordFromData(signId, signData));
		return list;
	}

	/**
	 * 发送奖励物品
	 * 
	 * @param itemId
	 * @param count
	 */
	private void sendReward(String itemId, int count) {
		if (isNumberic(itemId)) {
			player.getItemBagMgr().addItem(Integer.valueOf(itemId), count);
		} else {
			for (int i = 0; i < count; i++) {
				player.getHeroMgr().addHero(itemId);
			}
		}
	}

	/*
	 * 进入这里之前必先判断年份和月份保证是同一个月内的...
	 */
	public boolean disablePrevious() {
		TreeMap<String, SignData> map = signDataHolder.getSignDataMap();
		boolean isNeedFresh = false;
		if (map.size() > 0) {
			SignData signData = getLastData();
			if (signData.isOpen()) // 最后一个签到记录是开放的...
			{
				if (signData.getLastSignDate() != null) // 最后一个记录有签到时间
				{
					int lastday = signData.getLastSignDate().get(Calendar.DAY_OF_MONTH);
					int lastHour = signData.getLastSignDate().get(Calendar.HOUR_OF_DAY);
					if (checkUpdateByLastTime(lastday, lastHour)) {
						updateData(true);
						isNeedFresh = true;
					} else {
						GameLog.debug("不需要将之前的记录都打勾");
					}
				} else
				// 最后一个记录没有签到时间...
				{
					if (map.size() != 1) {
						int openDay = signData.getOpenSignDate().get(Calendar.DAY_OF_MONTH);
						int openHour = signData.getOpenSignDate().get(Calendar.HOUR_OF_DAY);
						if (checkUpdateByOpenTime(openDay, openHour)) {
							updateData(false);
							isNeedFresh = true;
						} else {
							GameLog.debug("不需要将之前的记录都打勾");
						}
					} else
					// 只有一个并且没有签到时间，那么肯定是没签到
					{
						isNeedFresh = true;
						GameLog.debug("不需要将之前的记录都打勾");
					}
				}
			} else
			// 最后一个签到记录没开放...
			{
				if (signData.getLastSignDate() == null) {
					GameLog.debug("服务端记录初始化出错");
				} else {
					int day = signData.getLastSignDate().get(Calendar.DAY_OF_MONTH);
					int hour = signData.getLastSignDate().get(Calendar.HOUR_OF_DAY);
					if (checkUpdateByLastTime(day, hour)) {
						updateData(true);
						isNeedFresh = true;
					} else {
						GameLog.debug("服务端记录初始化出错");
					}
				}
			}
		}
		this.signDataHolder.update(player);
		return isNeedFresh;
	}

	public SignData getLastData() {
		TreeMap<String, SignData> map = signDataHolder.getSignDataMap();
		SignData firstData = map.firstEntry().getValue();
		for (Map.Entry<String, SignData> entry : map.entrySet()) {
			firstData = entry.getValue().getOpenSignDate().getTimeInMillis() > firstData.getOpenSignDate().getTimeInMillis() ? entry.getValue() : firstData;
		}
		return firstData;
	}

	/**
	 * 上一次签到的数据
	 * 
	 * @return
	 */
	public SignData getLastSignData() {
		TreeMap<String, SignData> map = signDataHolder.getSignDataMap();
		SignData firstData = null;
		if (map.size() > 0) {

			firstData = map.firstEntry().getValue();
			if (firstData.getLastSignDate() == null) {
				return null;
			}

			for (Map.Entry<String, SignData> entry : map.entrySet()) {
				if (entry.getValue().getLastSignDate() != null) {
					firstData = entry.getValue().getOpenSignDate().getTimeInMillis() > firstData.getOpenSignDate().getTimeInMillis() ? entry.getValue() : firstData;
				}
			}
		}
		return firstData;
	}

	public boolean isSignToday() {
		TreeMap<String, SignData> signDataMap = signDataHolder.getSignDataMap();
		if (signDataMap == null || signDataMap.size() <= 0) {
			return false;
		}
		SignData lastSignData = getLastSignData();
		if (lastSignData == null) {
			return false;
		}
		long refreshTime = DateUtils.getHour(System.currentTimeMillis(), 5);
		long currentTime = System.currentTimeMillis();
		long lastSignTime = lastSignData.getLastSignDate().getTimeInMillis();
		if (currentTime >= refreshTime) {
			return lastSignTime > refreshTime;
		} else {
			return lastSignTime < refreshTime;
		}
	}

	/*
	 *
	 */
	private void updateData(boolean isLastSignYet) {
		SignData data = null;
		TreeMap<String, SignData> map = signDataHolder.getSignDataMap();
		for (Map.Entry<String, SignData> record : map.entrySet()) {
			data = record.getValue();
			data.setOpen(false);
		}
		signDataHolder.setCurrentResignCount(0);

		if (isLastSignYet) {
			addRecord(false);
		} else {
			map.lastEntry().getValue().setOpen(true);
			map.lastEntry().getValue().setDouble(false);
			map.lastEntry().getValue().setResign(false);
			map.lastEntry().getValue().setOpenSignDate(Calendar.getInstance());
			signDataHolder.setLastUpdate(Calendar.getInstance());
		}
	}

	/*
	 * 获取当前补签次数...
	 */
	public int getResignCount() {
		return signDataHolder.getCurrentResignCount();
	}

	/*
	 * 获取客户端需要返回的所有签到记录...
	 */
	public List<String> getAllSignRecord() {
		TreeMap<String, SignData> map = signDataHolder.getSignDataMap();
		List<String> signDataList = null;
		if (map != null) {
			signDataList = new ArrayList<String>();
			for (Map.Entry<String, SignData> record : map.entrySet()) {
				signDataList.add(getStringRecordFromData(record.getKey(), record.getValue()));
			}
		}
		return signDataList;
	}

	/*
	 * 从上一次签到时间进行判断同一个月内是否需要刷新...
	 */
	private boolean checkUpdateByLastTime(int day, int hour) {
		boolean isUpdateTime = false;
		int curDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (curDay > day) {
			if ((curDay - day > 1) || ((curDay - day == 1) && hour >= 5)) {
				isUpdateTime = true;
			} else {
				GameLog.debug("不需要将之前的记录都打勾");
			}
		} else {
			if (hour < 5 && curHour >= 5) {
				isUpdateTime = true;
			} else {
				GameLog.debug("不需要将之前的记录都打勾");
			}
		}
		return isUpdateTime;
	}

	/*
	 * 从上一次签到记录开启时间进行判断是否需要刷新...
	 */
	private boolean checkUpdateByOpenTime(int day, int hour) {
		boolean isUpdateTime = false;
		int curDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (curDay > day) {
			if (curHour >= 5) {
				isUpdateTime = true;
			} else {
				GameLog.debug("不需要将之前的记录都打勾");
			}
		} else {
			if (hour < 5 && curHour >= 5) {
				isUpdateTime = true;
			} else {
				GameLog.debug("不需要将之前的记录都打勾");
			}
		}
		return isUpdateTime;
	}

	/*
	 * 检查此时请求的时间是否处于本签到周期内...
	 */
	public boolean checkRefreshTime() {
		boolean isNeedToUpdate = false;
		TreeMap<String, SignData> map = signDataHolder.getSignDataMap();
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); // 当前小时数
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH); // 当前日期数
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1; // 当前月份数(比实际月份小1）
		int year = Calendar.getInstance().get(Calendar.YEAR); // 当前年份数

		if (map != null) // 本月有签到可用...
		{
			Calendar lastUpdate = signDataHolder.getLastUpdate();
			if (year > lastUpdate.get(Calendar.YEAR)) // 不在同一年
			{
				isNeedToUpdate = true;
			} else
			// 在同一年
			{
				if (month > (lastUpdate.get(Calendar.MONTH) + 1)) // 不同月份（下一个月）
				{
					if (day == 1) // 如果是一号，则需要判断是否是在第一天的凌晨5点前
					{
						if (hour >= 5) // 5点后就要更新
						{
							isNeedToUpdate = true;
						}
					} else
					// 不在一号必然要更新...
					{
						isNeedToUpdate = true;
					}
				}
			}
		} else {
			GameLog.debug("没有初始化");
		}
		return isNeedToUpdate;
	}

	/*
	 * 检查id的合法性,以服务器记录为准
	 */
	public boolean Checklegal(String signId) {
		if (signDataHolder.getSignDataMap().containsKey(signId)) {
			return true;
		}
		return false;
	}

	private SignData getSignData(String signId) {
		SignData signData = null;
		TreeMap<String, SignData> map = signDataHolder.getSignDataMap();
		signData = map.get(signId);
		return signData;
	}

	public int getCurrentMonth() {
		int month = signDataHolder.getLastUpdate().get(Calendar.MONTH) + 1;
		return month;
	}

	public int getCurrentYear() {
		int year = signDataHolder.getLastUpdate().get(Calendar.YEAR);
		return year;
	}

	/*
	 * 构造记录返回给客户端
	 */
	private String getStringRecordFromData(String signId, SignData data) {
		int doubleable = 0;
		int reSignable = 0; // 0代表false, 1代表true...
		if (data.isOpen()) // 已经开放签到了...
		{
			if (data.getLastSignDate() == null) // 没有签到过...
			{
				if (data.isResign()) // 但是是补签,不用判断是否double...
				{
					reSignable = 1;
					doubleable = 0;
				} else
				// 当前签到...
				{
					reSignable = 1;
					doubleable = 1;
				}
			} else
			// 有签到过...
			{
				if (data.isResign()) {
					reSignable = 1;
				}
				if (data.isDouble()) {
					doubleable = 1;
				}
			}
		}
		String signDataString = signId + "," + doubleable + "," + reSignable;
		return signDataString;
	}

	/*
	 * 检查是否是数字
	 */
	public static boolean isNumberic(String _string) {
		if (_string.equals(null) || _string.equals(""))
			return false;
		try {
			Integer.valueOf(_string);
		} catch (Exception e) {
			GameLog.debug("不合法的字符串");
			return false;
		}
		return true;
	}

}