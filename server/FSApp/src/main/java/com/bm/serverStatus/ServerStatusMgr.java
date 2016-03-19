package com.bm.serverStatus;

import java.util.List;

import javax.print.attribute.standard.Severity;

import com.gm.task.GmEmailAll;
import com.rwbase.dao.serverData.ServerDataHolder;
import com.rwbase.dao.serverData.ServerGmEmail;
import com.rwbase.dao.serverData.ServerGmEmailHolder;


public class ServerStatusMgr {

	private static ServerStatus status = ServerStatus.CLOSE;
	
	//是否开启白名单  默认开启白名单
	private static boolean whiteListOn = true;
	
	private static ServerDataHolder dataHolder = new ServerDataHolder();
	private static ServerGmEmailHolder mailHolder = new ServerGmEmailHolder();

	public static ServerStatus getStatus() {
		return status;
	}

	public static void setStatus(ServerStatus statusP) {
		status = statusP;
	} 
	
	public static int getOnlineLimit(){
		return dataHolder.getOnlineLimit();
	}
	
	public static void setOnlineLimit(int limit){
		dataHolder.setOnlineLimit(limit);
	}
	
	public static  List<String> getWhiteList(){
		return dataHolder.getWhiteList();
	}
	
	public static void addWhite(String userId){
		dataHolder.addWhite(userId);
	}
	public static void removeWhite(String userId){
		dataHolder.removeWhite(userId);
	}
	
	public static void switchWhiteList(boolean isOn){
		whiteListOn = isOn;
	}
	
	public static boolean isWhilteListON(){
		return whiteListOn;
	}
	
	public static void setChargeOn(boolean chargeOn){
		dataHolder.setChargeOn(chargeOn);
	}
	
	public static void setLastBIStatLogTime(long lastBIStatLogTime){
		dataHolder.setLastBIStatLogTime(lastBIStatLogTime);
	}
	
	public static long getLastBIStatLogTime(){
		return dataHolder.getLastBIStatLogTime();
	}
	
	public static boolean isChargeOn(){
		return dataHolder.isChargeOn();
	}
	
	public static void addGmMail(ServerGmEmail gmEmail){
		mailHolder.addGmMail(gmEmail);
	}
	
	public static ServerGmEmail getGmMail(long taskId){
		return mailHolder.getGmMailByTaskId(taskId);
	}
	
	public static void updateGmMail(ServerGmEmail gmEmail){
		mailHolder.updateGmMail(gmEmail);
	}
	
	public static List<ServerGmEmail> getGmMails(){
		return mailHolder.getGmMailList();
	}
	
	public static long getTaskId(){
		return dataHolder.getTaskId();
	}
	
	public static void setTaskId(long taskId){
		dataHolder.setTaskId(taskId);
	}
}
