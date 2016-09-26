package com.rw.db.tablesBeforeMerge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.rw.DBMergeMgr;
import com.rw.constant.ModuleName;
import com.rw.db.DBInfo;
import com.rw.db.TableInfo;
import com.rw.db.annotation.BPTableName;
import com.rw.db.dao.DBMgr;
import com.rw.db.tables.info.RenameInfo;
import com.rw.db.tables.info.UserNameInfo;
import com.rw.log.DBLog;
import com.rw.utils.CommonUtils;

@BPTableName(name="user")
public class UserTableProcesser extends AbsTableProcesser {

	// <oldName, newName>
	private Map<String, RenameInfo> RenameMap = new HashMap<String, RenameInfo>();

	@Override
	public void exec(DBInfo oriDBInfo, DBInfo tarDBInfo, TableInfo oriTableInfo, TableInfo tarTableInfo) {
		Map<String, Class<?>> oriFieldMap = oriTableInfo.getFieldsMap();
		Map<String, Class<?>> tarFieldMap = tarTableInfo.getFieldsMap();

		boolean compareResult = CommonUtils.compareTableField(oriFieldMap, tarFieldMap);
		String tableName = oriTableInfo.getTableName();

		if (!compareResult) {
			DBLog.LogError(ModuleName.MEGER.getName(), tableName + " has different table structure");
			return;
		}

		String sql = "select * from user order by " + oriTableInfo.getKeyName() + " limit ? offset ?;";

		int OFFSET = 0;
		final int LIMIT = 500;
		List<Map<String, Object>> query = DBMgr.getInstance().query(tarDBInfo.getDBName(), sql, new Object[] {LIMIT, OFFSET});

		String checkDuplicateUserNameSql = "select userId, userName from " + tableName + " group by userName";
		List<UserNameInfo> existNameList = DBMgr.getInstance().query(oriDBInfo.getDBName(), checkDuplicateUserNameSql, new Object[] {}, UserNameInfo.class);
		Map<String, UserNameInfo> existNameMap = new HashMap<String, UserNameInfo>();
		for (UserNameInfo userNameInfo : existNameList) {
			existNameMap.put(userNameInfo.getUserName(), userNameInfo);
		}

		while (query != null) {
			processDuplicationUserName(query, oriTableInfo.getTableName(), tarDBInfo, existNameMap);
			OFFSET = LIMIT + OFFSET;
			query = DBMgr.getInstance().query(tarDBInfo.getDBName(), sql, new Object[] {LIMIT, OFFSET});
		}

	}

	private void processDuplicationUserName(List<Map<String, Object>> list, String tableName, DBInfo tarDBInfo, Map<String, UserNameInfo> map) {

		RenameMap.clear();
		String dbName = tarDBInfo.getDBName();
		// 处理重复名字
		for (Map<String, Object> tempMap : list) {
			String userName = (String) tempMap.get("userName");
			if(StringUtils.isEmpty(userName)){
				continue;
			}
			String userId = (String) tempMap.get("userId");
			if (map.containsKey(userName)) {
				RenameInfo renameInfo = new RenameInfo();
				renameInfo.setId(userId);
				renameInfo.setOriName(userName);
				renameInfo.setNewUserName("[" + tarDBInfo.getZoneId() + "]" + userName);
				RenameMap.put(userName, renameInfo);
			}
		}

		DBLog.LogInfo("processDuplicationUserName", "---------------------RenameMap size:"+RenameMap.size());
		// 处理user表 好友表 更新排行榜（战力榜， 五人小队战力， 等级 ，竞技场数据， 在线帮战伤害排行榜 在线帮战杀敌排行榜 帮派成员名字）
		/**
		 * user friend kv type 4 ranking arena_data ranking_swap group_member
		 */

		List<Map<String, Object>> param1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> param2 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> param3 = new ArrayList<Map<String, Object>>();
		for (Iterator<Entry<String, RenameInfo>> iterator = RenameMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, RenameInfo> entry = iterator.next();
			String userName = entry.getKey();
			RenameInfo renameInfo = entry.getValue();
			
			DBMergeMgr.getInstance().addRenameUser(userName, renameInfo);
			
			Map<String, Object> subParam1 = new LinkedHashMap<String, Object>();
			subParam1.put("newUserName", renameInfo.getUserName());
			subParam1.put("userId", renameInfo.getId());
			param1.add(subParam1);
			Map<String, Object> subParam2 = new LinkedHashMap<String, Object>();
			subParam2.put("userName", "userName\":\"" + renameInfo.getOriName());
			subParam2.put("newUserName", "userName\":\"" + renameInfo.getUserName());
			subParam2.put("userId", renameInfo.getId());
			param2.add(subParam2);
			Map<String, Object> subParam3 = new LinkedHashMap<String, Object>();
			subParam3.put("userName", "userName\":\"" + renameInfo.getOriName());
			subParam3.put("newUserName", "userName\":\"" + renameInfo.getUserName());
			subParam3.put("primary_key", renameInfo.getId());
			param3.add(subParam3);
			StringBuilder renameInfoSB = new StringBuilder();
			renameInfoSB.append("重名玩家(区号:").append(tarDBInfo.getZoneId()).append(";userId:").append(renameInfo.getId()).append(";userName:").append(renameInfo.getOriName()).append(";newUserName:").append(renameInfo.getUserName()).append(")");
			DBLog.LogInfo("重名信息", renameInfoSB.toString());
		}

		// user
		String updateSQL1 = "update user set userName=? where userId = ?";
		DBMgr.getInstance().update(dbName, updateSQL1, param1);

		// table_kvdata
		for (int i = 0; i <= 9; i++) {
			String updateSQL2 = "update table_kvdata0" + i + " set dbvalue = REPLACE (`dbvalue`,?,?) where dbkey = ? and type = 4";
			DBMgr.getInstance().update(dbName, updateSQL2, param2);
		}

		String updateSQL3 = "update ranking set extension = REPLACE (`extension`,?,?) where primary_key = ?";
		DBMgr.getInstance().update(dbName, updateSQL3, param3);
		String updateSQL4 = "update ranking_swap set extension = REPLACE (`extension`,?,?) where primary_key = ?";
		DBMgr.getInstance().update(dbName, updateSQL4, param3);
		
		String updateSQL5 = "update group_member set name=? where userId=?";
		DBMgr.getInstance().update(dbName, updateSQL5, param1);
	}

}
