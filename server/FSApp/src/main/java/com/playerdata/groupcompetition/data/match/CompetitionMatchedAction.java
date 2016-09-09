package com.playerdata.groupcompetition.data.match;

public interface CompetitionMatchedAction<T extends CompetitionMatchSource> {

	public void onMatch(T t1, T t2);
}
