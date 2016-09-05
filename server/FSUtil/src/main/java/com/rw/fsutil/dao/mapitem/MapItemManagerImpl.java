package com.rw.fsutil.dao.mapitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.common.Tuple;
import com.rw.fsutil.dao.cache.CacheKey;
import com.rw.fsutil.util.SpringContextUtil;

public class MapItemManagerImpl implements MapItemManager {

	private JdbcTemplate template;
	private PlatformTransactionManager tm;
	private DefaultTransactionDefinition df;
	private HashMap<CacheKey, Pair<String, RowMapper<? extends IMapItem>>> storeInfos;

	public MapItemManagerImpl(String dsName, Map<CacheKey, Pair<String, RowMapper<? extends IMapItem>>> storeInfos) {
		DruidDataSource dataSource = SpringContextUtil.getBean(dsName);
		if (dataSource == null) {
			throw new ExceptionInInitializerError("Ranking dataSource is null");
		}
		this.template = new JdbcTemplate(dataSource);
		// 初始化事务相关
		tm = new DataSourceTransactionManager(dataSource);
		df = new DefaultTransactionDefinition();
		df.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		this.storeInfos = new HashMap<CacheKey, Pair<String, RowMapper<? extends IMapItem>>>(storeInfos);
	}

	public List<Pair<CacheKey, List<? extends IMapItem>>> load(List<Pair<CacheKey, String>> searchInfos, String userId) {
		Object[] param = new Object[] { userId };
		ArrayList<Tuple<CacheKey, String, RowMapper<? extends IMapItem>>> sqls = new ArrayList<Tuple<CacheKey, String, RowMapper<? extends IMapItem>>>();
		for (int i = 0, size = searchInfos.size(); i < size; i++) {
			Pair<CacheKey, String> info = searchInfos.get(i);
			CacheKey pairKey = info.getT1();
			Pair<String, RowMapper<? extends IMapItem>> tableInfo = storeInfos.get(pairKey);
			if (tableInfo == null) {
				continue;
			}
			String tableName = info.getT2();
			String searchKey = tableInfo.getT1();
			String sql = "select * from " + tableName + " where " + searchKey + "=?";
			sqls.add(Tuple.<CacheKey, String, RowMapper<? extends IMapItem>>Create(pairKey, sql, tableInfo.getT2()));
		}
		ArrayList<Pair<CacheKey, List<? extends IMapItem>>> datas = new ArrayList<Pair<CacheKey, List<? extends IMapItem>>>();
		int size = sqls.size();
		TransactionStatus ts = tm.getTransaction(df);
		try {
			for (int i = 0; i < size; i++) {
				Tuple<CacheKey, String, RowMapper<? extends IMapItem>> info = sqls.get(i);
				List<? extends IMapItem> list = template.query(info.getT2(), info.getT3(), param);
				if (list != null) {
					Pair<CacheKey, List<? extends IMapItem>> p = Pair.<CacheKey, List<? extends IMapItem>>Create(info.getT1(), list);
					datas.add(p);
				}
			}
			tm.commit(ts);
		} catch (Throwable t) {
			tm.rollback(ts);
			t.printStackTrace();
		}
		return datas;
	}
}
