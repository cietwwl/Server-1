package com.rw.db.tablesBeforeMerge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.DBMergeMgr;
import com.rw.constant.ModuleName;
import com.rw.db.DBInfo;
import com.rw.db.TableInfo;
import com.rw.db.annotation.BPTableName;
import com.rw.db.dao.DBMgr;
import com.rw.db.tables.info.GroupNameInfo;
import com.rw.db.tables.info.RenameInfo;
import com.rw.log.DBLog;
import com.rw.utils.CommonUtils;

@BPTableName(name="group_data")
public class GroupDataTableProcesser extends AbsTableProcesser{

	private HashMap<String, RenameInfo> RenameMap = new HashMap<String, RenameInfo>();
	
	@Override
	public void exec(DBInfo oriDBInfo, DBInfo tarDBInfo, TableInfo oriTableInfo, TableInfo tarTableInfo) {
		// TODO Auto-generated method stub
		Map<String, Class<?>> oriFieldMap = oriTableInfo.getFieldsMap();
		Map<String, Class<?>> tarFieldMap = tarTableInfo.getFieldsMap();

		boolean compareResult = CommonUtils.compareTableField(oriFieldMap, tarFieldMap);
		String tableName = oriTableInfo.getTableName();

		if (!compareResult) {
			DBLog.LogError(ModuleName.MEGER.getName(), tableName + " has different table structure");
			return;
		}
		
		String sql = "select * from group_data order by groupName limit ? offset ? ;";
		
		int OFFSET = 0;
		final int LIMIT = 500;
		List<Map<String, Object>> query = DBMgr.getInstance().query(tarDBInfo.getDBName(), sql, new Object[] {LIMIT, OFFSET});
				
		String checkDuplicateUserNameSql = "select id, groupName from group_data group by groupName";
		List<GroupNameInfo> existNameList = DBMgr.getInstance().query(oriDBInfo.getDBName(), checkDuplicateUserNameSql, new Object[] {}, GroupNameInfo.class);
		Map<String, GroupNameInfo> existNameMap = new HashMap<String, GroupNameInfo>();
		for (GroupNameInfo userNameInfo : existNameList) {
			existNameMap.put(userNameInfo.getGroupName(), userNameInfo);
		}
		
		while (query != null) {
			processDuplicationGroupName(query, oriTableInfo.getTableName(), tarDBInfo, existNameMap);
			OFFSET = LIMIT + OFFSET;
			query = DBMgr.getInstance().query(tarDBInfo.getDBName(), sql, new Object[] {LIMIT, OFFSET});
		}
	}

	private void processDuplicationGroupName(List<Map<String, Object>> list, String tableName, DBInfo tarDBInfo, Map<String, GroupNameInfo> map){
		RenameMap.clear();
		String dbName = tarDBInfo.getDBName();
		// 处理重复名字
		for (Map<String, Object> tempMap : list) {
			String groupName = (String) tempMap.get("groupName");
			String id = (String) tempMap.get("id");
			if (map.containsKey(groupName)) {
				RenameInfo renameInfo = new RenameInfo();
				renameInfo.setId(id);
				renameInfo.setOriName(groupName);
				renameInfo.setNewUserName("[" + tarDBInfo.getZoneId() + "]" + groupName);
				RenameMap.put(groupName, renameInfo);
			}
		}
		
		//处理 group_data 在线帮战竞标排行榜
		List<Map<String, Object>> param1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> param2 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> param3 = new ArrayList<Map<String, Object>>();
		for (Iterator<Entry<String, RenameInfo>> iterator = RenameMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, RenameInfo> entry = iterator.next();
			String groupName = entry.getKey();
			RenameInfo renameInfo = entry.getValue();
			
			DBMergeMgr.getInstance().addRenameGroup(groupName, renameInfo);
			
			Map<String, Object> subParam1 = new LinkedHashMap<String, Object>();
			subParam1.put("newGroupName", renameInfo.getUserName());
			subParam1.put("groupName", renameInfo.getOriName());
			param1.add(subParam1);
			Map<String, Object> subParam2 = new LinkedHashMap<String, Object>();
			subParam2.put("groupName", "groupName\":\"" +renameInfo.getOriName());
			subParam2.put("newGroupName", "groupName\":\"" +renameInfo.getUserName());
			param2.add(subParam2);
			Map<String, Object> subParam3 = new LinkedHashMap<String, Object>();
			subParam3.put("groupName", "unionName\":\"" +renameInfo.getOriName());
			subParam3.put("newGroupName", "unionName\":\"" +renameInfo.getUserName());
			param3.add(subParam3);
			StringBuilder renameInfoSB = new StringBuilder();
			renameInfoSB.append("重名帮派名(区号:").append(tarDBInfo.getZoneId()).append(";id:").append(renameInfo.getId()).append(";groupName:").append(renameInfo.getOriName()).append(";newGroupName:").append(renameInfo.getUserName()).append(")");
			DBLog.LogInfo("重名帮派名信息", renameInfoSB.toString());
		}
		
		String updateSQL1 = "update group_data set groupName = ? where groupName = ?";
		DBMgr.getInstance().update(dbName, updateSQL1, param1);

		// table_kvdata
		for (int i = 0; i <= 9; i++) {
			String updateSQL2 = "update table_kvdata0" + i + " set dbvalue = REPLACE (`dbvalue`,?,?) where type = 4";
			DBMgr.getInstance().update(dbName, updateSQL2, param3);
		}
		
		String updateSQL3 = "update ranking set extension = REPLACE (`extension`,?,?)";
		DBMgr.getInstance().update(dbName, updateSQL3, param2);
		
		DBLog.LogInfo("GroupDataTableProcesser", "GroupDataTableProcesser process finish");
	}
}
