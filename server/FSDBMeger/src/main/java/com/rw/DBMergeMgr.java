package com.rw;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.constant.ModuleName;
import com.rw.db.DBInfo;
import com.rw.db.PlatformDBInfo;
import com.rw.db.dao.DBMgr;
import com.rw.db.dao.JdbcTemplateFactory;
import com.rw.db.dao.RawSqlJdbc;
import com.rw.db.platform.PlatformProcess;
import com.rw.db.process.DBBackupProcesser;
import com.rw.db.process.DBImportProcesser;
import com.rw.db.process.generate.GenDBInfoProcesser;
import com.rw.db.tables.info.RenameInfo;
import com.rw.db.tablesAfterMerge.AfterMergeMgr;
import com.rw.db.tablesBeforeMerge.BeforeMergeMgr;
import com.rw.db.tablesMerge.MegerMgr;
import com.rw.dblog.DBLog;
import com.rw.utils.FileUtils;
import com.rw.utils.SpringContextUtil;

public class DBMergeMgr {

	private static DBMergeMgr instance = new DBMergeMgr();

	private DBInfo originalDB;
	private DBInfo targetDB;
	private DBInfo Backup_OriginalDB;
	private DBInfo Backup_TargetDB;
	private PlatformDBInfo platformDB;

	private Map<String, RenameInfo> RenameUserMap = new HashMap<String, RenameInfo>();
	private Map<String, RenameInfo> RenameGroupMap = new HashMap<String, RenameInfo>();

	private List<String> MergeSQLList = new ArrayList<String>();

	public String savePath;

	public String sqlPath;
	
	public String shellPath;

	public static DBMergeMgr getInstance() {
		if (instance == null) {
			instance = new DBMergeMgr();
		}
		return instance;
	}

	public void StartMeger() {

		long startT = System.currentTimeMillis();
		init();

		boolean result = false;

		DBLog.LogInfo(ModuleName.START.getName(), "---------------------backup db start--------------------");
		result = processBackupNImportDB();

		if (!result) {
			DBLog.LogInfo(ModuleName.BACKUP_DB.getName(), "----------------------backup db fail!");
			return;
		}
		long start = System.currentTimeMillis();
		DBLog.LogInfo(ModuleName.INIT_DBINFO.getName(), "----------------------start get db info!");
		
		/**
		 * 生成数据库信息
		 */
		initDBTableInfo();
		long end = System.currentTimeMillis();
		DBLog.LogInfo(ModuleName.INIT_DBINFO.getName(), "----------------------end get db info!cost time:" + (end - start));
		start = end;

		
		/**
		 * 处理重名的处理
		 */
		DBLog.LogInfo(ModuleName.BEFORE_MEGER.getName(), "----------------------start before meger!");
		BeforeMergeMgr.getInstance().loadProcessTable();
		BeforeMergeMgr.getInstance().BeforeMegerProcessTable(Backup_OriginalDB, Backup_TargetDB);
		end = System.currentTimeMillis();
		DBLog.LogInfo(ModuleName.BEFORE_MEGER.getName(), "----------------------end before meger!cost time:" + (end - start));
		start = end;

		/**
		 * 生成导表sql，并执行导入合并数据库里面
		 */
		DBLog.LogInfo(ModuleName.MEGER.getName(), "----------------------start meger!");
		MegerMgr.getInstance().processMeger(Backup_OriginalDB, Backup_TargetDB);
		end = System.currentTimeMillis();
		DBLog.LogInfo(ModuleName.MEGER.getName(), "----------------------end meger!cost time:" + (end - start));
		start = end;
		
		DBLog.LogInfo("process logic", "----------------------start process logic!");
		AfterMergeMgr.getInstance().processAfterMerge(Backup_OriginalDB);
		end = System.currentTimeMillis();
		DBLog.LogInfo("process logic", "----------------------end process logic!" + (end - start));
		
		DBLog.LogInfo("update platform", "----------------------start update platform!");
		PlatformProcess process =new PlatformProcess();
		process.exec(platformDB, Backup_OriginalDB, Backup_TargetDB);
		DBLog.LogInfo("update platform", "----------------------finish update platform!");
		
		long endT= System.currentTimeMillis();
		DBLog.LogInfo("Merge finish", "Merge finish! Total cost time:" + (endT - startT));
		System.exit(0);
	}

	private void init() {
		initConst();
		initDBInfo();
		AfterMergeMgr.getInstance().init();
	}

	/**
	 * 初始化合并的数据库信息
	 */
	private void initDBInfo() {
		originalDB = SpringContextUtil.getBean("db1");
		targetDB = SpringContextUtil.getBean("db2");
		Backup_OriginalDB = SpringContextUtil.getBean("backup_db1");
		Backup_TargetDB = SpringContextUtil.getBean("backup_db2");
		platformDB = SpringContextUtil.getBean("platform");

		Map<String, DruidDataSource> map = new HashMap<String, DruidDataSource>();
		DruidDataSource ds1 = SpringContextUtil.getBean(originalDB.getDataSourceName());
		map.put(originalDB.getDBName(), ds1);
		DruidDataSource ds2 = SpringContextUtil.getBean(targetDB.getDataSourceName());
		map.put(targetDB.getDBName(), ds2);
		DruidDataSource ds3 = SpringContextUtil.getBean(platformDB.getDataSourceName());
		map.put(platformDB.getDBName(), ds3);
		
		DBMgr.getInstance().init(map);
	}

	private void initConst() {
		savePath = System.getProperty("user.dir");
		sqlPath = savePath + File.separator + "SQL";
		FileUtils.checkAndCreateFolder(sqlPath);
		shellPath = savePath + File.separator + "Shell";
		FileUtils.checkAndCreateFolder(shellPath);
	}

	/**
	 * 进行备份数据库
	 * 
	 * @return
	 */
	private boolean processBackupNImportDB() {

		String newOriDBName = Backup_OriginalDB.getDBName();
		String newTarDBName = Backup_TargetDB.getDBName();
		
		DBBackupProcesser backup1 = new DBBackupProcesser(newOriDBName);
		DBBackupProcesser backup2 = new DBBackupProcesser(newTarDBName);

		
		backup1.executeBackup(originalDB);
		backup2.executeBackup(targetDB);

		if (!(backup1.checkSuccess() && backup2.checkSuccess())) {
			return false;
		}
		String oriSQL = backup1.getBackSql();
		String tarSQL = backup2.getBackSql();
		
		DBImportProcesser import1 = new DBImportProcesser(newOriDBName, oriSQL);
		DBImportProcesser import2 = new DBImportProcesser(newTarDBName, tarSQL);
		
		import1.executeImport(originalDB);
		import2.executeImport(targetDB);
		
		if (!(import1.checkSuccess() && import2.checkSuccess())) {
			return false;
		}
		
		
		try {
			DruidDataSource backupOriDataSource = JdbcTemplateFactory.newDataSource(Backup_OriginalDB.getUrl(), Backup_OriginalDB.getUsername(), Backup_OriginalDB.getPassword(), 100);
			
			DBMgr.getInstance().addRawSqlJdbcMap(Backup_OriginalDB.getDBName(), backupOriDataSource);
			
			DruidDataSource backupTarDataSource = JdbcTemplateFactory.newDataSource(Backup_TargetDB.getUrl(), Backup_TargetDB.getUsername(), Backup_TargetDB.getPassword(), 100);
			
			DBMgr.getInstance().addRawSqlJdbcMap(Backup_TargetDB.getDBName(), backupTarDataSource);
		} catch (Exception ex) {
			DBLog.LogError("processBackupNImportDB", ex.getMessage());
		}
		
		return true;
	}

	private boolean initDBTableInfo() {
		GenDBInfoProcesser pro = new GenDBInfoProcesser();
		pro.GenDBInfo(Backup_OriginalDB);
		pro.GenDBInfo(Backup_TargetDB);
		return true;

	}

	public void addRenameUser(String userName, RenameInfo renameInfo) {
		RenameUserMap.put(userName, renameInfo);
	}

	public void addRenameGroup(String groupName, RenameInfo renameInfo) {
		RenameGroupMap.put(groupName, renameInfo);
	}

	public Map<String, RenameInfo> getRenameUserMap() {
		return RenameUserMap;
	}

	public Map<String, RenameInfo> getRenameGroupMap() {
		return RenameGroupMap;
	}

	public void addMergeSQLList(String sql) {
		MergeSQLList.add(sql);
	}

	public String getSavePath() {
		return savePath;
	}

	public String getSqlPath() {
		return sqlPath;
	}

	public String getShellPath() {
		return shellPath;
	}

	public List<String> getMergeSQLList() {
		return MergeSQLList;
	}
}
