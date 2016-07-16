package com.rwbase.dao.sign.pojo;

import java.util.Calendar;
import java.util.TreeMap;

import com.log.GameLog;
import com.playerdata.Player;
import com.rwbase.dao.sign.TableSignDataDAO;

public class SignDataHolder {
	
	private TableSignDataDAO tableSignDataDAO = TableSignDataDAO.getInstance();
	private String userId;
	
	//初始化
	public SignDataHolder(String userId){
		this.userId = userId;
	}
	
	public Calendar getLastUpdate(){
		return getTableSignData().getLastUpate();
	}
	
	public void setLastUpdate(Calendar calendar){
		this.getTableSignData().setLastUpate(calendar);
	}
	
	public void update(Player player){
		tableSignDataDAO.update(userId);
	}
	
	public void refreshData(){
		TableSignData tableSignData = getTableSignData();
		tableSignData.getSignDataMap().clear();
		tableSignData.setCurrentResignCount(0);
		tableSignData.setLastUpate(Calendar.getInstance());
	}
	
	public TreeMap<String, SignData> getSignDataMap(){
		return getTableSignData().getSignDataMap();
	}
	
	public int getCurrentResignCount(){
		return getTableSignData().getCurrentResignCount();
	}
	
	public void setCurrentResignCount(int value){
		getTableSignData().setCurrentResignCount(value);
	}
	
	/**
	 * 签到次数
	 * @return
	 */
	public int getSignNum(){
		return getTableSignData().getSignNum();
	}
	
	public void setSignNum(int value){
		getTableSignData().setSignNum(value);
	}
	
	/**
	 * 已领取的签到奖励id
	 * @return
	 */
	public String getAchieveSignNum(){
		return getTableSignData().getAchieveSignNum();
	}
	
	public void setAchieveSignNum(String value){
		getTableSignData().setAchieveSignNum(value);
	}
	
	/**
	 * 设置超过配置表的次数
	 * @return
	 */
	public int getOverSignNum(){
		return getTableSignData().getOverSignNum();
	}
	
	public void setOverSignNum(int value){
		getTableSignData().setOverSignNum(value);
	}
	
	public void flush(){
	}
	
	public TableSignData getTableSignData(){
		return tableSignDataDAO.get(userId);
	}
}
