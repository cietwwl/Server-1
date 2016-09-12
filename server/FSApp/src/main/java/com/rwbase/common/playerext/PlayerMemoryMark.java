package com.rwbase.common.playerext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <pre>
 * 玩家内存标记
 * 用于记录玩家使用进行了某项属性的改变，从而进行更复杂的计算
 * 用初始化标志位表示是否第一次载入内存，如果是第一次载入内存，以默认改变进行处理(即触发外部重新计算)
 * </pre>
 * @author Jamaz
 *
 */
public class PlayerMemoryMark {

	private final AtomicBoolean notInit;
	private final AtomicBoolean changed;

	public PlayerMemoryMark() {
		this.notInit = new AtomicBoolean(true);
		this.changed = new AtomicBoolean();
	}

	public void setChanged() {
		this.changed.set(true);
	}

	public boolean checkAndResetChanged() {
		if (notInit.get()) {
			boolean tryInit = notInit.getAndSet(false);
			if (tryInit) {
				return true;
			}
		}
		boolean isChanged = changed.get();
		if (!isChanged) {
			return isChanged;
		}
		return changed.getAndSet(false);
	}

}
