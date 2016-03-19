package com.rwbase.dao.secretArea.pojo;

public class SecretAreaCfg {
    private String typeListBg;//页签图标
    private int totalTime;//产出时间
    private String secretBg;//产出时间背景图(无作用需到资源上修改引用)
    private String giftId;//产出类型
    private float typeRate;//每分钟产出权重
    private int robPercent;//掠夺比率
    private float guildRate;//每分钟帮派建筑点产出
    private int robCount;//可掠夺次数
    private int robGold;//掠夺钻石
    private int protectTime;//保护时间
    private String name;//名称
    private String tips;//搜索提示
	public String getTypeListBg() {
		return typeListBg;
	}
	public void setTypeListBg(String typeListBg) {
		this.typeListBg = typeListBg;
	}
	public int getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	public String getSecretBg() {
		return secretBg;
	}
	public void setSecretBg(String secretBg) {
		this.secretBg = secretBg;
	}
	public String getGiftId() {
		return giftId;
	}
	public void setGiftId(String giftId) {
		this.giftId = giftId;
	}
	public float getTypeRate() {
		return typeRate;
	}
	public void setTypeRate(float typeRate) {
		this.typeRate = typeRate;
	}
	public int getRobPercent() {
		return robPercent;
	}
	public void setRobPercent(int robPercent) {
		this.robPercent = robPercent;
	}
	public float getGuildRate() {
		return guildRate;
	}
	public void setGuildRate(float guildRate) {
		this.guildRate = guildRate;
	}
	public int getRobCount() {
		return robCount;
	}
	public void setRobCount(int robCount) {
		this.robCount = robCount;
	}
	public int getRobGold() {
		return robGold;
	}
	public void setRobGold(int robGold) {
		this.robGold = robGold;
	}
	public int getProtectTime() {
		return protectTime;
	}
	public void setProtectTime(int protectTime) {
		this.protectTime = protectTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTips() {
		return tips;
	}
	public void setTips(String tips) {
		this.tips = tips;
	}
}
