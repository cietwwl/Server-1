package com.rw.service.log.eLog;

public enum eBILogType {
	RegLog(1,"机型注册", "RegLog"),
	ZoneReg(2,"区注册", "ZoneReg"),
	ZoneLogin(3,"区登入", "ZoneLogin"),
	ZoneLogout(4,"区登出", "ZoneLogout"),
	RoleCreated(5,"角色创建", "RoleCreated"),
	OnlineCount(6,"在线人数", "OnlineCount"),
	CopyBegin(7,"副本关卡开始", "CopyBegin"),
	CopyEnd(8,"副本关卡结束", "CopyEnd"),
	RoleLogin(9,"角色登入", "RoleLogin"),
	RoleLogout(10,"角色登出", "RoleLogout"),
	AccountLogout(11,"平台帐号登出", "AccountLogout"),
	ItemChanged(12,"物品变动", "ItemChanged"),
	CoinChanged(13,"游戏币变动", "CoinChanged"),
	ZoneCountCoin(14,"区游戏币余额", "ZoneCountCoin"),
	TaskBegin(15,"任务开始", "TaskBegin"),
	TaskEnd(16,"任务结束", "TaskEnd"),
	TotalAccount(17,"总账号数", "TotalAccount"),
	LevelSpread(18,"等级分布", "LevelSpread"),
	VipSpread(19,"vip等级分布", "VipSpread"),
	ActivityBegin(20,"任务开始", "ActivityBegin"),
	ActivityEnd(21,"任务结束", "ActivityEnd"), 
	RoleUpgrade(22,"角色升级", "RoleUpgrade"),
	GiftGoldChanged(23,"赠送充值币变动", "GiftGoldChanged"),
	ZoneCountGiftGold(24,"区充值币余额", "ZoneCountGiftGold"),
	ZoneCountChargeGold(25,"区付费充值币余额", "ZoneCountChargeGold"),
	Chat(26,"聊天日志","Chat"),
	GoldChange(27, "充值币变动", "GoldChanged"),
	Email(28, "邮件监控", "Email"),
	Pay(29, "付费", "Pay"),
	FinanceMainCoinAdd(30, "财务充值币新增", "FinanceMainCoinAdd"),
	FinanceMainCoinConsume(31, "财务充值币消耗", "FinanceMainCoinConsume"),
	FinanceMainCoinHold(32, "财务充值币持有", "FinanceMainCoinHold"),
	GiftPackage(33, "市场推广_礼包", "GiftPackage"),
	Guide(34, "新手引导", "Guide"),
	;
	
	
	private int logId;
	private String logDesc;
	private String logName;
	
	private eBILogType(int _logId, String _logDesc, String _logName){
		this.logId = _logId;
		this.logDesc = _logDesc;
		this.logName = _logName;
	}

	public int getLogId() {
		return logId;
	}
	public String getLogDesc() {
		return logDesc;
	}
	
	public String getLogName() {
		return logName;
	}

	private static eBILogType[] allValue;
	
	public static eBILogType getLogType(int type){
		if(allValue == null){
			allValue = eBILogType.values();
		}
		
		for (eBILogType logType : allValue) {
			if(logType.getLogId() == type){
				return logType;
			}
		}
		return RegLog;
	}
	
	public static eBILogType[] getAllValue(){
		if(allValue == null){
			allValue = eBILogType.values();
		}
		return allValue;
	}
}
