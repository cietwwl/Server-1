package com.rw.fsutil.dao.optimize;

public interface ComputeFunction<V, R> {

	public R computeIfPersent(V value);
}
