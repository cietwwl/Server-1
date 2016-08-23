package com.playerdata.dataSyn.sameSceneSyn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

class WaitingQueue<T extends Comparable<T>> {
	
	private ConcurrentSkipListSet<T> cSet;
	
	public WaitingQueue(){
		cSet = new ConcurrentSkipListSet<T>();
	}
	
	public boolean addElement(T element){
		return cSet.add(element);
	}
	
	public boolean addElement(Collection<? extends T> c){
		return cSet.addAll(c);
	}
	
	public T pollElement(){
		return cSet.pollFirst();
	}
	
	public List<T> pollElement(int size){
		ArrayList<T> result = new ArrayList<T>();
		for(int i = 0; i < size; i++){
			T element = pollElement();
			if(null == element) return result;
			result.add(element);
		}
		return result;
	}
	
	public void clear(){
		cSet.clear();
	}
}
