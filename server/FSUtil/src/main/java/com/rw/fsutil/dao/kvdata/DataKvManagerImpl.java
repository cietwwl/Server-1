package com.rw.fsutil.dao.kvdata;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.DataAccessStaticSupport;
import com.rw.fsutil.util.SpringContextUtil;

public class DataKvManagerImpl implements DataKvManager {

	private JdbcTemplate jdbcTemplate;
	private PlatformTransactionManager tm;
	private DefaultTransactionDefinition df;
	private final String[] selectSqlArray;
	private final String[] selectAllSqlArray;
	private final String[] delectSqlArray;
	private final String[] updateSqlArray;
	private final String[] insertSqlArray;
	private final String[] checkSelectArray;
	private final int length;
	private final HashMap<Class<? extends DataKVDao<?>>, Integer> dataKvMap;
	private final HashMap<Class<? extends DataKVDao<?>>, DataExtensionCreator<?>> creatorMap;
	private final DataKvRowMapper rowMapper = new DataKvRowMapper();
	private final int dataKvCapacity;

	public DataKvManagerImpl(Map<Integer, Class<? extends DataKVDao<?>>> map, Map<Class<? extends DataKVDao<?>>, DataExtensionCreator<?>> extensionMap, int dataKvCapacity) {
		DruidDataSource dataSource = SpringContextUtil.getBean("dataSourceMT");
		if (dataSource == null) {
			throw new ExceptionInInitializerError("Ranking dataSource is null");
		}
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataKvCapacity = dataKvCapacity;
		// 初始化事务相关
		tm = new DataSourceTransactionManager(dataSource);
		df = new DefaultTransactionDefinition();
		df.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		List<String> tableNameList = DataAccessStaticSupport.getDataKVTableNameList(jdbcTemplate);
		this.length = tableNameList.size();
		this.selectAllSqlArray = new String[this.length];
		this.selectSqlArray = new String[this.length];
		this.delectSqlArray = new String[this.length];
		this.updateSqlArray = new String[this.length];
		this.insertSqlArray = new String[this.length];
		this.checkSelectArray = new String[this.length];
		for (int i = 0; i < this.length; i++) {
			String tableName = tableNameList.get(i);
			selectAllSqlArray[i] = "select dbkey,dbvalue,type from " + tableName + " where dbkey=?";
			selectSqlArray[i] = "select dbvalue from " + tableName + " where dbkey=? and type=?";
			delectSqlArray[i] = "delete from " + tableName + " where dbkey=? and type=?";
			updateSqlArray[i] = "update " + tableName + " set dbvalue=? where dbkey=? and type=?";
			insertSqlArray[i] = "insert into " + tableName + "(dbkey,dbvalue,type) values(?,?,?)";
			checkSelectArray[i] = "select count(1) from " + tableName + " where dbkey=?";
		}
		dataKvMap = new HashMap<Class<? extends DataKVDao<?>>, Integer>();
		for (Map.Entry<Integer, Class<? extends DataKVDao<?>>> entry : map.entrySet()) {
			Class<? extends DataKVDao<?>> daoClass = entry.getValue();
			if (daoClass == null) {
				throw new ExceptionInInitializerError("DataKVDao class is null");
			}
			dataKvMap.put(daoClass, entry.getKey());
		}
		creatorMap = new HashMap<Class<? extends DataKVDao<?>>, DataExtensionCreator<?>>();
		for (Map.Entry<Class<? extends DataKVDao<?>>, DataExtensionCreator<?>> entry : extensionMap.entrySet()) {
			Class<? extends DataKVDao<?>> daoClass = entry.getKey();
			if (daoClass == null) {
				throw new ExceptionInInitializerError("DataKVDao class is null");
			}
			if (!dataKvMap.containsKey(daoClass)) {
				throw new ExceptionInInitializerError("DataKVDao class is not exist:" + daoClass);
			}
			creatorMap.put(daoClass, entry.getValue());
		}

	}

	public String[] getSelectSqlArray() {
		return getStringArrayCopy(selectSqlArray);
	}

	public String[] getDeleteSqlArray() {
		return getStringArrayCopy(delectSqlArray);
	}

	public String[] getUpdateSqlArray() {
		return getStringArrayCopy(updateSqlArray);
	}

	public String[] getInsertSqlArray() {
		return getStringArrayCopy(insertSqlArray);
	}

	public String[] getCheckExistArray() {
		return getStringArrayCopy(checkSelectArray);
	}

	public int getDataKvCapacity() {
		return dataKvCapacity;
	}

	private String[] getStringArrayCopy(String[] ordinalArray) {
		String[] copy = new String[ordinalArray.length];
		System.arraycopy(ordinalArray, 0, copy, 0, ordinalArray.length);
		return copy;
	}

	@Override
	public Integer getDataKvType(Class<? extends DataKVDao<?>> clazz) {
		return dataKvMap.get(clazz);
	}

	public void batchInsert(String userId, final List<? extends DataKvEntity> list) throws Throwable {
		int tableIndex = DataAccessFactory.getSimpleSupport().getTableIndex(userId, length);
		String sql = insertSqlArray[tableIndex];
		TransactionStatus ts = tm.getTransaction(df);
		try {
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement pstmt, int i) throws SQLException {
					DataKvEntity entityData = list.get(i);
					pstmt.setString(1, entityData.getUserId());
					pstmt.setString(2, entityData.getValue());
					pstmt.setInt(3, entityData.getType());
				}

				@Override
				public int getBatchSize() {
					return list.size();
				}
			});
			tm.commit(ts);
		} catch (Throwable t) {
			tm.rollback(ts);
			throw t;
		}
	}

	public boolean insert(String userId, DataKvEntity entity) {
		int tableIndex = DataAccessFactory.getSimpleSupport().getTableIndex(userId, length);
		String sql = insertSqlArray[tableIndex];
		int result = jdbcTemplate.update(sql, new Object[] { entity.getUserId(), entity.getValue(), entity.getType() });
		return result > 0;
	}

	public int getDataKVRecordCount(String userId) {
		int tableIndex = DataAccessFactory.getSimpleSupport().getTableIndex(userId, length);
		String sql = checkSelectArray[tableIndex];
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class,userId);
		return count == null ? 0 : count;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> DataExtensionCreator<T> getCreator(Class<? extends DataKVDao<T>> clazz) {
		return (DataExtensionCreator<T>) creatorMap.get(clazz);
	}

	@Override
	public List<DataKvEntity> getAllDataKvEntitys(String userId) {
		int tableIndex = DataAccessFactory.getSimpleSupport().getTableIndex(userId, length);
		String sql = selectAllSqlArray[tableIndex];
		return jdbcTemplate.query(sql, rowMapper, userId);
	}

}
