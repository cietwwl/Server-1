package com.rw.fsutil.dao.optimize;

public interface ValueConsumer<V, P, R> {

	public R apply(V value, P param);
}
