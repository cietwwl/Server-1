package com.playerdata.groupcompetition.prepare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.sameSceneSyn.DataAutoSynMgr;
import com.playerdata.dataSyn.sameSceneSyn.SameSceneContainer;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.holder.GCompEventsDataMgr;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.rw.netty.UserChannelMgr;
import com.rw.service.fashion.FashionHandle;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.FashionServiceProtos.FashionUsed;
import com.rwproto.GroupCompetitionProto.AreaPosition;
import com.rwproto.GroupCompetitionProto.CommonRspMsg.Builder;
import com.rwproto.GroupCompetitionProto.GCResultType;
import com.rwproto.GroupCompetitionProto.MagicInfo;
import com.rwproto.GroupCompetitionProto.PlayerBaseInfo;

public class PrepareAreaMgr {
	
	public static eSynType synType = eSynType.GC_PREPARE_POSITION;
	public static long SCENE_KEEP_TIME = 5 * 60 * 1000l;
	private HashMap<String, Long> groupScene;
	
	private static PrepareAreaMgr instance = new PrepareAreaMgr();

	public static PrepareAreaMgr getInstance() {
		return instance;
	}
	
	/**
	 * 因为GM命令，中间本来该有的空间没有了，所以需要这个变量
	 */
	private volatile boolean needRemoveScene = true;
	
	/**
	 * 加入备战区
	 * @param player
	 * @param gcRsp
	 * @param position
	 */
	public void enterPrepareArea(Player player, Builder gcRsp, AreaPosition position) {
		String groupId = GroupHelper.getGroupId(player);
		if (GroupCompetitionMgr.getInstance().getCurrentStageType() != GCompStageType.EVENTS) {
			gcRsp.setRstType(GCResultType.NO_SAME_SCENE);
			gcRsp.setTipMsg("当前不是赛事阶段！");
			return;
		}
		switch (GroupCompetitionMgr.getInstance().getCurrentEventsStatus()) {
		case NONE:
			gcRsp.setRstType(GCResultType.NO_SAME_SCENE);
			gcRsp.setTipMsg("当前不是比赛状态！");
			return;
		case FINISH:
		default:
			break;
		}
		if (player.getUserGroupAttributeDataMgr().getUserGroupAttributeData().getJoinTime() > GroupCompetitionMgr.getInstance().getEndTimeOfSelection()) {
			gcRsp.setRstType(GCResultType.NO_SAME_SCENE);
			gcRsp.setTipMsg("海选期结束之后加入的成员不能参与帮战！");
			return;
		}
		if(StringUtils.isBlank(groupId)){
			gcRsp.setRstType(GCResultType.NO_SAME_SCENE);
			gcRsp.setTipMsg("请先加入帮派");
			return;
		}
		if(groupScene == null || !groupScene.containsKey(groupId)){
			gcRsp.setRstType(GCResultType.NO_SAME_SCENE);
			gcRsp.setTipMsg("您的帮派今日没有比赛，无法进入备战区！");
			return;
		}
		if (GCompEventsDataMgr.getInstance().isEnemyEmpty(groupId, GroupCompetitionMgr.getInstance().getCurrentEventsType())) {
			gcRsp.setRstType(GCResultType.NO_SAME_SCENE);
			gcRsp.setTipMsg("您的帮派本轮比赛轮空，已自动晋级！");
			return;
		}
		informPreparePosition(player, gcRsp, position);
		if(gcRsp.getRstType() == GCResultType.SUCCESS){
			long sceneId = groupScene.get(groupId);
			DataAutoSynMgr.getInstance().synDataToOnePlayer(player, sceneId, synType, new SameSceneSynData());
			List<String> usersInScene = SameSceneContainer.getInstance().getAllSceneUser(sceneId);
			List<PlayerBaseInfo> allBaseInfo = getAllPlayer(usersInScene);
			if(null != allBaseInfo && !allBaseInfo.isEmpty()){
				gcRsp.addAllPlayers(allBaseInfo);
			}
		}
	}

	/**
	 * 变更自己在备战区的位置
	 * @param player
	 * @param gcRsp
	 * @param position
	 */
	public void informPreparePosition(Player player, Builder gcRsp, AreaPosition position) {
		String groupId = GroupHelper.getGroupId(player);
		if(StringUtils.isBlank(groupId)){
			gcRsp.setRstType(GCResultType.NO_SAME_SCENE);
			gcRsp.setTipMsg("请先加入帮派");
			return;
		}
		if(groupScene == null || !groupScene.containsKey(groupId)){
			gcRsp.setRstType(GCResultType.NO_SAME_SCENE);
			gcRsp.setTipMsg("您的帮派今日没有比赛，无法进入备战区！");
			return;
		}
		PositionInfo pInfo = new PositionInfo();
		pInfo.setPx(position.getX());
		pInfo.setPy(position.getY());
		SameSceneContainer.getInstance().putUserToScene(groupScene.get(groupId), player.getUserId(), pInfo);
		gcRsp.setRstType(GCResultType.SUCCESS);
	}

	/**
	 * 离开备战区
	 * @param player
	 * @param gcRsp
	 */
	public void leavePrepareArea(Player player, Builder gcRsp) {
		String groupId = GroupHelper.getGroupId(player);
		if(StringUtils.isBlank(groupId)){
			gcRsp.setRstType(GCResultType.SUCCESS);
			gcRsp.setTipMsg("没有帮派");
			return;
		}
		if(groupScene == null || !groupScene.containsKey(groupId)){
			gcRsp.setRstType(GCResultType.SUCCESS);
			gcRsp.setTipMsg("场景未开启");
			return;
		}
		SameSceneContainer.getInstance().removeUserFromScene(groupScene.get(groupId), player.getUserId());
		gcRsp.setRstType(GCResultType.SUCCESS);
		GroupCompetitionMgr.getInstance().onPlayerLeavePrepareArea(player);
	}
	
	/**
	 * 前端请求缺失的同屏玩家详细信息
	 * @param player
	 * @param gcRsp
	 * @param idList
	 */
	public void applyUsersBaseInfo(Player player, Builder gcRsp, List<String> idList) {
		String groupId = GroupHelper.getGroupId(player);
		if(StringUtils.isBlank(groupId)){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setTipMsg("请先加入帮派");
			return;
		}
		if(groupScene == null || !groupScene.containsKey(groupId)){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setTipMsg("场景未开启");
			return;
		}
		List<PlayerBaseInfo> allBaseInfo = getAllPlayer(idList);
		if(null != allBaseInfo && !allBaseInfo.isEmpty()){
			gcRsp.addAllPlayers(allBaseInfo);
		}
		gcRsp.setRstType(GCResultType.SUCCESS);
	}
	
	/**
	 * 把玩家从备战区，移除
	 * @param userId
	 */
	public void leavePrepareArea(String userId) {
		String groupId = GroupHelper.getUserGroupId(userId);
		if(StringUtils.isBlank(groupId)){
			return;
		}
		if(groupScene == null || !groupScene.containsKey(groupId)){
			return;
		}
		SameSceneContainer.getInstance().removeUserFromScene(groupScene.get(groupId), userId);
	}
	
	/**
	 * 备战阶段开始
	 * 为每个帮派生成一个备战区
	 */
	public void prepareStart(List<String> prepareGroups){
		if(null == prepareGroups || prepareGroups.isEmpty()){
			return;
		}
		if(needRemoveScene){
			if(null != groupScene && !groupScene.isEmpty()){
				for(Long sceneId : groupScene.values()){
					SameSceneContainer.getInstance().addRemoveScene(sceneId);
				}
			}
			needRemoveScene = false;
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
		if(null == groupScene || groupScene.isEmpty()){
			return;
		}
		needRemoveScene = true;
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if(needRemoveScene){
					if(null != groupScene){
						//延时清除每个帮派的准备区
						for(Long sceneId : groupScene.values()){
							SameSceneContainer.getInstance().addRemoveScene(sceneId);
						}
						groupScene = null;
					}
				}
			}
		}, SCENE_KEEP_TIME);
	}
	
	/**
	 * 获取指定玩家的详细信息
	 * （用于前端显示人物）
	 * @param idList
	 * @return
	 */
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
			infoBuilder.setStarLevel(player.getStarLevel());
			infoBuilder.setQualityId(player.getHeroMgr().getMainRoleHero(player).getQualityId());
			FashionUsed.Builder fashionUsing = FashionHandle.getInstance().getFashionUsedProto(userId);
			if (fashionUsing != null){
				infoBuilder.setFashionUsage(fashionUsing);
			}
			ItemData magicItem = player.getMagic();
			if(null != magicItem){
				MagicInfo.Builder magicBuilder = MagicInfo.newBuilder();
				magicBuilder.setModelId(magicItem.getModelId());
				magicBuilder.setAptitude(magicItem.getMagicAptitude());
				magicBuilder.setLevel(magicItem.getMagicLevel());
				infoBuilder.setMagic(magicBuilder.build());
			}
			result.add(infoBuilder.build());
		}
		return result;
	}
	
	/**
	 * 获取某帮派备战区内，连接正常的玩家id
	 * @param groupId
	 * @return
	 */
	public List<String> getOnlineUserFromPrepareScene(String groupId){
		List<String> onlineUsers = new ArrayList<String>();
		if(!groupScene.containsKey(groupId)){
			return onlineUsers;
		}
		long sceneId = groupScene.get(groupId);
		List<String> usersInScene = SameSceneContainer.getInstance().getAllSceneUser(sceneId);
		for(String userId : usersInScene){
			if (UserChannelMgr.isConnecting(userId)) {
				onlineUsers.add(userId);
			}
		}
		return onlineUsers;
	}

	/**
	 * 完成加载界面，进入备战区
	 * @param player
	 */
	public void inPrepareArea(Player player) {
		GroupCompetitionMgr.getInstance().onPlayerEnterPrepareArea(player);
	}
	
	/**
	 * 获取帮派的同屏场景id
	 * @param groupId
	 * @return
	 */
	public Long getGroupScene(String groupId){
		return groupScene.get(groupId);
	}
}
