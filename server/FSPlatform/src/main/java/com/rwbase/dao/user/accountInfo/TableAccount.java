package com.rwbase.dao.user.accountInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.dao.annotation.NonSave;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rwproto.PlatformGSMsg.UserInfoResponse;



/**
 * 用户帐号，登录的时候用。
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_account")
public class TableAccount{

	@Id
	private String accountId;   //
	private String openAccount;    //第三方的登录帐号
	
	
	private String password;
	
	private String oid; //用户的oid
	
	private boolean  binded; // 是否已绑定，自动注册的要手动绑定一次
	
	private String channelId;
	
	private String pub;
	
	private long lastLoginTime = System.currentTimeMillis(); // 上次登录时间
	
	private long registerTime = 0L; // 注册时间
	
	@NonSave
	private AccountLoginRecord record;   //记录玩家的登陆信息
	
	private String imei;
	@SaveAsJson
	private List<Integer> lastLoginList = new ArrayList<Integer>();
	@SaveAsJson
	private List<Integer> iphoneLastLoginList = new ArrayList<Integer>();
	@SaveAsJson
	private Map<Integer, UserZoneInfo> userZoneInfoMap = new HashMap<Integer, UserZoneInfo>();
	
	public  TableAccount(){}
		
	
	public TableAccount(String account, String password){
		this.accountId = account;
		this.password = password;
	}

	public void addUserZoneInfo(UserZoneInfo zoneInfo){
		if(!this.userZoneInfoMap.containsKey(zoneInfo.getZoneId())){
			this.userZoneInfoMap.put(zoneInfo.getZoneId(), zoneInfo);
		}
		
		addToLastLogin(zoneInfo, this.lastLoginList);
	}

	private void addToLastLogin(UserZoneInfo ZoneInfo,
			List<Integer> lastLoginListP) {
		if (lastLoginListP.size() == 0) {
			lastLoginListP.add(0, ZoneInfo.getZoneId());
		} else {
			int lastZoneId = lastLoginListP.get(0);
			if (lastZoneId != ZoneInfo.getZoneId()) {
				lastLoginListP.add(0, lastZoneId);
				if (lastLoginListP.size() > 2) {
					lastLoginListP.remove(2);
				}
			}
		}
	}
	
	public UserZoneInfo getUserZoneInfoByZoneId(int zoneId){
		UserZoneInfo userZoneInfo = getZoneIdInUserZoneInfoMap(zoneId);
		return userZoneInfo;
	}
	
	public void addUserZoneInfo(int zoneId){
		UserZoneInfo ZoneInfo = new UserZoneInfo();
		ZoneInfo.setZoneId(zoneId);
		addUserZoneInfo(ZoneInfo);
	}
	
	public void addUserZoneInfo(int zoneId, UserInfoResponse userInfoResponse){
		UserZoneInfo ZoneInfo = new UserZoneInfo();
		ZoneInfo.setZoneId(zoneId);
		ZoneInfo.setLevel(userInfoResponse.getLevel());
		ZoneInfo.setVipLevel(userInfoResponse.getVipLevel());
		ZoneInfo.setHeadImage(userInfoResponse.getHeadImage());
		ZoneInfo.setLastLoginMillis(userInfoResponse.getLastLoginTime());
		ZoneInfo.setCareer(userInfoResponse.getCareer());
		ZoneInfo.setUserName(userInfoResponse.getUserName());
		addUserZoneInfo(ZoneInfo);
	}

	public UserZoneInfo getLastLogin(boolean isIPhone) {
		UserZoneInfo ZoneInfo = null;
		if(isIPhone && !iphoneLastLoginList.isEmpty()) {
			int zoneId = iphoneLastLoginList.get(0);
			ZoneInfo = getZoneIdInUserZoneInfoMap(zoneId);
		}else if(!isIPhone && !lastLoginList.isEmpty()){
			int zoneId = lastLoginList.get(0);
			ZoneInfo = getZoneIdInUserZoneInfoMap(zoneId);
		}
		return ZoneInfo;
	}
	
	public void setLastLogin(boolean isIPhone, int zoneId){
		if (isIPhone) {
			iphoneLastLoginList.clear();
			iphoneLastLoginList.add(zoneId);
		} else {
			lastLoginList.clear();
			lastLoginList.add(zoneId);
		}
	}
	
	public UserZoneInfo getZoneIdInUserZoneInfoMap(int zoneId){
		return this.userZoneInfoMap.get(zoneId);
	}

	public Map<Integer, Object> getUserZoneIDList(){
		Map<Integer, Object> idList = new HashMap<Integer, Object>();
		for (Iterator<Entry<Integer, UserZoneInfo>> iterator = userZoneInfoMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, UserZoneInfo> next = iterator.next();
			idList.put(next.getKey(), next.getValue());
		}
		return idList;
	}
	
	public String getAccount() {
		return accountId;
	}

	public void setAccount(String account) {
		this.accountId = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Integer> getLastLoginList(boolean isIphone) {
		if(isIphone) {
			return iphoneLastLoginList;
		}
		return lastLoginList;
	}
	
	public List<Integer> getLastLoginList() {
		return lastLoginList;
	}

	public void setLastLoginList(List<Integer> lastLoginList) {
		this.lastLoginList = lastLoginList;
	}

	public String getOpenAccount() {
		return openAccount;
	}

	public void setOpenAccount(String openAccount) {
		this.openAccount = openAccount;
	}

	public long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public long getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(long registerTime) {
		this.registerTime = registerTime;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public List<Integer> getIphoneLastLoginList() {
		return iphoneLastLoginList;
	}

	public void setIphoneLastLoginList(List<Integer> iphoneLastLoginList) {
		this.iphoneLastLoginList = iphoneLastLoginList;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public boolean isBinded() {
		return binded;
	}

	public void setBinded(boolean binded) {
		this.binded = binded;
	}


	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getPub() {
		return pub;
	}

	public void setPub(String pub) {
		this.pub = pub;
	}
	
	public List<UserZoneInfo> getUserZoneInfoList(){
		return new ArrayList<UserZoneInfo>(userZoneInfoMap.values());
	}


	public AccountLoginRecord getRecord() {
		return record;
	}


	public void setRecord(AccountLoginRecord record) {
		this.record = record;
	}
}
