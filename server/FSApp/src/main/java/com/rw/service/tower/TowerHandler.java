package com.rw.service.tower;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.TowerMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.pve.PveHandler;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.dao.anglearray.pojo.db.TableAngleArrayData;
import com.rwbase.dao.anglearray.pojo.db.TableAngleArrayFloorData;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.tower.pojo.TowerHeroChange;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;
import com.rwproto.TowerServiceProtos.MsgTowerRequest;
import com.rwproto.TowerServiceProtos.MsgTowerResponse;
import com.rwproto.TowerServiceProtos.TagTowerData;
import com.rwproto.TowerServiceProtos.TagTowerHeadInfo;
import com.rwproto.TowerServiceProtos.TagTowerHeroChange;
import com.rwproto.TowerServiceProtos.eTowerDeadType;
import com.rwproto.TowerServiceProtos.eTowerResultType;
import com.rwproto.TowerServiceProtos.eTowerType;

public class TowerHandler {
	private static TowerHandler instance;
	private static final int TOTAL_TOWER_NUM = 15;// 总塔层

	private Comparator<TagTowerHeadInfo> comparator = new Comparator<TagTowerHeadInfo>() {

		@Override
		public int compare(TagTowerHeadInfo o1, TagTowerHeadInfo o2) {
			if (o1.getTowerId() < o2.getTowerId())
				return -1;
			if (o1.getTowerId() > o2.getTowerId())
				return 1;
			return 0;
		}
	};

	private TowerHandler() {
	}

	public static TowerHandler getInstance() {
		if (instance == null) {
			instance = new TowerHandler();
		}
		return instance;
	}

	// 关卡的状态
	public enum FloorState {
		UN_PASS, UN_AWARD, FINISH;
	}

	/**
	 * 获取面板数据
	 * 
	 * @param request
	 * @param player
	 * @return
	 */
	public ByteString getTowerPanelInfo(MsgTowerRequest request, Player player) {
		MsgTowerResponse.Builder response = MsgTowerResponse.newBuilder();
		response.setTowerType(request.getTowerType());

		String userId = player.getUserId();
		int level = player.getLevel();
		int openLevel = CfgOpenLevelLimitDAO.getInstance().checkIsOpen(eOpenLevelType.TOWER, level);
		if (openLevel != -1) {
			GameLog.error("万仙阵获取面板信息", userId, String.format("万仙阵需要角色[%s]级才开启，当前角色等级是[%s]", openLevel, level));
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		TowerMgr towerMgr = player.getTowerMgr();
		TableAngleArrayData angleArrayData = towerMgr.getAngleArrayData();
		if (angleArrayData == null) {
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		int curFloorState = angleArrayData.getCurFloorState();
		if (curFloorState == -1) {
			towerMgr.resetAngleArrayData(player, true);
		}

		int floor = angleArrayData.getCurFloor();
		TagTowerData towerData = getTowerData(player, floor, false);
		response.setTowerData(towerData);
		response.setTowerResultType(eTowerResultType.TOWER_SUCCESS);
		return response.build().toByteString();
	}

	/**
	 * 填充消息
	 * 
	 * @param player
	 * @param floor
	 * @param isAddInfo
	 * @return
	 */
	private TagTowerData getTowerData(Player player, int floor, boolean isAddInfo) {
		TowerMgr towerMgr = player.getTowerMgr();
		TableAngleArrayData angleArrayData = towerMgr.getAngleArrayData();
		if (angleArrayData == null) {
			return null;
		}

		TableAngleArrayFloorData floorData = towerMgr.getAngleArrayFloorData();
		if (floorData == null) {
			return null;
		}

		TagTowerData.Builder towerData = TagTowerData.newBuilder();
		int curFloor = angleArrayData.getCurFloor();
		int curFloorState = angleArrayData.getCurFloorState();

		int maxFloor = angleArrayData.getMaxFloor();
		towerData.setCurrTowerID(curFloor);// 当前层
		towerData.setUserId(angleArrayData.getUserId());// 角色Id？
		towerData.setEnemyTowerID(floor);// 敌人数据Id？
		towerData.setRefreshTimes(angleArrayData.getResetTimes());// 剩余的重置次数

		List<Boolean> openList = new ArrayList<Boolean>(TOTAL_TOWER_NUM);// 开放列表
		List<Boolean> firstList = new ArrayList<Boolean>(TOTAL_TOWER_NUM);// 第一次攻打列表
		List<Boolean> beatList = new ArrayList<Boolean>(TOTAL_TOWER_NUM);// 打败的列表
		List<Boolean> awardList = new ArrayList<Boolean>(TOTAL_TOWER_NUM);// 领奖的列表

		for (int tempFloor = 0; tempFloor < TOTAL_TOWER_NUM; tempFloor++) {
			if (tempFloor <= curFloor) {// 层数小于等于当前层，就会开放
				openList.add(true);
				if (tempFloor == curFloor) {
					if (curFloorState == FloorState.UN_PASS.ordinal()) {
						beatList.add(false);// 与当前层相同，就说明还没打过，并且还没有领取奖励
						awardList.add(false);// 未领奖
					} else if (curFloorState == FloorState.FINISH.ordinal()) {
						beatList.add(true);
						awardList.add(true);
					} else {
						beatList.add(true);
						awardList.add(false);
					}
				} else {
					beatList.add(true);
					awardList.add(true);
				}
			} else {
				openList.add(false);
				beatList.add(false);// 与当前层相同，就说明还没打过，并且还没有领取奖励
				awardList.add(false);// 未领奖
			}

			// 第一次列表
			if (tempFloor > maxFloor) {
				firstList.add(false);
			} else {
				firstList.add(true);
			}
		}

		towerData.addAllTowerOpenList(openList);
		towerData.addAllTowerFirstList(firstList);
		towerData.addAllTowerBeatList(beatList);
		towerData.addAllTowerGetArardList(awardList);

		List<TowerHeroChange> playerChangeList = angleArrayData.getHeroChangleList();
		for (int i = 0; i < playerChangeList.size(); i++) {
			TowerHeroChange tHeroChange = playerChangeList.get(i);
			TagTowerHeroChange.Builder heroChange = TagTowerHeroChange.newBuilder();
			heroChange.setUserId(tHeroChange.getRoleId());
			heroChange.setReduceLife(tHeroChange.getReduceLife());
			heroChange.setReduceEnegy(tHeroChange.getReduceEnegy());
			heroChange.setIsDead(eTowerDeadType.valueOf(tHeroChange.getHeroState()));
			towerData.addHeroChageMap(heroChange);
		}

		StringBuilder sb = new StringBuilder();
		// 敌方阵容信息
		Enumeration<ArmyInfo> tableEnemyInfoList = floorData.getEnemyEnumeration();
		List<TagTowerHeadInfo> enemyHeadList = new ArrayList<TagTowerHeadInfo>();
		int towerIdCount = 0;
		while (tableEnemyInfoList.hasMoreElements()) {
			ArmyInfo enemyInfo = (ArmyInfo) tableEnemyInfoList.nextElement();
			TagTowerHeadInfo headInfo = getTowerHeadInfo(enemyInfo, towerIdCount);
			enemyHeadList.add(headInfo);
			towerIdCount++;

			int fighting = enemyInfo.getPlayer().getFighting();
			List<ArmyHero> heroList = enemyInfo.getHeroList();
			for (int i = 0, size = heroList.size(); i < size; i++) {
				fighting += heroList.get(i).getFighting();
			}

			sb.append("\n层数：").append(towerIdCount).append("，名字：").append(enemyInfo.getPlayerName()).append("，战力：").append(fighting);
		}

		System.err.println(sb.toString());

		// 没有看到实质意义
		Collections.sort(enemyHeadList, comparator);
		towerData.addAllHeadInfos(enemyHeadList);

		if (isAddInfo) {
			ArmyInfo enemyInfo = floorData.getEnemyInfo(floor);// 层敌人数据
			try {
				String armyInfoClient = enemyInfo.toJson();
				towerData.setEnemyArmyInfo(armyInfoClient);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return towerData.build();
	}

	/**
	 * 获取数据
	 * 
	 * @param towerTableInfo
	 * @param towerId
	 * @return
	 */
	private TagTowerHeadInfo getTowerHeadInfo(ArmyInfo towerTableInfo, int towerId) {
		TagTowerHeadInfo.Builder headInfo = TagTowerHeadInfo.newBuilder();
		if (towerTableInfo != null) {
			headInfo.setUserId(towerTableInfo.getPlayer().getRoleBaseInfo().getId());
			headInfo.setTempleteId(towerTableInfo.getPlayer().getRoleBaseInfo().getTemplateId());
			headInfo.setHeadImage(towerTableInfo.getPlayerHeadImage());
			headInfo.setTowerId(towerId);
			headInfo.setLevel(towerTableInfo.getPlayer().getRoleBaseInfo().getLevel());
			if (towerTableInfo.getPlayerName() == null) {
				headInfo.setName("奇怪无名字");
			} else {
				headInfo.setName(towerTableInfo.getPlayerName());
			}

		}
		return headInfo.build();

	}

	/**
	 * 获取塔层敌人信息
	 * 
	 * @param request
	 * @param player
	 * @return
	 */
	public ByteString getTowerEnemyInfo(MsgTowerRequest request, Player player) {
		MsgTowerResponse.Builder response = MsgTowerResponse.newBuilder();
		response.setTowerType(request.getTowerType());

		String userId = player.getUserId();
		int level = player.getLevel();
		int openLevel = CfgOpenLevelLimitDAO.getInstance().checkIsOpen(eOpenLevelType.TOWER, level);
		if (openLevel != -1) {
			GameLog.error("万仙阵获取敌人信息", userId, String.format("万仙阵需要角色[%s]级才开启，当前角色等级是[%s]", openLevel, level));
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		TableAngleArrayFloorData floorData = player.getTowerMgr().getAngleArrayFloorData();
		if (floorData == null) {
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		int towerId = request.getTowerID();
		ArmyInfo enemyInfo = floorData.getEnemyInfo(towerId);
		if (enemyInfo == null) {
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		response.setTowerID(towerId);
		try {
			String enemyJon = enemyInfo.toJson();
			response.setArmyInfo(enemyJon);
			response.setTowerResultType(eTowerResultType.TOWER_SUCCESS);
		} catch (Exception e) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "数据转换错误");
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
		}

		return response.build().toByteString();
	}

	/**
	 * 更新塔层数据
	 * 
	 * @param request
	 * @param player
	 * @return
	 */
	public ByteString endFightTower(MsgTowerRequest request, Player player) {
		MsgTowerResponse.Builder response = MsgTowerResponse.newBuilder();
		response.setTowerType(request.getTowerType());

		String userId = player.getUserId();
		int level = player.getLevel();
		int openLevel = CfgOpenLevelLimitDAO.getInstance().checkIsOpen(eOpenLevelType.TOWER, level);
		if (openLevel != -1) {
			GameLog.error("万仙阵结束战斗", userId, String.format("万仙阵需要角色[%s]级才开启，当前角色等级是[%s]", openLevel, level));
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		TowerMgr towerMgr = player.getTowerMgr();
		TableAngleArrayData angleData = towerMgr.getAngleArrayData();
		if (angleData == null) {
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		// 敌人的主角信息修改
		TableAngleArrayFloorData floorData = towerMgr.getAngleArrayFloorData();
		if (floorData == null) {
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		TagTowerData requireTowerData = request.getTowerData();
		int towerId = requireTowerData.getCurrTowerID();

		// 验证关卡层
		if (towerId != angleData.getCurFloor()) {
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		int win = request.getWin();
		if (win == 1) {// 胜利
			if (towerId == TOTAL_TOWER_NUM) {
				MainMsgHandler.getInstance().sendPmdWxz(player);
			}

			angleData.setCurFloorState(FloorState.UN_AWARD.ordinal());
			towerMgr.saveAngleArrayData();
		}

		// 己方数据变化
		List<TagTowerHeroChange> heroChageMapList = requireTowerData.getHeroChageMapList();
		if (heroChageMapList != null && !heroChageMapList.isEmpty()) {// 玩家数据有改变更新
			towerMgr.updateHeroChange(returnTableChangeList(heroChageMapList));// 修改玩家的数据
		} else {
			GameLog.error("万仙阵战斗结束", userId, String.format("万仙阵[%s]层，结果[%s],客户端战斗结束后没有发送己方血量变化信息", towerId, win));
		}

		// 敌方改变数据
		List<TagTowerHeroChange> enemyChangeList = request.getEnemyHeroChangeListList();// 敌方数据改变
		if (enemyChangeList != null && !enemyChangeList.isEmpty()) {// 关卡敌人数据改变更新
			towerMgr.updateEnemyChange(player, towerId, returnTableChangeList(enemyChangeList));// 敌方改变数据
		} else {
			GameLog.error("万仙阵战斗结束", userId, String.format("万仙阵[%s]层，结果[%s],客户端战斗结束后没有发送敌方血量变化信息", towerId, win));
		}

		// 任务通知
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Tower, 1);

		TagTowerData towerData = getTowerData(player, towerId, true);
		response.setTowerData(towerData);
		response.setTowerResultType(eTowerResultType.TOWER_SUCCESS);

		// 战斗结束，推送pve消息给前端
		PveHandler.getInstance().sendPveInfo(player);

		return response.build().toByteString();
	}

	/**
	 * 返回更改信息
	 * 
	 * @param changeList
	 * @return
	 */
	private List<TowerHeroChange> returnTableChangeList(List<TagTowerHeroChange> changeList) {
		List<TowerHeroChange> tableHeroChangeList = new ArrayList<TowerHeroChange>();
		for (int i = 0, size = changeList.size(); i < size; i++) {
			TagTowerHeroChange heroChange = changeList.get(i);
			TowerHeroChange tableHeroChange = new TowerHeroChange();
			tableHeroChange.setRoleId(heroChange.getUserId());
			tableHeroChange.setReduceLife(heroChange.getReduceLife());
			tableHeroChange.setReduceEnegy(heroChange.getReduceEnegy());
			tableHeroChange.setHeroState(heroChange.getIsDead().getNumber());
			tableHeroChangeList.add(tableHeroChange);
		}
		return tableHeroChangeList;
	}

	/**
	 * 领取奖励
	 * 
	 * @param request
	 * @param player
	 * @return
	 */
	public ByteString getAward(MsgTowerRequest request, Player player) {
		MsgTowerResponse.Builder response = MsgTowerResponse.newBuilder();
		response.setTowerType(request.getTowerType());

		String userId = player.getUserId();
		int level = player.getLevel();
		int openLevel = CfgOpenLevelLimitDAO.getInstance().checkIsOpen(eOpenLevelType.TOWER, level);
		if (openLevel != -1) {
			GameLog.error("万仙阵获取奖励", userId, String.format("万仙阵需要角色[%s]级才开启，当前角色等级是[%s]", openLevel, level));
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		TowerMgr towerMgr = player.getTowerMgr();
		TableAngleArrayData angleData = towerMgr.getAngleArrayData();
		if (angleData == null) {
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		TableAngleArrayFloorData floorData = towerMgr.getAngleArrayFloorData();
		if (floorData == null) {
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		if (angleData.getCurFloorState() != FloorState.UN_AWARD.ordinal()) {// 如果是未通过状态不能领奖
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		int currTowerId = request.getTowerID();
		if (currTowerId != angleData.getCurFloor()) {
			GameLog.error("万仙阵获取奖励", userId, String.format("当前记录层是[%s],请求领奖层是[%s],数据不一致", angleData.getCurFloor(), currTowerId));
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		String totalArardStr = towerMgr.getAwardByFloor(player, currTowerId);// 奖品数据字符串
		if (totalArardStr.length() > 0) {
			response.setAwardListStr(totalArardStr);
			response.setTowerResultType(eTowerResultType.TOWER_SUCCESS);
		} else {
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
		}

		// 开放下层人物
		int nextTowerId = currTowerId + 1;
		if (nextTowerId >= TOTAL_TOWER_NUM) {
			angleData.setCurFloorState(FloorState.FINISH.ordinal());
		} else {
			angleData.setCurFloor(nextTowerId);
			angleData.setCurFloorState(FloorState.UN_PASS.ordinal());
		}

		if (currTowerId > angleData.getMaxFloor()) {
			angleData.setMaxFloor(currTowerId);
		}

		towerMgr.saveAngleArrayData();

		// 更新一下层
		if (nextTowerId < TOTAL_TOWER_NUM && (nextTowerId % TowerMgr.towerUpdateNum == 0)) {
			towerMgr.updateAngleArrayFloorData(angleData.getUserId(), angleData.getResetLevel(), angleData.getResetFighting(), angleData.getCurFloor(), false);
		}

		TagTowerData towerData = getTowerData(player, nextTowerId, false);
		response.setTowerData(towerData);
		response.setTowerID(currTowerId);// 更新上层状态
		response.setTowerResultType(eTowerResultType.TOWER_SUCCESS);
		return response.build().toByteString();
	}

	/**
	 * 重置数据
	 * 
	 * @param request
	 * @param player
	 * @return
	 */
	public ByteString restTowerData(MsgTowerRequest request, Player player) {
		MsgTowerResponse.Builder response = MsgTowerResponse.newBuilder();
		response.setTowerType(eTowerType.TOWER_RESET_DATA);

		String userId = player.getUserId();
		int level = player.getLevel();
		int openLevel = CfgOpenLevelLimitDAO.getInstance().checkIsOpen(eOpenLevelType.TOWER, level);
		if (openLevel != -1) {
			GameLog.error("万仙阵重置数据", userId, String.format("万仙阵需要角色[%s]级才开启，当前角色等级是[%s]", openLevel, level));
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		TowerMgr towerMgr = player.getTowerMgr();
		TableAngleArrayData angleArrayData = towerMgr.getAngleArrayData();
		if (angleArrayData == null) {
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		PrivilegeCfg privilegeCfg = PrivilegeCfgDAO.getInstance().getCfg(player.getVip());
		if (privilegeCfg.getExpeditionCount() - angleArrayData.getResetTimes() > 0) {
			towerMgr.resetAngleArrayData(player, false);// 玩家手动重置
			TagTowerData towerData = getTowerData(player, 0, false);
			response.setTowerData(towerData);
			response.setTowerResultType(eTowerResultType.TOWER_SUCCESS);
		} else {
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
		}
		return response.build().toByteString();
	}
}