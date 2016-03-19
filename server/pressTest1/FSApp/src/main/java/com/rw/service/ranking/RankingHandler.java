package com.rw.service.ranking;

import java.util.List;

import com.bm.rank.RankType;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.RankingMgr;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingFactory;
import com.rwbase.dao.ranking.CfgRankingDAO;
import com.rwbase.dao.ranking.RankingUtils;
import com.rwbase.dao.ranking.TableRankingMgr;
import com.rwbase.dao.ranking.pojo.CfgRanking;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwproto.MsgDef.Command;
import com.rwproto.RankServiceProtos.BaseRankInfo;
import com.rwproto.RankServiceProtos.ERankRequestType;
import com.rwproto.RankServiceProtos.ERankResultType;
import com.rwproto.RankServiceProtos.MsgRankRequest;
import com.rwproto.RankServiceProtos.MsgRankResponse;
import com.rwproto.RankServiceProtos.RankInfo;


public class RankingHandler {
	private static RankingHandler instance = new RankingHandler();
	
	private RankingHandler(){
		
	}
	
	public static RankingHandler getInstance(){
		return instance;
	}
	
	/**请求排行信息*/
	public ByteString rankingInfo(MsgRankRequest request, Player player){
		MsgRankResponse.Builder response = MsgRankResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		CfgRanking cfgRanking = CfgRankingDAO.getInstance().getRankingCf(request.getRankType());
		if(player.getLevel() < cfgRanking.getLimitLevel()){
			response.setResultType(ERankResultType.NOT_LEVEL);
			return response.build().toByteString();
		}
		String requestUserId = request.getUserId();
		response.setRankType(request.getRankType());
		response.setBaseRankInfo(getBaseRankInfo(request.getUserId(), ERankingType.valueOf(request.getRankType())));
		RankType rankType = RankType.getRankType(request.getRankType());
		Ranking ranking = RankingFactory.getRanking(rankType);
		response.setMyRankInfo(RankingUtils.createOneRankInfo(RankingMgr.getInstance().getRankLevelData(rankType, requestUserId),ranking.getRanking(requestUserId)));
		return response.build().toByteString();
	}
	
	/**请求排行列表*/
	public ByteString rankingList(MsgRankRequest request, Player player){
		MsgRankResponse.Builder response = MsgRankResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.setRankType(request.getRankType());
		String userId = player.getUserId();
		CfgRanking cfgRanking = CfgRankingDAO.getInstance().getRankingCf(request.getRankType());
		if(player.getLevel() < cfgRanking.getLimitLevel()){
			response.setResultType(ERankResultType.NOT_LEVEL);
			return response.build().toByteString();
		}
		
		List<RankInfo> rankList = RankingUtils.createRankList(RankType.getRankType(request.getRankType()));
		pushRankList(player, rankList.size() > 20 ? rankList.subList(0, 20) : rankList, ERankRequestType.RANK_LIST_PART1);
		if(rankList.size() > 20){
			pushRankList(player, rankList.subList(20, rankList.size()), ERankRequestType.RANK_LIST_PART2);
		}
		
		RankType rankType = RankType.getRankType(request.getRankType());
		Ranking ranking = RankingFactory.getRanking(rankType);
		RankingLevelData myInfoData = RankingMgr.getInstance().getRankLevelData(rankType, userId);
		if(myInfoData != null){
			response.setMyRankInfo(RankingUtils.createOneRankInfo(myInfoData,ranking.getRanking(userId)));
		}
		return response.build().toByteString();
	}
	
	/**获取某个玩家的基础排行数据*/
	private BaseRankInfo getBaseRankInfo(String userId, ERankingType rankType){
		Player player = PlayerMgr.getInstance().find(userId);
		int job = player.getCareer();
		BaseRankInfo.Builder baseRankInfo = BaseRankInfo.newBuilder();
		baseRankInfo.setFightingAll(RankingMgr.getInstance().getRankLevel(RankType.FIGHTING_ALL, userId));//全员战斗力排行
		baseRankInfo.setLevelAll(RankingMgr.getInstance().getRankLevel(RankType.LEVEL_ALL, userId));//全员等级排行
		baseRankInfo.setJobDay(RankingMgr.getInstance().getRankLevel(RankType.getJobDay(job), userId));//本职业每日排行
		baseRankInfo.setJobCurrent(RankingMgr.getInstance().getRankLevel(RankType.getJobCurrent(job), userId));//本职业实时排行
//		baseRankInfo.setAthleticsDay(RankingMgr.getInstance().getRankLevel(RankType.ATHLETICS_DAY, userId));//巅峰竞技每日排行
		baseRankInfo.setAthleticsDay(0);//巅峰竞技每日排行
//		baseRankInfo.setAthleticsCurrent(RankingMgr.getInstance().getRankLevel(RankType.ATHLETICS_CURRENT, userId));//巅峰竞技实时排行
		baseRankInfo.setAthleticsCurrent(0);//巅峰竞技实时排行
		baseRankInfo.setTeam(RankingMgr.getInstance().getRankLevel(RankType.TEAM_FIGHTING_DAILY, userId));//五人小队战斗力排行
//		baseRankInfo.setEndless(RankingMgr.getInstance().getRankLevel(RankType.ENDLESS, userId));//无尽战火排行
		baseRankInfo.setEndless(0);//无尽战火排行
//		baseRankInfo.setGlory(RankingMgr.getInstance().getRankLevel(RankType.GLORY, userId));//荣耀山谷排行
		baseRankInfo.setGlory(0);//荣耀山谷排行
//		baseRankInfo.setAthleticsFighting(RankingMgr.getInstance().getRankLevel(RankType.ATHLETICS_FIGHTING, userId));//巅峰竞技战斗力排行
		baseRankInfo.setAthleticsFighting(0);//巅峰竞技战斗力排行
		baseRankInfo.addAllTeamData(RankingUtils.createTeamData(RankingMgr.getInstance().getTeamList(rankType, userId)));//获取队伍数据
		baseRankInfo.setArenaWinCount(TableRankingMgr.getInstance().getArenaTeamWinCount(userId, rankType));
		return baseRankInfo.build();
	}
	
	/**推送排行榜列表列表(分包)*/
	public void pushRankList(Player player, List<RankInfo> list, ERankRequestType requestType){
		MsgRankResponse.Builder response = MsgRankResponse.newBuilder();
		response.setRequestType(requestType);
		response.addAllRankList(list);
		response.setResultType(ERankResultType.SUCCESS);
		player.SendMsg(Command.MSG_RANKING, response.build().toByteString());
	}
}
