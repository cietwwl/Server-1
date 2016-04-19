package com.rw.fsutil.dao.kvdata;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
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
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.DataAccessStaticSupport;
import com.rw.fsutil.util.SpringContextUtil;

public class DataKvManagerImpl implements DataKvManager {

	private JdbcTemplate jdbcTemplate;
	private PlatformTransactionManager tm;
	private DefaultTransactionDefinition df;
	private final String[] selectSqlArray;
	private final String[] delectSqlArray;
	private final String[] updateSqlArray;
	private final String[] insertSqlArray;
	private final int length;
	private final HashMap<Class<? extends DataKVDao>, Integer> dataKvMap;
	private final int dataKvCapacity;

	public DataKvManagerImpl(Map<Integer, Class<? extends DataKVDao>> map, int dataKvCapacity) {
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
		this.selectSqlArray = new String[this.length];
		this.delectSqlArray = new String[this.length];
		this.updateSqlArray = new String[this.length];
		this.insertSqlArray = new String[this.length];
		for (int i = 0; i < this.length; i++) {
			String tableName = tableNameList.get(i);
			selectSqlArray[i] = "select dbvalue from " + tableName + " where dbkey=? and type=?";
			delectSqlArray[i] = "delete from " + tableName + " where dbkey=? and type=?";
			updateSqlArray[i] = "update " + tableName + " set dbvalue=? where dbkey=? and type=?";
			insertSqlArray[i] = "insert into " + tableName + "(dbkey,dbvalue,type) values(?,?,?)";
		}
		dataKvMap = new HashMap<Class<? extends DataKVDao>, Integer>();
		for (Map.Entry<Integer, Class<? extends DataKVDao>> entry : map.entrySet()) {
			dataKvMap.put(entry.getValue(), entry.getKey());
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

	public int getDataKvCapacity() {
		return dataKvCapacity;
	}

	private String[] getStringArrayCopy(String[] ordinalArray) {
		String[] copy = new String[ordinalArray.length];
		System.arraycopy(ordinalArray, 0, copy, 0, ordinalArray.length);
		return copy;
	}

	@Override
	public Integer getDataKvType(Class<? extends DataKVDao> clazz) {
		return dataKvMap.get(clazz);
	}

	public void batchInsert(String userId, final List<? extends DataKvEntity> list) throws Throwable {
		int tableIndex = DataAccessFactory.getSimpleSupport().getTableIndex(userId, length);
		// String[] tableName =
		// DataAccessFactory.getSimpleSupport().getKVTableName();
		// StringBuilder sb = new StringBuilder(4000);
		// sb.append("insert into ").append(tableName[tableIndex]).append("(dbkey,dbvalue,type) values");
		// int size = list.size();
		// for (int i = 0; i < size; i++) {
		// DataKvEntity entity = list.get(i);
		// sb.append("('").append(entity.getUserId()).append("','").append(entity.getValue()).append("',").append(entity.getType()).append(")");
		// if((i+1) < size){
		// sb.append(",");
		// }
		// }
		// String sql = sb.toString();
		// jdbcTemplate.update(sql);
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

}
