package com.rw.fsutil.dao.mapitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.common.Tuple;
import com.rw.fsutil.dao.cache.CacheKey;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.DataAccessStaticSupport;
import com.rw.fsutil.util.SpringContextUtil;

public class MapItemManagerImpl implements MapItemManager {

	private JdbcTemplate template;
	private PlatformTransactionManager tm;
	private DefaultTransactionDefinition df;
	private HashMap<CacheKey, Pair<String, MapItemRowBuider<? extends IMapItem>>> storeInfos;
	private final String[] mapItemTableName;

	public MapItemManagerImpl(String dsName, Map<CacheKey, Pair<String, MapItemRowBuider<? extends IMapItem>>> storeInfos) {
		DruidDataSource dataSource = SpringContextUtil.getBean(dsName);
		if (dataSource == null) {
			throw new ExceptionInInitializerError("Ranking dataSource is null");
		}
		this.template = new JdbcTemplate(dataSource);
		// 初始化事务相关
		tm = new DataSourceTransactionManager(dataSource);
		df = new DefaultTransactionDefinition();
		df.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		this.storeInfos = new HashMap<CacheKey, Pair<String, MapItemRowBuider<? extends IMapItem>>>(storeInfos);
		List<String> list = DataAccessStaticSupport.getTableNameList(template, "map_item_store");
		mapItemTableName = new String[list.size()];
		list.toArray(mapItemTableName);
	}

	public List<MapItemEntity> load(String userId, List<Integer> typeList) {
		int index = DataAccessFactory.getSimpleSupport().getTableIndex(userId, mapItemTableName.length);
		String tableName = mapItemTableName[index];
		int size = typeList.size();
		int last = size - 1;
		Object[] params = new Object[size + 1];
		params[0] = userId;
		StringBuilder sb = new StringBuilder();
		sb.append("select id,extention,type from ").append(tableName).append(" where userId=? and type in(");
		for (int i = 0; i < size; i++) {
			sb.append('?');
			if (i < last) {
				sb.append(',');
			}
			params[i + 1] = typeList.get(i);
		}

		sb.append(')');
		List<Map<String, Object>> datas = template.queryForList(sb.toString(), params);
		ArrayList<MapItemEntity> entitys = new ArrayList<MapItemEntity>();
		for (int i = datas.size(); --i >= 0;) {
			Map<String, Object> map = datas.get(i);
			String id = (String) map.get("id");
			String extension = (String) map.get("extention");
			Integer type = (Integer) map.get("type");
			MapItemEntity entity = new MapItemEntity(id, extension, type);
			entitys.add(entity);
		}
		return entitys;
	}

	public List<Pair<CacheKey, List<? extends IMapItem>>> load(List<Pair<CacheKey, String>> searchInfos, String userId) {
		Object[] param = new Object[] { userId };
		ArrayList<Tuple<CacheKey, String, MapItemRowBuider<? extends IMapItem>>> sqls = new ArrayList<Tuple<CacheKey, String, MapItemRowBuider<? extends IMapItem>>>();
		for (int i = 0, size = searchInfos.size(); i < size; i++) {
			Pair<CacheKey, String> info = searchInfos.get(i);
			CacheKey pairKey = info.getT1();
			Pair<String, MapItemRowBuider<? extends IMapItem>> tableInfo = storeInfos.get(pairKey);
			if (tableInfo == null) {
				continue;
			}
			String tableName = info.getT2();
			String searchKey = tableInfo.getT1();
			String sql = "select * from " + tableName + " where " + searchKey + "=?";
			sqls.add(Tuple.<CacheKey, String, MapItemRowBuider<? extends IMapItem>> Create(pairKey, sql, tableInfo.getT2()));
		}
		ArrayList<Pair<CacheKey, List<? extends IMapItem>>> datas = new ArrayList<Pair<CacheKey, List<? extends IMapItem>>>();
		ArrayList<Tuple<CacheKey, List<Map<String, Object>>, MapItemRowBuider<? extends IMapItem>>> resultMaps = new ArrayList<Tuple<CacheKey, List<Map<String, Object>>, MapItemRowBuider<? extends IMapItem>>>();
		final int size = sqls.size();
		TransactionStatus ts = tm.getTransaction(df);
		try {
			for (int i = 0; i < size; i++) {
				Tuple<CacheKey, String, MapItemRowBuider<? extends IMapItem>> info = sqls.get(i);
				List<Map<String, Object>> list = template.queryForList(info.getT2(), param);
				if (list != null) {
					Tuple<CacheKey, List<Map<String, Object>>, MapItemRowBuider<? extends IMapItem>> tuple = Tuple.<CacheKey, List<Map<String, Object>>, MapItemRowBuider<? extends IMapItem>> Create(info.getT1(), list, info.getT3());
					resultMaps.add(tuple);
				}
			}
			tm.commit(ts);
		} catch (Throwable t) {
			tm.rollback(ts);
			t.printStackTrace();
		}
		for (int i = resultMaps.size(); --i >= 0;) {
			Tuple<CacheKey, List<Map<String, Object>>, MapItemRowBuider<? extends IMapItem>> resultTuple = resultMaps.get(i);
			List<Map<String, Object>> result = resultTuple.getT2();
			int resultSize = result.size();
			ArrayList<IMapItem> mapItems = new ArrayList<IMapItem>(resultSize);
			for (int j = 0; j < resultSize; j++) {
				MapItemRowBuider<? extends IMapItem> builder = resultTuple.getT3();
				IMapItem item = builder.mapRow(result.get(j));
				mapItems.add(item);
			}
			Pair<CacheKey, List<? extends IMapItem>> pp = Pair.<CacheKey, List<? extends IMapItem>> Create(resultTuple.getT1(), mapItems);
			datas.add(pp);
		}
		return datas;
	}

	@Override
	public String getTableName(String userId) {
		int index = DataAccessFactory.getSimpleSupport().getTableIndex(userId, mapItemTableName.length);
		return mapItemTableName[index];
	}

}
