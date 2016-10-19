package com.rw.fsutil.dao.optimize;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.InterruptibleBatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Statement;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.common.CommonRowMapper;
import com.rw.fsutil.dao.common.JdbcTemplateFactory;
import com.rw.fsutil.util.SpringContextUtil;

/**
 * <pre>
 * 数据访问的简单支持类
 * 结合游戏中常用逻辑，通过封装Spring的方法实现
 * </pre>
 * 
 * @author Jamaz
 *
 */
public class DataAccessSimpleSupport {

	private final PlatformTransactionManager tm;
	private final DefaultTransactionDefinition df;
	private final JdbcTemplate template;
	private final int[] charMapper;
	private final int[] emptyIntResult;

	public DataAccessSimpleSupport(String dsName) {
		// 初始化事务相关
		DruidDataSource dataSource = SpringContextUtil.getBean(dsName);
		if (dataSource == null) {
			throw new ExceptionInInitializerError("find dataSource fail:" + dsName);
		}
		this.emptyIntResult = new int[0];
		this.template = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
		this.tm = new DataSourceTransactionManager(dataSource);
		this.df = new DefaultTransactionDefinition();
		this.df.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		// 初始化
		char[] array = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		TreeSet<Character> set = new TreeSet<Character>();
		for (int i = 0; i < array.length; i++) {
			set.add(Character.toUpperCase(array[i]));
			set.add(Character.toLowerCase(array[i]));
		}
		int max = set.last() + 1;
		this.charMapper = new int[max];
		for (int i = 0; i < charMapper.length; i++) {
			charMapper[i] = -1;
		}
		for (Character ch : set) {
			charMapper[ch] = Character.digit(ch, 16);
		}
	}

	public JdbcTemplate getMainTemplate() {
		return template;
	}

	/**
	 * <pre>
	 * 插入一条记录
	 * 返回由数据库生成的主键
	 * </pre>
	 * 
	 * @param sql
	 * @param setter
	 * @return
	 * @throws DuplicatedKeyException
	 * @throws Exception
	 */
	public long insert(final String sql, final PreparedStatementSetter setter) throws DuplicatedKeyException, Exception {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			int result = template.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
					setter.setValues(ps);
					return ps;
				}
			}, keyHolder);
			if (result < 0) {
				throw new RuntimeException("insert affect 0 row:" + sql);
			}
			long id = keyHolder.getKey().longValue();
			if (id <= 0) {
				throw new RuntimeException("insert generate id fail:" + sql + ",affect rows:" + result);
			}
			return id;
		} catch (DuplicateKeyException e) {
			throw new DuplicatedKeyException(e);
		}
	}

	/**
	 * <pre
	 * 批量插入并返回数据库生成的主键
	 * 只支持<li>insert into</li>格式 
	 * 不支持{@link InterruptibleBatchPreparedStatementSetter}方式限制batchSize
	 * 通过事务保证全部成功或者全部失败，如存在主键重复，则所有记录插入失败
	 * </pre>
	 * 
	 * @param template
	 * @param sql
	 * @param pss
	 * @return
	 */
	public long[] batchInsert(final String sql, final BatchPreparedStatementSetter pss) throws Exception {
		TransactionStatus ts = tm.getTransaction(df);
		try {
			long[] keys = template.execute(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					return con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				}
			}, new GeneratedKeysCallback(pss));
			tm.commit(ts);
			return keys;
		} catch (Exception e) {
			tm.rollback(ts);
			throw e;
		}
	}

	static class GeneratedKeysCallback implements PreparedStatementCallback<long[]> {

		private final BatchPreparedStatementSetter pss;

		public GeneratedKeysCallback(BatchPreparedStatementSetter pss) {
			this.pss = pss;
		}

		@Override
		public long[] doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
			try {
				int batchSize = pss.getBatchSize();
				long[] generatedKeys = new long[batchSize];
				if (JdbcUtils.supportsBatchUpdates(ps.getConnection())) {
					for (int i = 0; i < batchSize; i++) {
						pss.setValues(ps, i);
						ps.addBatch();
					}
					ps.executeBatch();
				} else {
					List<Integer> rowsAffected = new ArrayList<Integer>();
					for (int i = 0; i < batchSize; i++) {
						pss.setValues(ps, i);
						int result = ps.executeUpdate();
						rowsAffected.add(result);
					}

				}
				ResultSet rs = ps.getGeneratedKeys();
				int index = 0;
				while (rs.next()) {
					generatedKeys[index++] = rs.getLong(1);
				}
				return generatedKeys;
			} finally {
				if (pss instanceof ParameterDisposer) {
					((ParameterDisposer) pss).cleanupParameters();
				}
			}
		}
	}

	/**
	 * <pre>
	 * 批量插入和删除，如果执行成功，返回插入记录由数据库生成的主键
	 * 事务原子性保证插入和删除的记录全部成功或全部失败，抛出异常表示全部失败
	 * </pre>
	 * 
	 * @param insertSql
	 * @param insertBps
	 * @param deleteSql
	 * @param deleteKeys
	 * @return
	 * @throws DataNotExistException
	 * @throws Exception
	 */
	public long[] insertAndDelete(final String insertSql, final BatchPreparedStatementSetter insertBps, String deleteSql, long[] deleteKeys) throws DataNotExistException, Exception {
		String recordNotExist = null;
		TransactionStatus ts = tm.getTransaction(df);
		try {
			long[] keys = template.execute(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					return con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
				}
			}, new GeneratedKeysCallback(insertBps));

			int[] result = batchDelete(deleteSql, deleteKeys);
			for (int i = result.length; --i >= 0;) {
				if (result[i] <= 0) {
					tm.rollback(ts);
					recordNotExist = "delete fail cause by not exist:" + deleteKeys[i];
					break;
				}
			}
			if (recordNotExist == null) {
				tm.commit(ts);
				return keys;
			}
		} catch (Exception t) {
			tm.rollback(ts);
			throw t;
		}
		throw new DataNotExistException(recordNotExist);
	}

	// 批量删除long类型的记录
	private int[] batchDelete(String sql, final long[] deleteKeys) {
		return this.template.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setLong(1, deleteKeys[i]);
			}

			@Override
			public int getBatchSize() {
				return deleteKeys.length;
			}
		});
	}

	/**
	 * <pre>
	 * 执行多个指定id的delete操作，要么全部成功，返回true，要么全部失败，返回false
	 * 此方法中的对象是由逻辑自己生成主键，即泛型K
	 * </pre>
	 * 
	 * @param sql
	 * @param idList
	 * @return
	 * @throws Exception
	 */
	public <K extends Object> boolean forceDelete(String sql, final List<K> idList) throws Exception {
		if (idList.isEmpty()) {
			return false;
		}
		String recordNotExist = null;
		TransactionStatus ts = tm.getTransaction(df);
		try {
			int[] result = batchDelete(sql, idList);
			for (int i = result.length; --i >= 0;) {
				if (result[i] <= 0) {
					tm.rollback(ts);
					recordNotExist = "item not exist:" + idList.get(i);
					break;
				}
			}
			// 全部成功
			if (recordNotExist == null) {
				tm.commit(ts);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			tm.rollback(ts);
			return false;
		}
		throw new DataNotExistException(recordNotExist);
	}

	/**
	 * <pre>
	 * 执行多个指定id的delete操作，返回删除成功的id列表
	 * 此方法中的对象是由逻辑自己生成主键，即泛型K
	 * </pre>
	 * 
	 * @param sql
	 * @param idList
	 * @return
	 * @throws Exception
	 */
	public <K extends Object> List<K> delete(String sql, final List<K> idList) throws Exception {
		final int size = idList.size();
		if (size == 0) {
			return Collections.emptyList();
		}
		try {
			int[] result = batchDelete(sql, idList);
			ArrayList<K> resultList = new ArrayList<K>(size);
			for (int i = 0; i < result.length; i++) {
				if (result[i] > 0) {
					resultList.add(idList.get(i));
				}
			}
			return resultList;
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	private int[] batchDelete(String sql, final List<? extends Object> idList) throws Exception {
		int[] result = this.template.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Object id = idList.get(i);
				ps.setObject(1, id);
			}

			@Override
			public int getBatchSize() {
				return idList.size();
			}
		});
		// 按语义不会出现null，多加个判断保证不受JDBC or Spring影响
		if (result == null) {
			FSUtilLogger.error("batch update return null:" + sql + "," + idList);
			return new int[] { idList.size() };
		} else {
			return result;
		}
	}

	/**
	 * <pre>
	 * 执行批量添加和删除操作，要么全部成功，要么全部失败
	 * 此方法中的对象是由逻辑自己生成主键,即泛型K
	 * </pre>
	 * 
	 * @param addSql 执行添加的sql语句
	 * @param addList 添加列表
	 * @param delSql 执行删除的sql语句
	 * @param delList 删除列表
	 * @return
	 */
	public <K, T> boolean insertAndDelete(ClassInfo classInfo, String addSql, List<T> addList, String delSql, List<K> delList) throws DuplicatedKeyException, DataNotExistException {
		if (delList.isEmpty()) {
			//删除列表为空转换成insert
			try {
				insert(classInfo, addSql, addList);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		String itemNotExist = null;
		TransactionStatus ts = tm.getTransaction(df);
		try {
			insert(classInfo, addSql, addList);
			int[] result = batchDelete(delSql, delList);
			for (int i = result.length; --i >= 0;) {
				if (result[i] <= 0) {
					tm.rollback(ts);
					itemNotExist = "item not exist:" + delList.get(i);
					break;
				}
			}
			if (itemNotExist == null) {
				tm.commit(ts);
				return true;
			}
		} catch (DuplicatedKeyException e) {
			tm.rollback(ts);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			tm.rollback(ts);
			return false;
		}
		throw new DataNotExistException(itemNotExist);
	}

	/**
	 * <pre>
	 * 插入多条记录
	 * 此方法中的对象是由逻辑自己生成主键
	 * </pre>
	 * 
	 * @param sql
	 * @param list
	 * @throws DuplicatedKeyException
	 * @throws Exception
	 */
	public <T> void insert(ClassInfo classInfo, String sql, final List<T> list) throws Exception {
		final int size = list.size();
		if (size == 0) {
			return;
		}
		final ArrayList<List<Object>> fieldValues = new ArrayList<List<Object>>(); // 字段值
		for (int i = 0; i < size; i++) {
			T t = list.get(i);
			fieldValues.add(classInfo.extractInsertAttributes(t));
		}
		try {
			this.template.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					List<Object> sqlContext = fieldValues.get(i);
					int index = 0;
					for (Object param : sqlContext) {
						ps.setObject(++index, param);
					}
				}

				@Override
				public int getBatchSize() {
					return size;
				}
			});
		} catch (DuplicateKeyException e) {
			throw new DuplicatedKeyException(e);
		}
	}

	/**
	 * <pre>
	 * 插入单条记录
	 * 此方法中的对象是由逻辑自己生成主键，即泛型K
	 * </pre>
	 * 
	 * @param sql
	 * @param key
	 * @param target
	 * @return
	 * @throws DuplicatedKeyException
	 * @throws Exception
	 */
	public <K, T> boolean insert(final ClassInfo classInfo, final String sql, K key, T target) throws DuplicatedKeyException, Exception {
		final List<Object> fieldValues = classInfo.extractInsertAttributes(target);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			int result = template.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, new String[] { classInfo.getPrimaryKey() });
					int index = 0;
					for (Object param : fieldValues) {
						index++;
						ps.setObject(index, param);
					}
					return ps;
				}
			}, keyHolder);
			return result > 0;
		} catch (DuplicateKeyException e) {
			throw new DuplicatedKeyException(e);
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}

	public <T> List<T> queryForList(ClassInfo classInfo, String sql, Object[] params, Object ownerId) {
		List<T> resultList = template.query(sql, new CommonRowMapper<T>(classInfo, ownerId), params);
		return resultList;
	}

	/**
	 * <pre>
	 * 执行SQL语句的更新操作
	 * </pre>
	 * 
	 * @param sql
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public <K> int update(String sql, K id) throws Exception {
		return template.update(sql, id);
	}

	/**
	 * <pre>
	 * 更新多条记录，忽略数据库中是否真的更新成功
	 * 抛出异常表示更新失败
	 * 此方法中的对象是由逻辑自己生成主键，即泛型K
	 * </pre>
	 * 
	 * @param sql
	 * @param map
	 * @return
	 */
	public <K, T> boolean updateToDB(ClassInfo classInfo, String sql, Map<K, T> map) throws Exception {
		try {
			final int size = map.size();
			final ArrayList<List<Object>> fieldValues = new ArrayList<List<Object>>(); // 字段值
			for (Map.Entry<K, T> entry : map.entrySet()) {
				K key = entry.getKey();
				T target = entry.getValue();
				List<Object> fieldValueList = classInfo.extractUpdateAttributes(target);
				fieldValueList.add(key);
				fieldValues.add(fieldValueList);
			}
			template.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					List<Object> list = fieldValues.get(i);
					int len = list.size();
					for (int j = 0; j < len; j++) {
						Object param = list.get(j);
						ps.setObject(j + 1, param);
					}
				}

				@Override
				public int getBatchSize() {
					return size;
				}
			});
			return true;
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}

	/**
	 * <pre>
	 * 更新单条记录
	 * 此方法中的对象是由逻辑自己生成主键，即泛型K
	 * </pre>
	 * 
	 * @param sql
	 * @param key
	 * @param target
	 * @return
	 */
	public <K, T> boolean updateToDB(ClassInfo classInfo, String sql, K key, T target) throws Exception {
		try {
			final List<Object> fieldValues = classInfo.extractUpdateAttributes(target);
			fieldValues.add(key);
			template.update(sql, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int size = fieldValues.size();
					for (int i = 0; i < size; i++) {
						ps.setObject(i + 1, fieldValues.get(i));
					}
				}
			});
			return true;
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}

	public int getTableIndex_(String userId, int tableCount) {
		char lastChar = userId.charAt(userId.length() - 1);
		int index = lastChar;
		if (index < charMapper.length) {
			int value = charMapper[index];
			if (value >= 0) {
				if (value < tableCount) {
					return value;
				} else {
					return value % tableCount;
				}
			}
		}
		return Math.abs(userId.hashCode() % tableCount);
	}

	public int getTableIndex(String userId, int tableCount) {
		// 兼容旧数据，如果清数据可以直接调用上面的方法
		if (userId.length() == 12 || tableCount == 16) {
			return getTableIndex_(userId, tableCount);
		}
		boolean isNumber = true;
		int len = userId.length();
		for (int i = 0; i < len; i++) {
			char c = userId.charAt(i);
			if (!Character.isDigit(c)) {
				isNumber = false;
				break;
			}
		}
		int tableIndex;
		if (isNumber) {
			Long id = Long.parseLong(userId);
			tableIndex = (int) (id % tableCount);
		} else {
			tableIndex = Math.abs(userId.hashCode() % tableCount);
		}
		return tableIndex;
	}

	@Deprecated
	public <T> List<T> findByKey(ClassInfo classInfo, String tableName, String keyName, Object value) throws Exception {
		// 获得表名
		String sql = "select * from " + tableName + " where " + keyName + "=?";
		List<T> resultList = template.query(sql, new CommonRowMapper<T>(classInfo, value), value);
		return resultList;
	}

}
