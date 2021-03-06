package com.rwbase.dao.dropitem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import com.rw.service.dropitem.DropResult;
import com.rwbase.common.enu.eTaskFinishDef;

/**
 * 掉落记录存储实体
 * 
 * @author Jamaz
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "drop_record")
public class DropRecord {

	private static final String PRESENT = "1";
	@Id
	private String userId;
	private ConcurrentHashMap<Integer, String> firstDropMap;
	// key = DropRecordId
	private ConcurrentHashMap<Integer, Integer> dropMissTimesMap;
	// key = DropRuleId(只存在内存)
	private ConcurrentHashMap<Integer, DropResult> pretreatMap;
	
	public DropRecord() {
		this.pretreatMap = new ConcurrentHashMap<Integer, DropResult>();
		this.firstDropMap = new ConcurrentHashMap<Integer, String>();
		this.dropMissTimesMap = new ConcurrentHashMap<Integer, Integer>();
	}
	
//	private class MapSerializer extends JsonSerializer<Map>{
//
//		@Override
//		public void serialize(Map value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
//			ArrayList<String> list = new ArrayList<String>(value.size());
//			for(Object o:value.keySet()){
//				list.add(o.toString());
//			}
//			jgen.writeObject(list);
//		}	
//	}
//	
//	private class ListDeserializer extends JsonDeserializer<List>{
//
//		@Override
//		public List deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
//			// TODO Auto-generated method stub
//			return null;
//		}
//		
//	}
	
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

	public ConcurrentHashMap<Integer, String> getFirstDropMap() {
		return firstDropMap;
	}

	public void setFirstDropMap(ConcurrentHashMap<Integer, String> firstDropMap) {
		this.firstDropMap = firstDropMap;
	}

	public ConcurrentHashMap<Integer, Integer> getDropMissTimesMap() {
		return dropMissTimesMap;
	}

	public void setDropMissTimesMap(ConcurrentHashMap<Integer, Integer> dropMissTimesMap) {
		this.dropMissTimesMap = dropMissTimesMap;
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
