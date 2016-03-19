package com.rw.netty;

import io.netty.channel.Channel;

public class ChannelWriteMgr {

	
	
	public static void write(Channel channel, Object msg){
		
		channel.writeAndFlush(msg);
		
//		try {
//			
//			
//			while(!channel.isActive() ||!channel.isWritable()){
//				if(!channel.isActive()){
//					//如果关闭则直接退出
//					break;
//				}
//				Thread.sleep(100);
//				if(!channel.isWritable()){
//					GameLog.error("ChannelWriteMgr", "channel:"+channel.toString(), "ChannelWriteMgr[write] channel is not writable now, wait 100ms");
//				}
//			}
//			
//			if(channel.isActive() && channel.isWritable()){
//				channel.writeAndFlush(msg);
//			}else{
//				GameLog.error("ChannelWriteMgr", "channel:"+channel.toString(), "ChannelWriteMgr[write] miss write,isActive:"+channel.isActive()+" isWritable:"+channel.isWritable());
//			}
//		} catch (Exception e) {
//			GameLog.error("ChannelWriteMgr", "channel:"+channel.toString(), "ChannelWriteMgr[write] Exception", e);
//		}
		
	}
	
}
