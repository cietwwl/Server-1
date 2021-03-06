package com.rwbase.dao.majorDatas;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.rw.fsutil.cacheDao.ObjectConvertor;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.dao.cache.DataDeletedException;
import com.rw.fsutil.dao.cache.DataKVCache;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.PersistentLoader;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.trace.listener.MajorDataListener;
import com.rwbase.dao.majorDatas.pojo.MajorData;

public class MajorDataCache {

	private final DataKVCache<String, MajorData> cache;
	private final JdbcTemplate template;

	public MajorDataCache(JdbcTemplate template) {
		this.template = template;
		// 数量需要做成配置
		int capcity = 5000;
		DataValueParser<MajorData> parser = (DataValueParser<MajorData>) DataCacheFactory.getParser(MajorData.class);
		this.cache = DataCacheFactory.createDataKVCache(MajorData.class, capcity, 60, new MajorDataLoader(), null, parser == null ? null
				: new ObjectConvertor<MajorData>(parser), MajorDataListener.class);
	}

	public MajorData get(String userId) {
		try {
			return this.cache.getOrLoadFromDB(userId);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public void updateCoin(MajorData data) {
		cache.submitUpdateTask(data.getId());
	}

	public boolean update(MajorData data) {
		try {
			cache.put(data.getId(), data);
			return true;
		} catch (DataDeletedException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	public void updateGold(MajorData data) {
		int[] values = new int[] { data.getGold(), data.getGiftGold(), data.getChargeGold() };
		update("update majordata set gold=?, giftGold=?, chargeGold=? where id=?", values, data.getId());
	}

	private void update(String sql, int[] values, String userId) {
		new MajorUpdateTask(sql, values, userId).run();
	}

	class MajorUpdateTask implements Runnable {

		private final String sql;
		private final int[] values;// 这里最好是long或者Object
		private final String userId;

		public MajorUpdateTask(String sql, int[] values, String userId) {
			this.sql = sql;
			this.values = values;
			this.userId = userId;
		}

		@Override
		public void run() {
			try {
				template.update(sql, new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						int index = 1;
						for (int val : values) {
							ps.setInt(index, val);
							index++;
						}
						ps.setString(index, userId);
					}
				});
				cache.submitRecordTask(userId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Long valueOf(Object value) {
		if (value instanceof Long) {
			return (Long) value;
		}
		// 直接抛出异常
		Number n = (Number) value;
		return Long.valueOf(n.longValue());
	}

	class MajorDataLoader extends PersistentLoader<String, MajorData> {

		private final String updateSql;

		public MajorDataLoader() {
			this.updateSql = "update majordata set coin=?,gold=?,chargeGold=?,giftGold=? where id=?";
		}

		@Override
		public MajorData load(String key) throws DataNotExistException, Exception {
			List<Map<String, Object>> set = template.queryForList("select coin,gold,giftGold,chargeGold from majordata where id=?", key);
			if (set == null || set.isEmpty()) {
				throw new DataNotExistException();
			}
			Map<String, Object> map = set.get(0);
			MajorData data = new MajorData();
			data.setId(key);
			data.setCoin(valueOf(map.get("coin")));
			data.setGold((Integer) map.get("gold"));
			data.setGiftGold((Integer) map.get("giftGold"));
			data.setChargeGold((Integer) map.get("chargeGold"));
			return data;
		}

		@Override
		public boolean delete(String key) throws DataNotExistException, Exception {
			return false;
		}

		@Override
		public boolean insert(String key, final MajorData value) throws DuplicatedKeyException, Exception {
			int result = template.update("insert into majordata (id,coin,gold,chargeGold,giftGold) values(?,?,?,?,?)", new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, value.getId());
					ps.setLong(2, value.getCoin());
					ps.setInt(3, value.getGold());
					ps.setInt(4, value.getChargeGold());
					ps.setInt(5, value.getGiftGold());
				}
			});
			return result > 0;
		}

		@Override
		public boolean updateToDB(String key, final MajorData value) {
			try {
				template.update(updateSql, new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setLong(1, value.getCoin());
						ps.setInt(2, value.getGold());
						ps.setInt(3, value.getChargeGold());
						ps.setInt(4, value.getGiftGold());
						ps.setString(5, value.getId());
					}
				});
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public String getTableName(String key) {
			return "majordata";
		}

		@Override
		public Map<String, String> getUpdateSqlMapping() {
			HashMap<String, String> map = new HashMap<String, String>(2);
			map.put("majordata", this.updateSql);
			return map;
		}

		@Override
		public Object[] extractParams(String key, MajorData value) {
			Object[] array = new Object[5];
			array[0] = value.getCoin();
			array[1] = value.getGold();
			array[2] = value.getChargeGold();
			array[3] = value.getGiftGold();
			array[4] = value.getId();
			return array;
		}

		@Override
		public boolean hasChanged(String key, MajorData value) {
			return true;
		}

	}
}
