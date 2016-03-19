//package com.playerdata;
//
//import com.bm.arena.ArenaBM;
//import com.rw.service.Email.EmailUtils;
//import com.rwbase.dao.arena.ArenaInfoCfgDAO;
//import com.rwbase.dao.arena.ArenaPrizeCfgDAO;
//import com.rwbase.dao.arena.TableArenaDataDAO;
//import com.rwbase.dao.arena.pojo.ArenaInfoCfg;
//import com.rwbase.dao.arena.pojo.TableArenaData;
//
//public class ArenaMgr extends IDataMgr {
//
//	private static final int FIGHTING_TIMEOUT = 100000;
//	private TableArenaDataDAO tableArenaDataDAO = TableArenaDataDAO.getInstance();
//	private TableArenaData tableArenaData;
//	private boolean fighting;// 0不在战斗，1是战斗中
//	private long lastStateTime;
//
//	public void init(IRole pOwner) {
//		initPlayer(pOwner);
//		tableArenaData = tableArenaDataDAO.get(pOwner.m_strRoleId);
//		if (tableArenaData == null)
//			return;
//	}
//
//	@Override
//	public boolean save() {
//		if (tableArenaData != null) {
//			return tableArenaDataDAO.update(tableArenaData);
//		}
//		return false;
//	}
//
//	public void clearAtenaData() {
//		tableArenaData = null;
//	}
//
//	public TableArenaData getMyArenaData() {
//		if (tableArenaData == null) {
//			tableArenaData = tableArenaDataDAO.get(m_pOwner.m_strRoleId);
//		}
//		return tableArenaData;
//	}
//
//	// 重置次数
//	public void resetDataInNewDay() {
//		if (tableArenaData == null) {
//			return;
//		}
//		ArenaInfoCfg infoCfg = ArenaInfoCfgDAO.getInstance().getArenaInfo();
//		tableArenaData.setRemainCount(infoCfg.getCount());
//		save();
//	}
//
//	// 奖励结算
//	public void arenaDailyPrize() {
//		if (tableArenaData == null) {
//			return;
//		}
//		String strPrize = ArenaPrizeCfgDAO.getInstance().getArenaPrizeCfgByPlace(tableArenaData.getPlace());
//		EmailUtils.sendEmail(tableArenaData.getUserId(), "10010", strPrize);
//	}
//
//	// 更新战斗状态
//	public void resetInfoState() {
//		if (tableArenaData == null) {
//			return;
//		}
//		TableArenaInfo me = ArenaBM.getInstance().getArenaInfoFromList(tableArenaData);
//		this.fighting = false;
//		this.lastStateTime = 0;
//		TableArenaInfoDAO.getInstance().update(me);
//	}
//
//	public boolean isFighting() {
//		return fighting;
//	}
//
//	public long getLastStateTime() {
//		return lastStateTime;
//	}
//
//	/**
//	 * 检查战斗状态是否超时，返回是否在战斗中
//	 * @return
//	 */
//	public boolean adjustTimeOutState(){
//		if(fighting && (System.currentTimeMillis() - this.lastStateTime) > FIGHTING_TIMEOUT){
//			fighting = false;
//		}
//		return fighting;
//	}
//
//	public void setFighting(long timeMillis){
//		this.lastStateTime = timeMillis;
//		this.fighting = true;
//	}
//	
//	public void setNotFighting(){
//		this.fighting = false;
//		this.lastStateTime = 0;
//	}
//	
//	public void saveRecord() {
//	}
//}
