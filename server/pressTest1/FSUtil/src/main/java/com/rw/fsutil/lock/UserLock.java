package com.rw.fsutil.lock;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserLock {

	private static ConcurrentHashMap<String, UserLock> lockMap=new ConcurrentHashMap<String, UserLock>();
	private static long mapCreated=new Date().getTime();
	private final static long CREATED_INTERVAL=24*60*60*1000;
	private AtomicInteger requestCount = new AtomicInteger(0);
	
	public static UserLock getSyncUserLock(String oid)
	{
		synchronized(lockMap)
		{
			if(System.currentTimeMillis()>(mapCreated+CREATED_INTERVAL))
			{
				lockMap.clear();
				mapCreated=System.currentTimeMillis();
			}
			lockMap.putIfAbsent(oid, new UserLock());
		}

		return lockMap.get(oid);
	}

	public int getAndIntrRequestCount() {
		return requestCount.getAndIncrement();
	}

	public void decrRequestCount() {
		requestCount.decrementAndGet();
	}
	

}
