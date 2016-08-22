package com.playerdata.groupcompetition.data.match;

public interface GCMatchedAction<T extends GCMatchSource> {

	public void onMatch(T t1, T t2);
}
