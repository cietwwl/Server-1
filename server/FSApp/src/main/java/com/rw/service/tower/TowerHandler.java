package com.rw.service.tower;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.common.RefParam;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.TowerMgr;
import com.playerdata.activity.retrieve.userFeatures.UserFeatruesMgr;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.playerdata.army.ArmyInfo;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIActivityCode;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rw.service.log.template.BilogItemInfo;
import com.rw.service.pve.PveHandler;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.dao.angelarray.AngelArrayConst;
import com.rwbase.dao.angelarray.AngelArrayUtils;
import com.rwbase.dao.angelarray.pojo.db.TableAngelArrayData;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.tower.pojo.TowerHeroChange;
import com.rwproto.PrivilegeProtos.PvePrivilegeNames;
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
		UN_Battle(-2), // 还没有正式战斗
		UN_Join(-1), // 还没有成功加入万仙阵
		UN_PASS(0), // 未成功
		UN_AWARD(1), // 成功未领奖
		FINISH(2);// 整个万仙阵结束

		public final int value;

		private FloorState(int value) {
			this.value = value;
		}
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
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.TOWER, player, outTip)) {
			GameLog.error("万仙阵获取面板信息", userId, outTip.value);
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		TowerMgr towerMgr = player.getTowerMgr();
		TableAngelArrayData angleArrayData = towerMgr.getAngleArrayData();
		if (angleArrayData == null) {
			GameLog.error("万仙阵获取面板信息", userId, "万仙阵获取不到个人的TableAngleArrayData数据");
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
		String userId = player.getUserId();
		TowerMgr towerMgr = player.getTowerMgr();
		TableAngelArrayData angleArrayData = towerMgr.getAngleArrayData();
		if (angleArrayData == null) {
			GameLog.error("getTowerData()-Method", userId, "万仙阵获取不到个人的TableAngleArrayData数据");
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

		int totalTowerNum = AngelArrayConst.TOTAL_TOWER_NUM;
		List<Boolean> openList = new ArrayList<Boolean>(totalTowerNum);// 开放列表
		List<Boolean> firstList = new ArrayList<Boolean>(totalTowerNum);// 第一次攻打列表
		List<Boolean> beatList = new ArrayList<Boolean>(totalTowerNum);// 打败的列表
		List<Boolean> awardList = new ArrayList<Boolean>(totalTowerNum);// 领奖的列表

		for (int tempFloor = 0; tempFloor < totalTowerNum; tempFloor++) {
			if (tempFloor <= curFloor) {// 层数小于等于当前层，就会开放
				openList.add(true);
				if (tempFloor == curFloor) {
					if (curFloorState == FloorState.UN_PASS.value || curFloorState == FloorState.UN_Battle.value) {
						beatList.add(false);// 与当前层相同，就说明还没打过，并且还没有领取奖励
						awardList.add(false);// 未领奖
					} else if (curFloorState == FloorState.FINISH.value) {
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
		for (int i = 0, heroChangeSize = playerChangeList.size(); i < heroChangeSize; i++) {
			TowerHeroChange tHeroChange = playerChangeList.get(i);
			TagTowerHeroChange.Builder heroChange = TagTowerHeroChange.newBuilder();
			heroChange.setUserId(tHeroChange.getRoleId());
			heroChange.setReduceLife(tHeroChange.getReduceLife());
			heroChange.setReduceEnegy(tHeroChange.getReduceEnegy());
			heroChange.setIsDead(eTowerDeadType.valueOf(tHeroChange.getHeroState()));
			towerData.addHeroChageMap(heroChange);
		}

		// 敌方阵容信息
		List<String> readOnlyKeyList = towerMgr.getEnemyInfoIdList();

		// TODO HC 检查万仙阵的层信息
		int size = readOnlyKeyList.size();
		int curGroupId = curFloor / AngelArrayConst.TOWER_UPDATE_NUM + 1;// 按照3层为一组，当前层应该是第几组
		int maxGroupSize = curGroupId * AngelArrayConst.TOWER_UPDATE_NUM;// 检查当前组中最大的层数应该是多少
		if (size < maxGroupSize) {// 当前随机到的人数不能足够，最大层，需要补缺，需要补缺的范围是从size到maxGroupSize
			towerMgr.updateAngleArrayFloorData(userId, angleArrayData.getResetLevel(), angleArrayData.getResetFighting(), size, false);
		}

		readOnlyKeyList = towerMgr.getEnemyInfoIdList();
		size = readOnlyKeyList.size();
		if (size < maxGroupSize) {
			GameLog.error("getTowerData()-Method", userId, String.format("个人万仙阵匹配敌人信息不完整,curFloor[%s],maxGroupFloor[%s]", curFloor, maxGroupSize));
		}

		List<TagTowerHeadInfo> enemyHeadList = new ArrayList<TagTowerHeadInfo>();
		for (int i = 0; i < size; i++) {
			String id = readOnlyKeyList.get(i);
			enemyHeadList.add(getTowerHeadInfo(towerMgr.getEnemyArmyInfo(id), towerMgr.getKey4FloorId(id)));
		}

		// 没有看到实质意义
		Collections.sort(enemyHeadList, comparator);
		towerData.addAllHeadInfos(enemyHeadList);

		// 填充一下自己法宝的剩余怒气
		towerData.setMagicPercent(angleArrayData.getMagic());

		if (isAddInfo) {
			ArmyInfo enemyInfo = towerMgr.getEnemyArmyInfo(AngelArrayUtils.getAngelArrayFloorDataId(userId, floor));// 层敌人数据
			try {
				String armyInfoClient = enemyInfo.toJson();
				towerData.setEnemyArmyInfo(armyInfoClient);
			} catch (Exception e) {
				GameLog.error("getTowerData()-Method", userId, "附加敌人信息的时候，不能正常处理，AttachEnemyInfo，throwException", e);
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
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.TOWER, player, outTip)) {
			GameLog.error("万仙阵获取敌人信息", userId, outTip.value);
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		int towerId = request.getTowerID();
		ArmyInfo enemyInfo = player.getTowerMgr().getEnemyArmyInfo(AngelArrayUtils.getAngelArrayFloorDataId(userId, towerId));
		if (enemyInfo == null) {
			GameLog.error("万仙阵获取敌人信息", userId, String.format("万仙阵获取第[%s]层，EnemyInfo Not Exist!", towerId));
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		response.setTowerID(towerId);
		try {
			String enemyJon = enemyInfo.toJson();
			response.setArmyInfo(enemyJon);
			response.setTowerResultType(eTowerResultType.TOWER_SUCCESS);
		} catch (Exception e) {
			GameLog.error("万仙阵获取敌人信息", userId, "Set EnemyInfo Json To Protobuf throw Exception", e);
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
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.TOWER, player, outTip)) {
			GameLog.error("万仙阵结束战斗", userId, outTip.value);
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		TowerMgr towerMgr = player.getTowerMgr();
		TableAngelArrayData angleData = towerMgr.getAngleArrayData();
		if (angleData == null) {
			GameLog.error("万仙阵战斗结束", userId, "角色对应的TableAngleArratData的数据为Null");
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		TagTowerData requireTowerData = request.getTowerData();
		int towerId = requireTowerData.getCurrTowerID();

		// 验证关卡层
		int curFloor = angleData.getCurFloor();
		if (towerId != curFloor) {
			GameLog.error("万仙阵战斗结束", userId, String.format("万仙阵当前[%s]层，客户端发送层[%s]，与服务器数据不一致", curFloor, towerId));
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		int win = request.getWin();
		if (win == 1) {// 胜利
			if (towerId == AngelArrayConst.TOTAL_TOWER_NUM) {
				MainMsgHandler.getInstance().sendPmdWxz(player);
			}

			angleData.setCurFloorState(FloorState.UN_AWARD.value);
			towerMgr.saveAngleArrayData();
		} else {
			if (angleData.getCurFloorState() != FloorState.UN_PASS.value) {
				angleData.setCurFloorState(FloorState.UN_PASS.value);
				towerMgr.saveAngleArrayData();
			}
		}

		// 己方数据变化
		List<TagTowerHeroChange> heroChageMapList = requireTowerData.getHeroChageMapList();
		if (heroChageMapList != null && !heroChageMapList.isEmpty()) {// 玩家数据有改变更新
			towerMgr.updateHeroChange(returnTableChangeList(heroChageMapList), requireTowerData.getMagicPercent());// 修改玩家的数据
		} else {
			GameLog.error("万仙阵战斗结束", userId, String.format("万仙阵[%s]层，结果[%s],客户端战斗结束后没有发送己方血量变化信息", towerId, win));
		}

		// 敌方改变数据
		List<TagTowerHeroChange> enemyChangeList = request.getEnemyHeroChangeListList();// 敌方数据改变
		if (enemyChangeList != null && !enemyChangeList.isEmpty()) {// 关卡敌人数据改变更新
			towerMgr.updateEnemyChange(player, towerId, returnTableChangeList(enemyChangeList), request.getMagicPercent());// 敌方改变数据
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
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.TOWER, player, outTip)) {
			GameLog.error("万仙阵获取奖励", userId, outTip.value);
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		TowerMgr towerMgr = player.getTowerMgr();
		TableAngelArrayData angleData = towerMgr.getAngleArrayData();
		if (angleData == null) {
			GameLog.error("万仙阵获取奖励", userId, "万仙阵获取不到TableAngleArrayData Null");
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		int currTowerId = request.getTowerID();
		if (angleData.getCurFloorState() != FloorState.UN_AWARD.value) {// 如果是未通过状态不能领奖
			GameLog.error("万仙阵获取奖励", userId, String.format("万仙阵第[%s]层，状态是[%s]，Can't Award", currTowerId, angleData.getCurFloorState()));
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		if (currTowerId != angleData.getCurFloor()) {
			GameLog.error("万仙阵获取奖励", userId, String.format("当前记录层是[%s],请求领奖层是[%s],数据不一致,client&server data not same", angleData.getCurFloor(), currTowerId));
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.COPY_TYPE_TOWER_GETREWARDS, 0, 0);
		String totalArardStr = towerMgr.getAwardByFloor(player, currTowerId);// 奖品数据字符串
		List<BilogItemInfo> list = BilogItemInfo.fromStr(totalArardStr);
		String rewardInfoActivity = BILogTemplateHelper.getString(list);
		BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.COPY_TYPE_TOWER_GETREWARDS, 0, true, 0, rewardInfoActivity, 0);
		if (totalArardStr.length() > 0) {
			response.setAwardListStr(totalArardStr);
			response.setTowerResultType(eTowerResultType.TOWER_SUCCESS);
		} else {
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			GameLog.error("万仙阵获取奖励", userId, String.format("请求领奖层是[%s],no rewardItems", currTowerId));
		}

		// 开放下层人物
		int nextTowerId = currTowerId + 1;
		if (nextTowerId >= AngelArrayConst.TOTAL_TOWER_NUM) {
			angleData.setCurFloorState(FloorState.FINISH.value);
		} else {
			angleData.setCurFloor(nextTowerId);
			angleData.setCurFloorState(FloorState.UN_PASS.value);
		}

		if (currTowerId > angleData.getMaxFloor()) {
			angleData.setMaxFloor(currTowerId);
		}

		towerMgr.saveAngleArrayData();

		// 更新一下层
		if (nextTowerId < AngelArrayConst.TOTAL_TOWER_NUM && (nextTowerId % AngelArrayConst.TOWER_UPDATE_NUM == 0)) {
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
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.TOWER, player, outTip)) {
			GameLog.error("万仙阵重置数据", userId, outTip.value);
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		TowerMgr towerMgr = player.getTowerMgr();
		TableAngelArrayData angleArrayData = towerMgr.getAngleArrayData();
		if (angleArrayData == null) {
			GameLog.error("万仙阵重置数据", userId, "万仙阵个人的数据TableAngelArrayData Is Null");
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			return response.build().toByteString();
		}

		int curFloorState = angleArrayData.getCurFloorState();
		if (curFloorState == FloorState.UN_Battle.value || curFloorState == FloorState.UN_Join.value) {
			GameLog.error("万仙阵重置数据", userId, "当前万仙阵的状态是：" + curFloorState);
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			response.setTipMsg("请尝试挑战万仙阵后再重置");
			return response.build().toByteString();
		}

		// by franky
		int resetCount = player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.arrayMaxResetCnt);
		if (resetCount - angleArrayData.getResetTimes() > 0) {
			// PrivilegeCfg privilegeCfg = PrivilegeCfgDAO.getInstance().getCfg(player.getVip());
			// if (privilegeCfg.getExpeditionCount() - angleArrayData.getResetTimes() > 0) {
			towerMgr.resetAngleArrayData(player, false);// 玩家手动重置
			TagTowerData towerData = getTowerData(player, 0, false);
			response.setTowerData(towerData);
			response.setTowerResultType(eTowerResultType.TOWER_SUCCESS);
			UserFeatruesMgr.getInstance().doFinish(player, UserFeaturesEnum.tower);
		} else {
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
			GameLog.error("万仙阵重置数据", userId, "万仙阵个人的数据重置次数用完了。Reset Times less 1");
			response.setTipMsg("重置次数不足");
		}
		return response.build().toByteString();
	}
}