package com.rw.service.PeakArena.datamodel;

import com.common.PairParser;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;

public class peakArenaMatchRule extends AbsRangeConfig {
	private int key; // 关键字段
	private String range; // 排名分段
	private String enemy1; // 对手1
	private String enemy2; // 对手2
	private String enemy3; // 对手3

	public int getKey() {
		return key;
	}

	private Pair<Integer, Integer> levelRange;
	private Pair<Integer, Integer> enemy1Range;
	private Pair<Integer, Integer> enemy2Range;
	private Pair<Integer, Integer> enemy3Range;
	private Pair<Integer, Integer>[] enemyRanges;

	public IReadOnlyPair<Integer, Integer> getEnemy1Range() {
		return enemy1Range;
	}

	public IReadOnlyPair<Integer, Integer> getEnemy2Range() {
		return enemy2Range;
	}

	public IReadOnlyPair<Integer, Integer> getEnemy3Range() {
		return enemy3Range;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void ExtraInitAfterLoad() {
		String errorId = "peakArenaMatchRule.csv"+",key="+key;
		levelRange = PairParser.ParseRange(range, "~", "巅峰竞技场", errorId, "无效排名分队", true);
		enemy1Range = PairParser.ParseRange(enemy1, "~", "巅峰竞技场", errorId, "无效对手筛选范围", true);
		enemy2Range = PairParser.ParseRange(enemy2, "~", "巅峰竞技场", errorId, "无效对手筛选范围", true);
		enemy3Range = PairParser.ParseRange(enemy3, "~", "巅峰竞技场", errorId, "无效对手筛选范围", true);
		if (!(enemy1Range.getT1()>enemy2Range.getT2() && enemy2Range.getT1() > enemy3Range.getT2() && enemy2Range.getT1()>=0)){
			throw new RuntimeException("三个对手的筛选范围配置重叠");
		}
		enemyRanges = new Pair[3];
		enemyRanges[0] = enemy1Range;
		enemyRanges[1] = enemy2Range;
		enemyRanges[2] = enemy3Range;
	}
	
	public int getEnemyCount(){
		return enemyRanges.length;
	}
	
	public IReadOnlyPair<Integer, Integer> getEnemyRange(int index){
		if (0 <= index && index < enemyRanges.length){
			return enemyRanges[index];
		}
		return null;
	}

	@Override
	public IReadOnlyPair<Integer, Integer> getRange() {
		return levelRange;
	}
}
