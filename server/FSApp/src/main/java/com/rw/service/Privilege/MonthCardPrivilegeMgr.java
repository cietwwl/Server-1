package com.rw.service.Privilege;

import java.util.Collection;
import java.util.HashMap;

import com.playerdata.Player;
import com.playerdata.charge.ChargeMgr;
import com.playerdata.charge.cfg.ChargeTypeEnum;
import com.rw.fsutil.common.stream.IStream;
import com.rw.fsutil.common.stream.StreamImpl;
import com.rw.service.Privilege.datamodel.ChargeTypePriority;

public class MonthCardPrivilegeMgr{
	private static MonthCardPrivilegeMgr instance = new MonthCardPrivilegeMgr();

	public static MonthCardPrivilegeMgr getShareInstance() {
		return instance;
	}

	private final String[] monthLevelStr = { "none","normal", "vip" };
	private final HashMap<Integer,String> previousLevelMap;
	private MonthCardPrivilegeMgr() {
		previousLevelMap = new HashMap<Integer,String>();
		previousLevelMap.put(0, ChargeTypePriority.vipPrefix + "0");
		previousLevelMap.put(1, ChargeTypePriority.vipPrefix + "0");
		previousLevelMap.put(2, ChargeTypePriority.monthPrefix + monthLevelStr[1]);

		cache = new HashMap<String, MonthCardPrivilegeMgr.PriProvider>();
	}

	private HashMap<String,PriProvider> cache;
	public void ClearCache(){
		if (cache != null){
			Collection<PriProvider> pros = cache.values();
			for (PriProvider priProvider : pros) {
				priProvider.close();
			}
			cache.clear();
			cache = null;
		}
	}
	
	//TODO 监听玩家下线消息，清理内存，否则会内存泄漏
	public void onPlayerOffLine(Player player){
		if (cache != null){
			cache.remove(player.getUserId());
		}
	}
	
	//TODO 改为放在player里面，避免内存泄漏
	public IPrivilegeProvider getPrivilige(Player player) {
		if (cache == null){
			cache = new HashMap<String, MonthCardPrivilegeMgr.PriProvider>();
		}
		String userId = player.getUserId();
		PriProvider impl = cache.get(userId);
		if (impl == null){
			impl = new PriProvider(player);
			cache.put(userId, impl);
		}
		return impl;
	}
	
	public String getChargeTypeStr(int level){
		String levelName = "";
		if (0 <= level && level < monthLevelStr.length){
			levelName = monthLevelStr[level];
		}
		return ChargeTypePriority.monthPrefix + levelName;
	}

	public int extractMonthLevel(String chargeTy) {
		if (chargeTy == null || !chargeTy.startsWith(ChargeTypePriority.monthPrefix)) {
			return -1;
		}
		String monthVal = chargeTy.substring(chargeTy.indexOf(ChargeTypePriority.monthPrefix) + ChargeTypePriority.monthPrefix.length());
		for (int i = 0; i < monthLevelStr.length; i++) {
			if (monthLevelStr[i].equals(monthVal)) {
				return i;
			}
		}
		return -1;
	}
	
	public int getBestMatchCharge(String[] chargeSources,int currentLevel){
		if (currentLevel <0 || chargeSources == null || chargeSources.length <= 0) return -1;
		int maxLevel = -1;
		int maxIndex = -1;
		for (int i = 0; i < chargeSources.length; i++) {
			String src = chargeSources[i];
			int lvl = extractMonthLevel(src);
			if (lvl <= currentLevel && lvl > maxLevel){
				maxLevel = lvl;
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	public String guessPreviousChargeLevel(String chargeType){
		int monthLevel = extractMonthLevel(chargeType);
		if (monthLevel >= 0){
			//普通月卡的前一档定义为VIP0
			//return (monthLevel > 1 ? ChargeTypePriority.monthPrefix + monthLevelStr[monthLevel-1] : ChargeTypePriority.vipPrefix + "0");
			String pre = previousLevelMap.get(monthLevel);
			if (pre != null){
				return pre;
			}
		}
		//无法估计前一档充值等级！
		return chargeType;
	}

	public void checkPrivilege(Player player) {
		// 每天凌晨查询月卡状态
		boolean normalOn = ChargeMgr.getInstance().isValid(player,ChargeTypeEnum.MonthCard);
		boolean vipOn = ChargeMgr.getInstance().isValid(player,ChargeTypeEnum.VipMonthCard);
		signalMonthCardChange(player,ChargeTypeEnum.MonthCard,normalOn);
		signalMonthCardChange(player,ChargeTypeEnum.VipMonthCard,vipOn);
	}
	
	public void signalMonthCardChange(Player player,ChargeTypeEnum type,boolean isOn){
		if (cache == null) return;
		PriProvider impl = cache.get(player.getUserId());
		if (impl != null){
			switch (type) {
			case MonthCard:
				if (impl.normal ^ isOn){
					impl.fireNormal(player,isOn);
				}
				break;
			case VipMonthCard:
				if (impl.vip ^ isOn){
					impl.fireVip(player,isOn);
				}
				break;
			default:
				break;
			}
		}
	}
	
	private static class PriProvider implements IPrivilegeProvider{

		public void fireVip(Player player, boolean isOn) {
			vip = isOn;
			if (stream != null){
				stream.fire(this);
			}
		}

		public void fireNormal(Player player, boolean isOn) {
			normal = isOn;
			if (stream != null){
				stream.fire(this);
			}
		}

		public void close(){
			stream.close();
			stream = null;
		}
		
		public PriProvider(Player player) {
			normal = ChargeMgr.getInstance().isValid(player,ChargeTypeEnum.MonthCard);
			vip = ChargeMgr.getInstance().isValid(player,ChargeTypeEnum.VipMonthCard);
		}

		public boolean normal = false;
		public boolean vip = false;
		public StreamImpl<IPrivilegeProvider> stream=new StreamImpl<IPrivilegeProvider>(); 
		@Override
		public IStream<IPrivilegeProvider> getPrivilegeProvider() {
			return stream;
		}

		@Override
		public int getBestMatchCharge(String[] chargeSources) {
			int currentLevel = getLevel();
			return MonthCardPrivilegeMgr.getShareInstance().getBestMatchCharge(chargeSources, currentLevel);
		}

		public int getLevel(){
			int normalLvl = normal ? 1 : 0;
			int vipLvl = vip ? 2 : 0;
			return Math.max(normalLvl, vipLvl);
		}
		
		@Override
		public String getCurrentChargeType() {
			return MonthCardPrivilegeMgr.getShareInstance().getChargeTypeStr(getLevel());
		}

		@Override
		public boolean reachChargeLevel(String chargeType) {
			int level = MonthCardPrivilegeMgr.getShareInstance().extractMonthLevel(chargeType);
			if (level < 0){
				return false;
			}
			return getLevel() >= level;
		}
		
	}
}
