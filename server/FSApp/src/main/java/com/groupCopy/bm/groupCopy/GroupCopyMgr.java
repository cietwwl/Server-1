package com.groupCopy.bm.groupCopy;

import java.util.ArrayList;
import java.util.List;

import com.bm.group.GroupBM;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyDonateCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyDonateCfgDao;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfgDao;
import com.groupCopy.rwbase.dao.groupCopy.db.CopyItemDropAndApplyRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.DropAndApplyRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyArmyDamageInfo;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyDamegeRankInfo;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMonsterSynStruct;
import com.groupCopy.rwbase.dao.groupCopy.db.ItemDropAndApplyTemplate;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyProgress;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyRewardRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyTeamInfo;
import com.groupCopy.rwbase.dao.groupCopy.db.ServerGroupCopyDamageRecordMgr;
import com.groupCopy.rwbase.dao.groupCopy.db.TeamHero;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.majorDatas.MajorDataDataHolder;
import com.rwproto.GroupCopyBattleProto.CopyBattleRoleStruct;
import com.rwproto.GroupCopyBattleProto.CopyRewardInfo;
import com.rwproto.GroupCopyBattleProto.GroupCopyBattleComRspMsg;
import com.rwproto.GroupCopyBattleProto.CopyRewardInfo.Builder;
import com.rwproto.GroupCopyBattleProto.CopyRewardStruct;
import com.rwproto.GroupCopyCmdProto.ArmyHurtStruct;
import com.rwproto.GroupCopyCmdProto.GroupCopyCmdReqMsg;
import com.rwproto.GroupCopyCmdProto.GroupCopyDonateData;
import com.rwproto.GroupCopyCmdProto.GroupCopyHurtRank;
import com.log.GameLog;
import com.log.LogModule;
import com.monster.cfg.CopyMonsterCfg;
import com.monster.cfg.CopyMonsterCfgDao;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.readonly.PlayerIF;

/**
 * 
 * 
 * @author Allen
 *
 */
public class GroupCopyMgr {

	/**帮派副本关卡数据*/
	private GroupCopyLevelRecordHolder lvRecordHolder;

	/**帮派副本地图数据*/
	private GroupCopyMapRecordHolder mapRecordHolder;

	/**帮派副本奖励分配记录*/
	private GroupCopyRewardRecordHolder rewardRecordHolder;
	
	/**帮派副本胜利品及申请列表*/
	private DropAndApplyRecordHolder dropHolder;

	public final static GroupCopyDamegeRankComparator RANK_COMPARATOR = new GroupCopyDamegeRankComparator();

	public static final int MAX_RANK_RECORDS = 10;
	
	/**最大赞助上限*/
	private static final int MAX_DONATE_COUNT = 100; 
	
	public GroupCopyMgr(String groupIdP) {
		lvRecordHolder = new GroupCopyLevelRecordHolder(groupIdP);
		mapRecordHolder = new GroupCopyMapRecordHolder(groupIdP);
		rewardRecordHolder = new GroupCopyRewardRecordHolder(groupIdP);
		dropHolder = new DropAndApplyRecordHolder(groupIdP);
	}
	
	/**
	 * 系统加载完所有数据后才可以执行此方法
	 */
	public void checkDataFromCfg(){
		lvRecordHolder.checkAndInitData();
		mapRecordHolder.checkAndInitData();
		dropHolder.checkAndInitData();
	}
	
	/**
	 * 开启副本地图
	 * @param mapId
	 * @return
	 */
	public synchronized GroupCopyResult  openMap(Player player, String mapId){
		return GroupCopyMapBL.openMap(player, mapRecordHolder, lvRecordHolder, mapId);
	}
	
	/**
	 * 重置副本地图
	 * @param player TODO
	 * @param mapId
	 * @return
	 */
	public synchronized GroupCopyResult resetMap(Player player, String mapId){
		return GroupCopyMapBL.openMap(player, mapRecordHolder, lvRecordHolder, mapId);
	}
	
	/**
	 * 进入副本战斗
	 * @param player
	 * @param levelId
	 * @return
	 */
	public synchronized GroupCopyResult  beginFight(Player player, String levelId){
		return GroupCopyLevelBL.beginFight(player, lvRecordHolder, levelId);
	}
	
	/**
	 * 结束战斗
	 * @param player
	 * @param levelId 关卡id
	 * @param mData 客户端返回的怪物数据
	 * @param heroList TODO
	 * @return
	 */
	public synchronized GroupCopyResult  endFight(Player player, String levelId, 
			List<GroupCopyMonsterSynStruct> mData, List<String> heroList){
		//获取伤害
		int damage = getDamage(mData, levelId);
		GroupCopyResult result = GroupCopyLevelBL.endFight(player, lvRecordHolder, levelId, mData, damage);
		//同步一下副本地图进度
		GroupCopyMapBL.calculateMapProgress(player, lvRecordHolder, mapRecordHolder,levelId);
		//检查是否进入章节前10伤害排行 
		checkDamageRank(player,levelId, damage, heroList);
		//将奖励入放帮派奖励缓存
		if(result.getItem() != null){
			addReward2Group(player,levelId, (CopyRewardInfo.Builder)result.getItem());
			addReward2Role(player, levelId, (CopyRewardInfo.Builder)result.getItem());
		}
		return result;
	}
	
	
	private void addReward2Role(Player player, String levelId, Builder item) {
		int gold = item.getGold();
		List<CopyRewardStruct> rewardList = item.getPersonalRewardList();
		if(gold > 0){
			player.getUserGameDataMgr().addCoin(gold);
		}
		if(!rewardList.isEmpty()){
			for (CopyRewardStruct struct : rewardList) {
				player.getItemBagMgr().addItem(struct.getItemID(), struct.getCount());
			}
		}
	}

	/**
	 * 作弊通关 
	 * @param player
	 * @param levelID
	 * @return
	 */
	public GroupCopyResult cheatEndFight(Player player, String levelID){
		//先找到原来的记录
		GroupCopyLevelRecord lvRecord = lvRecordHolder.getByLevel(levelID);
		GroupCopyProgress p;
		List<GroupCopyMonsterSynStruct> monsterList = new ArrayList<GroupCopyMonsterSynStruct>();
		if(lvRecord == null || lvRecord.getProgress() == null){
			//原来没有记录，则从配置表初始化
			GroupCopyLevelCfg levelCfg = GroupCopyLevelCfgDao.getInstance().getCfgById(levelID);
			List<String> list = levelCfg.getmIDList();
			CopyMonsterCfg monsterCfg = null;
			GroupCopyMonsterSynStruct mStruct = null;
			for (String id : list) {
				 monsterCfg = CopyMonsterCfgDao.getInstance().getCfgById(id);
				 mStruct = new GroupCopyMonsterSynStruct(monsterCfg);
				 monsterList.add(mStruct);
			}
		}else{
			List<GroupCopyMonsterSynStruct> getmDatas = lvRecord.getProgress().getmDatas();
			
			
		}
		
		//这些怪物扣掉500HP
		return null;
		
	}

	
	private int getDamage(List<GroupCopyMonsterSynStruct> mData, String level){
		GroupCopyProgress nowPro = new GroupCopyProgress(mData);
		GroupCopyLevelRecord record = lvRecordHolder.getByLevel(level);
		if(record.getProgress().getCurrentHp() == 0){
			return nowPro.getTotalHp() - nowPro.getCurrentHp();
		}
		return record.getProgress().getCurrentHp() - nowPro.getCurrentHp();
	}

	
	/**
	 * 添加帮派奖励
	 * @param player
	 * @param levelId
	 * @param item
	 */
	private void addReward2Group(Player player,
			String levelId, Builder item) {

		GroupCopyLevelCfg cfg = GroupCopyLevelCfgDao.getInstance().getCfgById(levelId);
		GroupCopyMapRecord mapRecord = mapRecordHolder.getItem(cfg.getChaterID());
		CopyItemDropAndApplyRecord dropAndApplyRecord = dropHolder.getItem(cfg.getChaterID());
		ItemDropAndApplyTemplate dropApplyRecord = null;
		List<CopyRewardStruct> list = item.getDropList();
		for (CopyRewardStruct d : list) {
			dropApplyRecord = dropAndApplyRecord.getDropApplyRecord(String.valueOf(d.getItemID()));
			if(dropApplyRecord == null){
				dropApplyRecord = new ItemDropAndApplyTemplate(d.getItemID());
			}
			dropApplyRecord.addDropItem(d.getCount());
			dropHolder.updateItem(player, dropAndApplyRecord);
			dropApplyRecord = null;
		}
		
		mapRecordHolder.updateItem(player, mapRecord);
	}


	private void checkDamageRank(Player player, String levelId, int damage, List<String> heroList) {
		try {

			GroupCopyLevelCfg cfg = GroupCopyLevelCfgDao.getInstance().getCfgById(levelId);
			
			ArmyInfo info = ArmyInfoHelper.getArmyInfo(player.getUserId(), heroList);
			GroupCopyArmyDamageInfo damageInfo = armyInfo2DamageInfo(info);
			damageInfo.setDamage(damage);
			mapRecordHolder.checkDamageRank(cfg.getChaterID(),damageInfo);
			//关卡全服单次伤害排行
			ServerGroupCopyDamageRecordMgr.getInstance().checkDamageRank(levelId,damageInfo);
			
			//增加成员章节总伤害
			GroupCopyMapRecord mapRecord = mapRecordHolder.getItem(cfg.getChaterID());
			mapRecord.addPlayerDamage(player.getUserId(), damage);
			
		} catch (Exception e) {
			GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[checkDamageRank]", "帮派副本战斗结束检查排行榜时出现异常", e);
		}
		
		
	}


	public static GroupCopyArmyDamageInfo armyInfo2DamageInfo(ArmyInfo info) {
		GroupCopyArmyDamageInfo damageInfo = new GroupCopyArmyDamageInfo();
		damageInfo.setPlayerID(info.getPlayer().getRoleBaseInfo().getId());
		damageInfo.setTime(System.currentTimeMillis());
		
		GroupCopyTeamInfo teamInfo = new GroupCopyTeamInfo();
		teamInfo.setArmyMagic(info.getArmyMagic());
		String groupName = GroupHelper.getGroupName(damageInfo.getPlayerID());
		teamInfo.setGuildName(groupName);
		
		List<ArmyHero> heroList = info.getHeroList();
		List<TeamHero> heros = new ArrayList<TeamHero>();
		TeamHero tHero;
		for (ArmyHero hero : heroList) {
			tHero = new TeamHero();
			heros.add(initTeamHero(tHero, hero));
		}
		teamInfo.setHeroList(heros);
		tHero = new TeamHero();
		teamInfo.setPlayer(initTeamHero(tHero, info.getPlayer()));
		teamInfo.setPlayerHeadImage(info.getPlayerHeadImage());
		teamInfo.setPlayerName(info.getPlayerName());
		
		damageInfo.setArmy(teamInfo);
		return damageInfo;
	}

	private static TeamHero initTeamHero(TeamHero tHero, ArmyHero hero){
		tHero.setExp(hero.getRoleBaseInfo().getExp());
		tHero.setLevel(hero.getRoleBaseInfo().getLevel());
		tHero.setModeId(hero.getRoleBaseInfo().getModeId());
		tHero.setQualityId(hero.getRoleBaseInfo().getQualityId());
		tHero.setStarLevel(hero.getRoleBaseInfo().getStarLevel());
		tHero.setTemplateId(hero.getRoleBaseInfo().getTemplateId());
		return tHero;
	}
	

	/**
	 * 同步副本地图数据
	 * @param player
	 * @param version
	 */
	public synchronized void synMapData(Player player, int version){
		
		mapRecordHolder.synAllData(player, version);
		
	}
	
	/**
	 * 同步副本关卡数据
	 * @param player
	 * @param version
	 */
	public synchronized void synLevelData(Player player, int version){
		
		lvRecordHolder.synAllData(player, version);
		
	}
	
	/**
	 * 同步奖励分配记录
	 * @param player
	 * @param version
	 */
	public synchronized void synRewardData(Player player, int version){
		rewardRecordHolder.synAllData(player, version);
	}
	
	public synchronized void synDropAppyData(Player player, int version){
		dropHolder.synAllData(player, version);
	}
	
	
	
	/**
	 * 赞助buff
	 * @param player
	 * @param buffValue
	 */
	public synchronized void submitBuff(Player player, int buffValue){
		
	}


	
	/**
	 * 请求是否可以进入关卡
	 * @param player
	 * @param level
	 * @return
	 */
	public synchronized GroupCopyBattleComRspMsg.Builder applyEnterCopy(Player player, String level,
			GroupCopyBattleComRspMsg.Builder rspMsg) {
		try {
			GroupCopyLevelRecord lvData = lvRecordHolder.getByLevel(level);
			if(lvData == null){
				GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[applyEnterCopy]", "角色请求进入关卡，找不到关卡id为"+ level
						+ "的记录" , null);
				rspMsg.setTipMsg("服务器繁忙！");
				return rspMsg;
			}
			if(lvData != null && GroupCopyLevelBL.isFighting(lvData, player)){
				int status = lvData.getStatus();
				//返回一下正在战斗角色信息
				String fighterId = lvData.getFighterId();
				PlayerIF fighter = PlayerMgr.getInstance().getReadOnlyPlayer(fighterId);
				
				CopyBattleRoleStruct.Builder roleStruct = CopyBattleRoleStruct.newBuilder();
				roleStruct.setRoleIcon(fighter.getHeadImage());
				roleStruct.setRoleName(fighter.getUserName());
				roleStruct.setState(GroupCopyLevelBL.getCopyStateTips(status));
				roleStruct.setLv(fighter.getLevel());
				rspMsg.setBattleRole(roleStruct);
			}else{
				//可以进入
				boolean enter = updateCopyState(player, lvData, GroupCopyLevelBL.STATE_COPY_WAIT);
				rspMsg.setIsSuccess(enter);
			}
			
		} catch (Exception e) {
			GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[applyEnterCopy]", "角色请求进入关卡异常", e);
		}
		
		return rspMsg;
	}


	/**
	 * 更新关卡状态
	 * @param player
	 * @param lvData
	 * @param state TODO
	 * @return
	 */
	private boolean updateCopyState(Player player, GroupCopyLevelRecord lvData, int state) {
		try {
			lvData.setFighterId(player.getUserId());
			lvData.setLastBeginFightTime(System.currentTimeMillis());;
			lvData.setStatus(state);
			lvRecordHolder.updateItem(player, lvData);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	
	//处理赞助
	public GroupCopyResult donateBuff(Player player, Group group, GroupCopyCmdReqMsg reqMsg) {
		GroupCopyResult result = GroupCopyResult.newResult();
		GroupCopyDonateData data = reqMsg.getDonateData();
		boolean suc = false;
		result.setTipMsg("操作失败！");
		try {
			//判断一下是否有足够的钻石
			GroupCopyDonateCfg donateCfg = GroupCopyDonateCfgDao.getInstance().getCfgById(String.valueOf(data.getDonateTime()));
			if(donateCfg == null){
				result.setTipMsg("找不到次数为" + data.getDonateTime() + "的配置！");
				result.setSuccess(suc);
				return result;
			}
			
			boolean engough = player.getUserGameDataMgr().isGoldEngough(-donateCfg.getGold());
			if(!engough){
				result.setTipMsg("钻石不足～");
				result.setSuccess(suc);
				return result;
			}

			synchronized (this) {
				GroupCopyLevelRecord record = lvRecordHolder.getByLevel(data.getLevel());
				if(record == null){
					GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[donateBuff]", "角色赞助Buff找不到关卡id为"+ data.getLevel() 
							+ "的记录" , null);
					result.setTipMsg("服务器繁忙！");
					result.setSuccess(suc);
					return result;
				}
				
				if((record.getBuffCount() + donateCfg.getIncreValue()) > MAX_DONATE_COUNT){
					result.setTipMsg("超过赞助上限！");
					result.setSuccess(suc);
					return result;
				}
				record.addRoleDonate(player.getUserId(), data.getDonateTime());
				record.addBuff(donateCfg.getIncreValue());
				//扣钱  加帮贡
				int code = player.getUserGameDataMgr().addGold(-donateCfg.getGold());
						
				if(code == 0){
					player.getUserGroupAttributeDataMgr().useUserGroupContribution(donateCfg.getContribution());
					lvRecordHolder.updateItem(player, record);
					result.setTipMsg("赞助成功");
					suc = true;
				}
				
			}
		} catch (Exception e) {
			GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[donateBuff]", "赞助buff出现异常", e);
		}
		
		result.setSuccess(suc);
		return result;
	}

	
	//发送帮派前10排行榜信息
	public GroupCopyResult getDamageRank(Player player, Group g,
			GroupCopyCmdReqMsg reqMsg) {
		GroupCopyResult result = GroupCopyResult.newResult();
		String chaterID = reqMsg.getChaterID();
		GroupCopyMapRecord mapRecord = mapRecordHolder.getItem(chaterID);
		if(mapRecord == null){
			result.setSuccess(false);
			result.setTipMsg("找不到对应id为"+chaterID+"的地图配置！");
			return result;
		}
		GroupCopyDamegeRankInfo rankInfo = mapRecord.getDamegeRankInfo();
		GroupCopyHurtRank.Builder hr = GroupCopyHurtRank.newBuilder();
		ArmyHurtStruct.Builder struct;
		for (GroupCopyArmyDamageInfo item : rankInfo.getDamageRank()) {
			struct = ArmyHurtStruct.newBuilder();
			struct.setHeadIcon(item.getArmy().getPlayerHeadImage());
			struct.setRoleName(item.getArmy().getPlayerName());
			struct.setLv(item.getArmy().getPlayer().getLevel());
			struct.setKillTime(item.getTime());
			struct.setDamage(item.getDamage());
			hr.addRankData(struct);
		}
		
		result.setItem(hr);
		result.setSuccess(true);
		result.setTipMsg("操作成功！");
		return result;
	}



}
