package com.rw.service.tower;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import com.bm.arena.ArenaBM;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.TowerMgr;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.pve.PveHandler;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.tower.pojo.TableTowerData;
import com.rwbase.dao.tower.pojo.TableTowerHeroData;
import com.rwbase.dao.tower.pojo.TowerEnemyInfo;
import com.rwbase.dao.tower.pojo.TowerHeroChange;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;
import com.rwproto.DataSynProtos.SynData;
import com.rwproto.SecretAreaProtos.ESuccessType;
import com.rwproto.SkillServiceProtos.TagSkillData;
import com.rwproto.SyncAttriProtos.TagAttriData;
import com.rwproto.TowerServiceProtos.MsgTowerRequest;
import com.rwproto.TowerServiceProtos.MsgTowerResponse;
import com.rwproto.TowerServiceProtos.TagHeroTowerData;
import com.rwproto.TowerServiceProtos.TagTowerData;
import com.rwproto.TowerServiceProtos.TagTowerEnemyInfo;
import com.rwproto.TowerServiceProtos.TagTowerHeadInfo;
import com.rwproto.TowerServiceProtos.TagTowerHeroChange;
import com.rwproto.TowerServiceProtos.eTowerResultType;
import com.rwproto.TowerServiceProtos.eTowerType;
public class TowerHandler {
	private static TowerHandler instance;
//	private TableArenaData m_MyArenaData;
	private int towerNum =15;
	private TowerHandler(){}
	
	public static TowerHandler getInstance()
	{
		if(instance == null){
			instance = new TowerHandler();
		}
		return instance;
	}
	//获取面板塔信息
	public ByteString getTowerPanelInfo(MsgTowerRequest request, Player player) {
		TableTowerData towerTableData = player.getTowerMgr().getMyTowerData();
		MsgTowerResponse.Builder response = MsgTowerResponse.newBuilder();
		response.setTowerType(request.getTowerType());
		int towerId = towerTableData.getCurrTowerID();
		TagTowerData towerData =getTowerData(player,towerId,false);
		response.setTowerData(towerData);
		response.setTowerResultType(eTowerResultType.TOWER_SUCCESS);
		return response.build().toByteString();
	}
	private TagTowerData getTowerData(Player player,int towerId,Boolean isAddInfo){//面板数据
		TableTowerData towerTableData = player.getTowerMgr().getMyTowerData();
		TagTowerData.Builder towerData = TagTowerData.newBuilder();
		towerData.setCurrTowerID(towerTableData.getCurrTowerID());
		towerData.setUserId(towerTableData.getUserId());
		towerData.setEnemyTowerID(towerId);
		towerData.setRefreshTimes(towerTableData.getRefreshTimes());
		towerData.addAllTowerOpenList(towerTableData.getOpenTowerList());
		towerData.addAllTowerFirstList(towerTableData.getFirstTowerList());
		towerData.addAllTowerBeatList(towerTableData.getBeatTowerList());
		List<Boolean> awardList = towerTableData.getAwardTowerList();
		towerData.addAllTowerGetArardList(awardList);
		
		List<TowerHeroChange> playerChangeList =towerTableData.getHeroChageList();
		//System.out.print("");
		for(int i=0;i<playerChangeList.size();i++){
			TagTowerHeroChange.Builder heroChange = TagTowerHeroChange.newBuilder();
			heroChange.setUserId(playerChangeList.get(i).getRoleId());
			heroChange.setReduceLife(playerChangeList.get(i).getReduceLife());
			heroChange.setReduceEnegy(playerChangeList.get(i).getReduceEnegy());
			heroChange.setIsDead(playerChangeList.get(i).getIsDead());
			towerData.addHeroChageMap(heroChange);
		}
		if(towerTableData.getEnemy(towerId)==null){
			player.getTowerMgr().getEnemyDataByTowerID(towerId);
		}
		Enumeration<ArmyInfo> tableEnemyInfoList= towerTableData.getEnemyEnumeration();
		List<TagTowerHeadInfo> enemyHeadList = new ArrayList<TagTowerHeadInfo>();
		int towerIdCount=0;
		while (tableEnemyInfoList.hasMoreElements()) {
			ArmyInfo enemyInfo = (ArmyInfo) tableEnemyInfoList.nextElement();
			TagTowerHeadInfo headInfo = getTowerHeadInfo(enemyInfo,towerIdCount);
			enemyHeadList.add(headInfo);
			towerIdCount++;
		}
		Collections.sort(enemyHeadList,new TowerEnemyInfoCompId());
		towerData.addAllHeadInfos(enemyHeadList);

		if(isAddInfo){
			ArmyInfo enemyInfo = player.getTowerMgr().getMyTowerData().getEnemy(towerId);;//层敌人数据
			//towerData.addEnemys(enemyInfo);
			try {
				String armyInfoClient =enemyInfo.toJson();
				System.out.print(armyInfoClient);
				towerData.setEnemyArmyInfo(armyInfoClient);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return towerData.build();
	}
	public class TowerEnemyInfoCompId implements Comparator<TagTowerHeadInfo>
	{
		public int compare(TagTowerHeadInfo o1, TagTowerHeadInfo o2) {
			if(o1.getTowerId()< o2.getTowerId()) return -1;
			if(o1.getTowerId() > o2.getTowerId()) return 1;
			return 0;
		}
	}
	private TagTowerHeadInfo getTowerHeadInfo(ArmyInfo towerTableInfo,int towerId){
	//	TableArenaData tableArena= ArenaBM.getInstance().getArenaData(towerTableInfo.getPlayer().getRoleBaseInfo().getId());
		TagTowerHeadInfo.Builder headInfo = TagTowerHeadInfo.newBuilder();
		if(towerTableInfo!=null){
			//玩家
			headInfo.setUserId(towerTableInfo.getPlayer().getRoleBaseInfo().getId());
			headInfo.setTempleteId(towerTableInfo.getPlayer().getRoleBaseInfo().getTemplateId());
			headInfo.setHeadImage(towerTableInfo.getPlayerHeadImage());
			headInfo.setTowerId(towerId);
			headInfo.setLevel(towerTableInfo.getPlayer().getRoleBaseInfo().getLevel());
			if(towerTableInfo.getPlayerName()==null){
				headInfo.setName("奇怪无名字");
			}else{
				headInfo.setName(towerTableInfo.getPlayerName());
			}

		}
		return headInfo.build();
		
	}
	
	//获取塔层信息
	public ByteString getTowerEnemyInfo(MsgTowerRequest request, Player player) {
		MsgTowerResponse.Builder response = MsgTowerResponse.newBuilder();
		response.setTowerType(request.getTowerType());

		TableTowerData towerTableData = player.getTowerMgr().getMyTowerData();
		int towerId = request.getTowerID();
		ArmyInfo enemyInfo = towerTableData.getEnemy(towerId);
		if(enemyInfo==null){
			enemyInfo =player.getTowerMgr().getEnemyDataByTowerID(towerId);
		}
		response.setTowerID(towerId);
		SynData.Builder armyInfoClient;
		try {
			String enemyJon = enemyInfo.toJson();
			response.setArmyInfo(enemyJon);
		} catch (Exception e) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "数据转换错误");
		}
		response.setTowerResultType(eTowerResultType.TOWER_SUCCESS);
		return response.build().toByteString();
	}
	//更新塔层数据
	public ByteString endFightTower(MsgTowerRequest request, Player player){
		TableTowerData towerTableData = player.getTowerMgr().getMyTowerData();
		MsgTowerResponse.Builder response = MsgTowerResponse.newBuilder();
		response.setTowerType(request.getTowerType());
		TagTowerData requireTowerData=request.getTowerData();
		int currTowerId = requireTowerData.getCurrTowerID();

		if(request.getWin()==1){//胜利
			List<Boolean> beatList= towerTableData.getBeatTowerList();
			beatList.set(currTowerId, true);
			if(currTowerId==player.getTowerMgr().totalTowerNum-1){
				MainMsgHandler.getInstance().sendPmdWxz(player);
			}
			player.getTowerMgr().save();
		}
		updateChange(request,player);
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Tower, 1);
		TagTowerData towerData= getTowerData(player,currTowerId,true);
		System.out.print(towerData.getEnemyArmyInfo());
		response.setTowerData(towerData);
		response.setTowerResultType(eTowerResultType.TOWER_SUCCESS);
		
		//战斗结束，推送pve消息给前端
		PveHandler.getInstance().sendPveInfo(player);
		
		return response.build().toByteString();
	}
	//更新玩家的血量数据
	private void updateChange(MsgTowerRequest request,Player player){
		List<TagTowerHeroChange> enemyChangeList  = request.getEnemyHeroChangeListList();//敌方数据改变
		TagTowerData requireTowerData = request.getTowerData();
		
		int towerId =requireTowerData.getCurrTowerID();
		int win = request.getWin();
		List<TowerHeroChange> changeList;
		if(enemyChangeList!=null){//关卡敌人数据改变更新
			changeList = returnTableChangeList(enemyChangeList);
			player.getTowerMgr().updateEnemyChange(towerId,changeList);//敌方改变数据
		}
		if(requireTowerData.getHeroChageMapList()!=null&&(requireTowerData.getHeroChageMapList().size())>0){//玩家数据有改变更新
			changeList = returnTableChangeList(requireTowerData.getHeroChageMapList());//玩家改变数据
			for(int i=0;i<changeList.size();i++){
				System.out.print("player towerId="+towerId+"   updateChange  id="+changeList.get(i).getRoleId()+"life="+changeList.get(i).getReduceLife()+"\n");
			}
			player.getTowerMgr().updatePlayerChange(towerId,changeList);
		}
		
	}
	private List<TowerHeroChange> returnTableChangeList(List<TagTowerHeroChange> changeList){
		List<TowerHeroChange> tableHeroChangeList = new ArrayList<TowerHeroChange>();
		for(int i=0;i<changeList.size();i++){
			TowerHeroChange tableHeroChange = new TowerHeroChange();
			tableHeroChange.setRoleId(changeList.get(i).getUserId());
			tableHeroChange.setReduceLife(changeList.get(i).getReduceLife());
			tableHeroChange.setReduceEnegy(changeList.get(i).getReduceEnegy());
			tableHeroChange.setIsDead(changeList.get(i).getIsDead());
			tableHeroChangeList.add(tableHeroChange);
		}
		return tableHeroChangeList;
	}
	
	public ByteString getAward(MsgTowerRequest request, Player player){
		MsgTowerResponse.Builder response = MsgTowerResponse.newBuilder();
		response.setTowerType(request.getTowerType());
		TagTowerData requireTowerData=request.getTowerData();
		
		int currTowerId = request.getTowerID();
		String totalArardStr=player.getTowerMgr().getAwardByTowerId(currTowerId);//奖品数据字符串
		if(totalArardStr.length()>0){
			response.setAwardListStr(totalArardStr);
			response.setTowerResultType(eTowerResultType.TOWER_SUCCESS);
		}else{	
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
		}
		
		//开放下层人物
		int nextTowerId = currTowerId+1;
		if(nextTowerId>=towerNum){
			nextTowerId = currTowerId;
		}
		TableTowerData towerTableData = player.getTowerMgr().getMyTowerData();
		List<Boolean> openList= towerTableData.getOpenTowerList();
		openList.set(nextTowerId, true);
		//towerTableData.setRefreshTimes(0);//test########
		towerTableData.setCurrTowerID(nextTowerId);
		player.getTowerMgr().save();
		
		TagTowerData towerData =getTowerData(player,nextTowerId,false);
		response.setTowerData(towerData);
		response.setTowerID(currTowerId);//更新上层状态
		response.setTowerResultType(eTowerResultType.TOWER_SUCCESS);
		return response.build().toByteString();
	}
	public ByteString restTowerData(MsgTowerRequest request, Player player){
		MsgTowerResponse.Builder response = MsgTowerResponse.newBuilder();
		TableTowerData towerTableData = player.getTowerMgr().getMyTowerData();
		PrivilegeCfg privilegeCfg= PrivilegeCfgDAO.getInstance().getCfg(player.getVip());
		if(privilegeCfg.getExpeditionCount()-towerTableData.getRefreshTimes()>0){
		 response.setTowerType(eTowerType.TOWER_RESET_DATA);
		 player.getTowerMgr().resetData(player.getTowerMgr().getMyTowerData(),false);
		 TagTowerData towerData =getTowerData(player,0,false);
		 response.setTowerData(towerData);
		 response.setTowerResultType(eTowerResultType.TOWER_SUCCESS);
		}else{
			//player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "重置次数不足");
			 response.setTowerType(eTowerType.TOWER_RESET_DATA);
			response.setTowerResultType(eTowerResultType.TOWER_FAIL);
		}
		 return response.build().toByteString();
	}
}
