package com.log.logToDataCenter.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.log.logToDataCenter.config.LogConst;
import com.log.logToDataCenter.exception.NoCanUseNodeException;

public enum ChannelNodeManager {
	INSTANCE;
	
	private EventLoopGroup senderGroup = new NioEventLoopGroup(16);
	ArrayList<ChannelNode> nodeQueue = new ArrayList<ChannelNode>();
	ConcurrentHashMap<Integer, AtomicInteger> nodeBusyTimes = new ConcurrentHashMap<Integer, AtomicInteger>();
	
	private volatile int disturbFactor = 0;
	
	{
		try {
			for(int i = 0; i < LogConst.MAX_CHANNEL_COUNT; i++){
				nodeQueue.add(new ChannelNode(senderGroup));
				nodeBusyTimes.put(i, new AtomicInteger(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ChannelNode getProperChannelNode() throws NoCanUseNodeException, UnsupportedEncodingException, URISyntaxException, InterruptedException {
		int index = (int) (Math.random() * LogConst.MAX_CHANNEL_COUNT);
		if(index == disturbFactor)
			index = (index+1)%LogConst.MAX_CHANNEL_COUNT;
		disturbFactor = index;
		if(nodeQueue.get(index).isChannelActive()) {
			nodeBusyTimes.get(index).set(0);
			return nodeQueue.get(index);
		}
		
		int busyTimes = nodeBusyTimes.get(index).incrementAndGet();
		if(busyTimes >= LogConst.NODE_MAX_BUSY_TIMES) {
			//nodeQueue.get(index).startCheckResponseTime("NODE_MAX_BUSY_TIMES");
			nodeBusyTimes.get(index).set(0);
		}
		
		int i = (index + 1)%LogConst.MAX_CHANNEL_COUNT;
		while(i != index){
			if(nodeQueue.get(i).isChannelActive()) 
				return nodeQueue.get(i);
			
			busyTimes = nodeBusyTimes.get(i).incrementAndGet();
			if(busyTimes >= LogConst.NODE_MAX_BUSY_TIMES) {
				nodeQueue.get(i).startCheckResponseTime("NODE_MAX_BUSY_TIMES");
				nodeBusyTimes.get(i).set(0);
			}
			
			i = (i+1)%LogConst.MAX_CHANNEL_COUNT;
		}
		throw new NoCanUseNodeException("找不到可以用的ChannelNode");
	}
}
