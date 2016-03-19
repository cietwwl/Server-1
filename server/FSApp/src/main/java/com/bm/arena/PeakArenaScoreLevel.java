package com.bm.arena;

public enum PeakArenaScoreLevel {

	FS(10000,Integer.MAX_VALUE-1,"封神",6,100),
	WZ(7000,9999,"王者",5,80),
	ZS(5000,6999,"钻石",4,50),
	HZ(3000,4999,"黄金",3,40),
	BZ(1000,2999,"白银",2,30),
	QT(0,999,"青铜",1,20)
	;

	private final int minScore;
	private final int maxScore;
	private final String name;
	private final int level;
	private final int gainCurrency;
	
	private PeakArenaScoreLevel(int minScore, int maxScore, String name, int level,int gainCurrency) {
		this.minScore = minScore;
		this.maxScore = maxScore;
		this.name = name;
		this.level = level;
		this.gainCurrency = gainCurrency;
	}

	public int getMinScore() {
		return minScore;
	}

	public int getMaxScore() {
		return maxScore;
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public static PeakArenaScoreLevel getSocre(int score){
		PeakArenaScoreLevel[] array = values();
		for(int i = 0;i<array.length;i++){
			PeakArenaScoreLevel sl = array[i];
			if(score < sl.maxScore && score> sl.minScore){
				return sl;
			}
		}
		return QT;
	}
	
	public int getGainCurrency() {
		return gainCurrency;
	}
}
