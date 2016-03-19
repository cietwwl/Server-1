package com.rw.fsutil.ranking.impl;

import com.rw.fsutil.ranking.ListRankingEntry;


public class MomentSRankingEntryImpl<K, E> implements ListRankingEntry<K, E> {

	private final int ranking;
	private final K key;
	private final E extension;

	public MomentSRankingEntryImpl(int ranking, K key, E extension) {
		this.ranking = ranking;
		this.key = key;
		this.extension = extension;
	}

	@Override
	public int getRanking() {
		return ranking;
	}

	@Override
	public E getExtension() {
		return extension;
	}

	@Override
	public K getKey() {
		return key;
	}

}
