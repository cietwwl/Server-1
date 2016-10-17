package com.playerdata.groupcompetition.util;

public interface IBIConsumer<T, E> {

	public void accept(T t, E e);
}
