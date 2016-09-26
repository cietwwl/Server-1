package com.rw.db.tablesMerge;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.rw.DBMergeMgr;
import com.rw.db.DBInfo;
import com.rw.db.TableInfo;
import com.rw.dblog.DBLog;
import com.rw.thread.DBThreadFactory;
import com.rw.utils.ShellUtils;

public class MegerMgr {
	private static MegerMgr instance = new MegerMgr();
	
	private static List<CommonTableProcesser> processerMap = new ArrayList<CommonTableProcesser>();
	
	public static MegerMgr getInstance(){
		if(instance == null){
			instance = new MegerMgr();
		}
		return instance;
	}
	
	public void processMeger(final DBInfo oriDBInfo,final DBInfo tarDBInfo){
		List<TableInfo> tables = oriDBInfo.getTables();
		
		for (TableInfo tableInfo : tables) {
			final TableInfo tarTableInfo = tarDBInfo.getTableInfo(tableInfo.getTableName());
			final TableInfo oriTableInfo = tableInfo;
			prepareMergeSQL(oriDBInfo, tarDBInfo, tarTableInfo, oriTableInfo);
		}
		
		processMergeSQL(oriDBInfo);
	}

	private void processMergeSQL(DBInfo oriDBInfo) {
		// 把生成的sql导进主数据库
		List<String> mergeSQLList = DBMergeMgr.getInstance().getMergeSQLList();
		DBLog.LogInfo("processMergeSQL", "mergeSQLList size:" + mergeSQLList.size());
		Runtime runtime = Runtime.getRuntime();
		String shell = oriDBInfo.getImportSQL();
		for (String sql : mergeSQLList) {
			try {
				File sqlFile = new File(sql);
				if (!sqlFile.exists()) {
					DBLog.LogError("processMergeSQL", "can not find the sql file:" + sql);
					continue;
				}
				String name = sqlFile.getName();

				String tempShell = shell + name;
				String path = DBMergeMgr.getInstance().getSqlPath() + File.separator + name + ".sh";
				ShellUtils.genShell(DBMergeMgr.getInstance().getSqlPath() + File.separator + name + ".sh", tempShell);
				
				DBLog.LogInfo("processMergeSQL", "processMergeSQL shell:" + tempShell);
				Process exec1 = runtime.exec("chmod 777 " + path);
				int tag = exec1.waitFor();
				if (tag != 0) {
					DBLog.LogInfo("processMergeSQL", "import SQL fail:" + name);
					continue;
				}
				
				Process exec2 = runtime.exec("/bin/sh " + path);
				tag = exec2.waitFor();
				if (tag == 0) {
					DBLog.LogInfo("processMergeSQL", "import SQL success:" + name);
				} else {
					DBLog.LogInfo("processMergeSQL", "import SQL fail:" + name);
					continue;
				}
				
			} catch (Exception ex) {
				DBLog.LogError("processMergeSQL", "process merge sql exception:" + ex.getMessage());
			}
		}
	}

	private void prepareMergeSQL(final DBInfo oriDBInfo, final DBInfo tarDBInfo, final TableInfo tarTableInfo, final TableInfo oriTableInfo) {
		if(tarTableInfo != null){
			final CommonTableProcesser processer =new CommonTableProcesser();
			processerMap.add(processer);
			DBThreadFactory.getInstance().asynExecute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					processer.exec(oriDBInfo, tarDBInfo, oriTableInfo, tarTableInfo);
				}
			});
		}
		
		for (Iterator<CommonTableProcesser> iterator = processerMap.iterator(); iterator.hasNext();) {
			CommonTableProcesser commonTableProcesser = iterator.next();
			if(commonTableProcesser.isBlnFinsh()){
				iterator.remove();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
