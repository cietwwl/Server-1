//package com.playerdata;
//
//public class PeakArenaMgr extends IDataMgr {
//
//	private int m_fightState = 0;//0不在战斗，1是战斗中
//	private long m_fightTime;	
//	private String m_enemyUserId;
//	
//	public int getFightState() {
//		return m_fightState;
//	}
//
//	public void setFightState(int m_fightState) {
//		this.m_fightState = m_fightState;
//	}
//	
//	public long getfightTime() {
//		return m_fightTime;
//	}
//
//	public void setfightTime(long m_fightTime) {
//		this.m_fightTime = m_fightTime;
//	}
//
//	public String getEnemyUserId() {
//		return m_enemyUserId;
//	}
//
//	public void setEnemyUserId(String m_enemyUserId) {
//		this.m_enemyUserId = m_enemyUserId;
//	}
//
//	public void init(IRole pOwner)
//	{
//		initPlayer(pOwner);
////		myPeakArenaData = tablePeakArenaDataDAO.get(pOwner.m_strRoleId);
////		if(myPeakArenaData == null) return;
////		myPeakArenaRecord = tablePeakArenaRecordDAO.get(pOwner.m_strRoleId);
////		updateGainScore();
//	}
//	
//	@Override
//	public boolean save() 
//	{
////		if(myPeakArenaData != null){
////			 return tablePeakArenaDataDAO.update(myPeakArenaData);
////		}
//		return false;
//	}
//	
////	public void clearAtenaData()
////	{
////		myPeakArenaData = null;
////	}
//	
////	public TablePeakArenaData getMyArenaData()
////	{
////		if(myPeakArenaData == null){
////			myPeakArenaData = tablePeakArenaDataDAO.get(m_pOwner.m_strRoleId);
////		}
////		return myPeakArenaData;
////	}
//	
////	public List<PeakRecordInfo> getMyArenaRecordList()
////	{
////		if(myPeakArenaRecord == null){
////			myPeakArenaRecord= new TablePeakArenaRecord();
////			myPeakArenaRecord.setUserId(m_pPlayer.m_strRoleId);
////			m_MyRecordList = new ArrayList<PeakRecordInfo>();
////			myPeakArenaRecord.setRecordList(m_MyRecordList);
////			saveRecord();
////		}
////		if(m_MyRecordList == null){
////			m_MyRecordList = myPeakArenaRecord.getRecordList();
////		}
////		return m_MyRecordList;
////	}
////	
////	public static void addOthersRecord(String userId,PeakRecordInfo record)
////	{
////		TablePeakArenaRecord table = TablePeakArenaRecordDAO.getInstance().get(userId);
////		List<PeakRecordInfo> list;
////		if(table == null){
////			table = new TablePeakArenaRecord();
////			table.setUserId(record.getUserId());
////			list = new ArrayList<PeakRecordInfo>();
////			list.add(record);
////			table.setRecordList(list);
////		}
////		else{
////			list = table.getRecordList();
////			list.add(record);
////			if(list.size() > 20){
////				list.remove(0);
////			}
////		}
////		TablePeakArenaRecordDAO.getInstance().update(table);
////	}
////	
////	public void saveRecord()
////	{
////		if(myPeakArenaRecord != null){
////			tablePeakArenaRecordDAO.update(myPeakArenaRecord);
////		}
////	}
////	
////	public void addMyArenaRecord(PeakRecordInfo record)
////	{
////		if(m_MyRecordList == null){
////			m_MyRecordList = getMyArenaRecordList();
////		}
////		m_MyRecordList.add(record);
////		if(m_MyRecordList.size() > 20){
////			m_MyRecordList.remove(0);
////		}
////	}
////	
////	public void resetDataInNewDay()
////	{
////		ArenaInfoCfg infoCfg = ArenaInfoCfgDAO.getInstance().getArenaInfo();
////		myPeakArenaData.setRemainCount(infoCfg.getCount());
////		save();
////	}
////	
////	public TablePeakArenaData switchTeam()
////	{
////		PeakArenaBM.getInstance().getPeakArenaData(userId);
////		Map<Integer, TeamData> teamMap = myPeakArenaData.getTeamMap();
////		TeamData team1 = teamMap.get(1);
////		teamMap.put(1, teamMap.get(2));
////		teamMap.put(2, teamMap.get(3));
////		teamMap.put(3, team1);
////		tablePeakArenaDataDAO.update(myPeakArenaData);
////		return myPeakArenaData;
////	}
////	
////	
////	public TablePeakArenaData gainScore()
////	{
////		TablePeakArenaData myPeakArenaData = tablePeakArenaDataDAO.get(m_pOwner.m_strRoleId);
////		int addValue = myPeakArenaData.getGainScore();
////		myPeakArenaData.setGainScore(0);
////		addScore(addValue);
////		return myPeakArenaData;
////	}
////	
////	public TablePeakArenaData addScore(int value)
////	{
////		String userId = m_pOwner.m_strRoleId;
////		TablePeakArenaData myPeakArenaData = tablePeakArenaDataDAO.get(m_pOwner.m_strRoleId);
////		//Ranking底层保证不为null
////		Ranking ranking = RankingFactory.getRanking(RankType.PEAK_ARENA);
////		RankingEntry<Integer, PeakAreanExtendedAttribute> entry = ranking.getRankingEntry(userId);
////		if(entry == null){
////			entry = ranking.addOrUpdateRankingEntry(userId, ArenaConstant.PEAK_AREAN_MIN_SCORE, null);
////		}
////		int score = entry.getComparable();
////		int newScore;
////		if(value < 0){
////			int minScore = ArenaConstant.PEAK_AREAN_MIN_SCORE;
////			if(score == minScore){
////				return myPeakArenaData;
////			}
////			newScore = score - value;
////			if(newScore < minScore){
////				newScore = minScore;
////			}
////		}else{
////			newScore = score + value;
////		}
////		ranking.updateRankingEntry(entry, newScore);
////		
////		int place = ranking.getRanking(userId);
////		if(myPeakArenaData.getMaxPlace() > place){
////			myPeakArenaData.setMaxPlace(place);
////		}
////		
////		return myPeakArenaData;
////	}
////	
////	public void checkFightState()
////	{
////		if(m_fightState == 0) return;
////		PeakArenaHandler.getInstance().dealFightResult(m_pPlayer, m_enemyUserId, 0);
////	}
//	
//	public void saveRecord(){}
//	public void checkFightState(){}
//	public void updateGainScore(){}
//	public void resetDataInNewDay(){}
//}
