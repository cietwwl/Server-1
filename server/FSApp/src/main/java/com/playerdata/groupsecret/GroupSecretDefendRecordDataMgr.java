package com.playerdata.groupsecret;

import java.util.Comparator;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.groupsecret.pojo.GroupSecretDefendRecordDataHolder;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretDefendRecordData;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendRecord;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/*
 * @author HC
 * @date 2016年6月2日 下午8:58:28
 * @Description 
 */
public class GroupSecretDefendRecordDataMgr {

	private Comparator<DefendRecord> comparator = new Comparator<DefendRecord>() {

		@Override
		public int compare(DefendRecord o1, DefendRecord o2) {
			if (o1.getRobTime() < o2.getRobTime()) {
				return 1;
			} else if (o1.getRobTime() < o2.getRobTime()) {
				return -1;
			}
			return 0;
		}
	};

	private static GroupSecretDefendRecordDataMgr mgr = new GroupSecretDefendRecordDataMgr();

	public static GroupSecretDefendRecordDataMgr getMgr() {
		return mgr;
	}

	/**
	 * 获取日志
	 * 
	 * @param userId
	 * @return
	 */
	private GroupSecretDefendRecordData get(String userId) {
		return GroupSecretDefendRecordDataHolder.getHolder().get(userId);
	}

	/**
	 * 更新日志数据
	 * 
	 * @param userId
	 */
	public void update(String userId) {
		GroupSecretDefendRecordDataHolder.getHolder().updateData(userId);
	}

	/**
	 * 获取防守记录
	 * 
	 * @param userId
	 * @param comparator
	 * @return
	 */
	public List<DefendRecord> getSortDefendRecordList(String userId) {
		return get(userId).getSortDefendRecordList(comparator, userId);
	}

	/**
	 * 添加防守记录
	 * 
	 * @param userId
	 * @param record
	 */
	public void addDefendRecord(Player player, DefendRecord record) {
		String userId = player.getUserId();
		get(userId).addRecord(record);
		update(userId);

		synData(player);
	}

	/**
	 * 获取某条防守记录
	 * 
	 * @param userId
	 * @param id
	 * @return
	 */
	public DefendRecord getDefendRecord(String userId, int id) {
		return get(userId).getDefendRecord(id);
	}

	/**
	 * 更新秘境防守记录的领取状态
	 * 
	 * @param player
	 * @param id
	 * @return
	 */
	public boolean updateDefendRecordKeyState(Player player, int id) {
		String userId = player.getUserId();
		DefendRecord defendRecord = get(userId).getDefendRecord(id);
		if (defendRecord == null) {
			return false;
		}

		defendRecord.setHasKey(false);
		update(userId);

		synData(player);
		return true;
	}

	private eSynType synType = eSynType.SECRETAREA_DEF_RECORD;

	public void synData(Player player) {
		List<DefendRecord> sortDefendRecordList = getSortDefendRecordList(player.getUserId());
		if (sortDefendRecordList.isEmpty()) {
			return;
		}

		ClientDataSynMgr.synDataList(player, sortDefendRecordList, synType, eSynOpType.UPDATE_LIST);
	}
}