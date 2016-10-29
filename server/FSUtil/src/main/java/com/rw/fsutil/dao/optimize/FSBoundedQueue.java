package com.rw.fsutil.dao.optimize;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.rw.fsutil.cacheDao.FSUtilLogger;

public class FSBoundedQueue<E> {

	private final E[] elements;
	private final int maxIndex;
	private int takeIndex;
	private int offerIndex;
	private final ReentrantLock lock;
	private final Condition waitCondition;
	private int count;
	private final String name;

	public FSBoundedQueue(String name, int maxCapacity) {
		if(maxCapacity <= 0){
			maxCapacity = 1;
		}
		this.name = name;
		this.elements = (E[]) new Object[maxCapacity];
		this.maxIndex = maxCapacity - 1;
		this.lock = new ReentrantLock();
		this.waitCondition = lock.newCondition();
	}

	public boolean offer(E element) {
		lock.lock();
		try {
			if (count > maxIndex) {
				FSUtilLogger.info("insert fail " + element + "," + count + "," + Thread.currentThread().getName());
				return false;
			}
			elements[offerIndex] = element;
			if (offerIndex == maxIndex) {
				offerIndex = 0;
			} else {
				offerIndex++;
			}
			count++;
		} finally {
			lock.unlock();
		}
		return true;
	}

	public E poll() {
		lock.lock();
		try {
			if (count == 0) {
				return null;
			}
			E element = elements[takeIndex];
			elements[takeIndex] = null;
			if (takeIndex == maxIndex) {
				takeIndex = 0;
			} else {
				takeIndex++;
			}
			--count;
			waitCondition.signal();
			return element;
		} finally {
			lock.unlock();
		}
	}

	public boolean waitForNotFull(TimeUnit unit, long maxWaitTime) throws InterruptedException {
		lock.lock();
		try {
			while (count > maxIndex) {
				if (!waitCondition.await(maxWaitTime, unit)) {
					return false;
				}
			}
			FSUtilLogger.info(name + "wait bound：" + count + "," + Thread.currentThread().getName());
			return true;
		} finally {
			lock.unlock();
		}
	}

	public int size() {
		lock.lock();
		try {
			return count;
		} finally {
			lock.unlock();
		}
	}

}
