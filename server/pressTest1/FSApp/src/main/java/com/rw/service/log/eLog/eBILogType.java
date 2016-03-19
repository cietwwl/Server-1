package com.rw.service.log.eLog;

public enum eBILogType {
	RegLog(1,"机型注册"),
	ZoneReg(2,"区注册"),
	ZoneLogin(3,"区登入"),
	ZoneLogout(4,"区登出"),
	RoleCreated(5,"角色创建"),
	OnlineCount(6,"在线人数"),
	CopyBegin(7,"副本关卡开始"),
	CopyEnd(8,"副本关卡结束"),
	RoleLogin(9,"角色登入"),
	RoleLogout(10,"角色登出"),
	AccountLogout(11,"平台帐号登出"),
	ItemChanged(12,"物品变动"),
	CoinChanged(13,"游戏币变动"),
	ZoneCountCoin(14,"区游戏币余额"),
	TaskBegin(15,"任务开始"),
	TaskEnd(16,"任务结束"),
	TotalAccount(17,"总账号数"),
	LevelSpread(18,"等级分布"),
	VipSpread(19,"vip等级分布"),
	ActivityBegin(20,"任务开始"),
	ActivityEnd(21,"任务结束"), 
	RoleUpgrade(22,"角色升级");
	
	
	private int logId;
	private String logDesc;
	
	private eBILogType(int _logId, String _logDesc){
		this.logId = _logId;
		this.logDesc = _logDesc;
	}

	public int getLogId() {
		return logId;
	}
	public String getLogDesc() {
		return logDesc;
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
}
