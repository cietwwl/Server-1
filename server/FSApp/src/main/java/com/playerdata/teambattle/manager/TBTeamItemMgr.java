package com.playerdata.teambattle.manager;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
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
		if(StringUtils.isNotBlank(utbData.getTeamID())){
			String hardID = TBTeamItemHolder.getInstance().getHardIDFromTeamID(utbData.getTeamID());
			TBTeamItem teamItem = TBTeamItemHolder.getInstance().getItem(hardID, utbData.getTeamID());
			if(teamItem == null) return;
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
	
	public void createNewTeam(Player player, String hardID){
		
	}
	
	public void joinTeam(Player player, String hardID){
		
	}
	
	private void setTeamMemberTeams(TBTeamItem teamItem){
		List<StaticMemberTeamInfo> memTeams = new ArrayList<StaticMemberTeamInfo>();
		if(teamItem == null) return;
		for(TeamMember member : teamItem.getMembers()){
			UserTeamBattleData utbMemData = UserTeamBattleDataHolder.getInstance().get(member.getUserID());
			if(utbMemData == null) {
				//TODO 如果为空，需要初始化
				continue;
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
}
