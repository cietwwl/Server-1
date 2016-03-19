package com.rw.fsutil.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.management.JMException;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcSqlStat;

public class DruidStatUtil {

	private String sqlStatOutPutDirPath;

	private File OutPutDir;

	private static int index = 0;

	private ScheduledExecutorService service = null;
	
	private String SEPARATOR = System.getProperty("line.separator");

	private String outFilePrefix="";
	
	public void setSqlStatOutPutDirPath(String sqlStatOutPutDirPath) {
		this.sqlStatOutPutDirPath = sqlStatOutPutDirPath;
	}
	

	public void setOutFilePrefix(String outFilePrefix) {
		this.outFilePrefix = outFilePrefix;
	}


	public void init() {
		
		if(StringUtils.isBlank(sqlStatOutPutDirPath)){
			return;
		}
		
		service = Executors.newScheduledThreadPool(1);

		OutPutDir = new File(sqlStatOutPutDirPath);

		service.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					outputSqlStat();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, 10, 60, TimeUnit.SECONDS);

	}

	private void outputSqlStat() throws JMException, IOException {

		List<SqlStat> sqlStatList = new ArrayList<SqlStat>();

		// for (DataSourceProxyImpl dataSource :
		// DruidDriver.getProxyDataSources().values()) {
		// Map<String, JdbcSqlStat> statMap =
		// dataSource.getDataSourceStat().getSqlStatMap();
		// for (Map.Entry<String, JdbcSqlStat> entry : statMap.entrySet()) {
		// if (entry.getValue().getExecuteCount() == 0 &&
		// entry.getValue().getRunningCount() == 0) {
		// continue;
		// }
		//
		// Map<String, Object> map = entry.getValue().getData();
		// map.put("URL", dataSource.getUrl());
		// }
		// sqlMap.putAll(statMap);
		// }

		for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
			Map<String, JdbcSqlStat> statMap = dataSource.getDataSourceStat().getSqlStatMap();
			for (Map.Entry<String, JdbcSqlStat> entry : statMap.entrySet()) {
				if (entry.getValue().getExecuteCount() == 0
						&& entry.getValue().getRunningCount() == 0) {
					continue;
				}

				Map<String, Object> map = entry.getValue().getData();
				SqlStat sqlStatTmp = getSqlStat(map);
				sqlStatList.add(sqlStatTmp);
			}
		}

		index++;
		String fileName = outFilePrefix+"sql_stat_" + index;
		File output = new File(OutPutDir, fileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));

		Collections.sort(sqlStatList, new Comparator<SqlStat>() {

			@Override
			public int compare(SqlStat soruce, SqlStat targert) {
				return -(int)(soruce.getCount()-targert.getCount());
			}
			
		});
		for (SqlStat sqlStat : sqlStatList) {
			bw.write(sqlStat.toString());
		}
		bw.close();

	}
	
	private SqlStat getSqlStat(Map<String, Object> map) {
		
		Long executeCount = (Long)map.get("ExecuteCount");
		long maxTimespan = (Long)map.get("MaxTimespan");
		String sql = (String)map.get("SQL");
		
		SqlStat sqlStat = new SqlStat(sql,executeCount,maxTimespan);
		return sqlStat;
	}

	public class SqlStat{
		private String sql;
		private long count;
		private long maxTimeSpan;
		
		public SqlStat(String sql, long count, long maxTimeSpan){
			this.sql = sql;
			this.count = count;
			this.maxTimeSpan = maxTimeSpan;
		}
		
		public String toString(){
			
			return " count:"+count+"|maxTimeSpan:"+maxTimeSpan+"|sql:"+sql+SEPARATOR;
		}

		public String getSql() {
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}

		public long getCount() {
			return count;
		}

		public void setCount(long count) {
			this.count = count;
		}

		public long getMaxTimeSpan() {
			return maxTimeSpan;
		}

		public void setMaxTimeSpan(long maxTimeSpan) {
			this.maxTimeSpan = maxTimeSpan;
		}
		
	}
}
