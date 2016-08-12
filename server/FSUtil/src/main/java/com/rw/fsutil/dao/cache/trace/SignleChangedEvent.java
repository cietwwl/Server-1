package com.rw.fsutil.dao.cache.trace;

/**
 * 单记录改变事件
 * 
 * @author Jamaz
 *
 * @param <V>
 */
public class SignleChangedEvent<V> implements DataChangedEvent<SingleChangedListener<V>> {

	private final V oldRecord;
	private final V currentRecord;

	public SignleChangedEvent(V oldRecord, V currentRecord) {
		super();
		this.oldRecord = oldRecord;
		this.currentRecord = currentRecord;
	}

	@Override
	public void accept(SingleChangedListener<V> visitor) {
		visitor.notifyDataChanged(this);
	}

	public V getOldRecord() {
		return oldRecord;
	}

	public V getCurrentRecord() {
		return currentRecord;
	}
}
