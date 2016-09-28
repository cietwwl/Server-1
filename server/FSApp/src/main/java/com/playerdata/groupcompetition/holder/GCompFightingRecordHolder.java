package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.mina.util.ConcurrentHashSet;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCompFightingRecordDAO;
import com.playerdata.groupcompetition.holder.data.GCompFightingRecord;
import com.playerdata.groupcompetition.holder.data.GCompPersonFightingRecord;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCompFightingRecordHolder {

	private static final GCompFightingRecordHolder _instance = new GCompFightingRecordHolder();
	
	public static final GCompFightingRecordHolder getInstance() {
		return _instance;
	}
	
	private GCompFightingRecordDAO _dao;
	//等待在直播页面的玩家
	private HashMap<Integer, ConcurrentHashSet<String>> liveUsers = new HashMap<Integer, ConcurrentHashSet<String>>();
	private AtomicLong recordIDCreator = new AtomicLong(System.currentTimeMillis());
	
	protected GCompFightingRecordHolder() {
		this._dao = GCompFightingRecordDAO.getInstance();
	}
	
	public void syn(Player player, int matchId, long time) {
		List<GCompFightingRecord> allRecords = _dao.getFightingRecord(matchId);
		List<GCompFightingRecord> synRecords = new ArrayList<GCompFightingRecord>();
		Iterator<GCompFightingRecord> itor = allRecords.iterator();
		while(itor.hasNext()){
			GCompFightingRecord record = itor.next();
			if(record.getTime() > time){
				synRecords.add(record);
			}
		}
		if (!synRecords.isEmpty()) {
			ClientDataSynMgr.updateDataList(player, synRecords, eSynType.GCompFightingRecord, eSynOpType.UPDATE_PART_LIST);
		}else{
			for(int i = 0; i < 6; i++){
				GCompFightingRecord rc = new GCompFightingRecord();
				rc.setId(String.valueOf(recordIDCreator.getAndIncrement()));
				rc.setMatchId(matchId + i%2);
				List<GCompPersonFightingRecord> recordList = new ArrayList<GCompPersonFightingRecord>();
				for(int j = 0; j < 3; j++){
					GCompPersonFightingRecord record = new GCompPersonFightingRecord();
					record.setOffendName("哈哈哈ss" + j);
					record.setDefendName("哦哦哦tt" + j);
					record.setGroupScore(3);
					record.setPersonalScore(2);
					record.setContinueWin(3);
					record.setOffendWin(true);
					recordList.add(record);
				}
				rc.setPersonalFightingRecords(recordList);
				synRecords.add(rc);
			}
			ClientDataSynMgr.updateDataList(player, synRecords, eSynType.GCompFightingRecord, eSynOpType.UPDATE_PART_LIST);
		}
	}
	
	public void initRecordList(int matchId) {
		ConcurrentHashSet<String> needSyn = liveUsers.get(matchId);
		if(null == needSyn){
			needSyn = new ConcurrentHashSet<String>();
			liveUsers.put(matchId, needSyn);
		}else{
			needSyn.clear();
		}
		_dao.initRecordList(matchId);
	}
	
	public void add(int matchId, GCompFightingRecord record) {
		record.setId(String.valueOf(recordIDCreator.incrementAndGet()));
		_dao.add(matchId, record);
		// 同步到相关的人
		ConcurrentHashSet<String> synUserIds= liveUsers.get(matchId);
		List<Player> needSynPlayers = new ArrayList<Player>();
		Iterator<String> itor = synUserIds.iterator();
		while(itor.hasNext()){
			Player player = PlayerMgr.getInstance().findPlayerFromMemory(itor.next());
			if(null != player){
				needSynPlayers.add(player);
			}else{
				//如果玩家已经不在线，就删除
				itor.remove();
			}
		}
		if(!needSynPlayers.isEmpty()){
			ClientDataSynMgr.synDataMutiple(needSynPlayers, record, eSynType.GCompFightingRecord, eSynOpType.UPDATE_SINGLE);
			GCompDetailInfoHolder.getInstance().synPlayers(matchId, needSynPlayers);
		}
	}
	
	/**
	 * 获取直播数据
	 * @param player
	 * @param matchId
	 * @param time 只同步这个时间之后的，如果想同步全部的，就传0
	 */
	public void getFightRecordLive(Player player, int matchId, long time){
		ConcurrentHashSet<String> userIds = liveUsers.get(matchId);
		if(null != userIds){
			userIds.add(player.getUserId());
		}
		syn(player, matchId, time);
		GCompDetailInfoHolder.getInstance().syn(matchId, player);
	}
	
	/**
	 * 离开直播页面
	 * @param player
	 * @param matchId
	 */
	public void leaveLivePage(Player player, int matchId){
		if(matchId > 0){
			ConcurrentHashSet<String> userIds = liveUsers.get(matchId);
			if(null != userIds){
				userIds.remove(player.getUserId());
			}
		}else{
			leaveLivePage(player);
		}
	}
	
	/**
	 * 离开直播页面(没传赛事id)
	 * @param player
	 */
	public void leaveLivePage(Player player){
		for(int matchId : liveUsers.keySet()){
			leaveLivePage(player, matchId);
		}
	}
	
	/**
	 * 结束当前正在直播的比赛
	 */
	public void endAllLiveMatch(){
		liveUsers.clear();
	}
}
