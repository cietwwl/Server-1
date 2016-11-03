package com.rw.trace.stat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import com.rw.netty.ServerHandler;

public class GetSessionInfo implements Callable<Void> {

	@Override
	public Void call() throws Exception {
		Field field = ServerHandler.class.getDeclaredField("channelMap");
		field.setAccessible(true);
		ConcurrentHashMap<Long, ChannelHandlerContext> map = (ConcurrentHashMap<Long, ChannelHandlerContext>) field.get(null);
		for (Map.Entry<Long, ChannelHandlerContext> entry : map.entrySet()) {
			ChannelHandlerContext ctx = entry.getValue();
			Channel channel = ctx.channel();
			System.out.println(entry.getKey()+",active="+channel.isActive()+",register="+channel.isRegistered()+",open="+channel.isOpen()+",removed="+ctx.isRemoved());
		}
		return null;
	}

}
