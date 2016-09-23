package com.rw.handler.groupCompetition.util;

public interface IBIConsumer<T, E> {

	public void accept(T t, E e);
}
