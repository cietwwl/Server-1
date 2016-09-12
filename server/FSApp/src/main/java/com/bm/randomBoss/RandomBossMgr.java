package com.bm.randomBoss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.timer.Timer;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.CurAttrData;
import com.playerdata.battleVerify.MonsterCfg;
import com.playerdata.battleVerify.MonsterCfgDao;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.util.DateUtils;
import com.rw.shareCfg.ChineseStringHelper;
import com.rwbase.common.RandomUtil;
import com.rwbase.dao.randomBoss.cfg.RBServerCfgDao;
import com.rwbase.dao.randomBoss.cfg.RandomBossCfg;
import com.rwbase.dao.randomBoss.cfg.RandomBossCfgDao;
import com.rwbase.dao.randomBoss.cfg.RandomBossServerCfg;
import com.rwbase.dao.randomBoss.db.BattleNewsData;
import com.rwbase.dao.randomBoss.db.RandomBossRecord;
import com.rwbase.dao.randomBoss.db.RandomBossRecordDAO;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.MsgDef.Command;
import com.rwproto.RandomBossProto.BattleRewardInfo;
import com.rwproto.RandomBossProto.ItemInfo;
import com.rwproto.RandomBossProto.MsgType;
import com.rwproto.RandomBossProto.RandomBossMsgResponse.Builder;
import com.rwproto.RandomBossProto.RandomBossPushMsg;


/**
 * 随机boss管理类
 * @author Alex
 * 2016年9月8日 下午5:09:50
 */
public class RandomBossMgr{

	//数据dao
	private RandomBossRecordDAO rbDao;
	
	//配置
	private RandomBossServerCfg rbServerCfg;
	
	private final int INVITED_TYPE_FRIEND = 1;
	private final int INVITED_TYPE_GROUP = 2;
	
	
	private static RandomBossMgr instance = new RandomBossMgr();
	
	private RandomBossMgr(){
		rbDao = RandomBossRecordDAO.getInstance();
		rbServerCfg = RBServerCfgDao.getInstance().getDefaultCfg();
	}

	public synchronized static RandomBossMgr getInstance(){
		return instance;
	}
	
	
	/**
	 * 同步角色随机boss数据到前端
	 * <p>此方法会检查角色随机boss数据，过滤已经不合条件的boss</p>
	 * @param player
	 */
	public boolean checkAndSynRandomBossData(Player player){
		if(player == null){
			return false;
		}
		long nowTime = System.currentTimeMillis();
		long resetTime = DateUtils.getCurrentDayResetTime();
		List<String> bossIDs = player.getUserGameDataMgr().getRandomBossIDs();
		List<String> removeList = new ArrayList<String>();
		Map<String, RandomBossCfg> map = RandomBossCfgDao.getInstance().getMaps();
		List<RandomBossRecord> synList = new ArrayList<RandomBossRecord>();
		for (String id : bossIDs) {
			RandomBossRecord record = rbDao.get(id);
			if(record == null){
				removeList.add(id);
				continue;
			}
			if(record.getExcapeTime() <= nowTime && record.getLeftHp() > 0){
				removeList.add(id);//超时没有击杀的不再显示
				rbDao.delete(id);//删除这些记录
				continue;
			}
			RandomBossCfg cfg = map.get(record.getBossTemplateId());
			if( cfg == null){
				//配置表里找不到对应的配置，可能策划删除了
				removeList.add(id);
				rbDao.delete(id);
				continue;
			}
			
			//如果已经超过了早上5点，把之前的击杀boss删除
			long bornTimeMs = record.getExcapeTime() - (cfg.getExistTime() * 1000);
			if(bornTimeMs < resetTime && record.getLeftHp() <= 0){
				removeList.add(id);
				rbDao.delete(id);
				continue;
			}

			int count = record.roleFightBossCount(player.getUserId());
			RandomBossRecord clone = record.clone();
			clone.setBattleTime(count);
			System.err.println("boss excape time:" + DateUtils.getDateTimeFormatString(record.getExcapeTime(), "yyyy-MM-dd HH:mm"));
			synList.add(clone);
		}
		
		bossIDs.removeAll(removeList);
		if(synList.isEmpty()){
			return false;
		}
		
		//同步到前端
		ClientDataSynMgr.synDataList(player, synList, eSynType.RANDOM_BOSS_DATA, eSynOpType.UPDATE_LIST);
		return true;
	}

	
	
	/**
	 * 接受邀请
	 * @param player
	 * @param bossID
	 * @param response
	 */
	public void acceptedInvited(Player player, String bossID, Builder response) {
		//检查一下boss是否还在
		RandomBossRecord record = rbDao.get(bossID);
		if(record == null){
			response.setIsSuccess(false);
			response.setTips(ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getInvitedTimeOutTips(), "邀请已过期，不能前往讨伐"));
			return;
		}
		//检查一下是否已经超时
		if(record.getExcapeTime() <= System.currentTimeMillis()){
			rbDao.delete(bossID);
			response.setIsSuccess(false);
			response.setTips(ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getInvitedTimeOutTips(), "邀请已过期，不能前往讨伐"));
			return;
		}
		
		//检查自己的列表里是否已经存在
		List<String> list = player.getUserGameDataMgr().getRandomBossIDs();
		if(list.contains(bossID)){
			response.setIsSuccess(false);
			response.setTips(ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getInvitedAccepted(), "邀请之前已经接受"));
			return;
		}
		
		list.add(bossID);
		try {
			ClientDataSynMgr.synData(player, record, eSynType.RANDOM_BOSS_DATA, eSynOpType.ADD_SINGLE);
			response.setIsSuccess(true);
		} catch (Exception e) {
			response.setIsSuccess(false);
			response.setTips("系统繁忙");
		}
	}

	
	/**
	 * 获取随机boss讨伐信息
	 * @param player
	 * @param bossID
	 * @return
	 */
	public List<BattleNewsData> getBattleInfo(Player player, String bossID) {
		RandomBossRecord record = rbDao.get(bossID);
		if(record == null){
			return null;
		}
		return record.getBattleInfo();
	}

	
	/**
	 * 请求进入战斗
	 * @param player
	 * @param bossID
	 * @param response
	 */
	public void applyEnterBattle(Player player, String bossID, Builder response) {
		RandomBossRecord record = rbDao.get(bossID);
		if(record == null){
			response.setIsSuccess(false);
			response.setTips(ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getBossExcapeTips(), "boss已经离开！"));
			return;
		}
		//检查是否已经达到上限
		
		RandomBossCfg bossCfg = RandomBossCfgDao.getInstance().getCfgById(record.getBossTemplateId());
		if(bossCfg.getCrusadeNum() <= record.roleFightBossCount(player.getUserId())){
			String t = ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getSingleBossFightLimitTips(), "你讨伐此魔神已达{0}次，不能再继续啦！");
			String tips = String.format(t, bossCfg.getCrusadeNum());
			response.setIsSuccess(false);
			response.setTips(tips);
			return;
		}
		
		if(player.getUserGameDataMgr().getFightRandomBossCount() >= rbServerCfg.getMaxBattleCount()){
			String t = ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getTotalFightLimitTips(), "今天已经参与了{0}次讨伐，不能再继续啦！");
			String tips = String.format(t, rbServerCfg.getMaxBattleCount());
			response.setIsSuccess(false);
			response.setTips(tips);
			return;
		}
		
		//是否已经被击杀
		if(record.getLeftHp() <= 0){
			response.setIsSuccess(false);
			response.setTips(ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getBossExcapeTips(), "boss已经被击杀！"));
			return;
		}
		
		
		//检查是否在战斗中
		if(!record.resetLastBattleTime()){
			response.setIsSuccess(true);
			response.setTips(ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getBossInBattleTips(), "魔神正在被讨伐中，请稍后"));
			return;
		}
		
		
		//可以进入战斗  构建armyInfo
		
		List<String> mID = new ArrayList<String>();
		mID.add(record.getBossTemplateId());
		List<CurAttrData> attrList = new ArrayList<CurAttrData>();
		CurAttrData ad = new CurAttrData();
		ad.setId(record.getBossTemplateId());
		ad.setCurLife((int) record.getLeftHp());
		attrList.add(ad);
		ArmyInfo armyInfo = ArmyInfoHelper.buildMonsterArmy(mID, attrList);
		
		if(armyInfo == null){
			response.setIsSuccess(false);
			response.setTips("怪物数据错误");
			return;
		}
		
		player.getUserGameDataMgr().increaseRandomBossFightCount();
		record.addBattleRole(player.getUserId());
		rbDao.update(record);
		
		response.setIsSuccess(true);
		armyInfo.genVCode();
		response.setArmy(ClientDataSynMgr.toClientData(armyInfo));
		
	}

	/**
	 * 战斗结束
	 * @param player
	 * @param bossID
	 * @param curHp
	 * @return
	 */
	public BattleRewardInfo.Builder endBattle(Player player, String bossID, long curHp) {
		RandomBossRecord record = rbDao.get(bossID);
		
		//检查战斗角色是否匹配
		if(!StringUtils.equals(record.getBattleRoleID(), player.getUserId())){
			GameLog.error("RandomBoss", "RandomBossMgr[endBattle]", "随机boss战斗结束，检查角色id不匹配", null);
			return null;
		}
		
		
		//检查伤害值
		long damage = record.getLeftHp() - curHp;
		if(damage <= 0){
			//TODO 没有造成伤害怎么处理  现在还是给奖励
			GameLog.error("RandomBoss", "RandomBossMgr[endBattle]", "随机boss战斗结束，发现伤害值不超过0，原来伤害:"+record.getLeftHp()+"，后来值:" + curHp, null);
		}

		if(curHp <= 0){
			record.setFinalHitRole(player.getUserId());
		}
		
		Map<Integer, Integer> rewardMap = new HashMap<Integer, Integer>();
		RandomBossCfg bossCfg = RandomBossCfgDao.getInstance().getCfgById(record.getBossTemplateId());
		rewardMap.putAll(bossCfg.getBattleRewardMap());
		//如果是发现者，添加发现者奖励
		if(StringUtils.equals(record.getOwnerID(), player.getUserId())){
			rewardMap.putAll(bossCfg.getFindRewardMap());			
		}
		//检查最后一击奖励
		int killBossRewardCount = player.getUserGameDataMgr().getKillBossRewardCount();
		if(curHp == 0 && killBossRewardCount <= rbServerCfg.getKillBossRewardLimit()){
			rewardMap.putAll(bossCfg.getKillRewardMap());
		}
		
		BattleRewardInfo.Builder rewardInfo = BattleRewardInfo.newBuilder();
		List<com.rwbase.dao.copy.pojo.ItemInfo> itemList = new ArrayList<com.rwbase.dao.copy.pojo.ItemInfo>();
		for (Iterator<Entry<Integer, Integer>> itr = rewardMap.entrySet().iterator(); itr.hasNext();) {
			Entry<Integer, Integer> entry =  itr.next();
			ItemInfo.Builder i = ItemInfo.newBuilder();
			i.setItemID(entry.getKey());
			i.setCount(entry.getValue());
			rewardInfo.addItems(i);
			com.rwbase.dao.copy.pojo.ItemInfo item = new com.rwbase.dao.copy.pojo.ItemInfo(entry.getKey(), entry.getValue());
			itemList.add(item);
		}
		
		//添加讨伐动态
		record.addBattleInfo(new BattleNewsData(player.getUserId(), damage, curHp == 0, record.getLastBattleTime(), player.getUserName()));
		player.getItemBagMgr().addItem(itemList);
		record.battleEnd();
		record.setLeftHp(curHp);
		rbDao.update(record);
		return rewardInfo;
	}

	
	

	/**
	 * 发现boss 暂时是100%发现boss
	 * @param player
	 * @return
	 */
	public void findBossBorn(Player player){
		//检查角色等级
		int level = player.getLevel();
		if(level < rbServerCfg.getOpenLv()){
			return;
		}
		//检查当天创建boss次数
		int num = player.getUserGameDataMgr().getCreateBossCount();
		if(num >= rbServerCfg.getCreateBossCountLimit()){
			return;
		}
		
		//随机机率
		int r = RandomUtil.getRandonIndexWithoutProb(100);
		if(r > rbServerCfg.getBossBornRate()){
			return;
		}
		
		
		List<RandomBossCfg> list = RandomBossCfgDao.getInstance().getLvCfgs(level);
		
		//随机一个
		int i = RandomUtil.getRandonIndexWithoutProb(list.size());
		try {
			RandomBossCfg cfg = list.get(i);
			
			MonsterCfg monsterCfg = MonsterCfgDao.getInstance().getConfig(cfg.getId());
			String id = player.getUserId()+"-"+System.currentTimeMillis();
			
			long excapeTime = System.currentTimeMillis() + (cfg.getExistTime() * 1000);
			rbDao.create(id, player.getUserId(), monsterCfg.getLife(), 
					cfg.getId(), excapeTime);
			RandomBossPushMsg.Builder msg = RandomBossPushMsg.newBuilder();
			msg.setMsgType(MsgType.FIND_BOSS);
			msg.setBossID(cfg.getId());
			player.getUserGameDataMgr().addRandomBoss(id);
			player.SendMsg(Command.MSG_RANDOM_BOSS, msg.build().toByteString());
			
//			String e = DateUtils.getDateTimeFormatString(excapeTime, "yyyy-MM-dd HH:mm:ss");
//			System.out.println("新随机boss生成，离开时间：" + e);
			
		} catch (Exception e) {
			e.printStackTrace();
			GameLog.error("随机boss", "RandomBossMgr[findBossBorn]", "随机boss生成boss时出现异常，角色等级：" + level 
					+ ",random data size:" + list.size() + ", random index:" + i, e);
		}
		
	}

	public String getBossBornTips() {
		return ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getBossBornTips(), "魔神已经出现");
	}

	public String getBossKilledKey() {
		return ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getBossWasKilledTips(), "魔神已经出现");
	}

	
	/**
	 * 获取随机boss战斗时长，返回的ms
	 * @return
	 */
	public long getBattleTimeLimit() {
		return rbServerCfg.getBattleTimeLimit()  * Timer.ONE_SECOND;
	}

	/**
	 * 记录邀请好友时间
	 * @param type
	 * @param bossId TODO
	 */
	public boolean recordInvitedTime(int type, String bossId) {
		RandomBossRecord record = rbDao.get(bossId);
		if(record == null){
			GameLog.error("RandomBoss", "RandomBossMgr[reocrdInvitedTime]", "记录邀请好友时间点时找不到对应记录，bossID:" + bossId, null);
			return false;
		}
		
		long time = System.currentTimeMillis();
		switch (type) {
		case INVITED_TYPE_FRIEND:
			record.setLastFriendInvitedTime(time);
			break;
		case INVITED_TYPE_GROUP:
			record.setLastGroudInvitedTime(time);
			break;
		default:
			return false;
		}
		rbDao.update(record);
		return true;
	}



	
	

	
	
	
	
	
	

}
