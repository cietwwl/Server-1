package com.rw.fsutil.dao.cache.trace;

public interface DataChangedVisitor<E extends DataChangedEvent<?>> {

	public void notifyDataChanged(E event);
	
}
