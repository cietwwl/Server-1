package com.playerdata.activity.rateType;

import com.playerdata.dataSyn.annotation.SynClass;
/**
 * 
 * @author 阳小飞  发给客户端的合货币相关的倍数信心，在战斗前发送
 *
 */
@SynClass
public class eSpecialItemIDUserInfo {
	private int coin; 
	private int playerExp;
	
	public int getCoin() {
		return coin;
	}
	public void setCoin(int coin) {
		this.coin = coin;
	}
	public int getPlayerExp() {
		return playerExp;
	}
	public void setPlayerExp(int playerExp) {
		this.playerExp = playerExp;
	}

	
	
}
