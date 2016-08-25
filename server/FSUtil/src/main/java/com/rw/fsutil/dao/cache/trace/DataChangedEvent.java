package com.rw.fsutil.dao.cache.trace;

public interface DataChangedEvent<V extends DataChangedVisitor<?>> {

	public void accept(V visitor);
}
