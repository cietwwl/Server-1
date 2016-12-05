package com.gm.onetimehotfix;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.log.GameLog;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rwbase.dao.sign.TableSignDataDAO;
import com.rwbase.dao.sign.pojo.TableSignData;

public class RecoverSignPlayers implements Callable<Void> {

	@Override
	public Void call() throws Exception {
		JdbcTemplate template = DataAccessFactory.getSimpleSupport().getMainTemplate();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 5);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Calendar fixCalendar = Calendar.getInstance();
		fixCalendar.set(Calendar.MONTH, 10);
		fixCalendar.set(Calendar.DATE, 30);
		fixCalendar.set(Calendar.HOUR_OF_DAY, 4);
		fixCalendar.set(Calendar.MINUTE, 0);
		fixCalendar.set(Calendar.SECOND, 0);
		fixCalendar.set(Calendar.MILLISECOND, 0);
		ArrayList<String> recorverList = new ArrayList<String>();
		long time = calendar.getTimeInMillis();
		for (int i = 0; i < 10; i++) {
			try {
				String tableName = "table_kvdata0" + i;
				long s = System.currentTimeMillis();
				List<Map<String, Object>> list = template.queryForList("SELECT dbkey,dbvalue from " + tableName + " where type = 5 and dbkey like '100100%' and dbvalue like '%2016_11_1%'");
				long start = System.currentTimeMillis();
				System.out.println("查询耗时：" + (start - s));
				for (Map<String, Object> map : list) {
					String dbKey = (String) map.get("dbkey");
					String dbValue = (String) map.get("dbvalue");
					JSONObject json = JSON.parseObject(dbValue);
					Long lastUpdate = json.getLong("lastUpate");
					if (lastUpdate == null) {
						continue;
					}
					if (lastUpdate > time) {
						System.out.println("有问题角色：" + dbKey + "," + lastUpdate);
						recorverList.add(dbKey);
					}
				}
				System.out.println("获取完成：" + (System.currentTimeMillis() - start));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		GameLog.warn("修复签到数据", "", recorverList.toString());
		System.out.println(recorverList.size());
		TableSignDataDAO signDAO = TableSignDataDAO.getInstance();
		for (String userId : recorverList) {
			TableSignData signData = signDAO.get(userId);
			if (signData == null) {
				GameLog.warn("修复签到数据", "获取数据失败：", userId);
				continue;
			}
			GameLog.warn("修复签到数据", "修复数据成功：", signData.getUserId() + "," + fixCalendar);
			signData.setLastUpate(fixCalendar);
			signDAO.update(userId);
		}
		return null;
	}

}
