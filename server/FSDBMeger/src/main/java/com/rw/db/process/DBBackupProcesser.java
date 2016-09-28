package com.rw.db.process;

import java.io.File;

import com.rw.DBMergeMgr;
import com.rw.constant.ModuleName;
import com.rw.db.DBInfo;
import com.rw.dblog.DBLog;
import com.rw.utils.DateUtils;
import com.rw.utils.ShellUtils;

/**
 * 数据库备份处理器
 * 
 * @author lida
 *
 */
public class DBBackupProcesser {

	public final static byte BACKUP_STATUS_START = 0;
	public final static byte BACKUP_STATUS_SUCCESS = 1;
	public final static byte BACKUP_STATUS_FAIL = 2;

	private String backupName;
	private int status = 0;
	private String backSql;

	public DBBackupProcesser(String backupName) {
		this.backupName = backupName;
		this.status = BACKUP_STATUS_START;
	}

	private String genDBBackupShell(DBInfo dbInfo) {
		String sqlName = this.backupName + DateUtils.getDateTimeFormatString(System.currentTimeMillis(), "yyyy-MM-ddHH:mm:ss") + ".sql";
		String shell = "mysqldump -h" + dbInfo.getIp() + " -u" + dbInfo.getUsername() + " -p" + dbInfo.getPassword() + " -P" + dbInfo.getPort() + " " + dbInfo.getDBName() + "> " + sqlName;
		String name = dbInfo.getDBName() + "backup.sh";
		String path = DBMergeMgr.getInstance().getShellPath() + File.separator + name;
		ShellUtils.genShell(path, shell);
		backSql = DBMergeMgr.getInstance().getShellPath() + File.separator + sqlName;
		return path;
	}

	public void executeBackup(DBInfo dbInfo) {
		try {
			DBLog.LogInfo("executeBackup", "execute backup:" + dbInfo.getDBName());
			String path = genDBBackupShell(dbInfo);
			Runtime runtime = Runtime.getRuntime();
			String shell1 = "chmod 777 " + path;
			
			
			Process exec1 = runtime.exec(shell1);
			int tag = exec1.waitFor();
			DBLog.LogInfo("executeBackup", "execute backup shell1 result:" + tag);
			if (tag == 0) {
				status = BACKUP_STATUS_SUCCESS;
			} else {
				status = BACKUP_STATUS_FAIL;
				return;
			}
			
			String shell2 = "/bin/sh " + path;
			Process exec2 = runtime.exec(shell2);
			
			DBLog.LogInfo("executeBackup", "execute backup shell:" + shell2);
			tag = exec2.waitFor();
			DBLog.LogInfo("executeBackup", "execute backup shell2 result:" + tag);
			if (tag == 0) {
				status = BACKUP_STATUS_SUCCESS;
			} else {
				status = BACKUP_STATUS_FAIL;
			}

		} catch (Exception ex) {
			DBLog.LogError(ModuleName.BACKUP_DB.getName(), ex.getMessage());
			status = BACKUP_STATUS_FAIL;
		}

	}

	public int getStatus() {
		return status;
	}

	public boolean checkFinish() {
		return status == BACKUP_STATUS_SUCCESS || status == BACKUP_STATUS_FAIL;
	}

	public boolean checkSuccess() {
		return status == BACKUP_STATUS_SUCCESS;
	}

	public String getBackSql() {
		return backSql;
	}
}
