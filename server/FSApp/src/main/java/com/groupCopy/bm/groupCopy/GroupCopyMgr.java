package com.groupCopy.bm.groupCopy;

import java.util.ArrayList;
import java.util.List;

import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfgDao;
import com.groupCopy.rwbase.dao.groupCopy.db.CopyItemDropAndApplyRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.DropAndApplyRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyArmyDamageInfo;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.ItemDropAndApplyTemplate;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyProgress;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyRewardRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyTeamInfo;
import com.groupCopy.rwbase.dao.groupCopy.db.ServerGroupCopyDamageRecordMgr;
import com.groupCopy.rwbase.dao.groupCopy.db.TeamHero;
import com.rwproto.GroupCopyBattleProto.CopyMonsterStruct;
import com.rwproto.GroupCopyBattleProto.CopyRewardInfo;
import com.rwproto.GroupCopyBattleProto.CopyRewardInfo.Builder;
import com.rwproto.GroupCopyBattleProto.CopyRewardStruct;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;

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
	
	public GroupCopyMgr(String groupIdP) {
		lvRecordHolder = new GroupCopyLevelRecordHolder(groupIdP);
		mapRecordHolder = new GroupCopyMapRecordHolder(groupIdP);
		rewardRecordHolder = new GroupCopyRewardRecordHolder(groupIdP);
		dropHolder = new DropAndApplyRecordHolder(groupIdP);
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
	 * @param levelId
	 * @param mData 客户端返回的怪物数据
	 * @param heroList TODO
	 * @return
	 */
	public synchronized GroupCopyResult  endFight(Player player, String levelId, 
			List<CopyMonsterStruct> mData, List<String> heroList){
		//获取伤害
		int damage = getDamage(mData, levelId);
		GroupCopyResult result = GroupCopyLevelBL.endFight(player, lvRecordHolder, levelId, mData);
		//同步一下副本地图进度
		GroupCopyMapBL.calculateMapProgress(player, lvRecordHolder, mapRecordHolder,levelId);
		//检查是否进入章节前10伤害排行 
		checkDamageRank(player,levelId, damage, heroList);
		//将奖励入放帮派奖励缓存
		addReward2Group(player,levelId, (CopyRewardInfo.Builder)result.getItem());
		
		
		return result;
	}

	
	private int getDamage(List<CopyMonsterStruct> mData, String level){
		GroupCopyProgress nowPro = new GroupCopyProgress(mData);
		GroupCopyLevelRecord record = lvRecordHolder.getByLevel(level);
		if(record.getProgress().getCurrentHp() == 0){
			return nowPro.getTotalHp() - nowPro.getCurrentHp();
		}
		return record.getProgress().getCurrentHp() - nowPro.getCurrentHp();
	}

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
		teamInfo.setGuildName(info.getGuildName());
		
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


	

}
