package com.rwbase.dao.tower.pojo;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.army.ArmyInfo;


@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "tower_data")
public class TableTowerData implements TableTowerDataIF{
	
	@Id
	private String userId; // 用户ID
	private int currTowerID;//当前塔层
	private int refreshTimes;//刷新次数
	private int fighting;// 玩家每天刷新对手时的战斗力
	private int level;// 玩家每天刷新对手时的等级
	
	private ConcurrentHashMap<Integer, ArmyInfo> enemyList=new ConcurrentHashMap<Integer, ArmyInfo>();//塔层敌人数据
	private List<TowerHeroChange> heroChageList;//玩家血量变化记录
	private List<Boolean> openTowerList;//开放塔层数据
	private List<Boolean> firstTowerList;//第一次领取奖励数据
	private List<Boolean> AwardTowerList;//领取奖品层数据
	private List<Boolean> BeatTowerList;//打败列表
	
//	private HashMap<String,TowerHeroChange> heroChageMap;//玩家血量变化记录

	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getFighting() {
		return fighting;
	}


	public void setFighting(int fighting) {
		this.fighting = fighting;
	}
	public int getCurrTowerID() {
		return currTowerID;
	}
	public void setCurrTowerID(int currTowerID) {
		this.currTowerID = currTowerID;
	}


	public int getRefreshTimes() {
		return refreshTimes;
	}


	public void setRefreshTimes(int refreshTimes) {
		this.refreshTimes = refreshTimes;
	}
	public Enumeration<ArmyInfo> getEnemyEnumeration(){
		return getEnemyList().elements();
	}
	public ArmyInfo getEnemy(int key){
		return getEnemyList().get(key);
	}
	public void addEnemy(int key,ArmyInfo enemyInfo){
		getEnemyList().put(key, enemyInfo);
	}


	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}


	public List<TowerHeroChange> getHeroChageList() {
		return heroChageList;
	}


	public void setHeroChageList(List<TowerHeroChange> heroChageList) {
		this.heroChageList = heroChageList;
	}


	public List<Boolean> getOpenTowerList() {
		return openTowerList;
	}


	public void setOpenTowerList(List<Boolean> openTowerList) {
		this.openTowerList = openTowerList;
	}


	public List<Boolean> getFirstTowerList() {
		return firstTowerList;
	}


	public void setFirstTowerList(List<Boolean> firstTowerList) {
		this.firstTowerList = firstTowerList;
	}

	public List<Boolean> getAwardTowerList() {
		return AwardTowerList;
	}


	public void setAwardTowerList(List<Boolean> arardTowerList) {
		this.AwardTowerList = arardTowerList;
	}


	public List<Boolean> getBeatTowerList() {
		return BeatTowerList;
	}


	public void setBeatTowerList(List<Boolean> beatTowerList) {
		BeatTowerList = beatTowerList;
	}


	public ConcurrentHashMap<Integer, ArmyInfo> getEnemyList() {
		return enemyList;
	}


	public void setEnemyList(ConcurrentHashMap<Integer, ArmyInfo> enemyList) {
		this.enemyList = enemyList;
	}
	
}
