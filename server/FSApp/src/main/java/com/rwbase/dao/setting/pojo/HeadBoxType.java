package com.rwbase.dao.setting.pojo;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class HeadBoxType //保持客户端与服务器同步！
{
	public static int HEADBOX_DEFAULT = 0;
	public static int HEADBOX_BASE = 1;
	public static int HEADBOX_MISSION = 2;
	public static int HEADBOX_FASHION = 3;//时装解锁头像框专用
	public static int NULL = 99;
	
	public static int getMin(){
		return HEADBOX_DEFAULT;
	}
	
	public static int getMax(){
		return HEADBOX_FASHION;
	}
	
	public static int getValidCount(){
		return 4;
	}
}
