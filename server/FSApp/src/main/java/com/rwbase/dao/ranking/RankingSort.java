package com.rwbase.dao.ranking;

import java.util.Comparator;

import com.rwbase.dao.ranking.pojo.RankingLevelData;

public final class RankingSort {
	private RankingSort() {}	
	/**等级排序*/
	public static final class RankLevel implements Comparator<RankingLevelData>{
		public int compare(RankingLevelData p1, RankingLevelData p2){
			int lCmp = ((Integer)p1.getLevel()).compareTo((Integer)p2.getLevel());//先根据等级
			if(lCmp == 0){
				int eCmp = ((Long)p1.getExp()).compareTo((Long)p2.getExp());//再根据当前等级经验值
				if(eCmp == 0){
					int uCmp = ((String)p1.getUserId()).compareTo((String)p2.getUserId());//再根据用户ID){
					return uCmp * -1;
				}else{
					return eCmp * -1;
				}
			}else{
				return lCmp * -1;
			}
		}
	}
	
	/**全员战斗力排序排序*/
	public static final class RankFight implements Comparator<RankingLevelData>{
		public int compare(RankingLevelData p1, RankingLevelData p2){
			int fCmp = ((Integer)p1.getFightingAll()).compareTo((Integer)p2.getFightingAll());//先根据战斗力
			if(fCmp == 0){
				int lCmp = ((Integer)p1.getLevel()).compareTo((Integer)p2.getLevel());//再根据当前等级
				if(lCmp == 0){
					int uCmp = ((String)p1.getUserId()).compareTo((String)p2.getUserId());//再根据用户ID){
					return uCmp * -1;
				}else{
					return lCmp * -1;
				}				
			}else{
				return fCmp * -1;
			}
		}
	}
	
	/**队伍战斗力排序排序*/
	public static final class RankTeamFight implements Comparator<RankingLevelData>{
		public int compare(RankingLevelData p1, RankingLevelData p2){
			int fCmp = ((Integer)p1.getFightingTeam()).compareTo((Integer)p2.getFightingTeam());//先根据战斗力
			if(fCmp == 0){
				int lCmp = ((Integer)p1.getLevel()).compareTo((Integer)p2.getLevel());//再根据当前等级
				if(lCmp == 0){
					int uCmp = ((String)p1.getUserId()).compareTo((String)p2.getUserId());//再根据用户ID){
					return uCmp * -1;
				}else{
					return lCmp * -1;
				}				
			}else{
				return fCmp * -1;
			}
		}
	}
	
	/**竞技场排名*/
	public static final class RankArenaFight implements Comparator<RankingLevelData>{
		public int compare(RankingLevelData p1, RankingLevelData p2){
			int fCmp = ((Integer)p1.getArenaPlace()).compareTo((Integer)p2.getArenaPlace());//根据排名
			return fCmp;
		}
	}
}
