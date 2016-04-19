package com.rw.dataaccess.processor;

import java.util.Calendar;
import java.util.TreeMap;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.sign.pojo.SignData;
import com.rwbase.dao.sign.pojo.TableSignData;

public class SignProcessor implements PlayerCreatedProcessor<TableSignData>{

	@Override
	public TableSignData create(PlayerCreatedParam param) {
		TableSignData pTableSignData = new TableSignData();
		pTableSignData.setUserId(param.getUserId());
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
		//从逻辑上看没有作用
		//getStringRecordFromData(signID, newOpenSignData);
		return pTableSignData;
	}

}
