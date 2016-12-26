package com.rwbase.common.playerext;

import java.util.concurrent.atomic.AtomicBoolean;

import com.rw.service.guide.datamodel.NewGuideClosure;
import com.rwbase.dao.chat.pojo.ChatTempAttribute;

/**
 * <pre>
 * 存储玩家临时属性
 * 不需要保存到数据库
 * </pre>
 * 
 * @author Jamaz
 *
 */
public class PlayerTempAttribute {

	private final AtomicBoolean expChanged;
	private final AtomicBoolean levelChanged;
	private final AtomicBoolean heroFightingChanged;
	private final AtomicBoolean checkRedPoint;
	private boolean recordChanged; // 竞技场日志变动
	private boolean refreshStore;
	private final PlayerMemoryMark arenaChanged;
	private volatile NewGuideClosure lastClosure;
	private ChatTempAttribute chatTempAttribute;// 聊天临时存储数据
	private volatile String ip;// 用户的IP

	public PlayerTempAttribute() {
		this.expChanged = new AtomicBoolean();
		this.levelChanged = new AtomicBoolean();
		this.heroFightingChanged = new AtomicBoolean();
		this.checkRedPoint = new AtomicBoolean();
		this.arenaChanged = new PlayerMemoryMark();
		this.chatTempAttribute = new ChatTempAttribute();
	}

	public void setExpChanged() {
		this.expChanged.set(true);
	}

	public void setLevelChanged() {
		this.levelChanged.set(true);
	}

	public void setHeroFightingChanged() {
		this.heroFightingChanged.set(true);
	}

	public void setRedPointChanged() {
		this.checkRedPoint.set(true);
	}

	/**
	 * 检查角色的经验是否发生变化，如果是重置为未变化
	 * 
	 * @return
	 */
	public boolean checkAndResetExpChanged() {
		return getBooleanAndReset(expChanged);
	}

	/**
	 * 检查角色的等级是否发生变化，如果是重置为未变化
	 * 
	 * @return
	 */
	public boolean checkAndResetLevelChanged() {
		return getBooleanAndReset(levelChanged);
	}

	/**
	 * 检查角色的战力是否发生变化，如果是重置为未变化
	 * 
	 * @return
	 */
	public boolean checkAndResetFightingChanged() {
		return getBooleanAndReset(heroFightingChanged);
	}

	public boolean checkAndResetRedPoint() {
		return getBooleanAndReset(checkRedPoint);
	}

	private boolean getBooleanAndReset(AtomicBoolean atomicBool) {
		boolean current = atomicBool.get();
		if (!current) {
			return current;
		}
		return atomicBool.getAndSet(false);
	}

	public boolean isRecordChanged() {
		return recordChanged;
	}

	public void setRecordChanged(boolean recordChanged) {
		this.recordChanged = recordChanged;
	}

	public boolean isRefreshStore() {
		return refreshStore;
	}

	public void setRefreshStore(boolean refreshStore) {
		this.refreshStore = refreshStore;
	}

	public boolean checkAndResetArenaChanged() {
		return this.arenaChanged.checkAndResetChanged();
	}

	public NewGuideClosure getLastClosure() {
		return lastClosure;
	}

	public void setLastClosure(NewGuideClosure lastClosure) {
		this.lastClosure = lastClosure;
	}

	/**
	 * 获取聊天的临时数据
	 * 
	 * @return
	 */
	public ChatTempAttribute getChatTempAttribute() {
		return chatTempAttribute;
	}

	// ===================获取IP
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}