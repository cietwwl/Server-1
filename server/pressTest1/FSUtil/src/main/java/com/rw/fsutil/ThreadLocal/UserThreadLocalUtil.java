package com.rw.fsutil.ThreadLocal;


//用户数据
public class UserThreadLocalUtil {

	private static final ThreadLocal<String> USERID_THREADLOCAL = new ThreadLocal<String>();
	
	public static void setUserId(String userId){
		USERID_THREADLOCAL.set(userId);
	}
	public static String getUserId(){
		return USERID_THREADLOCAL.get();
	}
}
