package com.rw.fsutil.ranking.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.util.SpringContextUtil;

/**
 * 排行榜数据库操作管理器
 * 
 * @author Jamaz
 */
public class RankingDataManager {

	private static JdbcTemplate jdbcTemplate;
	private static PlatformTransactionManager tm;
	private static DefaultTransactionDefinition df;

	static{
		DruidDataSource dataSource = SpringContextUtil.getBean("dataSourceMT");
		if(dataSource == null){
			throw new ExceptionInInitializerError("Ranking dataSource is null");
		}
		jdbcTemplate = new JdbcTemplate(dataSource);
		//初始化事务相关
		tm = new DataSourceTransactionManager(dataSource);
		df = new DefaultTransactionDefinition();
		df.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
	}
	
	public static void refreshSRanking(final int type, final List<ListRankingEntryData> list) throws Throwable {
		String sql = "delete from ranking_swap where type = ?";
		String sql2 = "insert into ranking_swap(primary_key,type,ranking,extension) values(?,?,?,?)";
		TransactionStatus ts = tm.getTransaction(df);
		try {
			jdbcTemplate.update(sql, new Object[] { type });
			jdbcTemplate.batchUpdate(sql2, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement pstmt, int i) throws SQLException {
					ListRankingEntryData entityData = list.get(i);
					pstmt.setString(1, entityData.getKey());
					pstmt.setInt(2, entityData.getType());
					pstmt.setInt(3, entityData.getRanking());
					pstmt.setString(4, entityData.getExtension());
				}

				@Override
				public int getBatchSize() {
					return list.size();
				}
			});
			tm.commit(ts);
		} catch (Throwable t) {
			tm.rollback(ts);
			throw t;
		}
	}

	public static void refreshRanking(final int deleteType, final List<RankingEntryData> entitys) throws Throwable {
		String sql = "delete from ranking where type = ?";
		String sql2 = "insert into ranking(ranking_sequence, type, primary_key, conditions, extension) values(?, ?, ?, ?, ?)";
		TransactionStatus ts = tm.getTransaction(df);
		try {
			jdbcTemplate.update(sql, new Object[] { deleteType });
			jdbcTemplate.batchUpdate(sql2, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement pstmt, int i) throws SQLException {
					RankingEntryData entityData = entitys.get(i);
					pstmt.setLong(1, entityData.getId());
					pstmt.setInt(2, entityData.getType());
					pstmt.setString(3, entityData.getKey());
					pstmt.setString(4, entityData.getCondition());
					pstmt.setString(5, entityData.getExtension());
				}

				@Override
				public int getBatchSize() {
					return entitys.size();
				}
			});
			tm.commit(ts);
		} catch (Throwable t) {
			tm.rollback(ts);
			throw t;
		}
	}

	public static long getMaxPrimaryKey() {
		String sql = "select max(ranking_sequence) from ranking";
		return jdbcTemplate.queryForLong(sql);
	}

	private static ResultSetExtractor<List<ListRankingEntryData>> extractor = new ResultSetExtractor<List<ListRankingEntryData>>() {

		@Override
		public List<ListRankingEntryData> extractData(ResultSet rs) throws SQLException, DataAccessException {
			ArrayList<ListRankingEntryData> list = new ArrayList<ListRankingEntryData>();
			while (rs.next()) {
				ListRankingEntryData rankingData = new ListRankingEntryData(rs.getString("primary_key"), rs.getInt("type"), rs.getInt("ranking"), rs.getString("extension"));
				list.add(rankingData);
			}
			return list;
		}
	};

	public static List<ListRankingEntryData> getSRankingEntryData(int type) {
		String sql = "select primary_key,type,ranking,extension from ranking_swap where type = ? ";
		return jdbcTemplate.query(sql, new Object[] {type}, extractor);
	}

	public static List<RankingEntryData> getRankingEntitys(int type) throws Exception {
		String sql = "select ranking_sequence, primary_key, conditions, extension from ranking where type = ? ";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, new Object[] { type });
		int size = list.size();
		ArrayList<RankingEntryData> rankingList = new ArrayList<RankingEntryData>(size);
		for (int i = 0; i < size; i++) {
			Map<String, Object> map = list.get(i);
			Long id = (Long) map.get("ranking_sequence");
			String key = (String) map.get("primary_key");
			String condition = (String) map.get("conditions");
			String extension = (String) map.get("extension");
			RankingEntryData data = new RankingEntryData(id, type, key, condition, extension);
			rankingList.add(data);
		}
		return rankingList;
	}

}