package com.rwbase.dao.copy.cfg;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.readonly.CopyCfgIF;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CopyCfg implements CopyCfgIF {
	private int mapID;
	private int previousLevelID;
	private int levelID;
	private String battleSceneID;
	private String name;
	private int posX;
	private int posY;
	private String storyLevel;
	private int levelType;
	private int subtype;
	private String eliteLevelIcon;
	private String intro;
	private String items;
	private String firstDropItems;
	private int enemys;
	private int coin;
	private int playerExp;
	private int heroExp;
	private int succSubPower;
	private int failSubPower;
	private String extraRewards;
	private String extraRewardsNum;
	private int resetNum;
	private String rewardInfo;// 关卡宝箱id，不一定有值
	private String dropGuarantee;

	public int getMapID() {
		return mapID;
	}

	public void setMapID(int mapID) {
		this.mapID = mapID;
	}

	public int getPlayerExp() {
		return playerExp;
	}

	public void setPlayerExp(int playerExp) {
		this.playerExp = playerExp;
	}

	public int getHeroExp() {
		return heroExp;
	}

	public void setHeroExp(int heroExp) {
		this.heroExp = heroExp;
	}

	public int getSubtype() {
		return subtype;
	}

	public void setSubtype(int subtype) {
		this.subtype = subtype;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public String getStoryLevel() {
		return storyLevel;
	}

	public void setStoryLevel(String storyLevel) {
		this.storyLevel = storyLevel;
	}

	public int getPreviousLevelID() {
		return previousLevelID;
	}

	public void setPreviousLevelID(int previousLevelID) {
		this.previousLevelID = previousLevelID;
	}

	public int getLevelID() {
		return levelID;
	}

	public void setLevelID(int levelID) {
		this.levelID = levelID;
	}

	public int getLevelType() {
		return levelType;
	}

	public void setLevelType(int levelType) {
		this.levelType = levelType;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public int getEnemys() {
		return enemys;
	}

	public void setEnemys(int enemys) {
		this.enemys = enemys;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public int getSuccSubPower() {
		return succSubPower;
	}

	public void setSuccSubPower(int succSubPower) {
		this.succSubPower = succSubPower;
	}

	public int getFailSubPower() {
		return failSubPower;
	}

	public void setFailSubPower(int failSubPower) {
		this.failSubPower = failSubPower;
	}

	public String getBattleSceneID() {
		return battleSceneID;
	}

	public void setBattleSceneID(String battleSceneID) {
		this.battleSceneID = battleSceneID;
	}

	public String getExtraRewards() {
		return extraRewards;
	}

	public void setExtraRewards(String extraRewards) {
		this.extraRewards = extraRewards;
	}

	public String getExtraRewardsNum() {
		return extraRewardsNum;
	}

	public void setExtraRewardsNum(String extraRewardsNum) {
		this.extraRewardsNum = extraRewardsNum;
	}

	public int getResetNum() {
		return resetNum;
	}

	public void setResetNum(int resetNum) {
		this.resetNum = resetNum;
	}

	public String getEliteLevelIcon() {
		return eliteLevelIcon;
	}

	public void setEliteLevelIcon(String eliteLevelIcon) {
		this.eliteLevelIcon = eliteLevelIcon;
	}

	public String getFirstDropItems() {
		return firstDropItems;
	}

	public void setFirstDropItems(String firstDropItems) {
		this.firstDropItems = firstDropItems;
	}

	public String getRewardInfo() {
		return rewardInfo;
	}

	public void setRewardInfo(String rewardInfo) {
		this.rewardInfo = rewardInfo;
	}

	public String getDropGuarantee() {
		return dropGuarantee;
	}

	public void setDropGuarantee(String dropGuarantee) {
		this.dropGuarantee = dropGuarantee;
	}

}
