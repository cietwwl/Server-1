package com.rw.handler.groupCompetition.data.match;

public interface GCMatchedAction<T extends IGCMatchSource> {

	public void onMatch(T t1, T t2);
}
