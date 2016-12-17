package com.rwbase.dao.chat.pojo.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.common.RefInt;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/**
 * @Author HC
 * @date 2016年12月17日 上午9:55:41
 * @desc 禁言时间配置DAO
 **/

public class TimeOfNotAllowedSpeechDAO extends CfgCsvDao<TimeOfNotAllowedSpeech> {

	public static TimeOfNotAllowedSpeechDAO getCfgDAO() {
		return SpringContextUtil.getBean(TimeOfNotAllowedSpeechDAO.class);
	}

	private TreeMap<Integer, List<TimeOfNotAllowedSpeech>> map;// 违规次数跟禁言时间的关联配置关系

	@Override
	protected Map<String, TimeOfNotAllowedSpeech> initJsonCfg() {
		Map<String, TimeOfNotAllowedSpeech> cfgMap = CfgCsvHelper.readCsv2Map("Chat/TimeOfNotAllowedSpeech.csv", TimeOfNotAllowedSpeech.class);

		if (cfgMap != null && !cfgMap.isEmpty()) {
			TreeMap<Integer, List<TimeOfNotAllowedSpeech>> map = new TreeMap<Integer, List<TimeOfNotAllowedSpeech>>();

			for (Entry<String, TimeOfNotAllowedSpeech> e : cfgMap.entrySet()) {
				TimeOfNotAllowedSpeech cfg = e.getValue();
				if (cfg == null) {
					continue;
				}

				int times = cfg.getTimes();
				List<TimeOfNotAllowedSpeech> list = map.get(times);
				if (list == null) {
					list = new ArrayList<TimeOfNotAllowedSpeech>();
					map.put(times, list);
				}

				list.add(cfg);
			}

			this.map = map;
			this.cfgCacheMap = cfgMap;
		}

		return cfgCacheMap;
	}

	/**
	 * 获取要禁言的时间
	 * 
	 * @param times 违规次数
	 * @param vipLevel vip等级
	 * @param outKey 返回触发的档次
	 * @return 返回0代表不禁言，返回-1代表永久性禁言
	 */
	public int getTimeOfNotAllowedSpeech(int times, int vipLevel, RefInt outKey) {
		if (map == null || map.isEmpty()) {
			return 0;
		}

		Entry<Integer, List<TimeOfNotAllowedSpeech>> floorEntry = map.floorEntry(times);
		if (floorEntry == null) {
			return 0;
		}

		List<TimeOfNotAllowedSpeech> value = floorEntry.getValue();
		if (value == null || value.isEmpty()) {
			return 0;
		}

		int lastVipLevelKey = -1;
		int time = 0;
		for (int i = value.size(); --i >= 0;) {
			TimeOfNotAllowedSpeech timeOfNotAllowedSpeech = value.get(i);
			int vipLevelKey = timeOfNotAllowedSpeech.getVipLevel();
			if (vipLevelKey > lastVipLevelKey && vipLevelKey <= vipLevel) {
				lastVipLevelKey = vipLevelKey;
				time = timeOfNotAllowedSpeech.getTime();
				if (outKey != null) {
					outKey.value = timeOfNotAllowedSpeech.getTimes();
				}
			}
		}

		return time;
	}
}