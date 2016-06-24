package com.playerdata.groupFightOnline.manager;

import com.playerdata.Player;
import com.playerdata.groupFightOnline.data.GFDefendArmyItem;
import com.playerdata.groupFightOnline.data.GFDefendArmyItemHolder;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupData;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupHolder;
import com.playerdata.groupFightOnline.data.UserGFightOnlineData;
import com.playerdata.groupFightOnline.data.UserGFightOnlineHolder;
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
	
	public void getEnimyDefender(Player player, GroupFightOnlineRspMsg.Builder gfRsp, String groupID) {
		GFightOnlineGroupData groupData = GFightOnlineGroupHolder.getInstance().get(groupID);
		if(!GFightConditionJudge.getInstance().isFightPeriod(groupData.getResourceID())){
			gfRsp.setRstType(GFResultType.NOT_IN_OPEN_TIME);
			return;
		}
		try {
			GFDefendArmyItemHolder.getInstance().selectEnimyItem(player, groupID, false);
			gfRsp.setRstType(GFResultType.SUCCESS);
		} catch (HaveSelectEnimyException e) {
			gfRsp.setRstType(GFResultType.HAVE_A_ENIMY);
		} catch (NoSuitableDefenderException e) {
			gfRsp.setRstType(GFResultType.CANNOT_FIND_PROP_DEFENDER);
		} catch (Exception e){
			gfRsp.setRstType(GFResultType.DATA_ERROR);
		}
	}
	
	public void changeEnimyDefender(Player player, GroupFightOnlineRspMsg.Builder gfRsp, String groupID){
		GFightOnlineGroupData groupData = GFightOnlineGroupHolder.getInstance().get(groupID);
		if(!GFightConditionJudge.getInstance().isFightPeriod(groupData.getResourceID())){
			gfRsp.setRstType(GFResultType.NOT_IN_OPEN_TIME);
			return;
		}
		try {
			GFDefendArmyItemHolder.getInstance().changeEnimyItem(player, groupID);
			gfRsp.setRstType(GFResultType.SUCCESS);
		} catch (HaveSelectEnimyException e) {
			gfRsp.setRstType(GFResultType.HAVE_A_ENIMY);
		} catch (NoSuitableDefenderException e) {
			gfRsp.setRstType(GFResultType.CANNOT_FIND_PROP_DEFENDER);
		} catch (Exception e){
			gfRsp.setRstType(GFResultType.DATA_ERROR);
		}
	}
	
	public void startFight(Player player, GroupFightOnlineRspMsg.Builder gfRsp){
		GFightOnlineGroupData groupData = GFightOnlineGroupHolder.getInstance().get(GroupHelper.getUserGroupId(player.getUserId()));
		if(!GFightConditionJudge.getInstance().isFightPeriod(groupData.getResourceID())){
			gfRsp.setRstType(GFResultType.NOT_IN_OPEN_TIME);
			return;
		}
		UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
		DefendArmySimpleInfo defenderSimple = userGFData.getRandomDefender();
		if(defenderSimple == null) {
			gfRsp.setRstType(GFResultType.NO_SELECTED_ENIMY);
			return;
		}
		if(System.currentTimeMillis() - defenderSimple.getLockArmyTime() > GFDefendArmyItemHolder.LOCK_ITEM_MAX_TIME){
			gfRsp.setRstType(GFResultType.SELECTED_EXPIRED);
			return;
		}
		GFDefendArmyItem armyItem = GFDefendArmyItemHolder.getInstance().getItem(defenderSimple.getGroupID(), defenderSimple.getDefendArmyID());
		if(armyItem == null) {
			gfRsp.setRstType(GFResultType.DATA_ERROR);
			return;
		}else if(GFArmyState.FIGHTING.equals(armyItem.getState())){
			gfRsp.setRstType(GFResultType.ON_FIGHTING);
			return;
		}else if(!GFArmyState.SELECTED.equals(armyItem.getState())){
			gfRsp.setRstType(GFResultType.DATA_ERROR);
			return;
		}
		GFDefendArmyItemHolder.getInstance().startFight(player, armyItem);
		defenderSimple.setLockArmyTime(System.currentTimeMillis());
		UserGFightOnlineHolder.getInstance().synData(player);
	}
	
	public void informFightResult(Player player, GroupFightOnlineRspMsg.Builder gfRsp, GFightResult fightResult){
		
	}
}
