package com.rw.service.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.service.FsService;
import com.rw.service.Email.EmailHandler;
import com.rw.service.arena.ArenaHandler;
import com.rw.service.dailyActivity.DailyActivityHandler;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwproto.RequestProtos.Request;
import com.rwproto.TaskProtos.OneKeyGetRewardRequest;
import com.rwproto.TaskProtos.OneKeyGetRewardResponse;
import com.rwproto.TaskProtos.OneKeyResultType;
import com.rwproto.TaskProtos.OneKeyRewardType;

public class OneKeyService implements FsService<OneKeyGetRewardRequest, OneKeyRewardType> {

	private ByteString getAllDailyReward(Player player) {
		OneKeyGetRewardResponse.Builder resp = OneKeyGetRewardResponse.newBuilder();
		int openLevel = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.DAILY_ONE_KEY_LEVEL);
		if (player.getLevel() >= openLevel) {
			HashMap<Integer, Integer> rewardMap = new HashMap<Integer, Integer>();
			resp.setResult(DailyActivityHandler.getInstance().taskAllFinish(player, rewardMap));
			List<ItemInfo> items = new ArrayList<ItemInfo>();
			for (Integer key : rewardMap.keySet()) {
				ItemInfo item = new ItemInfo();
				item.setItemID(key);
				item.setItemNum(rewardMap.get(key));
				items.add(item);
				resp.addRewardItems(ClientDataSynMgr.toClientData(item));
			}
			ItemBagMgr.getInstance().addItem(player, items);
		} else {
			resp.setResult(OneKeyResultType.LEVEL_LIMIT);
		}
		return resp.build().toByteString();
	}

	private ByteString getAllTaskReward(Player player) {
		OneKeyGetRewardResponse.Builder resp = OneKeyGetRewardResponse.newBuilder();
		int openLevel = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.TASK_ONE_KEY_LEVEL);
		if (player.getLevel() >= openLevel) {
			HashMap<Integer, Integer> rewardMap = new HashMap<Integer, Integer>();
			resp.setResult(player.getTaskMgr().getAllReward(rewardMap));
			List<ItemInfo> items = new ArrayList<ItemInfo>();
			for (Integer key : rewardMap.keySet()) {
				ItemInfo item = new ItemInfo();
				item.setItemID(key);
				item.setItemNum(rewardMap.get(key));
				items.add(item);
				resp.addRewardItems(ClientDataSynMgr.toClientData(item));
			}
			ItemBagMgr.getInstance().addItem(player, items);
		} else {
			resp.setResult(OneKeyResultType.LEVEL_LIMIT);
		}
		return resp.build().toByteString();
	}

	private ByteString getAllEmailReward(Player player) {
		OneKeyGetRewardResponse.Builder resp = OneKeyGetRewardResponse.newBuilder();
		int openLevel = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.EMAIL_ONE_KEY_LEVEL);
		if (player.getLevel() >= openLevel) {
			HashMap<Integer, Integer> rewardMap = new HashMap<Integer, Integer>();
			resp.setResult(EmailHandler.getInstance().getAllAttachment(player, rewardMap));
			List<ItemInfo> items = new ArrayList<ItemInfo>();
			for (Integer key : rewardMap.keySet()) {
				ItemInfo item = new ItemInfo();
				item.setItemID(key);
				item.setItemNum(rewardMap.get(key));
				items.add(item);
				resp.addRewardItems(ClientDataSynMgr.toClientData(item));
			}
			ItemBagMgr.getInstance().addItem(player, items);
		} else {
			resp.setResult(OneKeyResultType.LEVEL_LIMIT);
		}
		return resp.build().toByteString();
	}

	private ByteString getAllBattleScoreReward(Player player) {
		OneKeyGetRewardResponse.Builder resp = OneKeyGetRewardResponse.newBuilder();
		int openLevel = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.BATTLE_SCORE_ONE_KEY_LEVEL);
		if (player.getLevel() >= openLevel) {
			HashMap<Integer, Integer> rewardMap = new HashMap<Integer, Integer>();
			resp.setResult(ArenaHandler.getInstance().getAllScoreReward(player, rewardMap));
			List<ItemInfo> items = new ArrayList<ItemInfo>();
			for (Integer key : rewardMap.keySet()) {
				ItemInfo item = new ItemInfo();
				item.setItemID(key);
				item.setItemNum(rewardMap.get(key));
				items.add(item);
				resp.addRewardItems(ClientDataSynMgr.toClientData(item));
			}
			ItemBagMgr.getInstance().addItem(player, items);
		} else {
			resp.setResult(OneKeyResultType.LEVEL_LIMIT);
		}
		return resp.build().toByteString();
	}

	@Override
	public ByteString doTask(OneKeyGetRewardRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			OneKeyRewardType reqType = request.getOneKeyType();
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
				OneKeyGetRewardResponse.Builder resp = OneKeyGetRewardResponse.newBuilder();
				resp.setResult(OneKeyResultType.TYPE_ERROR);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public OneKeyGetRewardRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		OneKeyGetRewardRequest req = OneKeyGetRewardRequest.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public OneKeyRewardType getMsgType(OneKeyGetRewardRequest request) {
		// TODO Auto-generated method stub
		return request.getOneKeyType();
	}
}
