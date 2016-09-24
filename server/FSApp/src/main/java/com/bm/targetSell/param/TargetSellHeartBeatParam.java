package com.bm.targetSell.param;

import com.bm.targetSell.net.ITargetSellMsgExcutor;

/**
 * 5001心跳参数
 * @author Alex
 * 2016年9月17日 下午4:44:02
 */
public class TargetSellHeartBeatParam implements ITargetSellMsgExcutor {

	private String appId;
	
	private int time;
	
	private long linkId;
	
	private int isTest;
	


	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}


	@Override
	public void excuteMsg(int msgType) {
		
	}

	public long getLinkId() {
		return linkId;
	}

	public void setLinkId(long linkId) {
		this.linkId = linkId;
	}

	public int getIsTest() {
		return isTest;
	}

	public void setIsTest(int isTest) {
		this.isTest = isTest;
	}


	
	
}
