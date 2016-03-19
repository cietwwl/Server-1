package com.rwbase.dao.gamble.pojo.cfg;

public class GambleCfg 
{
	private int id;				//祭坛ID
	private int moneyType; 		//消耗货币类型
	private int moneyNum; 		//消耗货币
	private String probability; //概率
	private int heroProb; 		//整卡概率
	private int refreshTime;	//免费次数的刷新时间间隔
	private int dayFreeCount;	//日免费次数
	private String appearFactor;//祭坛出现条件，"1，10"表示"主角等级，VIP等级"的条件	
	
	private int freeFirst;//免费首抽单次必掉
	private int firstCount;//非免费首抽必掉次数
	private int firstTenCount;//首次十连保底数量
	private int tenCount;//非首次十连保底数量
	private int rewardItem;//必送
	
	public String[] getProbabilityList(){
		return this.probability.split(",");
	}
	
	/**这个功能是否有开放*/
	public boolean isOpen(int level, int vipLevel){
		String[] temp = appearFactor.split(",");
		if(level >= Integer.parseInt(temp[0]) && vipLevel >= Integer.parseInt(temp[1])){
			return true;
		}
		return false;
	}
	
	/**
	 * 根据首次抽奖次数判断是否有保底奖励
	 * @param count 当次抽奖次数
	 * @return
	 */
	public boolean hasOrder(int count){
		return count <= tenCount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(int moneyType) {
		this.moneyType = moneyType;
	}

	public int getMoneyNum() {
		return moneyNum;
	}

	public void setMoneyNum(int moneyNum) {
		this.moneyNum = moneyNum;
	}

	public String getProbability() {
		return probability;
	}

	public void setProbability(String probability) {
		this.probability = probability;
	}

	public int getHeroProb() {
		return heroProb;
	}

	public void setHeroProb(int heroProb) {
		this.heroProb = heroProb;
	}

	public int getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(int refreshTime) {
		this.refreshTime = refreshTime;
	}

	public int getDayFreeCount() {
		return dayFreeCount;
	}

	public void setDayFreeCount(int freeCount) {
		this.dayFreeCount = freeCount;
	}

	public int getTenCount() {
		return tenCount;
	}

	public void setTenCount(int tenCount) {
		this.tenCount = tenCount;
	}

	public String getAppearFactor() {
		return appearFactor;
	}

	public void setAppearFactor(String appearFactor) {
		this.appearFactor = appearFactor;
	}

	public int getFreeFirst() {
		return freeFirst;
	}

	public void setFreeFirst(int freeFirst) {
		this.freeFirst = freeFirst;
	}

	public int getFirstCount() {
		return firstCount;
	}

	public void setFirstCount(int firstCount) {
		this.firstCount = firstCount;
	}

	public int getFirstTenCount() {
		return firstTenCount;
	}

	public void setFirstTenCount(int firstTenCount) {
		this.firstTenCount = firstTenCount;
	}

	public int getRewardItem() {
		return rewardItem;
	}

	public void setRewardItem(int rewardItem) {
		this.rewardItem = rewardItem;
	}
}
