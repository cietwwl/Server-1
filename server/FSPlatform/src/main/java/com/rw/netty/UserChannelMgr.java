package com.rw.netty;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

public class UserChannelMgr {

	private static ConcurrentHashMap<ChannelHandlerContext, String> userChannelMap;
	private static ConcurrentHashMap<String, ChannelHandlerContext> userChannelMapCpy;

	static{
		userChannelMap = new ConcurrentHashMap<ChannelHandlerContext, String>();
		userChannelMapCpy = new ConcurrentHashMap<String, ChannelHandlerContext>();
	}
	
	private final static ThreadLocal<ChannelHandlerContext> CTX_THREADLOCAL = new ThreadLocal<ChannelHandlerContext>();

	public static void setThreadLocalCTX(ChannelHandlerContext ctx) {
		CTX_THREADLOCAL.set(ctx);
	}

	public static void removeThreadLocalCTX() {
		CTX_THREADLOCAL.remove();
	}

	public static ChannelHandlerContext getThreadLocalCTX() {
		return CTX_THREADLOCAL.get();
	}

	public static void bindUserID(String userId) {
		ChannelHandlerContext ctx = CTX_THREADLOCAL.get();
		if (ctx != null && StringUtils.isNotBlank(userId)) {
			userChannelMap.put(ctx, userId);
			userChannelMapCpy.put(userId, ctx);
		}
	}

	public static String getUserId() {
		ChannelHandlerContext ctx = CTX_THREADLOCAL.get();
		if (ctx != null) {
			return userChannelMap.get(ctx);
		}
		return null;
	}
	
	public static String getUserId(ChannelHandlerContext ctx){
		String userId = userChannelMap.get(ctx);
		return userId;
	}

	public static void remove(ChannelHandlerContext ctx) {
		String userId = userChannelMap.get(ctx);
		if (null != userId) {
			userChannelMap.remove(ctx);
			userChannelMapCpy.remove(userId);
		}
	}
	
	public static void exit(ChannelHandlerContext ctx) {
		String userId = userChannelMap.get(ctx);
		if (null != userId) {
			
			   userChannelMap.remove(ctx);
			   userChannelMapCpy.remove(userId);
			
			
		}
	}
	
	public static void onRepeat(String userId) {
		if (null != userId) {
		}
	}
	


	public static ChannelHandlerContext get(String userId) {
		if (StringUtils.isNotBlank(userId)
				&& userChannelMap.containsValue(userId)) {
			return userChannelMapCpy.get(userId);
		}
		return null;
	}
}
