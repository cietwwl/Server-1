package com.rwbase.dao.user;

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

public class UserIdCache {

	private final DataCache<UserParam, String> cache;

	public UserIdCache(DruidDataSource dataSource) {
		// 数据源名字需统一定义
		JdbcTemplate jdbcTemplate = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
		ClassInfo classInfo = new ClassInfo(User.class);
		CommonSingleTable<User> commonJdbc = new CommonSingleTable<User>(jdbcTemplate, classInfo);
		//数量需要做成配置
		int capcity = 5000;
		this.cache = DataCacheFactory.createDataDache(getClass().getSimpleName(), capcity, capcity, 120, new UserIdLoader(commonJdbc));
	}

	/**
	 * 通过账号+区 获取角色ID
	 * 
	 * @param accountId
	 * @param zoneId
	 * @return
	 */
	public String getUserId(String accountId, int zoneId) {
		try {
			return cache.getOrLoadFromDB(new UserParam(accountId, zoneId));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			SqlLog.error("get player id exception:" + accountId + "," + zoneId, e);
		}
		return null;
	}

	private class UserIdLoader implements PersistentLoader<UserParam, String> {

		private final CommonSingleTable<User> commonJdbc;
		private final String sql;

		UserIdLoader(CommonSingleTable<User> commonJdbc) {
			this.commonJdbc = commonJdbc;
			this.sql = "select userId from user where account =? and zoneId =?";
		}

		@Override
		public String load(UserParam key) throws DataNotExistException, Exception {
			return commonJdbc.queryForObject(sql, new Object[] { key.accountId, key.zoneId }, String.class);
		}

		@Override
		public boolean delete(UserParam key) throws DataNotExistException, Exception {
			return false;
		}

		@Override
		public boolean insert(UserParam key, String value) throws DuplicatedKeyException, Exception {
			return false;
		}

		@Override
		public boolean updateToDB(UserParam key, String value) {
			return false;
		}

	}

	static class UserParam {
		final String accountId;
		final int zoneId;
		final int hash;

		UserParam(String accountId, int zoneId) {
			this.accountId = accountId;
			this.zoneId = zoneId;
			final int prime = 31;
			int result = 1;
			result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
			result = prime * result + zoneId;
			this.hash = result;
		}

		@Override
		public int hashCode() {
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			UserParam other = (UserParam) obj;
			if (accountId == null) {
				if (other.accountId != null)
					return false;
			} else if (!accountId.equals(other.accountId))
				return false;
			if (zoneId != other.zoneId)
				return false;
			return true;
		}
	}

}
