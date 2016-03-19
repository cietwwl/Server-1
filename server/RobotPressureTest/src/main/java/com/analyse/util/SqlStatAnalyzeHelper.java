package com.analyse.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SqlStatAnalyzeHelper {

	
	
	public static void main(String[] args) throws IOException {
		
		
		String filePath = "D:\\dump\\sqlstat\\platformsql_stat_8";
		String filePath2 = "D:\\dump\\sqlstat\\platformsql_stat_9";
//		String filePath = "D:\\dump\\sqlstat\\gamesql_stat_8";
//		String filePath2 = "D:\\dump\\sqlstat\\gamesql_stat_9";
		SqlStatAnalyzeHelper analyzer = new SqlStatAnalyzeHelper();
		Map<String, SqlStat> b4 = analyzer.parse(filePath);
		Map<String, SqlStat> after = analyzer.parse(filePath2);
		analyzer.delta(b4, after);
	}

	private Map<String,SqlStat>  parse( String filePath )throws FileNotFoundException, IOException {
		
		Map<String,SqlStat> countMap = new HashMap<String,SqlStat>();
		
		FileInputStream inputstream = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));
		String line = br.readLine();
		while(line!=null){
			String[] split = line.split("\\|");
			if(split.length==3){
				String count = split[0].split(":")[1];
				String maxTimeSpan = split[1].split(":")[1];
				String sql = split[2].split(":")[1];
				
				SqlStat sqlStatTmp = new SqlStat(sql, Long.valueOf(count), Long.valueOf(maxTimeSpan));
				countMap.put(sql, sqlStatTmp);
			}
			line = br.readLine();
		}
		
		br.close();
		return countMap;
	}
	
	private void delta(Map<String,SqlStat> before, Map<String,SqlStat>  after){
		
		List<SqlStat> list = new ArrayList<SqlStat>();
		
		for (Entry<String, SqlStat> entryTmp : before.entrySet()) {
			String sql = entryTmp.getKey();
			SqlStat b4sqlStat = entryTmp.getValue();
			long countb4 = b4sqlStat.getCount();
			long maxTimeSpan = b4sqlStat.getMaxTimeSpan();
			long countAfter = 0;
			if(after.containsKey(sql)){
				SqlStat afterSqlStat = after.get(sql);
				countAfter = afterSqlStat.getCount();
				maxTimeSpan = afterSqlStat.getMaxTimeSpan()> maxTimeSpan?afterSqlStat.getMaxTimeSpan():maxTimeSpan;
			}
			long delta = countAfter - countb4;
			SqlStat sqlStatTmp = new SqlStat(sql, delta, maxTimeSpan);
			list.add(sqlStatTmp);
			
		}
		
		Collections.sort(list, new Comparator<SqlStat>() {

			@Override
			public int compare(SqlStat source, SqlStat target) {
				return -(int)(source.getCount()- target.getCount());
			}
		});
		
		for (SqlStat sqlStatTmp : list) {
			System.out.println(sqlStatTmp.toString());
		}
		
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
			return " count:"+count+"|maxTimeSpan:"+maxTimeSpan+"|sql:"+sql;
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
