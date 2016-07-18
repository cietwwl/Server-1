package com.rwbase.dao.groupsecret.pojo.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.playerdata.groupsecret.GroupSecretDefendRecordDataMgr;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretBaseCfgDAO;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendRecord;

/*
 * @author HC
 * @date 2016年5月26日 下午2:53:06
 * @Description 
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class GroupSecretDefendRecordData {
	private String userId;// 角色的
	private List<DefendRecord> recordList;// 记录的List
	@JsonIgnore
	private int oldestIndex = -1;// 最老的记录对应的Id

	public GroupSecretDefendRecordData() {
		recordList = new ArrayList<DefendRecord>();
		oldestIndex = -1;
	}

	// ////////////////////////////////////////////////逻辑Get区
	public String getUserId() {
		return userId;
	}

	// ////////////////////////////////////////////////逻辑Set区
	public void setUserId(String userId) {
		this.userId = userId;
	}

	// ////////////////////////////////////////////////逻辑区
	@JsonIgnore
	public synchronized void addRecord(DefendRecord record) {// 增加记录
		int newId = generateId(GroupSecretBaseCfgDAO.getCfgDAO().getUniqueCfg().getMaxDefendLogSize());
		if (newId == -1) {
			// 获取到需要删除的最老的记录
			if (oldestIndex == -1) {
				updateOldestIndex();
			}

			record.setId(oldestIndex);
			recordList.set(oldestIndex, record);
		} else if (recordList.isEmpty() || recordList.size() <= newId) {
			record.setId(newId);
			recordList.add(record);
		}

		updateOldestIndex();// 更新下新的最老的Id
	}

	/**
	 * 获取记录
	 * 
	 * @param id
	 * @return
	 */
	@JsonIgnore
	public synchronized DefendRecord getDefendRecord(int id) {
		for (int i = 0, size = recordList.size(); i < size; i++) {
			DefendRecord record = recordList.get(i);
			if (record == null) {
				continue;
			}

			if (record.getId() == id) {
				return record;
			}
		}

		return null;
	}

	/**
	 * 获取排序的领取记录
	 * 
	 * @param comparator
	 * @return
	 */
	public synchronized List<DefendRecord> getSortDefendRecordList(Comparator<DefendRecord> comparator, String userId) {
		if (recordList.isEmpty()) {
			return Collections.emptyList();
		}

		boolean hasModify = false;

		int size = recordList.size();
		List<DefendRecord> sortList = new ArrayList<DefendRecord>(size);
		for (int i = 0; i < size; i++) {
			DefendRecord record = recordList.get(i);
			if (record == null) {
				continue;
			}

			if (record.getId() != i) {
				record.setId(i);
				hasModify = true;
			}

			sortList.add(record);
		}

		if (hasModify) {
			GroupSecretDefendRecordDataMgr.getMgr().update(userId);
		}

		if (comparator != null) {
			Collections.sort(sortList, comparator);
		}

		return sortList;
	}

	/**
	 * 生成一个Id
	 * 
	 * @param maxId 允许生成的最大的Id
	 * @return -1：当前的记录数已经达到了最大的值，需要删除最老的记录
	 */
	@JsonIgnore
	private int generateId(int maxId) {
		int size = recordList.size();
		for (int i = 0; i < maxId; i++) {
			if (i >= size) {
				return size;
			}

			DefendRecord record = recordList.get(i);
			if (record == null) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 更新列表中最老的元素的索引
	 * 
	 * @return 返回一下当我第一个值被更新之后
	 */
	@JsonIgnore
	private void updateOldestIndex() {
		long oldTime = 0;
		int index = 0;

		int size = recordList.size();
		for (int i = 0; i < size; i++) {
			DefendRecord record = recordList.get(i);
			long robTime = record.getRobTime();
			if (oldTime == 0 || robTime < oldTime) {
				oldTime = robTime;
				index = i;
			}
		}

		oldestIndex = index;
	}
}