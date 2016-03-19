package com.playerdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.bm.arena.ArenaBM;
import com.bm.rank.ListRankingType;
import com.bm.rank.arena.ArenaExtAttribute;
import com.log.GameLog;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.CurAttrData;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.TableAttr;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.common.enu.eAttrIdDef;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.tower.TableTowerDataDAO;
import com.rwbase.dao.tower.TowerAwardCfg;
import com.rwbase.dao.tower.TowerAwardCfgDAO;
import com.rwbase.dao.tower.TowerFirstAwardCfgDAO;
import com.rwbase.dao.tower.TowerGoodsCfg;
import com.rwbase.dao.tower.TowerGoodsCfgDAO;
import com.rwbase.dao.tower.pojo.TableTowerData;
import com.rwbase.dao.tower.pojo.TowerHeroChange;
import com.rwproto.ItemBagProtos.EItemTypeDef;
import com.rwproto.SyncAttriProtos.TagAttriData;

public class TowerMgr implements TowerMgrIF {
	private TableTowerDataDAO tableTowerDataDAO = TableTowerDataDAO.getInstance();
	private TableTowerData tableTowerData;
	private int towerUpdateNum = 3;// 每次开放层
	public int totalTowerNum = 15;// 总塔层
	private int fightBlock = 12;// 战斗力分级数据块
	private HashMap<Integer, List<String>> enemyListBlock = new HashMap<Integer, List<String>>(fightBlock);// 12组敌人战斗力分级数据快，List敌人id列表
	private List<String> getEnemyList = new ArrayList<String>(); // 记录已经获取过的敌方数据
																	// userId
	private Player player;

	public void init(Player pOwner) {
		player = pOwner;
		tableTowerData = tableTowerDataDAO.get(pOwner.getUserId());
	}

	public boolean save() {
		if (tableTowerData != null) {
			return tableTowerDataDAO.update(tableTowerData);
		}
		return false;
	}

	public void clearTowerData() {
		tableTowerData = null;
	}

	public TableTowerData getMyTowerData() {
		if(tableTowerData!=null){
			return tableTowerData;
		}
		tableTowerData = tableTowerDataDAO.get(player.getUserId());
		if (tableTowerData == null) {
			tableTowerData = new TableTowerData();
			resetData(tableTowerData, true);
		}
		return tableTowerData;
	}

	public void resetData(TableTowerData tableTowerData, Boolean isInit) {// 重置数据
		tableTowerData.setCurrTowerID(0);
		updateDataBlock();
		tableTowerData.setFighting(player.getMainRoleHero().getFighting());
		tableTowerData.setUserId(player.getUserId());
		tableTowerData.setLevel(player.getLevel());

		List<Boolean> openList = new ArrayList<Boolean>();
		List<Boolean> isGetAwardList = new ArrayList<Boolean>();
		List<Boolean> isFirstList = new ArrayList<Boolean>();
		List<Boolean> BeatList = new ArrayList<Boolean>();
		getEnemyList.clear();
		for (int i = 0; i < totalTowerNum; i++) {
			BeatList.add(i, false);
			if (i == 0) {
				openList.add(true);
			} else {
				openList.add(false);
			}
			isGetAwardList.add(false);

			if (isInit) {
				isFirstList.add(true);
			}
		}
		tableTowerData.setOpenTowerList(openList);
		tableTowerData.setBeatTowerList(BeatList);

		if (isInit) {// 每日更新
			tableTowerData.setFirstTowerList(isFirstList);
			tableTowerData.setRefreshTimes(0);
		} else {// 重致
			tableTowerData.setFirstTowerList(tableTowerData.getFirstTowerList());
			tableTowerData.setRefreshTimes(tableTowerData.getRefreshTimes() + 1);
			// tableTowerData.setRefreshTimes(0);
		}
		tableTowerData.setAwardTowerList(isGetAwardList);
		List<TowerHeroChange> changeInfo = new ArrayList<TowerHeroChange>();
		tableTowerData.setHeroChageList(changeInfo);
		tableTowerData.setEnemyList(new ConcurrentHashMap<Integer, ArmyInfo>());
		updatePlayerData();
		addEnemyDataByTowerId(0);// 初始0~2层
		tableTowerDataDAO.update(tableTowerData);
	}

	public void addTowerNum(int num) {
		tableTowerData.setRefreshTimes(0);
		tableTowerDataDAO.update(tableTowerData);
	}

	private void updatePlayerData() {
		if (tableTowerData == null) {
			return;
		}
		tableTowerData.setFighting(player.getMainRoleHero().getFighting());
		tableTowerData.setUserId(player.getUserId());
		tableTowerData.setLevel(player.getLevel());
	}

	public ArmyInfo getEnemyDataByTowerID(int TowerId) {
		if (tableTowerData == null) {
			return null;
		}
		ArmyInfo enemyInfo = null;
		if (tableTowerData.getEnemy(TowerId) == null) {// 无则添加
			enemyInfo = addEnemyDataByTowerId(TowerId);
		} else {
			// 老数据信息不改变，只有重置改变
			enemyInfo = tableTowerData.getEnemy(TowerId);
		}
		return enemyInfo;
	}

	private ArmyInfo addEnemyDataByTowerId(int TowerId) {// 添加塔层敌人数据
		ConcurrentHashMap<Integer, ArmyInfo> enemyList = tableTowerData.getEnemyList();// 层敌人数据
		ArmyInfo enemyInfo = null;
		if (!enemyList.containsKey(TowerId)) {// 无则添加
			for (int i = TowerId; i < TowerId + towerUpdateNum; i++) {// 解锁三层
				enemyInfo = getTowerEnemyByLevel(i);// 竞技常数据
				if(enemyInfo!=null){
					enemyList.put(i, enemyInfo);// 添加塔层数据到Base
				}
			}
			enemyInfo = enemyList.get(TowerId);
			if (enemyInfo != null) {
				save();
			}
		}
		if (enemyInfo == null) {
			GameLog.error("当前塔层" + TowerId + "都无地方玩家数据");
		}
		return enemyInfo;
	}

	// 添加对应塔层敌人数据先根据等级判断
	private ArmyInfo getTowerEnemyByLevel(int TowerId) {
		if (enemyListBlock == null || enemyListBlock.size() == 0) {
			updateDataBlock();
		}
		int blockId = 0;
		List<String> roleIdList = enemyListBlock.get(blockId);// 当前塔层总敌人数据集
		ArmyInfo towerEnemyInfo = null;
		if (roleIdList != null) {
			int length = roleIdList.size();
			int test =3;
			for (int i = 0; i < test; i++) {
				int rand = (int) (Math.random() * length);
				String enemyId = roleIdList.get(rand);
				if (getEnemyList.indexOf(enemyId) != -1) {// 已经获取过
					continue;
				}

				TableArenaData arenData = ArenaBM.getInstance().getArenaData(enemyId);// XXXXXXXXXXXXXXxxxx
				if (arenData == null) {// 无数据 错误的id或数据库清表原因
					getEnemyList.add(enemyId);
					continue;
				}
				if(arenData.getHeroIdList()==null){
					continue;
					
				}
					
				if (arenData.getHeroIdList().size() <= 0) {
					getEnemyList.add(enemyId);// 无佣兵
					continue;
				}
				if (getEnemyList.size() == length - 1) {
					GameLog.error("爬塔无法获得 人物=" + enemyId + "@@@@@@@@@@@@@@@");
					player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "竞技场 无法获得 人物数据");
					return null;
				}

				// if(arenData.getLevel()>playerLevel+levelLowRollNum&&arenData.getLevel()<playerLevel+levelHighRollNum){//@@@@等级限制
				towerEnemyInfo = getTowerEnemyByFight(arenData, TowerId);
				if (towerEnemyInfo != null) {
					getEnemyList.add(enemyId);// 获取过的敌人数据
					break;
				}
				// }
			}
		}
		return towerEnemyInfo;
	}

	// 根据玩家等级和战斗力获取敌人数据
	private ArmyInfo getTowerEnemyByFight(TableArenaData arenData, int TowerId) {
		List<String> heroIdList = getHeroList(arenData.getUserId(),arenData.getHeroIdList());
		ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(arenData.getUserId(), heroIdList);
		if(armyInfo==null){
			return null; 
		}
		
		//初始血量能量
		CurAttrData playerAttrData;
		playerAttrData = armyInfo.getPlayer().getCurAttrData();
		if(playerAttrData==null){
			playerAttrData = new CurAttrData();
			armyInfo.getPlayer().setCurAttrData(playerAttrData);
		}
		int currLife = armyInfo.getPlayer().getAttrData().getLife();
		playerAttrData.setCurLife(currLife);
		playerAttrData.setCurEnergy(0);
		playerAttrData.setId(armyInfo.getPlayer().getRoleBaseInfo().getId());
		for (ArmyHero armyHeroTmp : armyInfo.getHeroList()) {
			CurAttrData	heroCurrAttri = armyHeroTmp.getCurAttrData();
			if(heroCurrAttri==null){
				heroCurrAttri = new CurAttrData();
				armyHeroTmp.setCurAttrData(heroCurrAttri);
			}
			heroCurrAttri.setCurLife(armyHeroTmp.getAttrData().getLife());
			heroCurrAttri.setCurEnergy(0);
			heroCurrAttri.setId(armyHeroTmp.getRoleBaseInfo().getId());
		}

		return armyInfo;
	}
	private List<String> getHeroList(String userId,List<String> roleIdList){
		List<String> heroIdList =new ArrayList<String>();
		for(String id:roleIdList){
			if(!id.equals(userId)){
				heroIdList.add(id);
			}		
		}
		return heroIdList;
	}

	// 保存的玩家血量和能量变化
	public void updatePlayerChange(int towerId, List<TowerHeroChange> changeInfoList) {
		for (int i = 0; i < changeInfoList.size(); i++) {
			TowerHeroChange getChangeInfo = changeInfoList.get(i);
			updatePlayerChange(getChangeInfo);
			//System.out.print("updatePlayerChange  id="+getChangeInfo.getRoleId()+"life="+getChangeInfo.getReduceLife()+"enegy="+getChangeInfo.getReduceEnegy()+"\n");
		}
		save();
	}

	// 更新敌方血量和能量 此TowerHeroChange 传入的为剩余血量和能量
	public void updateEnemyChange(int towerId, List<TowerHeroChange> heroChangeList) {// heroChangeList
		if (heroChangeList == null || heroChangeList.size() <= 0) {
			return;
		}
		TowerHeroChange heroChange;
		ArmyInfo enemyinfo = getEnemyDataByTowerID(towerId);// 获取当前层敌人数据
		CurAttrData CurrAttrData;
		CurrAttrData = enemyinfo.getPlayer().getCurAttrData();
		heroChange = heroChangeList.get(0);
		if(CurrAttrData==null){
			CurrAttrData = new CurAttrData();
			enemyinfo.getPlayer().setCurAttrData(CurrAttrData);
		}
		CurrAttrData.setId(enemyinfo.getPlayer().getRoleBaseInfo().getId());
		CurrAttrData.setCurLife(heroChange.getReduceLife());
		CurrAttrData.setCurEnergy(heroChange.getReduceEnegy());
		heroChangeList.remove(0);

		CurAttrData heroAttrData;
		for (int i = 0; i < heroChangeList.size(); i++) {// 0 主角 1~n佣兵
			heroChange = heroChangeList.get(i);
			ArmyHero hero = getHeroTableById(enemyinfo, heroChange.getRoleId());
			if (hero == null) {
				GameLog.error("没有找到改变的敌人数据 id=" + heroChange.getRoleId());
				continue;
			}
			heroAttrData = hero.getCurAttrData();
			if(heroAttrData==null){
				heroAttrData = new CurAttrData();
				hero.setCurAttrData(heroAttrData);
			}
			heroAttrData.setId(hero.getRoleBaseInfo().getId());
			heroAttrData.setCurLife((int) heroChange.getReduceLife());
			heroAttrData.setCurEnergy((int) heroChange.getReduceEnegy());
			System.out.print("updateEnemyChange  id="+heroChange.getRoleId()+"life="+heroChange.getReduceLife()+"enegy="+heroChange.getReduceEnegy()+"\n");
		}
		save();
	}

	private ArmyHero getHeroTableById(ArmyInfo towerEnemyInfo, String moderId) {
		List<ArmyHero> heroList = towerEnemyInfo.getHeroList();
		for (ArmyHero data : heroList) {
			String heroModerId =String.valueOf(data.getRoleBaseInfo().getModeId());
			if (heroModerId.equals(moderId)) {
				return data;
			}
		}
		return null;
	}

	private void updatePlayerChange(TowerHeroChange heroChange) {
		TowerHeroChange playerChange = getPlayerChangeById(heroChange.getRoleId());
		List<TowerHeroChange> changeInfoList = tableTowerData.getHeroChageList();
		if (playerChange != null) {
			playerChange.setRoleId(heroChange.getRoleId());
			playerChange.setReduceLife(heroChange.getReduceLife());
			playerChange.setReduceEnegy(heroChange.getReduceEnegy());
			playerChange.setIsDead(heroChange.getIsDead());
		} else {

			TowerHeroChange addHeroChange = new TowerHeroChange();
			addHeroChange.setRoleId(heroChange.getRoleId());
			addHeroChange.setReduceLife(heroChange.getReduceLife());
			addHeroChange.setReduceEnegy(heroChange.getReduceEnegy());
			addHeroChange.setIsDead(heroChange.getIsDead());
			changeInfoList.add(addHeroChange);
		}
	}

	private TowerHeroChange getPlayerChangeById(String id) {// 获得主角或佣兵
		List<TowerHeroChange> changeInfoList = tableTowerData.getHeroChageList();
		for (int i = 0; i < changeInfoList.size(); i++) {
			TowerHeroChange changeInfo = changeInfoList.get(i);
			if (changeInfo.getRoleId().equals(id)) {
				return changeInfo;
			}
		}
		return null;
	}

	public void resetDataInNewDay()// 重制敌人数据集信息
	{
		if (tableTowerData == null) {
			return;
		}
		updateDataBlock();
		tableTowerData.setRefreshTimes(0);
	}

	public void updateDataBlock() {// 每天更新数据集
		ArrayList<ListRankingEntry<String, ArenaExtAttribute>> enemyList = new ArrayList<ListRankingEntry<String, ArenaExtAttribute>>();
		enemyList.addAll(ArenaBM.getInstance().getArenaInfoList(ListRankingType.WARRIOR_ARENA));
		enemyList.addAll(ArenaBM.getInstance().getArenaInfoList(ListRankingType.PRIEST_ARENA));
		enemyList.addAll(ArenaBM.getInstance().getArenaInfoList(ListRankingType.SWORDMAN_ARENA));
		enemyList.addAll(ArenaBM.getInstance().getArenaInfoList(ListRankingType.MAGICAN_ARENA));
		Collections.sort(enemyList, new ArenaInfoCompFight());
		getEnemyDataByFight(enemyList);
	}

	// 返回奖励字符串集
	public String getAwardByTowerId(int towerId) {
		List<Boolean> firstList = tableTowerData.getFirstTowerList();// 第一次领奖
		Boolean isFirst = firstList.get(towerId);
		String totalGoodsStr = "";
		String goodListStr = "";
		if (isFirst) {
			firstList.set(towerId, false);
			// 第一次奖励
			totalGoodsStr = TowerFirstAwardCfgDAO.getInstance().GetGooldListStr(String.valueOf(towerId + 1));
		}
		List<Boolean> awardList = tableTowerData.getAwardTowerList();// 第一次领奖
		Boolean isGetAward = awardList.get(towerId);
		TowerAwardCfg awardCfg = TowerAwardCfgDAO.getInstance().GetLevelTowerCfgByTowerID(player.getLevel(), towerId + 1);
		if (!isGetAward) {
			awardList.set(towerId, true);
			// 过关奖励
			if(awardCfg.gold>0){
				goodListStr += eSpecialItemId.Coin.getValue() + "_" + awardCfg.gold + ",";// 金币
			}
			if(awardCfg.towerCoin>0){
				goodListStr += eSpecialItemId.BraveCoin.getValue() + "_" + awardCfg.towerCoin + ",";// 塔币
			}
			if (awardCfg != null) {
				for (int i = 0; i < 1; i++) {
					List<TowerGoodsCfg> formatList = TowerGoodsCfgDAO.getInstance().GetCfgsByFormatId(awardCfg.formatId);
					TowerGoodsCfg goodCfg = getRandomAward(formatList);
					int leastNum = goodCfg.leastNum;// 最小数量
					int maxNum = goodCfg.leastNum;// 最大数量
					int num = leastNum + (int) Math.random() * (maxNum - leastNum + 1);
					goodListStr += "" + goodCfg.itemid + "_" + num + ",";
				}
				if (totalGoodsStr.length() > 0) {// 与一次奖品连接
					totalGoodsStr = goodListStr + totalGoodsStr;
				} else {
					totalGoodsStr = goodListStr.substring(0, goodListStr.length() - 1);// 去除最后字符“，”
				}
			}
		}
		if (!StringUtils.isBlank(totalGoodsStr)) {
			addGoods(totalGoodsStr.split(","));// 发送奖励
		}
		save();
		return totalGoodsStr;
	}

	/** 获取一个随机奖励配置 */
	private TowerGoodsCfg getRandomAward(List<TowerGoodsCfg> formatList) {
		int weightToatal = 0;
		for (TowerGoodsCfg cfg : formatList) {
			weightToatal += cfg.weight;
		}
		int random = new Random().nextInt(weightToatal);
		int temp = 0;
		for (TowerGoodsCfg awardCfg : formatList) {
			temp += awardCfg.weight;
			if (random < temp) {
				return awardCfg;
			}
		}
		return null;
	}

	private void addGoods(String[] goodsStr) {

		for (int i = 0; i < goodsStr.length; i++) {
			String[] goodList = goodsStr[i].split("_");
			int templateId = Integer.valueOf(goodList[0]);
			int num = Integer.valueOf(goodList[1]);
			EItemTypeDef eItemType = ItemCfgHelper.getItemType(templateId);
			if (eItemType == EItemTypeDef.HeroItem) {// 是添加英雄物品
				player.getHeroMgr().addHero(String.valueOf(templateId));
				player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "得到英雄id=" + templateId);
			} else {
				player.getItemBagMgr().addItem(templateId, num);
			}
		}
	}

	private void getEnemyDataByFight(List<? extends ListRankingEntry<String, ArenaExtAttribute>> arena) {
		// 从竞技场获得敌人的数据
		int size = arena.size();
		System.out.print("竞技场人数=" + size);
		// int level = enemyListBlock.size();//12
		int level = 12;// 12
		int num = size / level;// 分12组人物

		for (int i = 0; i < size; i++) {
			ListRankingEntry<String, ArenaExtAttribute> arenaInfo = arena.get(i);
			ArenaExtAttribute arenaExt = arenaInfo.getExtension();
			int arenaLevel = arenaExt.getLevel();
			// if(arenaLevel>30&&arenaLevel<=90){//数据不够
		//	int levelNum = arenaLevel % 5;// 所属组
			int levelNum = 0;// 所属组
			if (levelNum < 12) {
				if (!enemyListBlock.containsKey(levelNum)) {
					enemyListBlock.put(levelNum, new ArrayList<String>());
				}
				if (enemyListBlock.containsKey(levelNum)) {// 添加块敌人数据
					List<String> strList = enemyListBlock.get(levelNum);
					if (strList.size() >= 500) {
						continue;
					}
					strList.add(arenaInfo.getKey());
				}
			}
			// }
		}
	}

	public class ArenaInfoCompFight implements Comparator<ListRankingEntry<String, ArenaExtAttribute>> {
		public int compare(ListRankingEntry<String, ArenaExtAttribute> o1, ListRankingEntry<String, ArenaExtAttribute> o2) {
			int o1Fighting = o1.getExtension().getFighting();
			int o2Fighting = o2.getExtension().getFighting();
			if (o1Fighting < o2Fighting)
				return -1;
			if (o1Fighting > o1Fighting)
				return 1;
			return 0;
		}
	}

	/** 转为属性list给客户端 **/
	public List<TagAttriData> getTagAttr(AttrData totalAttrData) {// 转为角色对应proto属性
		List<TagAttriData> attaList = new ArrayList<TagAttriData>();
		for (eAttrIdDef eAttr : eAttrIdDef.values()) {// 把总属性转为proto属性集合
			if (eAttr.getOrder() > eAttrIdDef.BATTLE_BEGIN.getOrder() && eAttr.getOrder() < eAttrIdDef.BATTLE_END.getOrder()) {
				TagAttriData.Builder tagAttr = AttrToProbuff(eAttr, totalAttrData);
				attaList.add(tagAttr.build());
			}
		}
		return attaList;
	}

	// 属性集合 转化为总属性
	public AttrData getTotalAttr(TableAttr m_TableAttr) {
		AttrData m_TotalAttrData = m_TableAttr.getAttrData();
		return m_TotalAttrData;
	}

	private TagAttriData.Builder AttrToProbuff(eAttrIdDef eAttr, AttrData m_TotalAttrData) {
		TagAttriData.Builder attriData = TagAttriData.newBuilder();
		attriData.setAttrId(eAttr.getOrder());

		switch (eAttr) {
		case LIFE:
			attriData.setAttValue(m_TotalAttrData.getLife());
			break;
//		case CURRENTL_LIFE:
//			attriData.setAttValue(m_TotalAttrData.getCurLife());
//			break;
		case ENERGY:
			attriData.setAttValue(m_TotalAttrData.getEnergy());
			break;
//		case CURRENT_ENERGY:
//			attriData.setAttValue(m_TotalAttrData.getCurEnergy());
//			break;
		case ATTACK:
			attriData.setAttValue(m_TotalAttrData.getAttack());
			break;
		case PHYSIQUE_DEF:
			attriData.setAttValue(m_TotalAttrData.getPhysiqueDef());
			break;
		case SPIRIT_DEF:
			attriData.setAttValue(m_TotalAttrData.getSpiritDef());
			break;
		case ATTACK_VAMPIRE:
			attriData.setAttValue(m_TotalAttrData.getAttackVampire());
			break;
		case CRITICAL:
			attriData.setAttValue(m_TotalAttrData.getCritical());
			break;
		case CRITICAL_HURT:
			attriData.setAttValue(m_TotalAttrData.getCriticalHurt());
			break;
		case TOUGHNESS:
			attriData.setAttValue(m_TotalAttrData.getToughness());
			break;
		case LIFE_RECEIVE:
			attriData.setAttValue(m_TotalAttrData.getLifeReceive());
			break;
		// -----------------------------------------------------------------------------------------------------------
		case ENERGY_RECEIVE:
			attriData.setAttValue(m_TotalAttrData.getEnergyReceive());
			break;
		case ATTACK_ENERGY_RECEIVE:
			attriData.setAttValue(m_TotalAttrData.getAttackEnergy());
			break;
		case STRUCK_ENERGY_RECEIVE:
			attriData.setAttValue(m_TotalAttrData.getStruckEnergy());
			break;
		case ENERGY_TRANS:
			attriData.setAttValue(m_TotalAttrData.getEnergyTrans());
			break;
		case ATTACK_SPEED:
			attriData.setAttValue(m_TotalAttrData.getAttackSpeed());
			break;
		// @allen 没有这个属性
		// case ATTACK_FREQUENCE:
		// attriData.setAttValue(m_TotalAttrData.getAttackFrequence());
		// break;
		case MOVE_SPEED:
			attriData.setAttValue(m_TotalAttrData.getMoveSpeed());
			break;
		case ATTACK_HURT_ADD:
			attriData.setAttValue(m_TotalAttrData.getAttackHurt());
			break;
		case CUT_HURT:
			attriData.setAttValue(m_TotalAttrData.getCutHurt());
			break;
		// ---------------------------------------------------------------------------------------------------
		case CUT_CRIT_HURT:
			attriData.setAttValue(m_TotalAttrData.getCutCritHurt());
			break;
		case RESIST:
			attriData.setAttValue(m_TotalAttrData.getResist());
			break;
		case CUT_CURE:
			attriData.setAttValue(m_TotalAttrData.getCutCure());
			break;
		case ADD_CURE:
			attriData.setAttValue(m_TotalAttrData.getAddCure());
			break;
		case REACTION_TIME:
			attriData.setAttValue(m_TotalAttrData.getReactionTime());
			break;
		case HARD_STRAIGHT:
			attriData.setAttValue(m_TotalAttrData.getHardStraight());
			break;
		// ------------------------------------------------------------------------------------------------------
		case LIFE_GROWUP:
			attriData.setAttValue(m_TotalAttrData.getLifeGrowUp());
			break;
		case ATTACK_GROWUP:
			attriData.setAttValue(m_TotalAttrData.getAttackGrowUp());
			break;
		case PHYSICQUE_DEF_GROWUP:
			attriData.setAttValue(m_TotalAttrData.getPhysicqueDefGrowUp());
			break;
		case SPIRIT_DEF_GROWUP:
			attriData.setAttValue(m_TotalAttrData.getSpiritDefGrowUp());
			break;

		case ENCHANTEXP:
			attriData.setAttValue(m_TotalAttrData.getEnchantExp());
			break;
		case SKILL_LEVEL:
			attriData.setAttValue(m_TotalAttrData.getSkillLevel());
			break;
		case ATTACK_TYPE:
			attriData.setAttValue(m_TotalAttrData.getAttackType());
			break;
		default:
			break;
		}

		return attriData;
	}
}
