package com.rwbase.dao.chat.pojo.cfg;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/**
 * @Author HC
 * @date 2016年12月17日 下午12:15:04
 * @desc
 **/

public class NotAllowedSpeechTipCfgDAO extends CfgCsvDao<NotAllowedSpeechTipCfg> {

	public static NotAllowedSpeechTipCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(NotAllowedSpeechTipCfgDAO.class);
	}

	private TreeMap<Integer, String> map;// 违规次数跟禁言时间的关联配置关系

	@Override
	protected Map<String, NotAllowedSpeechTipCfg> initJsonCfg() {
		Map<String, NotAllowedSpeechTipCfg> cfgMap = CfgCsvHelper.readCsv2Map("Chat/NotAllowedSpeechTip.csv", NotAllowedSpeechTipCfg.class);

		if (cfgMap != null && !cfgMap.isEmpty()) {
			TreeMap<Integer, String> map = new TreeMap<Integer, String>();

			for (Entry<String, NotAllowedSpeechTipCfg> e : cfgMap.entrySet()) {
				NotAllowedSpeechTipCfg cfg = e.getValue();
				if (cfg == null) {
					continue;
				}

				map.put(cfg.getTime(), cfg.getTip());
			}

			this.map = map;
			this.cfgCacheMap = cfgMap;
		}

		return cfgCacheMap;
	}

	/**
	 * 获取禁言的提示
	 * 
	 * @param time
	 * @return
	 */
	public String getNotAllowedSpeechTip(int time) {
		if (map == null || map.isEmpty()) {
			return "";
		}

		Entry<Integer, String> floorEntry = map.floorEntry(time);
		if (floorEntry == null) {
			return "";
		}

		return floorEntry.getValue();
	}
}