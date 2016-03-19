package com.rwbase.common.threadLocal;

import com.playerdata.Player;




public class UserThreadLocal {
	private final static ThreadLocal<Player> USER_THREADLOCAL = new ThreadLocal<Player>();
	
	public static void set(Player user){
		USER_THREADLOCAL.set(user);
	}
	
	public static Player get(){
		return USER_THREADLOCAL.get();
	}
	
	public static void remove(){
		USER_THREADLOCAL.remove();
	}
	

}
