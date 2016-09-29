package com.rwbase.dao.groupcompetition.pojo;
import com.common.BaseConfig;

public class GCompGeneralCfg extends BaseConfig {
	private String id; //id
	private String betAmount; //下注金额
	private int betOriginal; //押注初始金币
	private int maxReconnectTime; //最大重连时间
	private int maxMatchTime; //最大匹配时间
	private float initBetRate; //初始赔率
	private float minBetRate; //最低赔率
	private int playbackNum; //战报保存数量

	public String getId() {
		return id;
	}
	
	public String getBetAmount() {
		return betAmount;
	}
	
	public int getBetOriginal() {
		return betOriginal;
	}
	
	public int getMaxReconnectTime() {
		return maxReconnectTime;
	}
	
	public int getMaxMatchTime() {
		return maxMatchTime;
	}
	
	public float getInitBetRate() {
		return initBetRate;
	}
	
	public float getMinBetRate() {
		return minBetRate;
	}
	
	public int getPlaybackNum() {
		return playbackNum;
	}
}
