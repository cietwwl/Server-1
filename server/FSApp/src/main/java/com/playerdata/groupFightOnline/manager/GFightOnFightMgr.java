package com.playerdata.groupFightOnline.manager;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.CurAttrData;
import com.playerdata.army.simple.ArmyHeroSimple;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.data.GFDefendArmyItem;
import com.playerdata.groupFightOnline.data.GFDefendArmyItemHolder;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupData;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupHolder;
import com.playerdata.groupFightOnline.data.UserGFightOnlineData;
import com.playerdata.groupFightOnline.data.UserGFightOnlineHolder;
import com.playerdata.groupFightOnline.data.version.GFightDataVersion;
import com.playerdata.groupFightOnline.dataException.GFFightResultException;
import com.playerdata.groupFightOnline.dataException.HaveSelectEnimyException;
import com.playerdata.groupFightOnline.dataException.NoSuitableDefenderException;
import com.playerdata.groupFightOnline.dataForClient.DefendArmySimpleInfo;
import com.playerdata.groupFightOnline.dataForClient.GFArmyState;
import com.playerdata.groupFightOnline.dataForClient.GFightResult;
import com.rw.service.group.helper.GroupHelper;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;

/**
 * 在线帮战，战斗阶段管理类
 * @author aken
 *
 */
public class GFightOnFightMgr {
	
	private static class InstanceHolder{
		private static GFightOnFightMgr instance = new GFightOnFightMgr();
	}
	
	public static GFightOnFightMgr getInstance(){
		return InstanceHolder.instance;
	}
	
	private GFightOnFightMgr() { }
	
	/**
	 * 随机获取一个对手
	 * @param player
	 * @param gfRsp
	 * @param groupID
	 */
	public void getEnimyDefender(Player player, GroupFightOnlineRspMsg.Builder gfRsp, String groupID) {
		GFightOnlineGroupData groupData = GFightOnlineGroupHolder.getInstance().get(groupID);
		if(!GFightConditionJudge.getInstance().isFightPeriod(groupData.getResourceID())){
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("不在开战期间");
			return;
		}
		try {
			GFDefendArmyItemHolder.getInstance().selectEnimyItem(player, groupID, false);
			UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
			GFDefendArmyItem defender = GFDefendArmyItemHolder.getInstance().getItem(userGFData.getRandomDefender().getGroupID(), userGFData.getRandomDefender().getDefendArmyID());
			gfRsp.setEnimyDefenderDetails(ClientDataSynMgr.toClientData(defender));
			gfRsp.setRstType(GFResultType.SUCCESS);
		} catch (HaveSelectEnimyException e) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg(e.getMessage());
		} catch (NoSuitableDefenderException e) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg(e.getMessage());
		} catch (Exception e){
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("未知的异常错误");
		}
	}
	
	/**
	 * 更换一个对手
	 * @param player
	 * @param gfRsp
	 * @param groupID
	 */
	public void changeEnimyDefender(Player player, GroupFightOnlineRspMsg.Builder gfRsp, String groupID){
		GFightOnlineGroupData groupData = GFightOnlineGroupHolder.getInstance().get(groupID);
		if(!GFightConditionJudge.getInstance().isFightPeriod(groupData.getResourceID())){
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("不在开战期间");
			return;
		}
		try {
			//TODO 判断次数，计算费用
			GFDefendArmyItemHolder.getInstance().changeEnimyItem(player, groupID);
			//TODO 扣除费用，添加次数
			UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
			userGFData.addChangeEnimyTimes();
			GFDefendArmyItem defender = GFDefendArmyItemHolder.getInstance().getItem(userGFData.getRandomDefender().getGroupID(), userGFData.getRandomDefender().getDefendArmyID());
			gfRsp.setEnimyDefenderDetails(ClientDataSynMgr.toClientData(defender));
			gfRsp.setRstType(GFResultType.SUCCESS);
		} catch (HaveSelectEnimyException e) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg(e.getMessage());
		} catch (NoSuitableDefenderException e) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg(e.getMessage());
		} catch (Exception e){
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("未知的异常错误");
		}
	}
	
	/**
	 * 开始战斗
	 * @param player
	 * @param gfRsp
	 */
	public void startFight(Player player, GroupFightOnlineRspMsg.Builder gfRsp){
		GFightOnlineGroupData groupData = GFightOnlineGroupHolder.getInstance().get(GroupHelper.getUserGroupId(player.getUserId()));
		if(!GFightConditionJudge.getInstance().isFightPeriod(groupData.getResourceID())){
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("不在开战期间");
			return;
		}
		UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
		DefendArmySimpleInfo defenderSimple = userGFData.getRandomDefender();
		if(defenderSimple == null) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("还没有选择对手");
			return;
		}
		if(GFightConditionJudge.getInstance().isLockExpired(defenderSimple)){
			gfRsp.setRstType(GFResultType.SELECT_EXPIRED);
			gfRsp.setTipMsg("锁定对手的时间已过期");
			return;
		}
		GFDefendArmyItem armyItem = GFDefendArmyItemHolder.getInstance().getItem(defenderSimple.getGroupID(), defenderSimple.getDefendArmyID());
		if(armyItem == null) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("敌方队伍数据异常");
			return;
		}else if(GFArmyState.FIGHTING.equals(armyItem.getState())){
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("敌方正在被挑战");
			return;
		}else if(!GFArmyState.SELECTED.equals(armyItem.getState())){
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("您并未锁定该队伍");
			return;
		}
		GFDefendArmyItemHolder.getInstance().startFight(player, armyItem);
		defenderSimple.setLockArmyTime(System.currentTimeMillis());
		UserGFightOnlineHolder.getInstance().update(player, userGFData);
		ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(armyItem.getSimpleArmy());
		gfRsp.setEnimyDefenderDetails(ClientDataSynMgr.toClientData(armyInfo));
		gfRsp.setRstType(GFResultType.SUCCESS);
	}
	
	/**
	 * 前端通知战斗结果
	 * @param player
	 * @param gfRsp
	 * @param fightResult
	 */
	public void informFightResult(Player player, GroupFightOnlineRspMsg.Builder gfRsp, GFightResult fightResult, GFightDataVersion dataVersion){
		GFightOnlineGroupData groupData = GFightOnlineGroupHolder.getInstance().get(GroupHelper.getUserGroupId(player.getUserId()));
		if(!GFightConditionJudge.getInstance().isFightPeriod(groupData.getResourceID())){
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("不在开战期间");
			return;
		}
		if(!GFightConditionJudge.getInstance().haveSelectedEnimy(player.getUserId())){
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("并没有选择挑战对手");
			return;
		}
		UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
		DefendArmySimpleInfo defenderSimple = userGFData.getRandomDefender();
		if(GFightConditionJudge.getInstance().isLockExpired(defenderSimple)) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("锁定对手时间过长，已经失效");
			return;
		}
		if(!defenderSimple.getGroupID().equals(fightResult.getGroupID()) || 
				!defenderSimple.getDefendArmyID().equals(fightResult.getDefendArmyID())) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("提交的战斗结果异常，不是之前选择的对手");
			return;
		}
		GFDefendArmyItem armyItem = GFDefendArmyItemHolder.getInstance().getItem(defenderSimple.getGroupID(), defenderSimple.getDefendArmyID());
		if(armyItem == null || armyItem.getSimpleArmy() == null || armyItem.getSimpleArmy().getHeroList().size() + 1 != fightResult.getDefenderState().size()){
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
		//GFightOnlineGroupData中的队伍总数和存活数有变化，要同步
		GFightOnlineGroupHolder.getInstance().synAllData(player, groupData.getResourceID(), dataVersion.getOnlineGroupData());
	}
	
	/**
	 * 更新自己进攻队伍的英雄状态
	 * @param player
	 * @param stateList 队伍血量状态
	 * @param hurtValue 造成的伤害总值
	 * @param isVictory 是否获得胜利
	 */
	private void updateSelfHeroAndHurtState(Player player, List<CurAttrData> stateList, int hurtValue, boolean isVictory){
		UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
		List<String> activeHeros = new ArrayList<String>();
		for(CurAttrData attr : stateList){
			activeHeros.add(attr.getId());
			CurAttrData hero = userGFData.getSelfHeroInfo(attr.getId());
			if(hero == null) userGFData.getSelfHerosInfo().add(attr);
			else{
				hero.setCurEnergy(attr.getCurEnergy());
				hero.setCurLife(attr.getCurLife());
				hero.setMaxEnergy(attr.getMaxEnergy());
				hero.setMaxLife(attr.getMaxLife());
			}
		}
		if(isVictory) userGFData.addKillCount();
		userGFData.addHurtTotal(hurtValue);
		userGFData.setActiveHeros(activeHeros);
		UserGFightOnlineHolder.getInstance().updateAndInformRank(player, userGFData);
	}
	
	/**
	 * 更新敌方队伍英雄状态
	 * @param groupID 敌方帮派
	 * @param enimyArmyID 敌方队伍id
	 * @param stateList 英雄状态列表
	 * @return 返回是否阵亡
	 * @throws GFFightResultException 结果数据和战斗前数据有冲突的异常
	 */
	private void updateEnimyHeroState(String groupID, String enimyArmyID, List<CurAttrData> stateList, boolean isDefeated) throws GFFightResultException{
		GFDefendArmyItem armyItem = GFDefendArmyItemHolder.getInstance().getItem(groupID, enimyArmyID);
		ArmyInfoSimple simpleArmy = armyItem.getSimpleArmy();
		for(CurAttrData attr : stateList){
			ArmyHeroSimple hero = simpleArmy.getArmyHeroByID(attr.getId());
			if(hero == null) throw new GFFightResultException("战斗结果数据和防守整容数据不匹配");
			hero.setCurAttrData(attr);
		}
		GFArmyState state = GFArmyState.NORMAL;
		if(isDefeated) state = GFArmyState.DEFEATED;
		GFDefendArmyItemHolder.getInstance().updateItem(groupID, armyItem, state);
	}
}
