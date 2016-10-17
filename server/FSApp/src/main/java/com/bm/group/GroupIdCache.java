package com.bm.group;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.dao.cache.DataKVCache;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.common.CommonSingleTable;
import com.rw.fsutil.dao.common.JdbcTemplateFactory;
import com.rw.fsutil.dao.optimize.SimpleLoader;
import com.rw.fsutil.log.SqlLog;
import com.rwbase.dao.group.pojo.db.GroupBaseData;

public class GroupIdCache {
	private final DataKVCache<String, String> cache;

	public GroupIdCache(String dsName,DruidDataSource dataSource) {
		JdbcTemplate jdbcTemplate = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
		ClassInfo classInfo = new ClassInfo(GroupBaseData.class);
		CommonSingleTable<GroupBaseData> commonJdbc = new CommonSingleTable<GroupBaseData>(dsName,jdbcTemplate, classInfo);
		int capcity = 5000;
		this.cache = DataCacheFactory.createDataKVCache(getClass(), capcity, 120, new GroupIdLoader(commonJdbc));
	}

	public String getGroupId(String groupName) {
		try {
			return cache.getOrLoadFromDB(groupName);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			SqlLog.error("get group id exception:" + groupName, e);
		}
		return null;
	}

	private class GroupIdLoader extends SimpleLoader<String, String> {

		private final CommonSingleTable<GroupBaseData> commonJdbc;
		private final String sql;

		public GroupIdLoader(CommonSingleTable<GroupBaseData> commonJdbc) {
			this.commonJdbc = commonJdbc;
			this.sql = "select id from group_data where groupName = ?";
		}

		@Override
		public String load(String key) throws DataNotExistException, Exception {
			return commonJdbc.queryForObject(sql, new Object[] { key }, String.class);
		}

	}
}
