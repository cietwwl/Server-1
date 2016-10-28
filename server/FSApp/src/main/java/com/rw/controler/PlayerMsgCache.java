package com.rw.controler;

import io.netty.util.collection.IntObjectHashMap;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.rw.fsutil.common.FastPair;
import com.rw.fsutil.util.DateUtils;
import com.rwproto.MsgDef.Command;

/**
 * <pre>
 * Player Message CACHE
 * for Player reconnect operation
 * key = Message SeqId
 * value = Message
 * over ten minute message would be purge
 * </pre>
 * 
 * @author Jamaz
 *
 */
public class PlayerMsgCache {

	private final LinkedList<PlayerMsgTimeRecord> seqIdList;
	private final IntObjectHashMap<ByteString> responseMap;
	private final int maxCapacity;
	private final ReentrantLock lock;
	private final ConcurrentHashMap<Command, FastPair<Command, AtomicLong>> purgeStat;

	public PlayerMsgCache(int maxCapacity, ConcurrentHashMap<Command, FastPair<Command, AtomicLong>> purgeStat) {
		this.maxCapacity = maxCapacity;
		this.seqIdList = new LinkedList<PlayerMsgTimeRecord>();
		this.responseMap = new IntObjectHashMap<ByteString>(maxCapacity << 1);
		this.lock = new ReentrantLock();
		this.purgeStat = purgeStat;
	}

	public boolean add(Command command, int seqId, ByteString responseContent) {
		PlayerMsgTimeRecord msgRecord = new PlayerMsgTimeRecord(command, seqId, DateUtils.getSecondLevelMillis());
		lock.lock();
		try {
			if (this.responseMap.containsKey(seqId)) {
				return false;
			}
			this.responseMap.put(seqId, responseContent);
			this.seqIdList.add(msgRecord);
			if (this.seqIdList.size() > maxCapacity) {
				PlayerMsgTimeRecord oldSeqId = this.seqIdList.poll();
				this.responseMap.remove(oldSeqId.getSeqId());
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
					ByteString response = responseMap.remove(msgRecord.getSeqId());
					if (response == null) {
						GameLog.error("PlayerMsgCache", String.valueOf(msgRecord.getSeqId()), "remove msg fail by purge");
						continue;
					}
					Command command = msgRecord.getCommand();
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

	public ByteString getResponse(int seqId) {
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

}
