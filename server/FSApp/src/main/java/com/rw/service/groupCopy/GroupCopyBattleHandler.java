package com.rw.service.groupCopy;

import java.util.ArrayList;
import java.util.List;

import com.bm.groupCopy.GroupCopyResult;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfgDao;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.rwbase.dao.groupCopy.db.GroupCopyMonsterSynStruct;
import com.rwproto.GroupCopyBattleProto;
import com.rwproto.GroupCopyBattleProto.CopyRewardInfo;
import com.rwproto.GroupCopyBattleProto.GroupCopyBattleComReqMsg;
import com.rwproto.GroupCopyBattleProto.GroupCopyBattleComRspMsg;
import com.rwproto.GroupCopyBattleProto.GroupCopyMonsterData;
import com.rwproto.GroupCopyBattleProto.RequestType;

/*
 * @author HC
 * @date 2016年2月18日 下午3:16:30
 * @Description 帮派的基础处理
 */
public class GroupCopyBattleHandler {
	private static GroupCopyBattleHandler instance = new GroupCopyBattleHandler();

	public static GroupCopyBattleHandler getInstance() {
		return instance;
	}

	protected GroupCopyBattleHandler() {
	}

	/**
	 * TODO 协议有改动 详见 {@link GroupCopyBattleProto};
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString beginFight(Player player, GroupCopyBattleComReqMsg req) {
		GroupCopyBattleComRspMsg.Builder commonRsp = GroupCopyBattleComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.FIGHT_BEGIN);

		Group group = GroupHelper.getInstance().getGroup(player);
		boolean success = false;
		if (group != null) {
			String levelID = req.getLevel();
			int groupLevel = group.getGroupBaseDataMgr().getGroupData().getGroupLevel();
			GroupCopyLevelCfg levelCfg = GroupCopyLevelCfgDao.getInstance().getConfig(levelID);
			GroupCopyMapCfg mapCfg = GroupCopyMapCfgDao.getInstance().getCfgById(levelCfg.getChaterID());
			if (mapCfg.getUnLockLv() > groupLevel) {
				commonRsp.setTipMsg("帮派等级不足，无法进入战斗！");
			} else {
				GroupCopyResult result = group.getGroupCopyMgr().beginFight(player, levelID);

				success = result.isSuccess();
				if (success) {
					commonRsp.setMData((GroupCopyMonsterData.Builder) result.getItem());
				}
				commonRsp.setTipMsg(result.getTipMsg());
			}
		}

		commonRsp.setIsSuccess(success);
		return commonRsp.build().toByteString();
	}

	/**
	 * 客户端通知战斗结束，要求返回战斗掉落
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString endFight(Player player, GroupCopyBattleComReqMsg req) {
		GroupCopyBattleComRspMsg.Builder commonRsp = GroupCopyBattleComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.FIGHT_END);
		String levelID = req.getLevel();
		GroupCopyMonsterData mData = req.getMData();

		List<GroupCopyMonsterSynStruct> monsterList = new ArrayList<GroupCopyMonsterSynStruct>();
		GroupCopyMonsterSynStruct struct = null;
		for (String s : mData.getMonsterDataList()) {
			try {
				struct = (GroupCopyMonsterSynStruct) ClientDataSynMgr.fromClientJson2Data(GroupCopyMonsterSynStruct.class, s);
			} catch (Exception e) {
				e.printStackTrace();
			}
			monsterList.add(struct);
		}

		Group group = GroupHelper.getInstance().getGroup(player);

		boolean success = false;
		if (group != null) {
			GroupCopyResult result = group.getGroupCopyMgr().endFight(player, levelID, monsterList, req.getHeros().getIdList());
			success = result.isSuccess();
			if (result.getTipMsg() != null) {
				commonRsp.setTipMsg(result.getTipMsg());
			}
			if (result.getItem() != null) {
				commonRsp.setDropInfo((CopyRewardInfo.Builder) result.getItem());
			}

		}
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.GROUPCOPY_BATTLE, 1);
		commonRsp.setIsSuccess(success);
		return commonRsp.build().toByteString();
	}

	/**
	 * 客户端请求是否可以进入关卡战斗
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString applyEnterCopy(Player player, GroupCopyBattleComReqMsg req) {
		GroupCopyBattleComRspMsg.Builder commonRsp = GroupCopyBattleComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.ENTER_APPLY);
		String level = req.getLevel();
		Group group = GroupHelper.getInstance().getGroup(player);
		commonRsp.setIsSuccess(false);
		if (group != null) {
			group.getGroupCopyMgr().applyEnterCopy(player, level, commonRsp);
		} else {
			commonRsp.setTipMsg("角色不存在帮派");
		}
		return commonRsp.build().toByteString();
	}

	/**
	 * 作弊通关
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString cheatEndFight(Player player, GroupCopyBattleComReqMsg req) {
		GroupCopyBattleComRspMsg.Builder commonRsp = GroupCopyBattleComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.FIGHT_END);
		String levelID = req.getLevel();
		// GroupCopyMonsterData mData = req.getMData();
		Group group = GroupHelper.getInstance().getGroup(player);
		boolean success = false;
		if (group != null) {
			GroupCopyResult result = group.getGroupCopyMgr().endFight(player, levelID, null, null);
			success = result.isSuccess();
			commonRsp.setTipMsg(result.getTipMsg());
			commonRsp.setDropInfo((CopyRewardInfo) result.getItem());
		}
		commonRsp.setIsSuccess(success);
		return commonRsp.build().toByteString();
	}

}