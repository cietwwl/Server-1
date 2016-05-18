package com.rw.fsutil.concurrent;

public interface ParametricTask<E> {

	public void run(E e);
}
