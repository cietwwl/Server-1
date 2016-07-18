package com.groupCopy.rwbase.dao.groupCopy.cfg;

/**
 * 帮派奖励邮件配置
 * @author Alex
 * 2016年7月5日 上午11:42:34
 */
public class GroupCopyMailCfg {

	//个人奖励邮件id
	private String personMailID;
	//重置邮件id
	private String resetMailID;
	//通关邮件id
	private String passMailID;
	//普通通关邮件内容
	private String comMailContent;
	//限时通关邮件内容
	private String LTMailContent;
	//单次伤害进入排名邮件内容
	private String HDMailContent;
	
	public String getPersonMailID() {
		return personMailID;
	}
	public String getResetMailID() {
		return resetMailID;
	}
	public String getComMailContent() {
		return comMailContent;
	}
	public String getLTMailContent() {
		return LTMailContent;
	}
	public String getHDMailContent() {
		return HDMailContent;
	}
	public String getPassMailID() {
		return passMailID;
	}
	
	
	
}
