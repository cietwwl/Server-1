package com.bm.sameScene.data;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyFashion;
import com.playerdata.dataSyn.sameSceneSyn.DataAutoSynMgr;
import com.playerdata.dataSyn.sameSceneSyn.SameSceneContainer;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.rw.netty.UserChannelMgr;
import com.rw.service.fashion.FashionHandle;
import com.rw.service.group.helper.GroupHelper;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.FashionServiceProtos.FashionUsed;
import com.rwproto.GroupCompetitionProto.GCResultType;
import com.rwproto.SaloonServiceProto.AreaPosition;
import com.rwproto.SaloonServiceProto.CommonRspMsg;
import com.rwproto.SaloonServiceProto.ResultType;

public class SameSceneMgr {
	
	public static eSynType synType = eSynType.GC_PREPARE_POSITION;
	public static long SCENE_KEEP_TIME = 5 * 60 * 1000l;
	private HashMap<String, Long> groupScene;
	
	private static SameSceneMgr instance = new SameSceneMgr();

	public static SameSceneMgr getInstance() {
		return instance;
	}
	
	/**
	 * 因为GM命令，中间本来该有的空间没有了，所以需要这个变量
	 */
	private volatile boolean needRemoveScene = true;
	
	public void enterPrepareArea(Player player, CommonRspMsg.Builder gcRsp, AreaPosition position) {
		informPreparePosition(player, gcRsp, position);
		if(gcRsp.getRstType() == ResultType.SUCCESS){
			long sceneId = 0;
			DataAutoSynMgr.getInstance().synDataToOnePlayer(player, sceneId, synType, new SameSceneSynData());
			List<String> usersInScene = SameSceneContainer.getInstance().getSelfSceneUser(sceneId, player.getUserId());
			List<PlayerBaseInfo> allBaseInfo = getAllPlayer(usersInScene);
			//TODO 这里做数据同步
		}
	}

	/**
	 * 变更自己在备战区的位置
	 * @param player
	 * @param gcRsp
	 * @param position
	 */
	public void informPreparePosition(Player player, CommonRspMsg.Builder gcRsp, AreaPosition position) {
		PositionInfo pInfo = new PositionInfo();
		pInfo.setPx(position.getX());
		pInfo.setPy(position.getY());
		SameSceneContainer.getInstance().putUserToScene(groupScene.get(groupId), player.getUserId(), pInfo);
		gcRsp.setRstType(ResultType.SUCCESS);
	}

	/**
	 * 离开备战区
	 * @param player
	 * @param gcRsp
	 */
	public void leavePrepareArea(Player player, CommonRspMsg.Builder gcRsp) {
		String groupId = GroupHelper.getGroupId(player);
		if(StringUtils.isBlank(groupId)){
			gcRsp.setRstType(ResultType.SUCCESS);
			gcRsp.setTipMsg("没有帮派");
			return;
		}
		if(groupScene == null || !groupScene.containsKey(groupId)){
			gcRsp.setRstType(ResultType.SUCCESS);
			gcRsp.setTipMsg("场景未开启");
			return;
		}
		SameSceneContainer.getInstance().removeUserFromScene(groupScene.get(groupId), player.getUserId());
		gcRsp.setRstType(ResultType.SUCCESS);
		GroupCompetitionMgr.getInstance().onPlayerLeavePrepareArea(player);
	}
	
	/**
	 * 前端请求缺失的同屏玩家详细信息
	 * @param player
	 * @param gcRsp
	 * @param idList
	 */
	public void applyUsersBaseInfo(Player player, CommonRspMsg.Builder gcRsp, List<String> idList) {
		String groupId = GroupHelper.getGroupId(player);
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
					//延时清除每个帮派的准备区
					for(Long sceneId : groupScene.values()){
						SameSceneContainer.getInstance().addRemoveScene(sceneId);
					}
					groupScene = null;
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
			PlayerBaseInfo baseInfo = new PlayerBaseInfo();
			baseInfo.setUserId(userId);
			baseInfo.setUserName(player.getUserName());
			baseInfo.setLevel(player.getLevel());
			baseInfo.setImageId(player.getHeadImage());
			baseInfo.setCareer(player.getCareer());
			baseInfo.setSex(player.getSex());
			baseInfo.setCareerLevel(player.getStarLevel());
			baseInfo.setFightingAll(player.getHeroMgr().getFightingAll(player));
			baseInfo.setModelId(player.getModelId());
			baseInfo.setStarLevel(player.getStarLevel());
			baseInfo.setQualityId(player.getHeroMgr().getMainRoleHero(player).getQualityId());
			FashionUsed.Builder fashionUsing = FashionHandle.getInstance().getFashionUsedProto(userId);
			if (fashionUsing != null){
				ArmyFashion fashion = new ArmyFashion();
				fashion.setCareer(player.getCareer());
				fashion.setGender(player.getSex());
				fashion.setPetId(fashionUsing.getPetId());
				fashion.setSuitId(fashionUsing.getSuitId());
				fashion.setWingId(fashionUsing.getWingId());
				baseInfo.setFashionUsage(fashion);
			}
			result.add(baseInfo);
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
			ChannelHandlerContext ctx = UserChannelMgr.get(userId);
			if (ctx != null) {
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
