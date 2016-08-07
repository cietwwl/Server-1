package com.playerdata.teambattle.manager;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.playerdata.teambattle.dataForClient.StaticMemberTeamInfo;

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
			setTeamMemberTeams(teamItem);
			TBTeamItemHolder.getInstance().synData(player, teamItem);
		}
	}
	
	public void synData(String teamID){
		if(StringUtils.isNotBlank(teamID)){
			String hardID = TBTeamItemHolder.getInstance().getHardIDFromTeamID(teamID);
			TBTeamItem teamItem = TBTeamItemHolder.getInstance().getItem(hardID, teamID);
			if(teamItem == null) return;
			setTeamMemberTeams(teamItem);
			TBTeamItemHolder.getInstance().synData(teamItem);
		}
	}
	
	private void setTeamMemberTeams(TBTeamItem teamItem){
		List<StaticMemberTeamInfo> memTeams = new ArrayList<StaticMemberTeamInfo>();
		if(teamItem == null) return;
		for(TeamMember member : teamItem.getMembers()){
			UserTeamBattleData utbMemData = UserTeamBattleDataHolder.getInstance().get(member.getUserID());
			if(utbMemData == null) continue;
			if(utbMemData.getSelfTeamInfo() == null){
				Player player = PlayerMgr.getInstance().find(member.getUserID());
				if(player == null) continue;
				List<Hero> heros = player.getHeroMgr().getMaxFightingHeros();
				List<String> heroIDs = new ArrayList<String>();
				Map<String, Integer> heroPosMap = new HashMap<String, Integer>();
				for(int i = 1; i < heros.size(); i++){
					heroIDs.add(heros.get(i).getHeroData().getId());
					heroPosMap.put(heros.get(i).getHeroData().getId(), i);
				}
				StaticMemberTeamInfo teamInfo = new StaticMemberTeamInfo();
				teamInfo.setUserID(member.getUserID());
				teamInfo.setHeroPosMap(heroPosMap);
				teamInfo.setUserStaticTeam(ArmyInfoHelper.getSimpleInfo(member.getUserID(), "", (heroIDs.isEmpty() ? null : heroIDs)));
				utbMemData.setSelfTeamInfo(teamInfo);
			}
			memTeams.add(utbMemData.getSelfTeamInfo());
		}
		teamItem.setTeamMembers(memTeams);
	}
	
	public synchronized TBTeamItem getOneCanJionTeam(String hardID){
		List<TBTeamItem> jionAble = new ArrayList<TBTeamItem>();
		Enumeration<TBTeamItem> itemEnum = TBTeamItemHolder.getInstance().getItemStore(hardID).getEnum();
		while(itemEnum.hasMoreElements()){
			TBTeamItem item = itemEnum.nextElement();
			if(item.isCanFreeJion() && !item.isSelecting() && !item.isFull()){
				jionAble.add(item);
			}
		}
		if(jionAble.size() == 0) return null;
		int index = (int)(Math.random() * jionAble.size());
		TBTeamItem result = jionAble.get(index);
		result.setSelecting(true);
		return result;
	}
	
	public boolean removeTeam(TBTeamItem teamItem){
		if(!teamItem.removeAble()) return false;
		if(TBTeamItemHolder.getInstance().removeTeam(teamItem)) return true;
		else TBTeamItemHolder.getInstance().updateTeam(teamItem);
		return false;
	}
	
	/**
	 * 每日重置，清除所有的队伍数据
	 */
	public void dailyReset(){
		long lastRefreshTime = 0;
		ServerCommonData scdData = ServerCommonDataHolder.getInstance().get();
		if(null != scdData) lastRefreshTime = scdData.getTbLastRefreshTime();
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, TeamBattleConst.DAILY_REFRESH_HOUR);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		if(lastRefreshTime < cal.getTimeInMillis()){
			for(TeamCfg cfg : TeamCfgDAO.getInstance().getAllCfg()){
				TBTeamItemHolder.getInstance().getItemStore(cfg.getId()).clearAllRecords();
			}
			if(null != scdData) {
				scdData.setTbLastRefreshTime(System.currentTimeMillis());
				scdData.teamBattleDailyReset();
				ServerCommonDataHolder.getInstance().update(scdData);
			}
		}
	}
}
