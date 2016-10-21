package com.rw.controler;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import com.log.PlatformLog;
import com.rw.fsutil.common.FastPair;
import com.rw.fsutil.util.DateUtils;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/**
 * <pre>
 * Player Message CACHE
 * for Player reconnect operation
 * key = Message SeqId
 * value = Message
 * over twenty minute message would be purge
 * </pre>
 * 
 * @author Jamaz
 *
 */
public class PlayerMsgCache {

	private final LinkedList<PlayerMsgTimeRecord> seqIdList;
	private final HashMap<Integer, Response> responseMap;
	private final int maxCapacity;
	private final ReentrantLock lock;
	private final ConcurrentHashMap<Command, FastPair<Command, AtomicLong>> purgeStat;

	public PlayerMsgCache(int maxCapacity, ConcurrentHashMap<Command, FastPair<Command, AtomicLong>> purgeStat) {
		this.maxCapacity = maxCapacity;
		this.seqIdList = new LinkedList<PlayerMsgTimeRecord>();
		this.responseMap = new HashMap<Integer, Response>();
		this.lock = new ReentrantLock();
		this.purgeStat = purgeStat;
	}

	public boolean add(int seqId, Response response) {
		Integer seqIdValue = seqId;
		PlayerMsgTimeRecord msgRecord = new PlayerMsgTimeRecord(seqIdValue, DateUtils.getSecondLevelMillis());
		lock.lock();
		try {
			if (responseMap.containsKey(seqIdValue)) {
				return false;
			}
			responseMap.put(seqIdValue, response);
			this.seqIdList.add(msgRecord);
			if (this.seqIdList.size() > maxCapacity) {
				PlayerMsgTimeRecord oldSeqId = this.seqIdList.poll();
				responseMap.remove(oldSeqId.getSeqId());
			}
			return false;
		} finally {
			lock.unlock();
		}
	}

	public void purge(long purgeTime) {
		lock.lock();
		try {
			for (;;) {
				PlayerMsgTimeRecord msgRecord = this.seqIdList.peek();
				if (msgRecord == null) {
					break;
				}
				if (msgRecord.getTimeMillis() > purgeTime) {
					break;
				}
				msgRecord = this.seqIdList.poll();
				if (msgRecord != null) {
					Response response = responseMap.remove(msgRecord.getSeqId());
					if (response == null) {
						PlatformLog.error("PlayerMsgCache", String.valueOf(msgRecord.getSeqId()), "remove msg fail by purge");
						continue;
					}
					Command command = response.getHeader().getCommand();
					FastPair<Command, AtomicLong> count = purgeStat.get(command);
					if (count == null) {
						count = new FastPair<Command, AtomicLong>(command, new AtomicLong());
						FastPair<Command, AtomicLong> old = purgeStat.putIfAbsent(command, count);
						if (old != null) {
							count = old;
						}
					}
					count.secondValue.incrementAndGet();
				} else {
					break;
				}
			}
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

	public void clear() {
		lock.lock();
		try {
			this.responseMap.clear();
			this.seqIdList.clear();
		} finally {
			lock.unlock();
		}
	}

	public Enumeration<FastPair<Command, AtomicLong>> getPurgeCount() {
		return purgeStat.elements();
	}
}
