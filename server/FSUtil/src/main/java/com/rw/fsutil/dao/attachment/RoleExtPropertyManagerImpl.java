package com.rw.fsutil.dao.attachment;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.DataAccessStaticSupport;
import com.rw.fsutil.util.SpringContextUtil;

public class RoleExtPropertyManagerImpl implements RoleExtPropertyManager {

	private JdbcTemplate template;
	private final String[] selectAllArray;
	private final String[] selectRangeArray;
	private final String[] selectArray;
	private final String[] updateArray;
	private final String[] insertArray;
	private final String[] tableNameArray;
	private final String[] deleteArray;
	private final int tableSize;

	public RoleExtPropertyManagerImpl(String dsName, String name) {
		DruidDataSource dataSource = SpringContextUtil.getBean(dsName);
		if (dataSource == null) {
			throw new ExceptionInInitializerError("Ranking dataSource is null");
		}
		this.template = new JdbcTemplate(dataSource);
		List<String> tableNameList = DataAccessStaticSupport.getTableNameList(template, name);
		this.tableSize = tableNameList.size();
		this.selectRangeArray = new String[tableSize];
		this.selectAllArray = new String[tableSize];
		this.updateArray = new String[tableSize];
		this.selectArray = new String[tableSize];
		this.insertArray = new String[tableSize];
		this.deleteArray = new String[tableSize];
		this.tableNameArray = new String[tableSize];
		for (int i = 0; i < tableSize; i++) {
			String tableName = tableNameList.get(i);
			this.selectRangeArray[i] = "select id,type,sub_type,extention from " + tableName + " where owner_id=? and type in(";
			this.updateArray[i] = "update " + tableName + " set extention=? where id=?";
			this.selectArray[i] = "select id,sub_type,extention from " + tableName + " where owner_id=? and type=?";
			this.insertArray[i] = "insert into " + tableName + " (owner_id,type,sub_type,extention) values(?,?,?,?)";
			this.deleteArray[i] = "delete from " + tableName + " where id=?";
			this.selectAllArray[i] = "select id,sub_type,extention from " + tableName + " where owner_id=?";
			this.tableNameArray[i] = tableName;
		}
	}

	public long insert(final String ownerId, final InsertRoleExtPropertyData entry) throws DuplicatedKeyException, Exception {
		int index = DataAccessFactory.getSimpleSupport().getTableIndex(ownerId, tableSize);
		final String sql = insertArray[index];
		return DataAccessFactory.getSimpleSupport().insert(sql, entry);
	}

	public long[] insert(final String ownerId, final List<? extends InsertRoleExtPropertyData> list) throws Exception {
		int index = DataAccessFactory.getSimpleSupport().getTableIndex(ownerId, tableSize);
		final String sql = insertArray[index];
		final BatchPreparedStatementSetter pss = new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				InsertRoleExtPropertyData entity = list.get(i);
				entity.setValues(ps);
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		};

		return DataAccessFactory.getSimpleSupport().batchInsert(sql, pss);
	}

	public List<QueryRoleExtPropertyData> loadEntitys(String ownerId, Short type) {
		int index = DataAccessFactory.getSimpleSupport().getTableIndex(ownerId, tableSize);
		return template.query(selectArray[index], new RoleExtPropertySingleMapper(ownerId, type), ownerId, type);
	}

	@Override
	public List<QueryRoleExtPropertyData> loadAllEntitys(String ownerId) {
		int index = DataAccessFactory.getSimpleSupport().getTableIndex(ownerId, tableSize);
		return template.query(this.selectAllArray[index], new Object[] { ownerId }, new RoleExtPropertyMapper(ownerId));
	}

	public boolean updateAttachmentExtention(String ownerId, String extention, Long id) {
		int index = DataAccessFactory.getSimpleSupport().getTableIndex(ownerId, tableSize);
		return template.update(this.updateArray[index], extention, id) > 0;
	}

	public List<QueryRoleExtPropertyData> loadRangeEntitys(String ownerId, List<Short> typeList) {
		if (typeList.isEmpty()) {
			return Collections.emptyList();
		}
		int index = DataAccessFactory.getSimpleSupport().getTableIndex(ownerId, tableSize);
		Object[] params = new Object[typeList.size() + 1];
		params[0] = ownerId;
		String partialSql = this.selectRangeArray[index];
		StringBuilder sb = new StringBuilder(partialSql.length() + typeList.size() * 3 + 1);
		sb.append(partialSql);
		DataAccessStaticSupport.fillHolders(sb, typeList, params, 1);
		return template.query(sb.toString(), params, new RoleExtPropertyMapper(ownerId));
	}

	@Override
	public String getTableName(String ownerId) {
		int index = DataAccessFactory.getSimpleSupport().getTableIndex(ownerId, tableSize);
		return this.tableNameArray[index];
	}

	@Override
	public Map<String, String> getTableSqlMapping() {
		HashMap<String, String> map = new HashMap<String, String>(tableSize);
		for (int i = tableNameArray.length; --i >= 0;) {
			map.put(tableNameArray[i], updateArray[i]);
		}
		return map;
	}

	@Override
	public boolean delete(String searchId, Long id) throws DataNotExistException, Exception {
		int index = DataAccessFactory.getSimpleSupport().getTableIndex(searchId, tableSize);
		String sql = deleteArray[index];
		return template.update(sql, id) > 0;
	}

	@Override
	public List<Long> delete(String searchId, List<Long> idList) throws Exception {
		int index = DataAccessFactory.getSimpleSupport().getTableIndex(searchId, tableSize);
		String sql = deleteArray[index];
		return DataAccessFactory.getSimpleSupport().delete(sql, idList);
	}

	@Override
	public long[] insertAndDelete(String ownerId, final List<InsertRoleExtPropertyData> list, List<Long> deleteList) throws DataNotExistException, Exception {
		int index = DataAccessFactory.getSimpleSupport().getTableIndex(ownerId, tableSize);
		String insertSql = this.insertArray[index];
		String deleteSql = this.deleteArray[index];
		BatchPreparedStatementSetter insertBps = new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				InsertRoleExtPropertyData entry = list.get(i);
				entry.setValues(ps);
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		};
		int size = deleteList.size();
		long[] deleteKeys = new long[size];
		for (int i = 0; i < size; i++) {
			deleteKeys[i] = deleteList.get(i);
		}
		return DataAccessFactory.getSimpleSupport().insertAndDelete(insertSql, insertBps, deleteSql, deleteKeys);
	}

	@Override
	public String getInsertSql(String ownerId) {
		int index = DataAccessFactory.getSimpleSupport().getTableIndex(ownerId, tableSize);
		return insertArray[index];
	}

}
