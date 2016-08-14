package com.fy.lua;

import com.fy.SpringContextUtil;

public class LuaMgr {
	
	public static LuaMgr getInstance(){
		return SpringContextUtil.getBean("luaMgr");
	}
	
	public void init(){
		
	}
	
	public LuaInfo getChannelLuaInfo(String channel){
		LuaInfo luaInfo = LuaDao.getInstance().getLuaInfo(channel);
		return luaInfo;
	}
}
