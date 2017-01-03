package com.rw.service.ranodmBoss;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.chat.ChatBM;
import com.bm.chat.ChatInteractiveType;
import com.bm.randomBoss.RandomBossMgr;
import com.common.IHeroSynHandler;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.shareCfg.ChineseStringHelper;
import com.rwbase.common.herosynhandler.CommonHeroSynHandler;
import com.rwbase.dao.randomBoss.db.BattleNewsData;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.RandomBossProto.BattleRewardInfo;
import com.rwproto.RandomBossProto.InvitedFriends;
import com.rwproto.RandomBossProto.MsgType;
import com.rwproto.RandomBossProto.RandomBossMsgResponse;
import com.rwproto.RandomBossProto.RandomMsgRequest;

public class RandomBossMsgHandler {
	
	private static RandomBossMsgHandler handler = new RandomBossMsgHandler();
	
	private IHeroSynHandler _synHandler;
	protected RandomBossMsgHandler(){
		_synHandler = new CommonHeroSynHandler();
	}
	
	
	public static RandomBossMsgHandler getHandler(){
		return handler;
	}


	/**
	 * 接受战斗邀请
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString acceptedInvited(Player player, RandomMsgRequest request) {
		String bossID = request.getBossID();
		RandomBossMsgResponse.Builder response = RandomBossMsgResponse.newBuilder();
		response.setMsgType(MsgType.ACCEPTED_INVITED);
		
		if(StringUtils.isBlank(bossID)){
			response.setIsSuccess(false);
			response.setTips("参数错误");
			return response.build().toByteString();
		}
		
		try {
			
			RandomBossMgr.getInstance().acceptedInvited(player, bossID, response);
			
		} catch (Exception e) {
			GameLog.error("RandomBoss", "RandomBossMsgHandler[acceptedInvited]", "接受邀请好友战斗时出现异常", e);
			response.setIsSuccess(false);
		}
		
		return response.build().toByteString();
	}


	/**
	 * 邀请好友战斗
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString invitedFriendBattle(Player player,
			RandomMsgRequest request) {
		String bossID = request.getBossID();
		InvitedFriends friends = request.getFriends();
		int type = friends.getType();
		
		List<String> idList = friends.getFriendIDList();

		RandomBossMsgResponse.Builder response = RandomBossMsgResponse.newBuilder();
		response.setMsgType(MsgType.INVITE_FRIEND_BATTLE);
		
		if(StringUtils.isBlank(bossID) || friends == null){
			response.setIsSuccess(false);
			response.setTips("参数错误!");
			return response.build().toByteString();
					
		}
		try {
			boolean invied = RandomBossMgr.getInstance().recordInvitedTime(type, bossID);
			if(invied){
				
				//按策划要求，新加邀请直接加入到目标角色的boss列表
				addBossInternal(bossID, idList);
				
				
				ChatBM.getInstance().sendInteractiveMsg(player, ChatInteractiveType.RANDOM_BOSS, RandomBossMgr.getInstance().getBossBornInvitedTips(), bossID, bossID, idList);
				response.setIsSuccess(true);
			}else{
				response.setIsSuccess(false);
				response.setTips("邀请时间冷却中");
			}
		} catch (Exception e) {
			GameLog.error("RandomBoss", "RandomBossMsgHandler[invitedFriendBattle]", "发送邀请好友战斗聊天信息时出现异常", e);
			response.setIsSuccess(false);
		}
		return response.build().toByteString();
	}
	
	private void addBossInternal(final String bossID, List<String> targetRoleList){
		try {
			
			
			for (final String roleID : targetRoleList) {
				GameWorldFactory.getGameWorld().asyncExecute(roleID, new PlayerTask() {
					
					@Override
					public void run(Player e) {
						RandomBossMgr.getInstance().acceptedInternal(roleID,bossID);
						
					}
				});
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


	/**
	 * 请求boss数据
	 */
	public ByteString getBossList(Player player) {
		RandomBossMsgResponse.Builder response = RandomBossMsgResponse.newBuilder();
		response.setMsgType(MsgType.GET_BOSS_LIST);
		try {
			
			boolean suc = RandomBossMgr.getInstance().checkAndSynRandomBossData(player);
			response.setIsSuccess(suc);
			if(!suc){
				response.setTips("所有boss已经离开！");
			}
		} catch (Exception e) {
			GameLog.error("RandomBoss", "RandomBossMsgHandler[getBossList]", "获取随机boss数据时出现异常", e);
			response.setIsSuccess(false);
		}
		
		return response.build().toByteString();
	}

	
	
	

	/**
	 * 请求讨伐信息
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString getBattleInfo(Player player, RandomMsgRequest request) {
		String bossID = request.getBossID();
		RandomBossMsgResponse.Builder response = RandomBossMsgResponse.newBuilder();
		response.setMsgType(MsgType.GET_BATTLE_INFO);
		if(StringUtils.isBlank(bossID)){
			response.setIsSuccess(false);
			response.setTips("参数错误!");
			return response.build().toByteString();
		}
		try {
			
			List<BattleNewsData> infos = RandomBossMgr.getInstance().getBattleInfo(player, bossID);
			if(infos == null){
				response.setIsSuccess(false);
				response.setTips(ChineseStringHelper.getInstance().getLanguageString(RandomBossMgr.getInstance().getBossKilledKey(), "boss已经离开！"));
			}else{
				response.setIsSuccess(true);
				for (BattleNewsData data : infos) {
					if(data == null){
						continue;
					}
					response.addBattleInfo(ClientDataSynMgr.toClientData(data));
				}
			}
		} catch (Exception e) {
			GameLog.error("RandomBoss", "RandomBossMsgHandler[getBattleInfo]", "获取讨伐信息时出现异常", e);
			response.setIsSuccess(false);
		}
		
		
		return response.build().toByteString();
	}


	/**
	 * 请求进入boss战斗
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString applyEnterBattle(Player player, RandomMsgRequest request) {
		
		String bossID = request.getBossID();
		RandomBossMsgResponse.Builder response = RandomBossMsgResponse.newBuilder();
		response.setMsgType(MsgType.APPLY_BATTLE);
		if(StringUtils.isBlank(bossID)){
			response.setIsSuccess(false);
			response.setTips("参数错误!");
			return response.build().toByteString();
		}
		try {
			
			RandomBossMgr.getInstance().applyEnterBattle(player,bossID, response);
			
		} catch (Exception e) {
			GameLog.error("RandomBoss", "RandomBossMsgHandler[applyEnterBattle]", "请求进入boss战时出现异常", e);
			response.setIsSuccess(false);
			response.setTips("服务器繁忙");
		}
		
		_synHandler.synHeroData(player, eBattlePositionType.Normal, null);
		return response.build().toByteString();
	}


	
	/**
	 * 通知战斗结束
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString endBattle(Player player, RandomMsgRequest request) {
		String bossID = request.getBossID();
		long curHp = request.getCurHp();
		RandomBossMsgResponse.Builder response = RandomBossMsgResponse.newBuilder();
		response.setMsgType(MsgType.END_BATTLE);
		if(StringUtils.isBlank(bossID)){
			response.setIsSuccess(false);
			response.setTips("参数错误!");
			return response.build().toByteString();
		}
		
		
		try {
			BattleRewardInfo.Builder reward = RandomBossMgr.getInstance().endBattle(player, bossID, curHp);
			if(reward != null){
				response.setIsSuccess(true);
				response.setReward(reward);
			}else{
				response.setIsSuccess(false);
				response.setTips("无法获取奖励数据");
			}
		} catch (Exception e) {
			GameLog.error("RandomBoss", "RandomBossMsgHandler[endBattle]", "请求结束boss战时出现异常", e);
			response.setIsSuccess(false);
			response.setTips("服务器繁忙");
		}
		return response.build().toByteString();
	}
	
	

}
