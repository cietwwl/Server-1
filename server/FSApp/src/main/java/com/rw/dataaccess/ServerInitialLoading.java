package com.rw.dataaccess;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.playerdata.PlayerMgr;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.manager.GameManager;
import com.rw.manager.ServerPerformanceConfig;

public class ServerInitialLoading {

	private static boolean init;

	public static synchronized void preLoadPlayers() {
		if (init) {
			return;
		}
		init = true;
		ServerPerformanceConfig config = GameManager.getPerformanceConfig();
		int latestLoginCount = config.getLatestLoginCount();
		int highLevelCount = config.getHighLevelCount();
		JdbcTemplate template = DataAccessFactory.getSimpleSupport().getMainTemplate();
		List<Map<String, Object>> lastLoginPlayers = template.queryForList("select userId from user ORDER BY lastLoginTime DESC limit " + latestLoginCount);
		List<Map<String, Object>> highLevelPlayers = template.queryForList("SELECT id from hero where hero_type = 1 ORDER BY level DESC limit " + highLevelCount);
		HashSet<String> set = new HashSet<String>();
		fill(set, lastLoginPlayers, "userId");
		fill(set, highLevelPlayers, "id");
		PlayerMgr playerMgr = PlayerMgr.getInstance();
		System.out.println("preload size:" + set.size());
		long start = System.currentTimeMillis();
		for (String id : set) {
			try {
				playerMgr.find(id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("preload completed:" + (System.currentTimeMillis() - start));
	}

	private static void fill(HashSet<String> set, List<Map<String, Object>> list, String name) {
		for (Map<String, Object> map : list) {
			String userId = (String) map.get(name);
			if (userId != null) {
				set.add(userId);
			}
		}
	}
}
