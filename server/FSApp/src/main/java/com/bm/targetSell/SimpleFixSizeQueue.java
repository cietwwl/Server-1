package com.bm.targetSell;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 一个检查大小的queue,如果达到上限，会将最旧的元素踢掉,但不保证不会超过上限
 * @author Alex
 * 2016年9月24日 下午4:04:10
 */
public final class SimpleFixSizeQueue<T> {

	private final int capacity;
	
	private final ConcurrentLinkedQueue<T> queue;
	
	private final AtomicInteger size;

	public SimpleFixSizeQueue(int capacity) {
		this.capacity = capacity;
		this.queue = new ConcurrentLinkedQueue<T>();
		this.size = new AtomicInteger(0);
	}
	
	public T poll(){
		 T t = queue.poll();
		 if(t != null){
			 size.decrementAndGet();
		 }
		 return t;
	}
	
	
	public void add(T t){
		if(size.incrementAndGet() >= capacity){
			poll();
		}
		queue.add(t);
	}
	
	public int size(){
		return size.get();
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}
}
