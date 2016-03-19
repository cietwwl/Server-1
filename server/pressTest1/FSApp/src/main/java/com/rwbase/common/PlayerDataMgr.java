package com.rwbase.common;

import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.Player;

/**
 * 抽象Player中的Mgr接口
 * 
 * @author Jamaz
 *
 */
public class PlayerDataMgr {

	private RecordSynchronization recordSynchronization;

	private AtomicInteger version = new AtomicInteger();

	public PlayerDataMgr(RecordSynchronization recordSynchronizationP) {
		this.recordSynchronization = recordSynchronizationP;
	}

	public int versionIncr() {
		return version.incrementAndGet();
	}

	public int getVersion() {
		return version.get();
	}

	public void syn(Player player, int versionP) {
		int currentVersion = version.get();
		if (isVersionDiff(versionP)) {
			recordSynchronization.synAllData(player, currentVersion);
		}
	}

	private boolean isVersionDiff(int versionP) {
		boolean isDiff = false;
		int currentVersion = version.get();
		if (currentVersion == 0) {
			isDiff = true;
		} else {
			isDiff = (currentVersion != versionP);
		}
		return isDiff;
	}
}