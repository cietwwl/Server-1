package com.playerdata.dataEncode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.dataEncode.Node.NodeMaper;
import com.playerdata.dataEncode.Node.NodeMaperMgr;
import com.playerdata.hero.core.RoleBaseInfoImpl;
import com.rwbase.common.MD5;

public class DataEncoder {
	
	private static Map<Class<?>, ClassInfo4Encode> classInfoMap = new ConcurrentHashMap<Class<?>, ClassInfo4Encode>();
	
	public static boolean verify(ArmyInfo target, String md5){
		
		String newMd5 = encodeArmyInfo(target);
		return md5.equals(newMd5);
		
	}
	public static String encodeArmyInfo(ArmyInfo armyInfo){
		return encode(armyInfo, NodeMaperMgr.getInstance().getArmyInfo());
	}
	
	private static String encode(Object target,NodeMaper nodeMaper){
		
		Class<? extends Object> tagetClass = target.getClass();
		ClassInfo4Encode classInfo = getByClass(tagetClass,nodeMaper);
		String md5ofStr=null;
		try {
			String strToEncode = classInfo.toStr(target).toLowerCase();
			System.out.println(strToEncode);
			md5ofStr = MD5.getMD5ofStr(strToEncode).toLowerCase();
		} catch (Exception e) {			
			GameLog.error(LogModule.Util, tagetClass.toString(), "DataEncoder[encode] erro:", e);
		}
		
		return md5ofStr;
		
	}
	
	private static ClassInfo4Encode getByClass(Class<?> clazz,NodeMaper nodeMaper){

		ClassInfo4Encode classInfo = classInfoMap.get(clazz);
		if(classInfo == null){
			try {				
				classInfo = new ClassInfo4Encode(clazz,nodeMaper);
				classInfoMap.put(clazz, classInfo);
				nodeMaper.printZero();
			} catch (Throwable e) {
				GameLog.error(LogModule.Util, clazz.toString(), "DataEncoder[getByClass] erro:", e);
			}
		}
		return classInfo;
	}
	
	
	public static void main(String[] args) {
		
		ArmyInfo armyinfo = new ArmyInfo();
		armyinfo.setPlayerName("pName");
		armyinfo.setPlayerHeadImage("pimage");
		armyinfo.setGuildName("groupName");
		
		RoleBaseInfoImpl roleBaseInfo = new RoleBaseInfoImpl();
		roleBaseInfo.setCareerType(0);
		roleBaseInfo.setExp(1000);
		roleBaseInfo.setId("id");
		roleBaseInfo.setLevel(10);
		roleBaseInfo.setModeId(1001);
		roleBaseInfo.setQualityId("10");
		roleBaseInfo.setStarLevel(5);
		roleBaseInfo.setTemplateId("templateId-100");
		
		ArmyHero hero = new ArmyHero();
		hero.setFighting(100);
		hero.setPlayer(true);
		hero.setRoleBaseInfo(roleBaseInfo);
		
		ArmyHero heroB = new ArmyHero();
		heroB.setFighting(100);
		heroB.setPlayer(true);
		heroB.setRoleBaseInfo(roleBaseInfo);
		
		
		List<ArmyHero>  heroList = new ArrayList<ArmyHero>();
		heroList.add(hero);
		heroList.add(heroB);
		
		
		armyinfo.setPlayer(hero);
		armyinfo.setHeroList(heroList);
		
		String encode = DataEncoder.encodeArmyInfo(armyinfo);
		
		System.out.println("md5:");
		System.out.println(encode);
	}
	
}
