package com.rw.fsutil.common;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class ResponseMonitor {

	private final int monitorCount; // 指定需要监视最近消耗条目的数量
	private final AtomicInteger lastTimesCount; // 最近消耗的数量
	private final ConcurrentLinkedQueue<Long> lastCostQueue; // 最近消耗队列
	private final AtomicLong maxCost; // 最大消耗
	private final AtomicLong minCost; // 最小消耗
	private final AtomicReference<CostRecord> avgCost; // 平均消耗

	public ResponseMonitor(int monitorCount) {
		super();
		this.monitorCount = monitorCount;
		this.lastTimesCount = new AtomicInteger();
		this.lastCostQueue = new ConcurrentLinkedQueue<Long>();
		this.maxCost = new AtomicLong();
		this.minCost = new AtomicLong();
		this.avgCost = new AtomicReference<CostRecord>();
	}

	public void addCost(long cost) {
		this.lastCostQueue.offer(cost);
		if (this.lastTimesCount.incrementAndGet() > monitorCount) {
			this.lastCostQueue.poll();
		}
		// 平均时间
		for (;;) {
			CostRecord record = avgCost.get();
			int times;
			long total;
			if (record == null) {
				times = 1;
				total = cost;
			} else {
				times = record.times + 1;
				total = record.total + cost;
			}
			CostRecord newRecord = new CostRecord(total, times);
			if (avgCost.compareAndSet(record, newRecord)) {
				break;
			}
		}

		for (;;) {
			long min = this.minCost.get();
			if (min > 0 && min < cost) {
				break;
			}
			if (this.minCost.compareAndSet(min, cost)) {
				break;
			}
		}
		for (;;) {
			long max = this.maxCost.get();
			if (max > cost) {
				break;
			}
			if (this.maxCost.compareAndSet(max, cost)) {
				break;
			}
		}
	}

	public long getMaxCost() {
		return this.maxCost.get();
	}

	public long getMinCost() {
		return this.minCost.get();
	}

	public long getAvgCost() {
		CostRecord cost = this.avgCost.get();
		if (cost == null) {
			return 0;
		}
		return cost.total / cost.times;
	}

	public long getLastAvg() {
		int count = 0;
		int total = 0;
		for (int i = 0; i < monitorCount; i++) {
			Long time = this.lastCostQueue.peek();
			if (time == null) {
				break;
			}
			count++;
			total += time;
		}
		if (count == 0) {
			return 0;
		}
		return total / count;
	}

	static class CostRecord {
		private long total;
		private int times;

		public CostRecord(long total, int times) {
			super();
			this.total = total;
			this.times = times;
		}

	}

}
