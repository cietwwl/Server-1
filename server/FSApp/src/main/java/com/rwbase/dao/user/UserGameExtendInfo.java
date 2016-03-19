package com.rwbase.dao.user;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


/***
 * 玩家扩展信息
 * @author Allen
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGameExtendInfo {
	
	private int sendGold;// 游戏内赠送金钱
	
	private int chargedGold;// 充值金钱

	public int getSendGold() {
		return sendGold;
	}

	public void setSendGold(int sendGold) {
		this.sendGold = sendGold;
	}

	public int getChargedGold() {
		return chargedGold;
	}

	public void setChargedGold(int chargedGoldP) {
		this.chargedGold = chargedGoldP;
	}
	
	public void addChargedGold(int chargedGoldP){
		this.chargedGold = this.chargedGold + chargedGoldP;
	}
	public void addSendGold(int sendGoldP){
		this.sendGold = this.sendGold + sendGoldP;
	}
	
}
