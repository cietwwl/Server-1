package com.rwbase.common.timer.core;

import java.util.Iterator;

public interface ReusableIterator<E> extends Iterator<E> {

	public void rewind();
}
