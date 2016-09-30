package com.playerdata.teambattle.manager;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bm.robot.RandomData;
import com.common.serverdata.ServerCommonData;
import com.common.serverdata.ServerCommonDataHolder;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.teambattle.bm.TeamBattleConst;
import com.playerdata.teambattle.cfg.TeamCfg;
import com.playerdata.teambattle.cfg.TeamCfgDAO;
import com.playerdata.teambattle.data.TBTeamItem;
import com.playerdata.teambattle.data.TBTeamItemHolder;
import com.playerdata.teambattle.data.TBTeamNotFullContainer;
import com.playerdata.teambattle.data.TeamMember;
import com.playerdata.teambattle.data.UserTeamBattleData;
import com.playerdata.teambattle.data.UserTeamBattleDataHolder;
import com.playerdata.teambattle.dataException.JoinTeamException;
import com.playerdata.teambattle.dataException.NoTeamException;
import com.playerdata.teambattle.dataForClient.StaticMemberTeamInfo;
import com.playerdata.teambattle.enums.TBMemberState;
import com.rw.fsutil.util.DateUtils;

public class TBTeamItemMgr{
	private HashMap<String, TBTeamNotFullContainer> tbContainerMap = new HashMap<String, TBTeamNotFullContainer>();
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
	
	/**
	 * 启动服务器的时候，初始化可选择的队伍列表
	 */
	public void initNotFullTeam(){
		List<TeamCfg> teamCfgs = TeamCfgDAO.getInstance().getAllCfg();
		if(null == teamCfgs || teamCfgs.isEmpty()) return;
		for(TeamCfg cfg : teamCfgs){
			List<String> jionAbleIDs = new ArrayList<String>();
			Enumeration<TBTeamItem> itemEnum = TBTeamItemHolder.getInstance().getItemStore(cfg.getId()).getEnum();
			while(itemEnum.hasMoreElements()){
				TBTeamItem item = itemEnum.nextElement();
				if(item.isCanFreeJion() && !item.isFull()){
					jionAbleIDs.add(item.getId());
				}
			}
			TBTeamNotFullContainer container = new TBTeamNotFullContainer(jionAbleIDs);
			tbContainerMap.put(cfg.getId(), container);
		}
	}
	
	/**
	 * 当有队员变动的时候，通知可选择的列表
	 * @param teamItem
	 */
	public void changeTeamSelectable(TBTeamItem teamItem){
		TBTeamNotFullContainer container = tbContainerMap.get(teamItem.getHardID());
		if(null == container) return;
		synchronized (teamItem) {
			if(teamItem.isFull() || teamItem.removeAble()) {
				container.notifyTeamRemove(teamItem.getId());
			}else{
				container.notifyTeamAdd(teamItem.getId());
			}
		}
	}
	
	private void setTeamMemberTeams(TBTeamItem teamItem){
		if(null == teamItem) return;
		List<StaticMemberTeamInfo> memTeams = new ArrayList<StaticMemberTeamInfo>();
		List<TeamMember> members = teamItem.getMembers();
		for(TeamMember member : members){
			if(member.isRobot()){
				//TODO 根据robotId获取
				StaticMemberTeamInfo teamInfo = UserTeamBattleDataMgr.getInstance().getRobotStaticTeamInfo(member);
				memTeams.add(teamInfo);
			}else{
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
		}
		teamItem.setTeamMembers(memTeams);
	}
	
	/**
	 * 快速加入一个队伍
	 * @param player
	 * @param hardID
	 * @return
	 * @throws NoTeamException 
	 * @throws JoinTeamException 
	 */
	public void quickJionTeam(Player player, String hardID) throws NoTeamException, JoinTeamException{
		TBTeamNotFullContainer container = tbContainerMap.get(hardID);
		if(null == container) {
			container = new TBTeamNotFullContainer(null);
			tbContainerMap.put(hardID, container);
			throw new NoTeamException("没有队伍可以加入");
		}
		for(int i = 0; i < 5; i++){
			String suitTeamID = container.getRandomTeam();
			if(StringUtils.isBlank(suitTeamID)) throw new NoTeamException("没有队伍可以加入");
			TBTeamItem suitItem = TBTeamItemHolder.getInstance().getItem(hardID, suitTeamID);
			if(null == suitItem) throw new NoTeamException("没有队伍可以加入");
			if(null != suitItem.findMember(player.getUserId())) continue;
			synchronized (suitItem) {
				//再判断一次，是防止检索其它的时候，队伍数据有变化
				if(suitItem.isCanFreeJion() && !suitItem.isFull()){
					try {
						joinTeam(player, suitItem);
						return;
					} catch (JoinTeamException e) {
						GameLog.info("组队战", null, "快速加入队伍失败一次", e);
					}
				}
			}
		}
		throw new JoinTeamException("加入失败");
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
		changeTeamSelectable(canJionTeam);
	}
	
	/**
	 * 队伍中添加一个机器人
	 * @param player
	 * @param canJionTeam
	 * @param robot
	 * @throws JoinTeamException
	 */
	public void addRobot(Player player, TBTeamItem canJionTeam, StaticMemberTeamInfo robot, RandomData randomData) throws JoinTeamException {
		TeamMember tMem = new TeamMember();
		tMem.setUserID(robot.getUserID());
		tMem.setUserName(robot.getUserStaticTeam().getPlayerName());
		tMem.setState(TBMemberState.Ready);
		tMem.setRandomData(randomData);
		tMem.setRobot(true);
		synchronized (canJionTeam) {
			if(!canJionTeam.addMember(tMem)){
				throw new JoinTeamException("加入失败");
			}
			TBTeamItemHolder.getInstance().updateTeam(canJionTeam);
		}
		TBTeamItemMgr.getInstance().synData(canJionTeam.getId());
		changeTeamSelectable(canJionTeam);
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
				TBTeamNotFullContainer container = tbContainerMap.get(cfg.getId());
				if(null != container){
					container.clearRecord();
				}
			}
			if(null != scdData) {
				scdData.setTbLastRefreshTime(exeTime);
				scdData.teamBattleDailyReset();
				ServerCommonDataHolder.getInstance().update(scdData);
			}
		}
	}

	/**
	 * 创建一个新的队伍
	 * @param teamItem
	 */
	public void addNewTeam(TBTeamItem teamItem) {
		TBTeamItemHolder.getInstance().addNewTeam(teamItem);
		changeTeamSelectable(teamItem);
	}

	/**
	 * 获取一定数量的可加入队伍
	 * @param player
	 * @param hardID
	 * @return
	 */
	public List<String> getCanJionTeams(Player player, String hardID, int getCount) {
		TBTeamNotFullContainer container = tbContainerMap.get(hardID);
		if(null == container) {
			return new ArrayList<String>();
		}
		return container.getRandomTeam(getCount);
	}
}
