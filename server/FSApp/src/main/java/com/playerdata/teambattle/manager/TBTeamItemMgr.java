package com.playerdata.teambattle.manager;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.common.serverdata.ServerCommonData;
import com.common.serverdata.ServerCommonDataHolder;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.teambattle.bm.TeamBattleConst;
import com.playerdata.teambattle.cfg.TeamCfg;
import com.playerdata.teambattle.cfg.TeamCfgDAO;
import com.playerdata.teambattle.data.TBTeamItem;
import com.playerdata.teambattle.data.TBTeamItemHolder;
import com.playerdata.teambattle.data.TeamMember;
import com.playerdata.teambattle.data.UserTeamBattleData;
import com.playerdata.teambattle.data.UserTeamBattleDataHolder;
import com.playerdata.teambattle.dataException.JoinTeamException;
import com.playerdata.teambattle.dataForClient.StaticMemberTeamInfo;
import com.playerdata.teambattle.enums.TBMemberState;
import com.rw.fsutil.util.DateUtils;

public class TBTeamItemMgr{
	
	private static TBTeamItemMgr instance = new TBTeamItemMgr();
	
	public static TBTeamItemMgr getInstance(){
		return instance;
	}
	
	public TBTeamItem get(String teamID){
		if(StringUtils.isNotBlank(teamID)){
			String hardID = TBTeamItemHolder.getInstance().getHardIDFromTeamID(teamID);
			if(StringUtils.isNotBlank(hardID)){
				return TBTeamItemHolder.getInstance().getItem(hardID, teamID);
			}
		}
		return null;
	}
	
	public void synData(Player player){
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if(StringUtils.isNotBlank(utbData.getTeamID()) && utbData.isSynTeam()){
			String hardID = TBTeamItemHolder.getInstance().getHardIDFromTeamID(utbData.getTeamID());
			TBTeamItem teamItem = TBTeamItemHolder.getInstance().getItem(hardID, utbData.getTeamID());
			if(teamItem == null) {
				utbData.clearCurrentTeam();
				UserTeamBattleDataHolder.getInstance().update(player, utbData);
				return;
			}
			synchronized (teamItem) {
				setTeamMemberTeams(teamItem);
			}
			TBTeamItemHolder.getInstance().synData(player, teamItem);
		}
	}
	
	public void synData(String teamID){
		if(StringUtils.isNotBlank(teamID)){
			String hardID = TBTeamItemHolder.getInstance().getHardIDFromTeamID(teamID);
			TBTeamItem teamItem = TBTeamItemHolder.getInstance().getItem(hardID, teamID);
			if(teamItem == null) return;
			synchronized (teamItem) {
				setTeamMemberTeams(teamItem);
				TBTeamItemHolder.getInstance().synData(teamItem);
			}
		}
	}
	
	private void setTeamMemberTeams(TBTeamItem teamItem){
		if(null == teamItem || !teamItem.needRefreshTeamMembers()) return;
		List<StaticMemberTeamInfo> memTeams = new ArrayList<StaticMemberTeamInfo>();
		List<TeamMember> members = teamItem.getMembers();
		for(TeamMember member : members){
			UserTeamBattleData utbMemData = UserTeamBattleDataHolder.getInstance().get(member.getUserID());
			synchronized (utbMemData) {
				if(utbMemData.getSelfTeamInfo() == null){
					Player player = PlayerMgr.getInstance().find(member.getUserID());
					if(player == null) continue;
					List<Hero> heros = player.getHeroMgr().getMaxFightingHeros(player);
					List<String> heroIDs = new ArrayList<String>();
					Map<String, Integer> heroPosMap = new HashMap<String, Integer>();
					for(int i = 1; i < heros.size(); i++){
						heroIDs.add(heros.get(i).getId());
						heroPosMap.put(heros.get(i).getId(), i);
					}
					StaticMemberTeamInfo teamInfo = new StaticMemberTeamInfo();
					teamInfo.setUserID(member.getUserID());
					teamInfo.setHeroPosMap(heroPosMap);
					teamInfo.setUserStaticTeam(ArmyInfoHelper.getSimpleInfo(member.getUserID(), "", (heroIDs.isEmpty() ? null : heroIDs)));
					utbMemData.setSelfTeamInfo(teamInfo);
				}
			}
			memTeams.add(utbMemData.getSelfTeamInfo());
		}
		teamItem.setTeamMembers(memTeams);
	}
	
	public synchronized TBTeamItem getOneCanJionTeam(String userID, String hardID){
		List<TBTeamItem> jionAble = new ArrayList<TBTeamItem>();
		Enumeration<TBTeamItem> itemEnum = TBTeamItemHolder.getInstance().getItemStore(hardID).getEnum();
		while(itemEnum.hasMoreElements()){
			TBTeamItem item = itemEnum.nextElement();
			synchronized (item) {
				//不能加入自己已经打过的队伍
				if(null != item.findMember(userID)) continue;
				if(item.isCanFreeJion() && !item.isSelecting() && !item.isFull()){
					jionAble.add(item);
				}
			}
		}
		if(jionAble.size() == 0) return null;
		for(int i = 0; i < jionAble.size() && i < 5; i++){
			// 该循环一般只会执行一次(只有当发生并发问题的时候会有多次执行)
			int index = (int)(Math.random() * jionAble.size());
			TBTeamItem result = jionAble.get(index);
			synchronized (result) {
				//再判断一次，是防止检索其它的时候，队伍数据有变化
				if(result.isCanFreeJion() && !result.isSelecting() && !result.isFull()){
					result.setSelecting(true);
					return result;
				}
			}
		}
		return null;
	}
	
	public boolean removeTeam(TBTeamItem teamItem){
		synchronized (teamItem) {
			if(!teamItem.removeAble()) return false;
			if(TBTeamItemHolder.getInstance().removeTeam(teamItem)) return true;
			else TBTeamItemHolder.getInstance().updateTeam(teamItem);
			return false;
		}
	}
	
	/**
	 * 几种加入队伍方式的通用方法
	 * @param player
	 * @param canJionTeam
	 * @throws JoinTeamException
	 */
	public void joinTeam(Player player, TBTeamItem canJionTeam) throws JoinTeamException {
		//脱离当前的队伍
		UserTeamBattleDataMgr.getInstance().leaveTeam(player.getUserId());
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		TeamMember tMem = new TeamMember();
		tMem.setUserID(player.getUserId());
		tMem.setUserName(player.getUserName());
		tMem.setState(TBMemberState.Ready);
		synchronized (canJionTeam) {
			if(!canJionTeam.addMember(tMem)){
				throw new JoinTeamException("加入失败");
			}
			TBTeamItemHolder.getInstance().updateTeam(canJionTeam);
		}
		utbData.setTeamID(canJionTeam.getTeamID());
		utbData.setMemPos("");
		UserTeamBattleDataHolder.getInstance().update(player, utbData);
		UserTeamBattleDataHolder.getInstance().synData(player);
		TBTeamItemMgr.getInstance().synData(canJionTeam.getId());
	}
	
	/**
	 * 每日重置，清除所有的队伍数据
	 * @param exeTime 假定的执行时间（跨多天的时候，执行的时间，和所属于的应该执行时间是不一样的）
	 */
	public void dailyReset(long exeTime){
		long lastRefreshTime = 0;
		ServerCommonData scdData = ServerCommonDataHolder.getInstance().get();
		if(null != scdData) lastRefreshTime = scdData.getTbLastRefreshTime();
		if(DateUtils.isResetTime(TeamBattleConst.DAILY_REFRESH_HOUR, 0, 0, lastRefreshTime)){
			for(TeamCfg cfg : TeamCfgDAO.getInstance().getAllCfg()){
				TBTeamItemHolder.getInstance().getItemStore(cfg.getId()).clearAllRecords();
			}
			if(null != scdData) {
				scdData.setTbLastRefreshTime(exeTime);
				scdData.teamBattleDailyReset();
				ServerCommonDataHolder.getInstance().update(scdData);
			}
		}
	}
}
