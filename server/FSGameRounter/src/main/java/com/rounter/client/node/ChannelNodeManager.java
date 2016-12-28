package com.rounter.client.node;

import io.netty.channel.EventLoopGroup;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.rounter.client.config.RouterConst;
import com.rounter.client.exception.NoCanUseNodeException;
import com.rounter.client.exception.ParamInvalidException;
import com.rounter.innerParam.RouterReqestObject;
import com.rounter.param.IResponseData;
import com.rounter.service.IResponseHandler;


public class ChannelNodeManager {
	
	private ArrayList<ChannelNode> nodeQueue = new ArrayList<ChannelNode>();
	private ConcurrentHashMap<Integer, AtomicInteger> nodeBusyTimes = new ConcurrentHashMap<Integer, AtomicInteger>();
	
	private volatile int disturbFactor = 0;
	
	public ChannelNodeManager(EventLoopGroup senderGroup, String addr, int port){
		for(int i = 0; i < RouterConst.MAX_CHANNEL_COUNT; i++){
			ChannelNode cn = new ChannelNode(senderGroup, addr, port);
			nodeQueue.add(cn);
			nodeBusyTimes.put(i, new AtomicInteger(0));
		}
	}
	
	public boolean init(){
		boolean result = false;
		for(ChannelNode node : nodeQueue){
			try {
				if(node.connectOrReconnectChannel()){
					result = true;
				}else{
					node.setActiveState(false);
				}
			} catch (Exception ex) {
				System.out.println("init-Node建立连接失败..." + ex.toString());
				node.setActiveState(false);
			}
		}
		return result;
	}
	
	public void sendMessage(RouterReqestObject reqData, IResponseHandler resHandler, IResponseData resData) throws UnsupportedEncodingException, InterruptedException, ParamInvalidException, NoCanUseNodeException, URISyntaxException{
		try {
			getProperChannelNode().sendMessage(reqData, resHandler, resData);
		} catch (Exception e) {
			resHandler.handleSendFailResponse(resData);
			synchronized (resData) {
				resData.notify();
			}
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置目标服务器的活跃状态
	 * @param isActive
	 */
	public void setActiveState(boolean isActive){
		for(ChannelNode node : nodeQueue){
			node.setActiveState(isActive);
		}
	}
	
	public boolean isActive(){
		for(ChannelNode node : nodeQueue){
			if(node.isChannelActive()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 设置目标服务器的活跃状态
	 * @param isActive
	 */
	public void close(){
		for(ChannelNode node : nodeQueue){
			node.closeNode();
		}
	}
	
	/**
	 * 当有多个连接的时候，随机查找一个合适的
	 * @return
	 * @throws NoCanUseNodeException
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 * @throws InterruptedException
	 */
	private ChannelNode getProperChannelNode() throws NoCanUseNodeException, UnsupportedEncodingException, URISyntaxException, InterruptedException {
		int index = (int) (Math.random() * RouterConst.MAX_CHANNEL_COUNT);
		if(index == disturbFactor){
			index = (index+1)%RouterConst.MAX_CHANNEL_COUNT;
		}
		disturbFactor = index;
		if(nodeQueue.get(index).isChannelActive()) {
			nodeBusyTimes.get(index).set(0);
			return nodeQueue.get(index);
		}
		
		int busyTimes = nodeBusyTimes.get(index).incrementAndGet();
		if(busyTimes >= RouterConst.NODE_MAX_BUSY_TIMES) {
			nodeQueue.get(index).startCheckResponseTime();
			nodeBusyTimes.get(index).set(0);
		}
		
		int i = (index + 1)%RouterConst.MAX_CHANNEL_COUNT;
		while(i != index){
			if(nodeQueue.get(i).isChannelActive()) 
				return nodeQueue.get(i);
			
			busyTimes = nodeBusyTimes.get(i).incrementAndGet();
			if(busyTimes >= RouterConst.NODE_MAX_BUSY_TIMES) {
				nodeQueue.get(i).startCheckResponseTime();
				nodeBusyTimes.get(i).set(0);
			}
			
			i = (i+1)%RouterConst.MAX_CHANNEL_COUNT;
		}
		throw new NoCanUseNodeException("找不到可以用的ChannelNode");
	}
}
