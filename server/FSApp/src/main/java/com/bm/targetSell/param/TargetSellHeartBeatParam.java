package com.bm.targetSell.param;

import com.bm.targetSell.TargetSellManager;
import com.rw.fsutil.util.MD5;

/**
 * 5001心跳参数
 * @author Alex
 * 2016年9月17日 下午4:44:02
 */
public class TargetSellHeartBeatParam implements ITargetSellData {

	private String appId;
	
	private int time;

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
	public String initMD5Str() {
		return MD5.getMD5String("appId=" +appId+"||"+ TargetSellManager.appKey);
	}

	@Override
	public void handlerMsg() {
	}
	
	
}
