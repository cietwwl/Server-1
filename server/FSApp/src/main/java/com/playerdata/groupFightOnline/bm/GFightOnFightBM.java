package com.playerdata.groupFightOnline.bm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.rank.groupFightOnline.GFGroupBiddingRankMgr;
import com.bm.rank.groupFightOnline.GFOnlineHurtRankMgr;
import com.bm.rank.groupFightOnline.GFOnlineKillRankMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.CurAttrData;
import com.playerdata.army.simple.ArmyHeroSimple;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.cfg.GFightOnlineCostCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineCostDAO;
import com.playerdata.groupFightOnline.data.GFDefendArmyItem;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceHolder;
import com.playerdata.groupFightOnline.data.UserGFightOnlineData;
import com.playerdata.groupFightOnline.data.UserGFightOnlineHolder;
import com.playerdata.groupFightOnline.data.version.GFightDataVersion;
import com.playerdata.groupFightOnline.dataException.GFFightResultException;
import com.playerdata.groupFightOnline.dataException.HaveFightEnimyException;
import com.playerdata.groupFightOnline.dataException.HaveSelectEnimyException;
import com.playerdata.groupFightOnline.dataException.NoSuitableDefenderException;
import com.playerdata.groupFightOnline.dataForClient.DefendArmySimpleInfo;
import com.playerdata.groupFightOnline.dataForClient.GFFightRecord;
import com.playerdata.groupFightOnline.dataForClient.GFOnlineGroupInnerInfo;
import com.playerdata.groupFightOnline.dataForClient.GFUserSimpleInfo;
import com.playerdata.groupFightOnline.dataForClient.GFightResult;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
import com.playerdata.groupFightOnline.dataForRank.GFOnlineHurtItem;
import com.playerdata.groupFightOnline.dataForRank.GFOnlineKillItem;
import com.playerdata.groupFightOnline.enums.GFArmyState;
import com.playerdata.groupFightOnline.manager.GFDefendArmyMgr;
import com.playerdata.groupFightOnline.manager.GFightOnlineGroupMgr;
import com.playerdata.groupFightOnline.manager.GFightOnlineResourceMgr;
import com.rw.service.group.helper.GroupHelper;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;

/**
 * 在线帮战，战斗阶段管理类
 * 
 * @author aken
 *
 */
public class GFightOnFightBM {

	private static GFightOnFightBM instance = new GFightOnFightBM();

	public static GFightOnFightBM getInstance() {
		return instance;
	}

	/**
	 * 开战阶段开始时，要处理的事件
	 * 
	 * @param resourceID
	 */
	public void fightStart(int resourceID) {
		GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(resourceID);
		if (resData == null)
			return;
		List<GFGroupBiddingItem> groupBidRank = GFGroupBiddingRankMgr.getGFGroupBidRankList(resourceID);
		for (int i = 0; i < groupBidRank.size() && i < GFightConst.IN_FIGHT_MAX_GROUP; i++) {
			String groupID = groupBidRank.get(i).getGroupID();
			if (StringUtils.isBlank(groupID))
				continue;
			GFightOnlineGroupData gfgData = GFightOnlineGroupMgr.getInstance().get(groupID);
			if (gfgData == null || gfgData.getResourceID() <= 0)
				continue;
			GFDefendArmyMgr.getInstance().updateAllItem(groupID);
		}
	}

	/**
	 * 随机获取一个对手
	 * 
	 * @param player
	 * @param gfRsp
	 * @param groupID
	 */
	public void getEnimyDefender(Player player, GroupFightOnlineRspMsg.Builder gfRsp, String groupID) {
		GFightOnlineGroupData groupData = GFightOnlineGroupMgr.getInstance().get(groupID);
		if (!GFightConditionJudge.getInstance().isFightPeriod(groupData.getResourceID())) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("不在开战期间");
			return;
		}
		GFightOnlineGroupData selfGroupData = GFightOnlineGroupMgr.getInstance().get(GroupHelper.getInstance().getUserGroupId(player.getUserId()));
		if (selfGroupData == null || selfGroupData.getAliveCount() <= 0) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("帮派已经战败，不能再发起挑战");
			return;
		}
		try {
			GFDefendArmyMgr.getInstance().selectEnimyItem(player, groupID, false);
			UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
			GFDefendArmyItem defender = GFDefendArmyMgr.getInstance().getItem(userGFData.getRandomDefender().getGroupID(), userGFData.getRandomDefender().getDefendArmyID());
			gfRsp.setEnimyDefenderDetails(ClientDataSynMgr.toClientData(defender));
			gfRsp.setRstType(GFResultType.SUCCESS);
		} catch (HaveSelectEnimyException e) {
			setEnimyDefenderDetailsForClient(player.getUserId(), gfRsp);
			gfRsp.setTipMsg(e.getMessage());
		} catch (HaveFightEnimyException e) {
			setEnimyDefenderDetailsForClient(player.getUserId(), gfRsp);
			gfRsp.setTipMsg(e.getMessage());
		} catch (NoSuitableDefenderException e) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg(e.getMessage());
		} catch (Exception e) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("未知的异常错误");
		}
	}

	private void setEnimyDefenderDetailsForClient(String userID, GroupFightOnlineRspMsg.Builder gfRsp) {
		UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(userID);
		if (userGFData == null) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("个人帮战数据异常");
			return;
		}
		GFDefendArmyItem defender = GFDefendArmyMgr.getInstance().getItem(userGFData.getRandomDefender().getGroupID(), userGFData.getRandomDefender().getDefendArmyID());
		if (defender == null) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("锁定的防守队伍数据异常");
			return;
		}
		gfRsp.setEnimyDefenderDetails(ClientDataSynMgr.toClientData(defender));
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	/**
	 * 更换一个对手
	 * 
	 * @param player
	 * @param gfRsp
	 * @param groupID
	 */
	public void changeEnimyDefender(Player player, GroupFightOnlineRspMsg.Builder gfRsp, String groupID) {
		GFightOnlineGroupData groupData = GFightOnlineGroupMgr.getInstance().get(groupID);
		if (!GFightConditionJudge.getInstance().isFightPeriod(groupData.getResourceID())) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("不在开战期间");
			return;
		}
		GFightOnlineGroupData selfGroupData = GFightOnlineGroupMgr.getInstance().get(GroupHelper.getInstance().getUserGroupId(player.getUserId()));
		if (selfGroupData == null || selfGroupData.getAliveCount() <= 0) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("帮派已经战败，不能再发起挑战");
			return;
		}
		try {
			// 判断次数，计算费用
			UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
			int changeTimes = userGFData.getChangeEnimyTimes() + 1;
			GFightOnlineCostCfg costCfg = GFightOnlineCostDAO.getInstance().getCfgById(String.valueOf(changeTimes));
			if (costCfg == null) {
				List<GFightOnlineCostCfg> cfgList = GFightOnlineCostDAO.getInstance().getAllCfg();
				int maxIndex = 1;
				for (GFightOnlineCostCfg cfg : cfgList) {
					if (maxIndex < cfg.getKey())
						maxIndex = cfg.getKey();
				}
				costCfg = GFightOnlineCostDAO.getInstance().getCfgById(String.valueOf(maxIndex));
			}
			if (costCfg.getCost() > player.getUserGameDataMgr().getGold()) {
				gfRsp.setRstType(GFResultType.DIAMOND_NOT_ENOUGH);
				gfRsp.setTipMsg("钻石不足");
				return;
			}
			GFDefendArmyMgr.getInstance().changeEnimyItem(player, groupID);
			// 扣除费用，添加次数
			player.getUserGameDataMgr().addGold(-costCfg.getCost());
			userGFData.addChangeEnimyTimes();
			GFDefendArmyItem defender = GFDefendArmyMgr.getInstance().getItem(userGFData.getRandomDefender().getGroupID(), userGFData.getRandomDefender().getDefendArmyID());
			gfRsp.setEnimyDefenderDetails(ClientDataSynMgr.toClientData(defender));
			gfRsp.setRstType(GFResultType.SUCCESS);
		} catch (HaveSelectEnimyException e) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg(e.getMessage());
		} catch (HaveFightEnimyException e) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg(e.getMessage());
		} catch (NoSuitableDefenderException e) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg(e.getMessage());
		} catch (Exception e) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("未知的异常错误");
		}
	}

	/**
	 * 开始战斗
	 * 
	 * @param player
	 * @param gfRsp
	 */
	public void startFight(Player player, GroupFightOnlineRspMsg.Builder gfRsp) {
		GFightOnlineGroupData groupData = GFightOnlineGroupMgr.getInstance().get(GroupHelper.getInstance().getUserGroupId(player.getUserId()));
		if (!GFightConditionJudge.getInstance().isFightPeriod(groupData.getResourceID())) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("不在开战期间");
			return;
		}
		if (groupData.getAliveCount() <= 0) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("帮派已经战败，不能再发起挑战");
			return;
		}
		UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
		DefendArmySimpleInfo defenderSimple = userGFData.getRandomDefender();
		if (defenderSimple == null) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("还没有选择对手");
			return;
		}
		if (GFightConditionJudge.getInstance().isLockExpired(defenderSimple)) {
			if (!GFightConditionJudge.getInstance().isFightExpired(defenderSimple)) {
				gfRsp.setTipMsg("您已经挑战过该对手，请耐心等待战斗结果");
			} else {
				gfRsp.setTipMsg("锁定对手的时间已过期");
			}
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			return;
		}
		GFDefendArmyItem armyItem = GFDefendArmyMgr.getInstance().getItem(defenderSimple.getGroupID(), defenderSimple.getDefendArmyID());
		if (armyItem == null) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("敌方队伍数据异常");
			return;
		} else if (GFArmyState.FIGHTING.equals(armyItem.getState())) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("敌方正在被挑战");
			return;
		} else if (!GFArmyState.SELECTED.equals(armyItem.getState())) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("您并未锁定该队伍");
			return;
		}
		GFDefendArmyMgr.getInstance().startFight(player, armyItem);
		defenderSimple.setLockArmyTime(System.currentTimeMillis());
		UserGFightOnlineHolder.getInstance().update(player, userGFData);
		ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(armyItem.getSimpleArmy(), true);
		gfRsp.setEnimyDefenderDetails(ClientDataSynMgr.toClientData(armyInfo));
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	/**
	 * 前端通知战斗结果
	 * 
	 * @param player
	 * @param gfRsp
	 * @param fightResult
	 */
	public void informFightResult(Player player, GroupFightOnlineRspMsg.Builder gfRsp, GFightResult fightResult, GFightDataVersion dataVersion) {
		GFightOnlineGroupData groupData = GFightOnlineGroupMgr.getInstance().get(GroupHelper.getInstance().getUserGroupId(player.getUserId()));
		if (!GFightConditionJudge.getInstance().isFightPeriod(groupData.getResourceID())) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("不在开战期间");
			return;
		}
		if (!GFightConditionJudge.getInstance().haveSelectedEnimy(player.getUserId())) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("并没有选择挑战对手");
			return;
		}
		UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
		DefendArmySimpleInfo defenderSimple = userGFData.getRandomDefender();
		if (GFightConditionJudge.getInstance().isFightExpired(defenderSimple)) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("锁定对手时间过长，已经失效");
			return;
		}
		if (!defenderSimple.getGroupID().equals(fightResult.getGroupID()) || !defenderSimple.getDefendArmyID().equals(fightResult.getDefendArmyID())) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("提交的战斗结果异常，不是之前选择的对手");
			return;
		}
		GFDefendArmyItem armyItem = GFDefendArmyMgr.getInstance().getItem(defenderSimple.getGroupID(), defenderSimple.getDefendArmyID());
		if (armyItem == null || armyItem.getSimpleArmy() == null || armyItem.getSimpleArmy().getHeroList().size() + 1 != fightResult.getDefenderState().size()) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("提交的战斗结果异常，对手队伍中的英雄人数不匹配");
			return;
		}
		try {
			updateEnimyHeroState(defenderSimple.getGroupID(), defenderSimple.getDefendArmyID(), fightResult.getDefenderState(), fightResult.getState() == 1);
		} catch (GFFightResultException e) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg(e.getMessage());
			return;
		}
		updateSelfHeroAndHurtState(player, fightResult.getSelfArmyState(), fightResult.getHurtValue(), fightResult.getState() == 1);
		// 更新帮派中最新的击败对手的时间（后期用于排序）
		if (fightResult.getState() == 1)
			groupData.setLastkillTime(System.currentTimeMillis());
		GFFightRecord record = new GFFightRecord();
		record.setState(fightResult.getState());
		record.setCreateTime(System.currentTimeMillis());
		record.setOffend(getGFUserSimpleInfo(player));
		record.setDefend(getGFUserSimpleInfo(armyItem.getUserID()));
		GFightOnlineResourceMgr.getInstance().addFightRecord(groupData.getResourceID(), record);
		// GFightOnlineGroupData中的队伍总数和存活数有变化，要同步
		// 每次请求都有同步所有数据,这里就不用同步了
		// GFightOnlineGroupMgr.getInstance().synAllData(player, groupData.getResourceID(), dataVersion.getOnlineGroupData());
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	public void getKillRank(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID) {
		List<GFOnlineKillItem> killRank = GFOnlineKillRankMgr.getGFKillRankList(resourceID);
		for (GFOnlineKillItem item : killRank) {
			gfRsp.addRankData(ClientDataSynMgr.toClientData(item));
		}
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	public void getHurtRank(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID) {
		List<GFOnlineHurtItem> hurtRank = GFOnlineHurtRankMgr.getGFHurtRankList(resourceID);
		for (GFOnlineHurtItem item : hurtRank) {
			gfRsp.addRankData(ClientDataSynMgr.toClientData(item));
		}
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	public void getAllRankInGroup(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID) {
		List<GFGroupBiddingItem> groupBidRank = GFGroupBiddingRankMgr.getGFGroupBidRankList(resourceID);
		List<GFOnlineKillItem> killRank = GFOnlineKillRankMgr.getGFKillRankList(resourceID);

		for (int i = 0; i < GFightConst.IN_FIGHT_MAX_GROUP && i < groupBidRank.size(); i++) {
			GFGroupBiddingItem bidItem = groupBidRank.get(i);

			int killTotal = 0;
			for (GFOnlineKillItem killItem : killRank) {
				if (StringUtils.equals(killItem.getGroupID(), bidItem.getGroupID()))
					killTotal += killItem.getTotalKill();
			}

			GFOnlineGroupInnerInfo rankInfo = new GFOnlineGroupInnerInfo();
			rankInfo.setGroupName(bidItem.getGroupName());
			rankInfo.setTotalKill(killTotal);
			rankInfo.setHurtRank(GFOnlineHurtRankMgr.getGFHurtRankListInGroup(resourceID, bidItem.getGroupID(), GFightConst.GROUP_INNER_RANK_SIZE));
			rankInfo.setKillRank(GFOnlineKillRankMgr.getGFKillRankListInGroup(resourceID, bidItem.getGroupID(), GFightConst.GROUP_INNER_RANK_SIZE));
			gfRsp.addRankData(ClientDataSynMgr.toClientData(rankInfo));
		}
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	public void getFightRecord(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID) {
		List<GFFightRecord> fightRecords = GFightOnlineResourceMgr.getInstance().getFightRecord(resourceID);
		for (GFFightRecord record : fightRecords) {
			gfRsp.addFightRecord(ClientDataSynMgr.toClientData(record));
		}
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	/**
	 * 更新自己进攻队伍的英雄状态
	 * 
	 * @param player
	 * @param stateList 队伍血量状态
	 * @param hurtValue 造成的伤害总值
	 * @param isVictory 是否获得胜利
	 */
	private void updateSelfHeroAndHurtState(Player player, List<CurAttrData> stateList, int hurtValue, boolean isVictory) {
		UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
		List<String> activeHeros = new ArrayList<String>();
		for (CurAttrData attr : stateList) {
			activeHeros.add(attr.getId());
			CurAttrData hero = userGFData.getSelfHeroInfo(attr.getId());
			if (hero == null)
				userGFData.getSelfHerosInfo().add(attr);
			else {
				hero.setCurEnergy(attr.getCurEnergy());
				hero.setCurLife(attr.getCurLife());
				hero.setMaxEnergy(attr.getMaxEnergy());
				hero.setMaxLife(attr.getMaxLife());
			}
		}
		if (isVictory)
			userGFData.addKillCount();
		userGFData.addHurtTotal(hurtValue);
		userGFData.setActiveHeros(activeHeros);
		userGFData.setRandomDefender(null);
		UserGFightOnlineHolder.getInstance().updateAndInformRank(player, userGFData);
	}

	/**
	 * 更新敌方队伍英雄状态
	 * 
	 * @param groupID 敌方帮派
	 * @param enimyArmyID 敌方队伍id
	 * @param stateList 英雄状态列表
	 * @return 返回是否阵亡
	 * @throws GFFightResultException 结果数据和战斗前数据有冲突的异常
	 */
	private void updateEnimyHeroState(String groupID, String enimyArmyID, List<CurAttrData> stateList, boolean isDefeated) throws GFFightResultException {
		GFDefendArmyItem armyItem = GFDefendArmyMgr.getInstance().getItem(groupID, enimyArmyID);
		ArmyInfoSimple simpleArmy = armyItem.getSimpleArmy();
		for (CurAttrData attr : stateList) {
			if (StringUtils.isBlank(attr.getId()) || StringUtils.equals(attr.getId(), "0"))
				continue;
			ArmyHeroSimple hero = simpleArmy.getArmyHeroByID(attr.getId());
			if (hero == null)
				throw new GFFightResultException("战斗结果数据和防守整容数据不匹配");
			hero.setCurAttrData(attr);
		}
		GFArmyState state = GFArmyState.NORMAL;
		if (isDefeated)
			state = GFArmyState.DEFEATED;
		GFDefendArmyMgr.getInstance().updateItem(groupID, armyItem, state);
	}

	private GFUserSimpleInfo getGFUserSimpleInfo(String userId) {
		Player player = PlayerMgr.getInstance().find(userId);
		if (player == null)
			return null;
		return getGFUserSimpleInfo(player);
	}

	private GFUserSimpleInfo getGFUserSimpleInfo(Player player) {
		GFUserSimpleInfo info = new GFUserSimpleInfo();
		info.setUserName(player.getUserName());
		info.setPlayerHeadImage(player.getHeadImage());
		info.setPlayerHeadFrame(player.getHeadFrame());
		info.setGroupName(GroupHelper.getInstance().getGroupName(player.getUserId()));
		return info;
	}
}
