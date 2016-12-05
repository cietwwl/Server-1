package com.rw.dataaccess.processor;

import java.util.Calendar;
import java.util.TreeMap;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.sign.pojo.SignData;
import com.rwbase.dao.sign.pojo.TableSignData;

public class SignCreator implements DataExtensionCreator<TableSignData> {

	@Override
	public TableSignData create(String userId) {
		TableSignData pTableSignData = new TableSignData();
		pTableSignData.setUserId(userId);
		TreeMap<String, SignData> treeMap = new TreeMap<String, SignData>();
		pTableSignData.setSignDataMap(treeMap);
		// 判断是否跨年
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.DAY_OF_MONTH) == 1 && calendar.get(Calendar.HOUR_OF_DAY) < 5) {
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 0);
		}
		pTableSignData.setLastUpate(calendar);

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
		// 从逻辑上看没有作用
		// getStringRecordFromData(signID, newOpenSignData);
		return pTableSignData;
	}

}
