package com.rw.actionHelper;

import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ActionRateHelper{
	
	private ConcurrentLinkedQueue<ActionEnum> waitingQueue = new ConcurrentLinkedQueue<ActionEnum>();
	
	private ConcurrentHashMap<ActionEnum, Integer> map;
	{
		ActionEnum[] actions =  ActionEnum.values();
		for(ActionEnum act : actions){
			map.put(act, act.getInitRate());
		}
	}
	
	public void updateRate(ActionEnum act, int rate){
		map.put(act, rate);
	}
	
	public void addActionToQueue(ActionEnum act){
		waitingQueue.add(act);
	}
	
	public void resetActionQueue(ActionEnum act){
		waitingQueue.clear();
		waitingQueue.add(act);
	}
	
	public void resetActionQueue(){
		waitingQueue.clear();
	}
	
	public ActionEnum getRandomAction(){
		ActionEnum waitAct = waitingQueue.poll();
		if(null != waitAct){
			return waitAct;
		}
		
		int totalRate = 0;
		for(Entry<ActionEnum, Integer> entry : map.entrySet()){
			if(entry.getValue() >= 100){
				return entry.getKey();
			}
			totalRate += entry.getValue();
		}
		int rd = new Random().nextInt(totalRate);
		for(Entry<ActionEnum, Integer> entry : map.entrySet()){
			rd -= entry.getValue();
			if(rd <= 0){
				return entry.getKey();
			}
		}
		return null;
	}
}
