package com.rw.fsutil.ranking.exception;

/**
 * 排行榜容量已满
 * @author Jamaz
 *
 */
public class RankingCapacityNotEougthException extends Exception{

	public RankingCapacityNotEougthException(Throwable cause) {
		super(cause);
	}

	public RankingCapacityNotEougthException(String message, Throwable cause) {
		super(message, cause);
	}

	public RankingCapacityNotEougthException(String message) {
		super(message);
	}

}
