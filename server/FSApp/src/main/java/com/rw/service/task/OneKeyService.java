package com.rw.service.task;

import java.util.HashMap;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.service.FsService;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwproto.RequestProtos.Request;
import com.rwproto.TaskProtos.OneKeyGetRewardRequest;
import com.rwproto.TaskProtos.OneKeyGetRewardResponse;
import com.rwproto.TaskProtos.OneKeyRewardType;

public class OneKeyService implements FsService {

	@Override
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			OneKeyGetRewardRequest req = OneKeyGetRewardRequest.parseFrom(request.getBody().getSerializedContent());
			OneKeyRewardType reqType = req.getOneKeyType();
			switch (reqType) {
			case DAILY:
				result = getAllDailyReward(player);
				break;
			case TASK:
				result = getAllTaskReward(player);
				break;
			case EMAIL:
				result = getAllEmailReward(player);
				break;
			case BATTLE_SCORE:
				result = getAllBattleScoreReward(player);
				break;
			default:
				break;
			}
		}catch(InvalidProtocolBufferException e){
			e.printStackTrace();
		}
		return result;
	}
	
	private ByteString getAllDailyReward(Player player) {
		OneKeyGetRewardResponse.Builder resp = OneKeyGetRewardResponse.newBuilder();
		HashMap<Integer, Integer> rewardMap = new HashMap<Integer, Integer>();
		resp.setResult(player.getTaskMgr().getAllReward(rewardMap));
		for(Integer key : rewardMap.keySet()){
			ItemInfo item = new ItemInfo();
			item.setItemID(key);
			item.setItemNum(rewardMap.get(key));
			resp.addRewardItems(ClientDataSynMgr.toClientData(item));
		}
		return resp.build().toByteString();
	}
	
	private ByteString getAllTaskReward(Player player) {
		OneKeyGetRewardResponse.Builder resp = OneKeyGetRewardResponse.newBuilder();
		HashMap<Integer, Integer> rewardMap = new HashMap<Integer, Integer>();
		resp.setResult(player.getTaskMgr().getAllReward(rewardMap));
		for(Integer key : rewardMap.keySet()){
			ItemInfo item = new ItemInfo();
			item.setItemID(key);
			item.setItemNum(rewardMap.get(key));
			resp.addRewardItems(ClientDataSynMgr.toClientData(item));
		}
		return resp.build().toByteString();
	}
	
	private ByteString getAllEmailReward(Player player) {
		OneKeyGetRewardResponse.Builder resp = OneKeyGetRewardResponse.newBuilder();
		HashMap<Integer, Integer> rewardMap = new HashMap<Integer, Integer>();
		resp.setResult(player.getTaskMgr().getAllReward(rewardMap));
		for(Integer key : rewardMap.keySet()){
			ItemInfo item = new ItemInfo();
			item.setItemID(key);
			item.setItemNum(rewardMap.get(key));
			resp.addRewardItems(ClientDataSynMgr.toClientData(item));
		}
		return resp.build().toByteString();
	}
	
	private ByteString getAllBattleScoreReward(Player player) {
		OneKeyGetRewardResponse.Builder resp = OneKeyGetRewardResponse.newBuilder();
		HashMap<Integer, Integer> rewardMap = new HashMap<Integer, Integer>();
		resp.setResult(player.getTaskMgr().getAllReward(rewardMap));
		for(Integer key : rewardMap.keySet()){
			ItemInfo item = new ItemInfo();
			item.setItemID(key);
			item.setItemNum(rewardMap.get(key));
			resp.addRewardItems(ClientDataSynMgr.toClientData(item));
		}
		return resp.build().toByteString();
	}
}
