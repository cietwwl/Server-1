package com.rwbase.dao.chat.pojo.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/**
 * @Author HC
 * @date 2016年12月17日 上午9:55:20
 * @desc 聊天违规配置的DAO
 **/

public class ChatIllegalCfgDAO extends CfgCsvDao<ChatIllegalCfg> {

	public static ChatIllegalCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(ChatIllegalCfgDAO.class);
	}

	private TreeMap<Integer, List<ChatIllegalCfg>> map;

	@Override
	protected Map<String, ChatIllegalCfg> initJsonCfg() {
		Map<String, ChatIllegalCfg> cfgMap = CfgCsvHelper.readCsv2Map("Chat/ChatIllegalCfg.csv", ChatIllegalCfg.class);

		if (cfgMap != null && !cfgMap.isEmpty()) {
			TreeMap<Integer, List<ChatIllegalCfg>> map = new TreeMap<Integer, List<ChatIllegalCfg>>();

			for (Entry<String, ChatIllegalCfg> e : cfgMap.entrySet()) {
				ChatIllegalCfg cfg = e.getValue();
				if (cfg == null) {
					continue;
				}

				int level = cfg.getLevel();
				List<ChatIllegalCfg> list = map.get(level);
				if (list == null) {
					list = new ArrayList<ChatIllegalCfg>();
					map.put(level, list);
				}

				list.add(cfg);
			}

			this.map = map;
			this.cfgCacheMap = cfgMap;
		}

		return cfgCacheMap;
	}

	/**
	 * 获取聊天违规配置
	 * 
	 * @param level
	 * @param vipLevel
	 * @return 返回null，说明这个等级段不能发言
	 */
	public ChatIllegalCfg getChatIllegalCfg(int level, int vipLevel) {
		if (map == null || map.isEmpty()) {
			return null;
		}

		Entry<Integer, List<ChatIllegalCfg>> floorEntry = map.floorEntry(level);
		if (floorEntry == null) {
			return null;
		}

		List<ChatIllegalCfg> value = floorEntry.getValue();
		if (value == null || value.isEmpty()) {
			return null;
		}

		int lastVipLevelKey = -1;
		ChatIllegalCfg lastCfg = null;

		for (int i = value.size(); --i >= 0;) {
			ChatIllegalCfg chatViolationCfg = value.get(i);
			int vipLevelKey = chatViolationCfg.getVipLevel();
			if (vipLevelKey > lastVipLevelKey && vipLevelKey <= vipLevel) {
				lastVipLevelKey = vipLevelKey;
				lastCfg = chatViolationCfg;
			}
		}

		return lastCfg;
	}
}