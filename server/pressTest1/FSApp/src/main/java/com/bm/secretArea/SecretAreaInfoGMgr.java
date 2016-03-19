package com.bm.secretArea;

import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.guildSecretArea.SecretAreaInfo;
import com.rwbase.dao.guildSecretArea.SecretAreaInfoDAO;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;


/**
 * 秘境消息处理类，所有用户公用一个
 * @author Administrator
 *
 */

public class SecretAreaInfoGMgr {

	private static SecretAreaInfoGMgr m_instance = new SecretAreaInfoGMgr();
	private SecretAreaInfoGMgr(){}	
	public static SecretAreaInfoGMgr getInstance(){
		return m_instance;
	}

	
	private SecretAreaInfoDAO secretAreaInfoDAO = SecretAreaInfoDAO.getInstance();
	
	
	public SecretAreaInfo getById(String id){
		return secretAreaInfoDAO.get(id);
	}
	/**
	 * 更新秘境
	 * @param triggerPlayer  1.null全部关联玩家更新 2. 有值则 更新当前玩家
	 * @param secretAreaInfo 更新秘境信息
	 * @return 更新成功或失败
	 */
	public boolean update(Player triggerPlayer, SecretAreaInfo secretAreaInfo){
		boolean success = secretAreaInfoDAO.update(secretAreaInfo);
		if(success){
			synClientData(triggerPlayer, secretAreaInfo, eSynOpType.UPDATE_SINGLE);//
			if(triggerPlayer!=null)
			triggerPlayer.getSecretMgr().updateSecretRank(secretAreaInfo);//更新秘境排行
		}
		return success;
	}
	/**
	 * 所有关联秘境玩家更新秘境
	 * @param triggerPlayer null全部关联玩家更新  有值则 更新当前玩家
	 * @param secretAreaInfo 更新秘境
	 * @param synOpType  更新类型
	 */
	private void synClientData(Player triggerPlayer,final SecretAreaInfo secretAreaInfo, final eSynOpType synOpType) {
		Enumeration<String> joinUserIdList = secretAreaInfo.getJoinUserIdList();//驻守的玩家userId集合(可去除老的)
		Enumeration<String> secretArmyKeys = secretAreaInfo.getSecretArmyKeys();//驻守的玩家userId集合
		while (secretArmyKeys.hasMoreElements()) {
			String key = (String) secretArmyKeys.nextElement();
			secretAreaInfo.getSecretArmyMap().get(key);
		}
		while (joinUserIdList.hasMoreElements()) {
			String userId = joinUserIdList.nextElement();//玩家userId
			//当前secretAreaInfo玩家更新秘境
			if(triggerPlayer!=null && StringUtils.equals(userId, triggerPlayer.getUserId())){
				ClientDataSynMgr.synData(triggerPlayer, secretAreaInfo, eSynType.SECRETAREA_INFO, synOpType);
				continue;
			}
			
			//所有关联秘境玩家更新
			GameWorldFactory.getGameWorld().asyncExecute(userId, new PlayerTask() {				
				@Override
				public void run(Player player) {
					ClientDataSynMgr.synData(player, secretAreaInfo, eSynType.SECRETAREA_INFO, synOpType);
				}
			});
			
			
		}
	}
	/**
	 * 添加秘境
	 * @param triggerPlayer 添加的玩家对象
	 * @param secretAreaInfo 添加秘境信息
	 * @return
	 */
	public boolean add(Player triggerPlayer, SecretAreaInfo secretAreaInfo){
		boolean success = secretAreaInfoDAO.add(secretAreaInfo);
		if(success){
			synClientData(triggerPlayer, secretAreaInfo, eSynOpType.ADD_SINGLE);
		}
		return success;
	}
	//移除秘境
	public void remove(Player triggerPlayer,SecretAreaInfo secretAreaInfo){
		synClientData(triggerPlayer, secretAreaInfo, eSynOpType.REMOVE_SINGLE);
	}
//	//寻找敌方玩家秘境
	public SecretAreaInfo findSecretArea(Player player){
		SecretAreaInfo findSecret=secretAreaInfoDAO.findTarget(player);
		return findSecret;
	}
	public void flush(){//数据库刷新秘境保存数据
		secretAreaInfoDAO.flush();
	}
}
