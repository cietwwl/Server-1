package com.bm.randomBoss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.timer.Timer;

import org.apache.commons.lang3.StringUtils;

import com.common.Utils;
import com.log.GameLog;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.CurAttrData;
import com.playerdata.battleVerify.MonsterCfg;
import com.playerdata.battleVerify.MonsterCfgDao;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.Email.EmailUtils;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.role.MainMsgHandler;
import com.rw.service.role.PmdMsgType;
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
import com.rwproto.RandomBossProto.RandomBossSynBattleCount;

/**
 * 随机boss管理类
 * 
 * @author Alex 2016年9月8日 下午5:09:50
 */
public class RandomBossMgr {

	private final int INVITED_TYPE_FRIEND = 1;
	private final int INVITED_TYPE_GROUP = 2;

	private static RandomBossMgr instance = new RandomBossMgr();
	

	protected RandomBossMgr() {
	}

	public synchronized static RandomBossMgr getInstance() {
		return instance;
	}

	/**
	 * 同步角色随机boss数据到前端
	 * <p>
	 * 此方法会检查角色随机boss数据，过滤已经不合条件的boss
	 * </p>
	 * 
	 * @param player
	 */
	public boolean checkAndSynRandomBossData(Player player) {
		if (player == null) {
			return false;
		}
		RandomBossRecordDAO rbDao = RandomBossRecordDAO.getInstance();
		// 先同步一下次数
		long nowTime = System.currentTimeMillis();
		synBattleCount(player);
		long resetTime = DateUtils.getCurrentDayResetTime();
		List<String> bossIDs = player.getUserGameDataMgr().getRandomBossIDs();
		List<String> removeList = new ArrayList<String>();
		Map<String, RandomBossCfg> map = RandomBossCfgDao.getInstance().getMaps();
		List<RandomBossRecord> synList = new ArrayList<RandomBossRecord>();
		for (String id : bossIDs) {
			RandomBossRecord record = rbDao.get(id);
			if (record == null) {
				removeList.add(id);
				continue;
			}
			if (record.getExcapeTime() <= nowTime && record.getLeftHp() > 0) {
				removeList.add(id);// 超时没有击杀的不再显示
				rbDao.delete(id);// 删除这些记录
				continue;
			}
			RandomBossCfg cfg = map.get(record.getBossTemplateId());
			if (cfg == null) {
				// 配置表里找不到对应的配置，可能策划删除了
				removeList.add(id);
				rbDao.delete(id);
				continue;
			}

			// 如果已经超过了早上5点，把之前的击杀boss删除
			long bornTimeMs = record.getExcapeTime() - (cfg.getExistTime() * 1000);
			if (bornTimeMs < resetTime && record.getLeftHp() <= 0) {
				removeList.add(id);
				rbDao.delete(id);
				continue;
			}

			int count = record.roleFightBossCount(player.getUserId());
			RandomBossRecord clone = record.clone();
			clone.setBattleTime(count);
			// System.err.println("last invited time:" +record.getLastFriendInvitedTime() + ", " + record.getLastGroupInvitedTime());
			synList.add(clone);
		}

		bossIDs.removeAll(removeList);

		if (synList.isEmpty()) {
			return false;
		}

		// 检查等级，如果等级还没有到，不同步到前端
		RandomBossServerCfg rbServerCfg = RBServerCfgDao.getInstance().getDefaultCfg();
		int level = player.getLevel();
		if (level < rbServerCfg.getOpenLv()) {
			return false;
		}
		
		//TODO 在这里进行排序一下 ,客户端的排序逻辑也要改一下
		Collections.sort(synList, new RandomBossComparator(player.getUserId()));
		ClientDataSynMgr.synDataList(player, synList, eSynType.RANDOM_BOSS_DATA, eSynOpType.UPDATE_LIST);
		return true;
	}

	/**
	 * 接受邀请
	 * 
	 * @param player
	 * @param bossID
	 * @param response
	 */
	public void acceptedInvited(Player player, String bossID, Builder response) {
		RandomBossRecordDAO rbDao = RandomBossRecordDAO.getInstance();
		RandomBossServerCfg rbServerCfg = RBServerCfgDao.getInstance().getDefaultCfg();
		// 检查一下boss是否还在
		RandomBossRecord record = rbDao.get(bossID);
		if (record == null) {
			response.setIsSuccess(false);
			response.setTips(ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getInvitedTimeOutTips(), "找不到目标boss数据"));
			return;
		}
		// 检查一下是否已经超时
		if (record.getExcapeTime() <= System.currentTimeMillis()) {
			rbDao.delete(bossID);
			response.setIsSuccess(false);
			response.setTips(ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getInvitedTimeOutTips(), "邀请已过期，不能前往讨伐"));
			return;
		}

		// 检查自己的列表里是否已经存在
		List<String> list = player.getUserGameDataMgr().getRandomBossIDs();
		if (list.contains(bossID)) {
			response.setIsSuccess(false);
			response.setTips(ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getInvitedAccepted(), "已经存在自己的列表里"));
			return;
		}

		try {
			player.getUserGameDataMgr().addRBWithoutIncrease(bossID);
			
			ClientDataSynMgr.synData(player, record, eSynType.RANDOM_BOSS_DATA, eSynOpType.ADD_SINGLE);
			response.setIsSuccess(true);
		} catch (Exception e) {
			response.setIsSuccess(false);
			response.setTips("系统繁忙");
		}
	}
	
	
	public void acceptedInternal(String roleID, String bossID) {
		RandomBossRecordDAO rbDao = RandomBossRecordDAO.getInstance();
		// 检查一下boss是否还在
		RandomBossRecord record = rbDao.get(bossID);
		if (record == null) {
			return;
		}
		
		// 检查一下是否已经超时
		if (record.getExcapeTime() <= System.currentTimeMillis()) {
			return;
		}

		Player player = PlayerMgr.getInstance().find(roleID);
		if(player == null || player.isRobot()){//不加给机器人
			return;
		}
			
		// 检查自己的列表里是否已经存在
		List<String> list = player.getUserGameDataMgr().getRandomBossIDs();
		if (list.contains(bossID)) {
			return;
		}
		player.getUserGameDataMgr().addRBWithoutIncrease(bossID);
		if(PlayerMgr.getInstance().isOnline(roleID)){
			//角色在线，推送一下
			checkAndSynRandomBossData(player);
		}
		
		
	}
	
	

	/**
	 * 获取随机boss讨伐信息
	 * 
	 * @param player
	 * @param bossID
	 * @return
	 */
	public List<BattleNewsData> getBattleInfo(Player player, String bossID) {
		RandomBossRecordDAO rbDao = RandomBossRecordDAO.getInstance();
		RandomBossRecord record = rbDao.get(bossID);
		if (record == null) {
			return null;
		}
		return record.getBattleInfo();
	}

	/**
	 * 同步当前总的战斗次数
	 * 
	 * @param player
	 */
	private void synBattleCount(Player player) {
		if (player == null) {
			return;
		}
		int count = player.getUserGameDataMgr().getFightRandomBossCount();
		RandomBossServerCfg rbServerCfg = RBServerCfgDao.getInstance().getDefaultCfg();
		int maxBattleCount = rbServerCfg.getMaxBattleCount();
		RandomBossPushMsg.Builder msg = RandomBossPushMsg.newBuilder();
		RandomBossSynBattleCount.Builder bc = RandomBossSynBattleCount.newBuilder();
		bc.setCurCount(count);
		bc.setMaxCount(maxBattleCount);
		msg.setMsgType(MsgType.UPDATE_BATTLE_COUNT);
		msg.setBattleCount(bc);
		player.SendMsg(Command.MSG_RANDOM_BOSS, msg.build().toByteString());
	}

	/**
	 * 请求进入战斗
	 * 
	 * @param player
	 * @param bossID
	 * @param response
	 */
	public void applyEnterBattle(Player player, String bossID, Builder response) {
		RandomBossRecordDAO rbDao = RandomBossRecordDAO.getInstance();
		RandomBossRecord record = rbDao.get(bossID);
		RandomBossServerCfg rbServerCfg = RBServerCfgDao.getInstance().getDefaultCfg();
		if (record == null) {
			response.setIsSuccess(false);
			response.setTips(ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getBossExcapeTips(), "boss已经离开！"));
			return;
		}
		// 检查是否已经达到上限

		RandomBossCfg bossCfg = RandomBossCfgDao.getInstance().getCfgById(record.getBossTemplateId());
		if (bossCfg.getCrusadeNum() <= record.roleFightBossCount(player.getUserId())) {
			String t = ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getSingleBossFightLimitTips(), "你讨伐此魔神已达{0}次，不能再继续啦！");
			// String tips = String.format(t, bossCfg.getCrusadeNum());
			String tips = t.replace("{0}", String.valueOf(bossCfg.getCrusadeNum()));
			response.setIsSuccess(false);
			response.setTips(tips);
			return;
		}

		if (player.getUserGameDataMgr().getFightRandomBossCount() >= rbServerCfg.getMaxBattleCount()) {
			String t = ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getTotalFightLimitTips(), "今天已经参与了{0}次讨伐，不能再继续啦！");
			// String tips = String.format(t, rbServerCfg.getMaxBattleCount());
			String tips = t.replace("{0}", String.valueOf(rbServerCfg.getMaxBattleCount()));
			response.setIsSuccess(false);
			response.setTips(tips);
			return;
		}

		// 是否已经被击杀
		if (record.getLeftHp() <= 0) {
			response.setIsSuccess(false);
			response.setTips(ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getBossExcapeTips(), "boss已经被击杀！"));
			return;
		}

		// 检查是否在战斗中
		if (!record.resetLastBattleTime()) {
			response.setIsSuccess(false);
			response.setTips(ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getBossInBattleTips(), "魔神正在被讨伐中，请稍后"));
			return;
		}
		// 检查是否已经离开
		if (record.getExcapeTime() <= System.currentTimeMillis()) {
			response.setIsSuccess(false);
			response.setTips(ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getBossExcapeTips(), "boss已经离开！"));
			return;
		}

		// 可以进入战斗 构建armyInfo
		MonsterCfg monster = MonsterCfgDao.getInstance().getConfig(record.getBossTemplateId());
		List<String> mID = new ArrayList<String>();
		mID.add(record.getBossTemplateId());
		List<CurAttrData> attrList = new ArrayList<CurAttrData>();
		CurAttrData ad = new CurAttrData();
		ad.setId(record.getBossTemplateId());
		ad.setCurLife((int) record.getLeftHp());
		ad.setMaxLife((int) monster.getLife());
		ad.setMaxEnergy(monster.getEnergy());
		ad.setCurEnergy(0);
		attrList.add(ad);
		ArmyInfo armyInfo = ArmyInfoHelper.buildMonsterArmy(mID, attrList, bossCfg.getLevelID());

		if (armyInfo == null) {
			response.setIsSuccess(false);
			response.setTips("怪物数据错误");
			return;
		}

		record.setBattleRoleID(player.getUserId());
		// 战斗结束后再给角色增加次数
		// player.getUserGameDataMgr().increaseRandomBossFightCount();
		// rbDao.update(record);

		response.setIsSuccess(true);
		armyInfo.genVCode();
		response.setArmy(ClientDataSynMgr.toClientData(armyInfo));

	}

	/**
	 * 战斗结束
	 * 
	 * @param player
	 * @param bossID
	 * @param curHp
	 * @return
	 */
	public synchronized BattleRewardInfo.Builder endBattle(Player player, String bossID, long curHp) {
		RandomBossRecordDAO rbDao = RandomBossRecordDAO.getInstance();
		RandomBossRecord record = rbDao.get(bossID);
		RandomBossServerCfg rbServerCfg = RBServerCfgDao.getInstance().getDefaultCfg();
		// 检查战斗角色是否匹配
		if (!StringUtils.equals(record.getBattleRoleID(), player.getUserId())) {
			GameLog.error("RandomBoss", "RandomBossMgr[endBattle]", "随机boss战斗结束，检查角色id不匹配", null);
			return null;
		}

		// 检查伤害值
		long damage = record.getLeftHp() - curHp;
		if (damage <= 0) {
			// TODO 没有造成伤害怎么处理 现在还是给奖励
			GameLog.error("RandomBoss", "RandomBossMgr[endBattle]", "随机boss战斗结束，发现伤害值不超过0，原来伤害:" + record.getLeftHp() + "，后来值:" + curHp, null);
		}

		if (curHp <= 0 && record.getLeftHp() > 0) {// 如果原来已经被击杀，就不更新最后一击
			record.setFinalHitRole(player.getUserId());
		}

		Map<Integer, Integer> rewardMap = new HashMap<Integer, Integer>();
		RandomBossCfg bossCfg = RandomBossCfgDao.getInstance().getCfgById(record.getBossTemplateId());
		rewardMap.putAll(bossCfg.getBattleRewardMap());
		// 如果是发现者，添加发现者奖励 ---按策划要求，去掉这个奖励 by Alex 11.12.2016
		// if(StringUtils.equals(record.getOwnerID(), player.getUserId())){
		// Utils.combineAttrMap(bossCfg.getFindRewardMap(), rewardMap);
		// }
		// 检查最后一击奖励
		int killBossRewardCount = player.getUserGameDataMgr().getKillBossRewardCount();
		if (curHp == 0 && killBossRewardCount < rbServerCfg.getKillBossRewardLimit() && record.getLeftHp() > 0) {

			Utils.combineAttrMap(bossCfg.getKillRewardMap(), rewardMap);
			player.getUserGameDataMgr().increaseBossRewardCount();
		}
		if (curHp <= 0 && record.getLeftHp() > 0) {// 如果原来已经被击杀，就不发送击杀者奖励，避免发多次
			// 给发现者发奖励
			List<String> args = new ArrayList<String>();
			MonsterCfg monsterCfg = MonsterCfgDao.getInstance().getCfgById(bossCfg.getId());
			if (monsterCfg != null) {
				args.add(monsterCfg.getName());
			}
			args.add(player.getUserName());
			EmailUtils.sendEmail(record.getOwnerID(), bossCfg.getAwardID(), bossCfg.getFindReward(), args);
		}

		BattleRewardInfo.Builder rewardInfo = BattleRewardInfo.newBuilder();
		List<com.rwbase.dao.copy.pojo.ItemInfo> itemList = new ArrayList<com.rwbase.dao.copy.pojo.ItemInfo>();
		for (Iterator<Entry<Integer, Integer>> itr = rewardMap.entrySet().iterator(); itr.hasNext();) {
			Entry<Integer, Integer> entry = itr.next();
			ItemInfo.Builder i = ItemInfo.newBuilder();
			i.setItemID(entry.getKey());
			i.setCount(entry.getValue());
			rewardInfo.addItems(i);
			com.rwbase.dao.copy.pojo.ItemInfo item = new com.rwbase.dao.copy.pojo.ItemInfo(entry.getKey(), entry.getValue());
			itemList.add(item);
		}

		// 添加讨伐动态
		record.addBattleInfo(new BattleNewsData(player.getUserId(), damage, curHp == 0, record.getLastBattleTime(), player.getUserName()));
		ItemBagMgr.getInstance().addItem(player, itemList);
		player.getUserGameDataMgr().increaseRandomBossFightCount();
		record.battleEnd(player.getUserId());
		record.setLeftHp(curHp);
		rbDao.update(record);

		// 更新一下到前端
		int count = record.roleFightBossCount(player.getUserId());
		RandomBossRecord clone = record.clone();
		clone.setBattleTime(count);
		ClientDataSynMgr.synData(player, clone, eSynType.RANDOM_BOSS_DATA, eSynOpType.UPDATE_SINGLE);
		synBattleCount(player);

		// 通知日常任务系统
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.RANDOM_BOSS, 1);
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.RANDOMBOSS_BATTLE, 1);
		
		return rewardInfo;
	}

	/**
	 * 发现boss 暂时是100%发现boss
	 * 
	 * @param player
	 * @param hasRate TODO 是否检查机率
	 * @return
	 */
	public void findBossBorn(Player player, boolean hasRate) {
		// 检查角色等级
		RandomBossServerCfg rbServerCfg = RBServerCfgDao.getInstance().getDefaultCfg();
		int level = player.getLevel();
		if (level < rbServerCfg.getOpenLv()) {
			return;
		}
		// 检查当天创建boss次数
		int num = player.getUserGameDataMgr().getCreateBossCount();
		if (num >= rbServerCfg.getCreateBossCountLimit()) {
			return;
		}

		// 随机机率
		if (hasRate) {
			int r = RandomUtil.getRandonIndexWithoutProb(10000);
			if (r > rbServerCfg.getBossBornRate()) {
				return;
			}
		}

		// 这里要根据权重进行随机

		Pair<Integer, Map<Pair<Integer, Integer>, RandomBossCfg>> pair = RandomBossCfgDao.getInstance().getLvCfgs(level);
		if (pair == null) {
			GameLog.error("RandomBoss", "RandomBossMgr[findBossBorn]", "检查随机boss生成时，没有找到对应等级[" + level + "]的数据", null);
			return;
		}

		// 随机一个
		int i = RandomUtil.getRandonIndexWithoutProb(pair.getT1());
		try {

			RandomBossRecordDAO rbDao = RandomBossRecordDAO.getInstance();
			RandomBossCfg cfg = getTargetCfg(i, pair.getT2());

			MonsterCfg monsterCfg = MonsterCfgDao.getInstance().getConfig(cfg.getId());
			String id = player.getUserId() + "-" + System.currentTimeMillis();

			long excapeTime = System.currentTimeMillis() + (cfg.getExistTime() * 1000);
			RandomBossRecord newBoss = rbDao.create(id, player.getUserId(), monsterCfg.getLife(), cfg.getId(), excapeTime);
			RandomBossPushMsg.Builder msg = RandomBossPushMsg.newBuilder();
			msg.setMsgType(MsgType.FIND_BOSS);
			msg.setBossID(cfg.getId());
			player.getUserGameDataMgr().addRandomBoss(id);
			player.SendMsg(Command.MSG_RANDOM_BOSS, msg.build().toByteString());

			// String e = DateUtils.getDateTimeFormatString(excapeTime, "yyyy-MM-dd HH:mm:ss");
			// System.out.println("新随机boss生成，离开时间：" + e);
			List<String> pmbMsgList = new ArrayList<String>();
			pmbMsgList.add(player.getUserName());
			pmbMsgList.add(String.valueOf(monsterCfg.getLevel()));
			pmbMsgList.add(monsterCfg.getName());
			pmbMsgList.add(player.getUserId());
			MainMsgHandler.getInstance().sendWorldBossPmb(player, PmdMsgType.RandomBoss.getId(), pmbMsgList);
			// 同步到前端
			ClientDataSynMgr.synData(player, newBoss, eSynType.RANDOM_BOSS_DATA, eSynOpType.ADD_SINGLE);

		} catch (Exception e) {
			e.printStackTrace();
			GameLog.error("随机boss", "RandomBossMgr[findBossBorn]", "随机boss生成boss时出现异常，角色等级：" + level + ",random data size:" + pair.getT2().size() + ", random index:" + i, e);
		}

	}

	private RandomBossCfg getTargetCfg(int weight, Map<Pair<Integer, Integer>, RandomBossCfg> pair) {
		RandomBossCfg cfg = null;

		for (Iterator<Entry<Pair<Integer, Integer>, RandomBossCfg>> itr = pair.entrySet().iterator(); itr.hasNext();) {
			Entry<Pair<Integer, Integer>, RandomBossCfg> entry = itr.next();
			Pair<Integer, Integer> key = entry.getKey();
			if (key.getT1() < weight && key.getT2() >= weight) {
				cfg = entry.getValue();
				break;
			}
		}
		return cfg;
	}

	public String getBossBornInvitedTips() {
		RandomBossServerCfg rbServerCfg = RBServerCfgDao.getInstance().getDefaultCfg();
		return ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getBossBornTips(), "魔神已经出现");
	}

	public String getBossKilledKey() {
		RandomBossServerCfg rbServerCfg = RBServerCfgDao.getInstance().getDefaultCfg();
		return ChineseStringHelper.getInstance().getLanguageString(rbServerCfg.getBossWasKilledTips(), "魔神已经出现");
	}

	/**
	 * 获取随机boss战斗时长，返回的ms
	 * 
	 * @return
	 */
	public long getBattleTimeLimit() {
		RandomBossServerCfg rbServerCfg = RBServerCfgDao.getInstance().getDefaultCfg();
		return rbServerCfg.getBattleTimeLimit() * Timer.ONE_SECOND;
	}

	/**
	 * 记录邀请好友时间
	 * 
	 * @param type
	 * @param bossId TODO
	 */
	public boolean recordInvitedTime(int type, String bossId) {
		RandomBossRecordDAO rbDao = RandomBossRecordDAO.getInstance();
		RandomBossRecord record = rbDao.get(bossId);

		if (record == null) {
			GameLog.error("RandomBoss", "RandomBossMgr[reocrdInvitedTime]", "记录邀请好友时间点时找不到对应记录，bossID:" + bossId, null);
			return false;
		}
		long lastInvitedTime = 0;
		switch (type) {
		case INVITED_TYPE_FRIEND:
			lastInvitedTime = record.getLastFriendInvitedTime();
			break;
		case INVITED_TYPE_GROUP:
			lastInvitedTime = record.getLastGroupInvitedTime();
			break;
		default:
			return false;
		}

		long time = System.currentTimeMillis();
		if ((lastInvitedTime + (5 * 60 * 1000)) > time) {
			return false;
		}

		switch (type) {
		case INVITED_TYPE_FRIEND:
			record.setLastFriendInvitedTime(time);
			break;
		case INVITED_TYPE_GROUP:
			record.setLastGroupInvitedTime(time);
			break;
		default:
			return false;
		}
		rbDao.update(record);
		return true;
	}

	

}
