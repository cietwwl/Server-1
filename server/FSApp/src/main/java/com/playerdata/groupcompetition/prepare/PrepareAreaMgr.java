package com.playerdata.groupcompetition.prepare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.groupcompetition.syn.SameSceneContainer;
import com.rwproto.GroupCompetitionProto.AreaPosition;
import com.rwproto.GroupCompetitionProto.CommonRspMsg.Builder;
import com.rwproto.GroupCompetitionProto.GCResultType;

public class PrepareAreaMgr {
	
	private HashMap<String, Long> groupScene;
	
	private static PrepareAreaMgr instance = new PrepareAreaMgr();

	public static PrepareAreaMgr getInstance() {
		return instance;
	}
	
	public void enterPrepareArea(Player player, Builder gcRsp, AreaPosition position) {
		informPreparePosition(player, gcRsp, position);
	}

	public void informPreparePosition(Player player, Builder gcRsp, AreaPosition position) {
		//String groupId = GroupHelper.getGroupId(player);
		String groupId = "9899";
		if(StringUtils.isBlank(groupId)){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			return;
		}
		if(groupScene == null || !groupScene.containsKey(groupId)){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			return;
		}
		PositionInfo pInfo = new PositionInfo();
		pInfo.setPx(position.getX() > 0.01f ? position.getX() : 0.01f);
		pInfo.setPy(position.getY() > 0.01f ? position.getY() : 0.01f);
		SameSceneContainer.getInstance().putUserToScene(groupScene.get(groupId), player.getUserId(), pInfo);
		gcRsp.setRstType(GCResultType.SUCCESS);
	}

	public void leavePrepareArea(Player player, Builder gcRsp) {
		//String groupId = GroupHelper.getGroupId(player);
		String groupId = "9899";
		if(StringUtils.isBlank(groupId)){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			return;
		}
		if(groupScene == null || !groupScene.containsKey(groupId)){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			return;
		}
		SameSceneContainer.getInstance().removeUserFromScene(groupScene.get(groupId), player.getUserId());
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
}
