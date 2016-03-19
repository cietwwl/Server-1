package com.rw.netty;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import com.bm.login.ZoneBM;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.zone.TableZoneInfo;

public class ServerConfig {

	private String zoneId;
	
	//一个服一个区
	private TableZoneInfo zoneInfo;

	
	public static ServerConfig getInstance(){
		return SpringContextUtil.getBean("serverConfig");
	} 
	
	public void init(){
		
		zoneInfo = ZoneBM.getInstance().getTableZoneInfo(Integer.valueOf(zoneId.trim()));
		String serverIp = zoneInfo.getIntranetIp();
//		if(!checkIp(serverIp)){
//			throw(new RuntimeException("严重错误，启动失败。 zone 配置的serverIp不是本机Ip。zoneId:"+zoneId+" serverIp:"+serverIp));
//		}
	}

	private boolean checkIp(String serverIp) {
		Enumeration<NetworkInterface> networkInterfaces;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface netTmp = (NetworkInterface) networkInterfaces.nextElement();
				
				Enumeration<InetAddress> inetAddressesTmp = netTmp.getInetAddresses();
				while(inetAddressesTmp.hasMoreElements()){
					InetAddress targetTmp = inetAddressesTmp.nextElement();
					if(targetTmp.getHostAddress()!=null && targetTmp.getHostAddress().contains(serverIp)){
						return true;
					}
					
				}
				
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		
		return false;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public TableZoneInfo getServeZoneInfo(){
		return zoneInfo;
	}
	public String getServerIp(){
		return zoneInfo.getServerIp();
	}
	public String getServerPort(){
		return zoneInfo.getPort();
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getZoneId(){
		return ServerConfig.getInstance().getServeZoneInfo().getZoneId();
	}
	
	
	
}
