package com.rw.db.tablesAfterMerge.email;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.rw.DBMergeMgr;
import com.rw.config.email.EmailCfg;
import com.rw.config.email.EmailCfgDao;
import com.rw.db.DBInfo;
import com.rw.db.dao.DBMgr;
import com.rw.db.dao.kv.DataKvEntity;
import com.rw.db.tables.info.RenameInfo;
import com.rw.db.tablesAfterMerge.AbsAfterMergeProcess;
import com.rw.log.DBLog;
import com.rw.utils.CommonUtils;
import com.rw.utils.jackson.JsonUtil;

public class EmailAfterMergeProcess extends AbsAfterMergeProcess{
	
	private static String[] sqlArray = new String[10];
	private static String[] updateSqlArray = new String[10];
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	
	private static String userRenameMailId = "1";
	private static String groupRenameMailId = "2";
	
	static{
		for (int i = 0; i < sqlArray.length; i++) {
			String sql1 = "select dbvalue, type from table_kvdata0" + i +" where dbkey=? and type = 9";
			String sql2 = "update table_kvdata0" + i + " set dbvalue= '%s' where dbkey = '%s' and type = 9";
			sqlArray[i] = sql1;
			updateSqlArray[i] = sql2;
		}
	}

	@Override
	protected void exec(DBInfo dbInfo) {
		long start = System.currentTimeMillis();
		DBLog.LogInfo("EmailAfterMergeProcess", "EmailAfterMergeProcess start process");
		
		EmailCfg userRenameMail = EmailCfgDao.getInstance().getCfgById(userRenameMailId);
		EmailCfg groupRenameMail = EmailCfgDao.getInstance().getCfgById(groupRenameMailId);
		
		
		//角色重名补偿
		Map<String, RenameInfo> renameUserMap = DBMergeMgr.getInstance().getRenameUserMap();
		
		String userRenameSender = userRenameMail.getSender();
		String userRenameTitle = userRenameMail.getTitle();
		String userRenameContent = userRenameMail.getContent();
		String userRenameAttachment = userRenameMail.getAttachment();
		
		for (Iterator<Entry<String, RenameInfo>> iterator = renameUserMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, RenameInfo> entry = iterator.next();
			RenameInfo renameInfo = entry.getValue();
			String userId = renameInfo.getId();
			
			int tableIndex = CommonUtils.getTableIndex(userId, sqlArray.length);
			String sql = sqlArray[tableIndex];
			
			List<DataKvEntity> query = DBMgr.getInstance().query(dbInfo.getDBName(), sql, new Object[] { userId }, DataKvEntity.class);
			if (query == null || query.size() <= 0) {
				DBLog.LogError("EmailAfterMergeProcess", "can not find the user:" + userId);
				continue;
			}
			DataKvEntity dataKvEntity = query.get(0);

			if (dataKvEntity != null) {
				String value = dataKvEntity.getDbvalue();
				TableEmail tableEmail = JsonUtil.readValue(value, TableEmail.class);
				sendEmail(dbInfo, tableEmail, userId, userRenameSender, userRenameTitle, userRenameContent, userRenameAttachment);

			} else {
				DBLog.LogError("EmailAfterMergeProcess", "send duplicate name user compensate and can not find the email list:" + userId);
			}
		}
		
		String groupRenameSender = groupRenameMail.getSender();
		String groupRenameTitle = groupRenameMail.getTitle();
		String groupRenameContent = groupRenameMail.getContent();
		String groupRenameAttachment = groupRenameMail.getAttachment();
		
		//帮派重名补偿
		Map<String, RenameInfo> renameGroupMap = DBMergeMgr.getInstance().getRenameGroupMap();
		for (Iterator<Entry<String, RenameInfo>> iterator = renameGroupMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, RenameInfo> entry = iterator.next();
			RenameInfo renameInfo = entry.getValue();
			String groupId = renameInfo.getId();
			String sql = "select userId, groupId from group_member where groupId = "+groupId+" and post = 1";
			
			
			List<GroupMemberInfo> query = DBMgr.getInstance().query(dbInfo.getDBName(), sql, new Object[]{}, GroupMemberInfo.class);
			
			if (query == null || query.size() <= 0) {
				DBLog.LogError("EmailAfterMergeProcess", "can not find the group:" + groupId);
				continue;
			}
			
			GroupMemberInfo groupMemberInfo = query.get(0);
			if (groupMemberInfo != null) {
				String userId = groupMemberInfo.getUserId();
				int tableIndex = CommonUtils.getTableIndex(userId, sqlArray.length);
				String emailSql = sqlArray[tableIndex];
				
				List<DataKvEntity> emailQuery = DBMgr.getInstance().query(dbInfo.getDBName(), emailSql, new Object[] { userId }, DataKvEntity.class);
				DataKvEntity dataKvEntity = emailQuery.get(0);

				if (dataKvEntity != null) {
					String value = dataKvEntity.getDbvalue();
					TableEmail tableEmail = JsonUtil.readValue(value, TableEmail.class);
					sendEmail(dbInfo, tableEmail, userId, groupRenameSender, groupRenameTitle, groupRenameContent, groupRenameAttachment);
				} else {
					DBLog.LogError("EmailAfterMergeProcess", "send duplicate group name compensate and can not find the email list:" + userId);
				}
			} else {
				DBLog.LogError("EmailAfterMergeProcess", "send duplicate group name compensate and can not find group header:(group id)" + groupId);
			}
		}
		long end = System.currentTimeMillis();
		DBLog.LogInfo("EmailAfterMergeProcess", "EmailAfterMergeProcess end process! cost time:"+(end - start));
		
	}
	
	public void sendEmail(DBInfo dbInfo, TableEmail tableEmail, String userId, String sender, String title, String content, String attachment) {

		if (tableEmail == null) {
			DBLog.LogInfo("EmailAfterMergeProcess", "send email fail!userId:" + userId);
			return;
		}
		
		EmailData emailData = new EmailData();
		emailData.setSender(sender);
		emailData.setTitle(title);
		emailData.setContent(content);
		emailData.setEmailAttachment(attachment);
		emailData.setDeleteType(EEmailDeleteType.GET_DELETE);
		
		
		EmailItem item = new EmailItem();
		item.setEmailId(UUID.randomUUID().toString());
		item.setCfgid(emailData.getCfgid());
		item.setTaskId(emailData.getTaskId());
		item.setEmailAttachment(emailData.getEmailAttachment());
		item.setChecked(false);
		item.setTitle(emailData.getTitle());
		item.setContent(emailData.getContent());
		item.setSender(emailData.getSender());
		item.setCheckIcon(emailData.getCheckIcon());
		item.setSubjectIcon(emailData.getSubjectIcon());
		item.setDeleteType(emailData.getDeleteType().getValue());
		item.setSendTime(System.currentTimeMillis());
		item.setCoolTime(emailData.getCoolTime());
		item.setBeginTime(emailData.getBeginTime());
		item.setEndTime(emailData.getEndTime());
		
		if (emailData.isDeadline()) {
			try {
				item.setDeadlineTimeInMill(format.parse(emailData.getDeadlineTime()).getTime());
			} catch (ParseException e) {
				
			}
		} else {
			item.setDeadlineTimeInMill(System.currentTimeMillis() + emailData.getDelayTime() * 1000L);
		}
		tableEmail.addEmail(item);
		
		Map<String, TableEmail> map = new HashMap<String, TableEmail>();
		map.put(tableEmail.getUserId(), tableEmail);
		
		String dbValue = JsonUtil.writeValue(tableEmail);
		
		int tableIndex = CommonUtils.getTableIndex(userId, updateSqlArray.length);
		String emailSql = updateSqlArray[tableIndex];
		emailSql = String.format(emailSql, dbValue, userId);
		
		DBMgr.getInstance().update(dbInfo.getDBName(), emailSql);
	}
}
