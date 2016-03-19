package com.dx.gods.common.gameserver;

public class GameServerRequestType {
	
	public final static int TYPE_SERVER_LIST = 1;   	//获取服务器列表
	public final static int TYPE_UPDATE_RES = 2;    	//通知更新资源
	public final static int TYPE_ROLLBACK_RES = 3;  	//回滚服务器资源
	public final static int TYPE_SHUTDOWN = 4;      	//停服请求
	public final static int TYPE_SERVER_REGISTERED = 5; //请求服务器注册信息
}
