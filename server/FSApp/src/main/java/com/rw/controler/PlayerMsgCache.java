package com.rw.controler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import com.rwproto.ResponseProtos.Response;

public class PlayerMsgCache {

	private final LinkedList<Integer> seqIdList;
	private final HashMap<Integer, Response> responseMap;
	private final int maxCapacity;
	private final ReentrantLock lock;

	public PlayerMsgCache(int maxCapacity) {
		this.maxCapacity = maxCapacity;
		this.seqIdList = new LinkedList<Integer>();
		this.responseMap = new HashMap<Integer, Response>(maxCapacity);
		this.lock = new ReentrantLock();
	}

	public boolean add(int seqId, Response response) {
		Integer seqIdValue = seqId;
		lock.lock();
		try {
			if (responseMap.containsKey(seqIdValue)) {
				return false;
			}
			responseMap.put(seqIdValue, response);
			this.seqIdList.add(seqIdValue);
			if (this.seqIdList.size() > maxCapacity) {
				Integer oldSeqId = this.seqIdList.poll();
				responseMap.remove(oldSeqId);
			}
			return false;
		} finally {
			lock.unlock();
		}
	}

	public Response getResponse(int seqId) {
		lock.lock();
		try {
			return responseMap.get(seqId);
		} finally {
			lock.unlock();
		}
	}
	
	public void clear(){
		lock.lock();
		try{
			this.responseMap.clear();
			this.seqIdList.clear();
		}finally{
			lock.unlock();
		}
	}
}
