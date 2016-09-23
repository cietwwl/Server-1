package com.bm.group;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataCache;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.PersistentLoader;
import com.rw.fsutil.dao.common.CommonSingleTable;
import com.rw.fsutil.dao.common.JdbcTemplateFactory;
import com.rw.fsutil.log.SqlLog;
import com.rwbase.dao.group.pojo.db.GroupBaseData;

public class GroupIdCache {
	private final DataCache<String, String> cache;

	public GroupIdCache(DruidDataSource dataSource) {
		JdbcTemplate jdbcTemplate = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
		ClassInfo classInfo = new ClassInfo(GroupBaseData.class);
		CommonSingleTable<GroupBaseData> commonJdbc = new CommonSingleTable<GroupBaseData>(jdbcTemplate, classInfo);
		int capcity = 5000;
		this.cache = DataCacheFactory.createDataDache(getClass(), capcity, capcity, 120, new GroupIdLoader(commonJdbc));
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

	private class GroupIdLoader implements PersistentLoader<String, String> {

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

		@Override
		public boolean delete(String key) throws DataNotExistException, Exception {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean insert(String key, String value) throws DuplicatedKeyException, Exception {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean updateToDB(String key, String value) {
			// TODO Auto-generated method stub
			return false;
		}

	}
}
