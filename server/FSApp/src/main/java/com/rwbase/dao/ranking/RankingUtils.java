package com.rwbase.dao.ranking;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.RankType;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.RankingMgr;
import com.rw.service.ranking.ERankingType;
import com.rwbase.dao.ranking.pojo.CfgRanking;
import com.rwbase.dao.ranking.pojo.RankingCommonData;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwproto.RankServiceProtos;
import com.rwproto.RankServiceProtos.RankInfo;
import com.rwproto.RankServiceProtos.RankingHeroData;
import com.rwproto.RankServiceProtos.RankingMagicData;
import com.rwproto.RankServiceProtos.RankingTeamData;

/**
 *  旧有代码，需要整理，否则存在大量冗余
 */
public class RankingUtils {
	/**生成数据*/
	public static void initCreateCommonData(Player player, RankingLevelData toData){
		toData.setUserId(player.getUserId());
		toData.setUserName(player.getUserName());
		toData.setLevel(player.getLevel());
		toData.setExp(player.getExp());
		toData.setFightingAll(player.getHeroMgr().getFightingAll());
		toData.setFightingTeam(player.getHeroMgr().getFightingTeam());
		toData.setUserHead(player.getHeadImage());
		toData.setModelId(player.getModelId());
		toData.setJob(player.getCareer());
		toData.setSex(player.getSex());
		toData.setCareerLevel(player.getStarLevel());
	}
	
	/**每日排序后生成数据*/
	public static void createLevelToCommonData(RankingLevelData data, RankingCommonData toData){
		toData.setUserId(data.getUserId());
		toData.setUserName(data.getUserName());
		toData.setLevel(data.getLevel());
		toData.setExp(data.getExp());
		toData.setFightingAll(data.getFightingAll());
		toData.setFightingTeam(data.getFightingTeam());
		toData.setUserHead(data.getUserHead());
//		toData.setModelId(data.getModelId());
		toData.setModelId(RankingUtils.getModelId(data));
		toData.setJob(data.getJob());
		toData.setSex(data.getSex());
		toData.setCareerLevel(data.getCareerLevel());
	}
	
	/**每日排序后生成数据*/
	public static void createCommonToLevelData(RankingCommonData data, RankingLevelData toData){
		toData.setUserId(data.getUserId());
		toData.setUserName(data.getUserName());
		toData.setLevel(data.getLevel());
		toData.setExp(data.getExp());
		toData.setFightingAll(data.getFightingAll());
		toData.setFightingTeam(data.getFightingTeam());
		toData.setUserHead(data.getUserHead());
		toData.setModelId(data.getModelId());
		toData.setJob(data.getJob());
		toData.setSex(data.getSex());
		toData.setCareerLevel(data.getCareerLevel());
	}
		
	public static List<RankingLevelData> subListByLevelData(List<RankingLevelData> list, ERankingType rankType){
		CfgRanking cfg = CfgRankingDAO.getInstance().getRankingCf(rankType.getValue());
		if(list.size() >= cfg.getRankNum()){
			return list.subList(0, cfg.getRankNum());
		}else{
			return list;
		}
	}
	
	/** 生成或改变一个数据 */
	public static RankingCommonData createCommonData(Player pPlayer, RankingCommonData toData) {
		toData.setUserId(pPlayer.getUserId());
		toData.setLevel(pPlayer.getLevel());
		toData.setExp(pPlayer.getExp());
		toData.setUserName(pPlayer.getUserName());
		toData.setUserHead(pPlayer.getHeadImage());
		toData.setJob(pPlayer.getCareer());
		toData.setSex(pPlayer.getSex());
		toData.setCareerLevel(pPlayer.getStarLevel());
		toData.setModelId(pPlayer.getModelId());
		toData.setFightingAll(pPlayer.getHeroMgr().getFightingAll());
		toData.setFightingTeam(pPlayer.getHeroMgr().getFightingTeam());
		return toData;
	}
	
	/**获得相应生成列表*/
	public static List<RankServiceProtos.RankInfo> createRankList(RankType rankType){
		List<RankingLevelData> tableRankInfoList = RankingMgr.getInstance().getRankList(rankType);
		List<RankServiceProtos.RankInfo> rankInfoList = new ArrayList<RankServiceProtos.RankInfo>();
		RankingLevelData levelData;		
		for(int i = 0; i < tableRankInfoList.size(); i++){
			levelData = tableRankInfoList.get(i);
			rankInfoList.add(createOneRankInfo(levelData,i+1));
		}
		return rankInfoList;
	}
	
	/**写入一条数据*/
	public static RankInfo createOneRankInfo(RankingLevelData levelData,int ranking){
		RankInfo.Builder rankInfo;
		rankInfo = RankInfo.newBuilder();
		if(levelData != null){
			rankInfo.setHeroUUID(levelData.getUserId());
			rankInfo.setRankingLevel(ranking);
			rankInfo.setLevel(levelData.getLevel());
			rankInfo.setHeroName(levelData.getUserName());
			rankInfo.setImageId(levelData.getUserHead());
			rankInfo.setJob(levelData.getJob());
//			rankInfo.setModelId(levelData.getModelId());
			rankInfo.setModelId(RankingUtils.getModelId(levelData));
			rankInfo.setFightingAll(levelData.getFightingAll());
			rankInfo.setFightingTeam(levelData.getFightingTeam());
			rankInfo.setRankCount(levelData.getRankCount());
		}		
		return rankInfo.build();
	}
	
	/**转换通迅数据*/
	public static List<RankingTeamData> createTeamData(List<com.rwbase.dao.ranking.pojo.RankingTeamData> list){
		RankingTeamData.Builder serviceTeamData;
		List<RankingTeamData> result = new ArrayList<RankServiceProtos.RankingTeamData>();
		for(com.rwbase.dao.ranking.pojo.RankingTeamData teamData : list){
			serviceTeamData = RankingTeamData.newBuilder();			
			serviceTeamData.addAllHeroList(createHeroDatas(teamData.getHeroList()));
			serviceTeamData.setMagicData(createMagicData(teamData.getMagicData()));
			result.add(serviceTeamData.build());
		}
		return result;
	}
	
	private static List<RankingHeroData> createHeroDatas(List<com.rwbase.dao.ranking.pojo.RankingHeroData> list){
		List<RankingHeroData> result = new ArrayList<RankServiceProtos.RankingHeroData>();
		for(com.rwbase.dao.ranking.pojo.RankingHeroData heroData : list){
			result.add(createHeroData(heroData));
		}
		return result;
	}
	
	private static RankingHeroData createHeroData(com.rwbase.dao.ranking.pojo.RankingHeroData heroData){
		RankingHeroData.Builder serviceHeroData = RankingHeroData.newBuilder();
		serviceHeroData.setHeroId(heroData.getHeroId());
		serviceHeroData.setHeroHead(heroData.getHeroHead());
		serviceHeroData.setQuality(heroData.getQuality());
		serviceHeroData.setLevel(heroData.getLevel());
		serviceHeroData.setStarLevel(heroData.getStarLevel());
		return serviceHeroData.build();
	}
	
	private static RankingMagicData createMagicData(com.rwbase.dao.ranking.pojo.RankingMagicData magicData){
		RankingMagicData.Builder serviceMagicData = RankingMagicData.newBuilder();
		serviceMagicData.setMagicImage(magicData.getMagicImage());
		serviceMagicData.setMagicQuality(magicData.getMagicQuality());
		serviceMagicData.setMagicAttackType(magicData.getMagicAttackType());
		return serviceMagicData.build();
	}
	
	public static int getModelId(RankingLevelData data){
		int model = data.getModelId();
		if(model!=0){
			return model;
		}
		Player p = PlayerMgr.getInstance().find(data.getUserId());
		return p.getModelId();
	}
}
