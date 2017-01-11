package com.rwbase.dao.dropitem;

import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.log.GameLog;
import com.rw.service.dropitem.DropResult;

/**
 * 掉落记录存储实体
 * 
 * @author Jamaz
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "drop_record")
public class DropRecord {

	private static final Integer PRESENT = 1;
	@Id
	private String userId;
	private ConcurrentHashMap<Integer, Integer> firstDropMap;
	// key = DropRecordId
	private ConcurrentHashMap<Integer, Integer> dropMissTimesMap;
	// key = DropRuleId(只存在内存)
	@JsonIgnore
	private ConcurrentHashMap<Integer, DropResult> pretreatMap;

	public DropRecord() {
		this.pretreatMap = new ConcurrentHashMap<Integer, DropResult>(4, 1.0f, 1);
	}

	public DropRecord(String userId) {
		this.userId = userId;
		this.pretreatMap = new ConcurrentHashMap<Integer, DropResult>(4, 1.0f, 1);
		this.firstDropMap = new ConcurrentHashMap<Integer, Integer>(); 
		this.dropMissTimesMap = new ConcurrentHashMap<Integer, Integer>(); 
	}

	@JsonIgnore
	public int getDropRecordTimes(int dropRecordId) {
		Integer times = dropMissTimesMap.get(dropRecordId);
		return times == null ? 0 : times;
	}

	public int clearDropMissTimes(int dropRecordId) {
		Integer key = dropRecordId;
		for (;;) {
			Integer old = dropMissTimesMap.get(key);
			if (old == null) {
				return 0;
			}
			if (dropMissTimesMap.remove(key, old)) {
				return old;
			}
		}
	}

	public int addDropMissTimes(int dropRecordId, int offset) {
		Integer key = dropRecordId;
		for (;;) {
			Integer old = dropMissTimesMap.get(key);
			if (old == null) {
				Integer result = dropMissTimesMap.putIfAbsent(key, offset);
				if (result == null) {
					return offset;
				}
			} else {
				int newValue = old + offset;
				if (dropMissTimesMap.replace(key, old, newValue)) {
					return newValue;
				}
			}
		}
	}

	@JsonIgnore
	public DropResult extract(int dropRuleId) {
		return this.pretreatMap.remove(dropRuleId);
	}

	/**
	 * 获取已经预先生成的掉落列表
	 * 
	 * @param dropRecordId
	 * @return
	 */
	@JsonIgnore
	public DropResult getPretreatDropList(int dropRuleId) {
		return pretreatMap.get(dropRuleId);
	}

	@JsonIgnore
	public void putPretreatDropList(int dropRuleId, DropResult result) {
		pretreatMap.put(dropRuleId, result);
	}

	@JsonIgnore
	public boolean hasDropFirst(int dropRecordId) {
		return this.firstDropMap.containsKey(dropRecordId);
	}

	@JsonIgnore
	public void addFirstDrop(int dropRecordId) {
		this.firstDropMap.put(dropRecordId, PRESENT);
	}

	public ConcurrentHashMap<Integer, Integer> getFirstDropMap() {
		return firstDropMap;
	}

	public void setFirstDropMap(ConcurrentHashMap<Integer, Integer> firstDropMap) {
		if (firstDropMap == null) {
			GameLog.error("DropRecord", userId, "firstDropMap is null");
			this.firstDropMap = new ConcurrentHashMap<Integer, Integer>(8, 1.0f, 1);
		} else {
			int size = firstDropMap.size();
			this.firstDropMap = new ConcurrentHashMap<Integer, Integer>(size, 1.0f, 1);
			this.firstDropMap.putAll(firstDropMap);
		}
	}

	public ConcurrentHashMap<Integer, Integer> getDropMissTimesMap() {
		return dropMissTimesMap;
	}

	public void setDropMissTimesMap(ConcurrentHashMap<Integer, Integer> dropMissTimesMap) {
		if (dropMissTimesMap == null) {
			GameLog.error("DropRecord", userId, "firstDropMap is null");
			this.dropMissTimesMap = new ConcurrentHashMap<Integer, Integer>(8, 1.0f, 1);
		} else {
			int size = dropMissTimesMap.size();
			this.dropMissTimesMap = new ConcurrentHashMap<Integer, Integer>(size, 1.0f, 1);
			this.dropMissTimesMap.putAll(dropMissTimesMap);
		}
	}

	public ConcurrentHashMap<Integer, Integer> getRecordMap() {
		return dropMissTimesMap;
	}

	public void setRecordMap(ConcurrentHashMap<Integer, Integer> recordMap) {
		this.dropMissTimesMap = recordMap;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ConcurrentHashMap<Integer, DropResult> getPretreatMap() {
		return pretreatMap;
	}

	public void setPretreatMap(ConcurrentHashMap<Integer, DropResult> pretreatMap) {
		this.pretreatMap = pretreatMap;
	}

}
