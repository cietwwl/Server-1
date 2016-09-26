package com.rw.db.process;

import java.io.File;

import com.rw.DBMergeMgr;
import com.rw.constant.ModuleName;
import com.rw.db.DBInfo;
import com.rw.log.DBLog;
import com.rw.utils.ShellUtils;

public class DBImportProcesser {

	public final static byte IMPORT_STATUS_START = 0;
	public final static byte IMPORT_STATUS_SUCCESS = 1;
	public final static byte IMPORT_STATUS_FAIL = 2;

	private String dbName;
	private String sqlFile;
	private int status = 0;

	public DBImportProcesser(String dbName, String sqlFile) {
		this.dbName = dbName;
		this.sqlFile = sqlFile;
		this.status = IMPORT_STATUS_START;

	}

	private String genDBImportShell(DBInfo dbInfo) {
		StringBuilder sb = new StringBuilder();
		String shell = "mysql -h" + dbInfo.getIp() + " -u" + dbInfo.getUsername() + " -p" + dbInfo.getPassword() + " -P" + dbInfo.getPort() + " -e \"CREATE DATABASE IF NOT EXISTS " + dbName + " DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;\"";
		String shell1 = "mysql -h" + dbInfo.getIp() + " -u" + dbInfo.getUsername() + " -p" + dbInfo.getPassword() + " -P" + dbInfo.getPort() + " " + dbName + " < " + this.sqlFile;
		sb.append(shell).append("\n").append(shell1);
		String path = DBMergeMgr.getInstance().getShellPath() + File.separator + dbName + ".sh";
		ShellUtils.genShell(path, sb.toString());
		return path;

	}

	public void executeImport(DBInfo dbInfo) {
		try {
			DBLog.LogInfo("executeBackup", "execute backup:" + dbInfo.getDBName());
			String path = genDBImportShell(dbInfo);
			Runtime runtime = Runtime.getRuntime();
			String shell1 = "chmod 777 " + path;

			Process exec1 = runtime.exec(shell1);
			int tag = exec1.waitFor();
			DBLog.LogInfo("executeImport", "execute import shell1 result:" + tag);
			if (tag == 0) {
				status = IMPORT_STATUS_SUCCESS;
			} else {
				status = IMPORT_STATUS_FAIL;
				return;
			}

			String shell2 = "/bin/sh " + path;
			Process exec2 = runtime.exec(shell2);

			DBLog.LogInfo("executeImport", "execute import shell:" + shell2);
			tag = exec2.waitFor();
			DBLog.LogInfo("executeImport", "execute import shell2 result:" + tag);
			if (tag == 0) {
				status = IMPORT_STATUS_SUCCESS;
			} else {
				status = IMPORT_STATUS_FAIL;
			}

		} catch (Exception ex) {
			DBLog.LogError(ModuleName.BACKUP_DB.getName(), ex.getMessage());
			status = IMPORT_STATUS_FAIL;
		}
	}

	public int getStatus() {
		return status;
	}

	public boolean checkFinish() {
		return status == IMPORT_STATUS_SUCCESS || status == IMPORT_STATUS_FAIL;
	}

	public boolean checkSuccess() {
		return status == IMPORT_STATUS_SUCCESS;
	}
}
