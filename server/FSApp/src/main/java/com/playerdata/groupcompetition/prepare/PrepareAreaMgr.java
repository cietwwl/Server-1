package com.playerdata.groupcompetition.prepare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.groupcompetition.syn.SameSceneContainer;
import com.rw.service.fashion.FashionHandle;
import com.rwproto.FashionServiceProtos.FashionUsed;
import com.rwproto.GroupCompetitionProto.AreaPosition;
import com.rwproto.GroupCompetitionProto.CommonRspMsg.Builder;
import com.rwproto.GroupCompetitionProto.GCResultType;
import com.rwproto.GroupCompetitionProto.PlayerBaseInfo;

public class PrepareAreaMgr {
	
	private HashMap<String, Long> groupScene;
	
	private static PrepareAreaMgr instance = new PrepareAreaMgr();

	public static PrepareAreaMgr getInstance() {
		return instance;
	}
	
	public void enterPrepareArea(Player player, Builder gcRsp, AreaPosition position) {
		//String groupId = GroupHelper.getGroupId(player);
		String groupId = "9899";
		if(StringUtils.isBlank(groupId)){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setRstTip("请先加入帮派");
			return;
		}
		if(groupScene == null || !groupScene.containsKey(groupId)){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setRstTip("场景未开启");
			return;
		}
		List<String> usersInScene = SameSceneContainer.getInstance().getAllSceneUser(groupScene.get(groupId));
		List<PlayerBaseInfo> allBaseInfo = getAllPlayer(usersInScene);
		if(null != allBaseInfo && !allBaseInfo.isEmpty()){
			gcRsp.addAllPlayers(allBaseInfo);
		}
		informPreparePosition(player, gcRsp, position);
	}

	public void informPreparePosition(Player player, Builder gcRsp, AreaPosition position) {
		//String groupId = GroupHelper.getGroupId(player);
		String groupId = "9899";
		if(StringUtils.isBlank(groupId)){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setRstTip("请先加入帮派");
			return;
		}
		if(groupScene == null || !groupScene.containsKey(groupId)){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setRstTip("场景未开启");
			return;
		}
		PositionInfo pInfo = new PositionInfo();
		pInfo.setPx(position.getX() > 0.01f ? position.getX() : 0.01f);
		pInfo.setPy(position.getY() > 0.01f ? position.getY() : 0.01f);
		SameSceneContainer.getInstance().putUserToScene(groupScene.get(groupId), player.getUserId(), pInfo);
		gcRsp.setRstTip("成功");
		gcRsp.setRstType(GCResultType.SUCCESS);
	}

	public void leavePrepareArea(Player player, Builder gcRsp) {
		//String groupId = GroupHelper.getGroupId(player);
		String groupId = "9899";
		if(StringUtils.isBlank(groupId)){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setRstTip("没有帮派");
			return;
		}
		if(groupScene == null || !groupScene.containsKey(groupId)){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setRstTip("场景未开启");
			return;
		}
		SameSceneContainer.getInstance().removeUserFromScene(groupScene.get(groupId), player.getUserId());
		gcRsp.setRstTip("成功");
		gcRsp.setRstType(GCResultType.SUCCESS);
	}
	
	public void applyUsersBaseInfo(Player player, Builder gcRsp, List<String> idList) {
		//String groupId = GroupHelper.getGroupId(player);
		String groupId = "9899";
		if(StringUtils.isBlank(groupId)){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setRstTip("请先加入帮派");
			return;
		}
		if(groupScene == null || !groupScene.containsKey(groupId)){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setRstTip("场景未开启");
			return;
		}
		List<PlayerBaseInfo> allBaseInfo = getAllPlayer(idList);
		if(null != allBaseInfo && !allBaseInfo.isEmpty()){
			gcRsp.addAllPlayers(allBaseInfo);
		}
		gcRsp.setRstTip("成功");
		gcRsp.setRstType(GCResultType.SUCCESS);
	}
	
	/**
	 * 备战阶段开始
	 * 为每个帮派生成一个备战区
	 */
	public void prepareStart(){
		List<String> prepareGroups = getPrepareGroups();
		prepareGroups.add("9899");
		if(null == prepareGroups || prepareGroups.isEmpty()){
			return;
		}
		groupScene = new HashMap<String, Long>();
		//为每个帮派生成一个准备区
		for(String groupId : prepareGroups){
			long sceneId = SameSceneContainer.getInstance().createNewScene();
			groupScene.put(groupId, sceneId);
		}
	}
	
	/**
	 * 备战阶段结束
	 * 清除所有的备战区
	 */
	public void prepareEnd(){
		if(null == groupScene){
			return;
		}
		//清除每个帮派的准备区
		for(Long sceneId : groupScene.values()){
			SameSceneContainer.getInstance().removeScene(sceneId);
		}
		groupScene = null;
	}
	
	public List<String> getPrepareGroups(){
		return new ArrayList<String>();
	}
	
	private ArrayList<PlayerBaseInfo> getAllPlayer(List<String> idList){
		ArrayList<PlayerBaseInfo> result = new ArrayList<PlayerBaseInfo>();
		if(null == idList || idList.isEmpty()){
			return result;
		}
		for(String userId : idList){
			Player player = PlayerMgr.getInstance().findPlayerFromMemory(userId);
			if(null == player) continue;
			PlayerBaseInfo.Builder infoBuilder = PlayerBaseInfo.newBuilder();
			infoBuilder.setUserId(userId);
			infoBuilder.setUserName(player.getUserName());
			infoBuilder.setLevel(player.getLevel());
			infoBuilder.setImageId(player.getHeadImage());
			infoBuilder.setCareer(player.getCareer());
			infoBuilder.setSex(player.getSex());
			infoBuilder.setCareerLevel(player.getStarLevel());
			infoBuilder.setFightingAll(player.getHeroMgr().getFightingAll(player));
			infoBuilder.setModelId(player.getModelId());
			FashionUsed.Builder fashionUsing = FashionHandle.getInstance().getFashionUsedProto(userId);
			if (fashionUsing != null){
				infoBuilder.setFashionUsage(fashionUsing);
			}
			result.add(infoBuilder.build());
		}
		return result;
	}
}
