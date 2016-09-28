package com.rw.db.tablesMerge;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.DBMergeMgr;
import com.rw.constant.ModuleName;
import com.rw.db.DBInfo;
import com.rw.db.TableInfo;
import com.rw.db.dao.DBMgr;
import com.rw.db.tablesBeforeMerge.AbsTableProcesser;
import com.rw.dblog.DBLog;
import com.rw.utils.CommonUtils;
import com.rw.utils.FileUtils;
import com.rw.utils.SQLUtils;

/**
 * 合表处理
 * @author lida
 *
 */
public class CommonTableProcesser{

	private String name;
	private boolean blnFinsh;
	
	public void exec(DBInfo oriDBInfo, DBInfo tarDBInfo, TableInfo oriTableInfo, TableInfo tarTableInfo) {
		this.name = oriTableInfo.getTableName();
		// TODO Auto-generated method stub
		Map<String, Class<?>> oriFieldMap = oriTableInfo.getFieldsMap();
		Map<String, Class<?>> tarFieldMap = tarTableInfo.getFieldsMap();

		boolean compareResult = CommonUtils.compareTableField(oriFieldMap, tarFieldMap);
		String tableName = oriTableInfo.getTableName();

		if (!compareResult) {
			DBLog.LogError(ModuleName.MEGER.getName(), tableName + " has different table structure");
			blnFinsh = true;
			return;
		}

		String sql = "select * from " + tableName + " order by " + oriTableInfo.getKeyName() + ";";

		List<Map<String, Object>> query = DBMgr.getInstance().query(tarDBInfo.getDBName(), sql, new Object[] {});

		// 排列结果集
		List<Map<String, Object>> sortQuery = new LinkedList<Map<String, Object>>();
		List<String> fieldOrderList = oriTableInfo.getFieldOrderList();
		if(query == null){
			DBLog.LogInfo("CommonTableProcesser exec", "db name:" + tarDBInfo.getDBName() + ",table name:" + tableName + " can not find the query result!!!");
			blnFinsh = true;
			return;
		}
		for (Map<String, Object> map : query) {
			Map<String, Object> tempMap = new LinkedHashMap<String, Object>();
			for (String fieldName : fieldOrderList) {
				if(fieldName.equals(oriTableInfo.getAutoIncFieldName())){
					continue;
				}
				Object object = map.get(fieldName);
				tempMap.put(fieldName, object);
			}
			sortQuery.add(tempMap);
		}
		Map<String, Class<?>> fieldsMap = oriTableInfo.getFieldsMap();
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (Map<String, Object> map : sortQuery) {
			count++;
			sb.append("(");
			int subcount = 0;
			for (Iterator<Entry<String, Object>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				Class<?> type = fieldsMap.get(key);
				SQLUtils.packFieldValue(sb, type, value);
				subcount++;
				if (subcount < map.size()) {
					sb.append(",");
				}
			}
			sb.append(")");
			if (count < sortQuery.size()) {
				sb.append(",");
			}
		}
		
		sb.insert(0, oriTableInfo.getInsertSQL());
		String path = DBMergeMgr.getInstance().getSqlPath() + File.separator + oriTableInfo.getTableName() + ".sql";
		DBLog.LogInfo("CommonTableProcesser exec:", path);
		FileUtils.saveFile(path, sb.toString());
		DBMergeMgr.getInstance().addMergeSQLList(path);
		
		blnFinsh = true;
	}

	public String getName() {
		return name;
	}

	public boolean isBlnFinsh() {
		return blnFinsh;
	}

	
}
