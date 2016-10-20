package com.bm.saloon.impl.comImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.bm.saloon.ISaloonBm;
import com.bm.saloon.SaloonResult;
import com.playerdata.Player;


public class SaloonBmComImpl implements ISaloonBm{

	private Map<Integer, SaloonCom> saloonMap = new ConcurrentHashMap<Integer, SaloonCom>();

	private Map<String, Integer> userIdSaloonMap = new ConcurrentHashMap<String, Integer>();

	final private AtomicInteger saloonId = new AtomicInteger(0);

	private ReadLock readLock;
	private WriteLock writeLock;
	
	public SaloonBmComImpl() {
		ReentrantReadWriteLock treeRwLock = new ReentrantReadWriteLock();
		this.readLock = treeRwLock.readLock();
		this.writeLock = treeRwLock.writeLock();
	}

	@Override
	public void update() {
		for (SaloonCom saloonCom : saloonMap.values()) {
			saloonCom.update();
		}
	}

	private SaloonCom getByUserId(String userId) {
		SaloonCom target = null;

		Integer targetId = userIdSaloonMap.get(userId);
		if (targetId != null) {
			target = saloonMap.get(targetId);
		}

		return target;
	}

	private SaloonCom addNewSaloon() {
		SaloonCom saloonCom = null;
		this.writeLock.lock();
		try {
			int newId = saloonId.getAndIncrement();
			saloonCom = SaloonCom.newInstance(newId);
			saloonMap.put(saloonCom.getId(), saloonCom);
		} finally {
			this.writeLock.unlock();
		}
		return saloonCom;
	}
	@Override
	public SaloonResult enter(String userId, float px, float py) {
		SaloonCom targetSaloon = null;
		this.readLock.lock();
		try {
			targetSaloon = getByUserId(userId);
			if (targetSaloon == null) {
				for (SaloonCom saloonCom : saloonMap.values()) {
					if (saloonCom.canEnter()) {
						targetSaloon = saloonCom;
						break;
					}
				}
			}
		} finally{
			this.readLock.unlock();
		}

		// 多线程的时候有一定几率会添加多个，不过没关系，只是多创建了几个saloon,不影响业务
		if (targetSaloon == null) {
			targetSaloon = addNewSaloon();
		}
		SaloonResult result = targetSaloon.enter(userId, px, py);
		if (result.isSuccess()) {
			userIdSaloonMap.put(userId, targetSaloon.getId());
		}
		return result;
	}
	@Override
	public SaloonResult leave(String userId) {
		SaloonCom targetSaloon = getByUserId(userId);
		SaloonResult result = SaloonResult.newInstance(false);
		if(targetSaloon!=null){
			result = targetSaloon.leave(userId);
			if(result.isSuccess()){
				userIdSaloonMap.remove(userId);
			}
		}
		
		return result;
	}
	@Override
	public SaloonResult informPosition(String userId, float px, float py) {
		SaloonCom targetSaloon = getByUserId(userId);
		SaloonResult result = SaloonResult.newInstance(false);
		if(targetSaloon!=null){
			result = targetSaloon.informPosition(userId, px, py);
		}
		return result;
	}
	
	@Override
	public SaloonResult synAllPlayerInfo(Player player) {
		SaloonCom targetSaloon = getByUserId(player.getUserId());
		SaloonResult result = SaloonResult.newInstance(false);
		if(targetSaloon!=null){
			result = targetSaloon.synAllPlayerInfo(player);
		}
		return result;
	}

}
