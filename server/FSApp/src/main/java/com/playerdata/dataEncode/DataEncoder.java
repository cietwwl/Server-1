package com.playerdata.dataEncode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.army.ArmyInfo;
import com.playerdata.dataEncode.Node.NodeMaper;
import com.playerdata.dataEncode.Node.NodeMaperMgr;
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
			
//			System.out.println(strToEncode);
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
		
//		ArmyInfo armyinfo = new ArmyInfo();
//		armyinfo.setPlayerName("pName");
//		armyinfo.setPlayerHeadImage("pimage");
//		armyinfo.setGuildName("groupName");
//		
//		RoleBaseInfo roleBaseInfo = new RoleBaseInfo();
//		roleBaseInfo.setCareerType(0);
//		roleBaseInfo.setExp(1000);
//		roleBaseInfo.setId("id");
//		roleBaseInfo.setLevel(10);
//		roleBaseInfo.setModeId(1001);
//		roleBaseInfo.setQualityId("10");
//		roleBaseInfo.setStarLevel(5);
//		roleBaseInfo.setTemplateId("templateId-100");
//		
//		ArmyHero hero = new ArmyHero();
//		hero.setFighting(100);
//		hero.setPlayer(true);
//		hero.setRoleBaseInfo(roleBaseInfo);
//		
//		ArmyHero heroB = new ArmyHero();
//		heroB.setFighting(100);
//		heroB.setPlayer(true);
//		heroB.setRoleBaseInfo(roleBaseInfo);
//		
//		
//		List<ArmyHero>  heroList = new ArrayList<ArmyHero>();
//		heroList.add(hero);
//		heroList.add(heroB);
//		
//		
//		armyinfo.setPlayer(hero);
//		armyinfo.setHeroList(heroList);
//		
//		String encode = DataEncoder.encodeArmyInfo(armyinfo);
//		
//		System.out.println("md5:");
//		System.out.println(encode);
		
		String client = "{armymagic-{level-1|modelid-603001}|herolist-[{attrdata-{addcure-0|attackdistance-0|attackenergy-32|attackhurt-0|attackspeed-0|attackvampire-0|critical-1|criticalhurt-0|cutcrithurt-0|cutcure-0|cuthurt-0|dodge-22|dohurt-0|energy-1000|energypersecond-5|energyreceive-12|energytrans-1|hardstraight-0|hit-1|life-3343|lifegrowup-4065|lifereceive-373|movespeed-5.5|pattackgrowup-705|physicquedefgrowup-254|physiqueattack-274|physiquedef-88|reactiontime-0|resist-11|sattackgrowup-564|spiritattack-207|spiritdef-78|spiritdefgrowup-203|struckenergy-300|toughness-1|viewrange-1000|volumeradius-0}|curattrdata-{curenergy-0|curlife-3343|maxenergy-0|maxlife-0}|fighting-1478|isplayer-false|position-3|rolebaseinfo-{careertype-2|exp-0|id-201004_1|level-20|modeid-201004|qualityid-201004_2|starlevel-1}|skilllist-[{extradamage-0|level-0|order-3|skilldamage-0|skillid-20100404_1|skillrate-0}-{extradamage-0|level-1|order-0|skilldamage-41|skillid-20100401_1|skillrate-0.79}-{extradamage-0|level-1|order-1|skilldamage-55|skillid-20100402_1|skillrate-0.89}-{extradamage-0|level-1|order-2|skilldamage-0|skillid-20100403_1|skillrate-0}-{extradamage-0|level-1|order-6|skilldamage-0|skillid-20100400_1|skillrate-1}]}-{attrdata-{addcure-0|attackdistance-0|attackenergy-94|attackhurt-0|attackspeed-0|attackvampire-0|critical-1|criticalhurt-0|cutcrithurt-0|cutcure-0|cuthurt-0|dodge-1|dohurt-0|energy-1000|energypersecond-5|energyreceive-12|energytrans-1|hardstraight-0|hit-1|life-5376|lifegrowup-6514|lifereceive-598|movespeed-5.5|pattackgrowup-617|physicquedefgrowup-284|physiqueattack-237|physiquedef-93|reactiontime-0|resist-11|sattackgrowup-493|spiritattack-173|spiritdef-83|spiritdefgrowup-227|struckenergy-300|toughness-1|viewrange-1000|volumeradius-0}|curattrdata-{curenergy-0|curlife-5376|maxenergy-0|maxlife-0}|fighting-1390|isplayer-false|position-4|rolebaseinfo-{careertype-1|exp-0|id-200011_1|level-20|modeid-200011|qualityid-200011_2|starlevel-1}|skilllist-[{buffid-[20001111]|extradamage-0|level-1|order-0|selfbuffid-[20001112]|skilldamage-40|skillid-20001101_1|skillrate-0.76}-{buffid-[20001131]|extradamage-0|level-1|order-1|skilldamage-37|skillid-20001102_1|skillrate-0.7}-{extradamage-0|level-0|order-3|skilldamage-0|skillid-20001104_1|skillrate-0}-{extradamage-0|level-1|order-2|skilldamage-0|skillid-20001103_1|skillrate-0}-{extradamage-0|level-1|order-6|skilldamage-0|skillid-20001100_1|skillrate-1}]}-{attrdata-{addcure-0|attackdistance-0|attackenergy-94|attackhurt-0|attackspeed-0|attackvampire-0|critical-1|criticalhurt-0|cutcrithurt-0|cutcure-0|cuthurt-0|dodge-1|dohurt-0|energy-1000|energypersecond-5|energyreceive-25|energytrans-1|hardstraight-0|hit-1|life-3312|lifegrowup-4016|lifereceive-246|movespeed-5.5|pattackgrowup-822|physicquedefgrowup-215|physiqueattack-316|physiquedef-81|reactiontime-0|resist-11|sattackgrowup-658|spiritattack-245|spiritdef-52|spiritdefgrowup-172|struckenergy-300|toughness-1|viewrange-1000|volumeradius-0}|curattrdata-{curenergy-0|curlife-3312|maxenergy-0|maxlife-0}|fighting-1353|isplayer-false|position-1|rolebaseinfo-{careertype-5|exp-0|id-202001_1|level-20|modeid-202001|qualityid-202001_2|starlevel-1}|skilllist-[{buffid-[20200111]|extradamage-0|level-1|order-0|skilldamage-100|skillid-20200101_1|skillrate-1.42}-{extradamage-0|level-0|order-3|skilldamage-0|skillid-20200104_1|skillrate-0}-{extradamage-0|level-1|order-1|skilldamage-66|skillid-20200102_1|skillrate-0.94}-{extradamage-0|level-1|order-2|skilldamage-0|skillid-20200103_1|skillrate-0}-{extradamage-0|level-1|order-6|skilldamage-0|skillid-20200100_1|skillrate-1}]}-{attrdata-{addcure-0|attackdistance-0|attackenergy-94|attackhurt-0|attackspeed-0|attackvampire-0|critical-1|criticalhurt-0|cutcrithurt-0|cutcure-0|cuthurt-0|dodge-1|dohurt-0|energy-1000|energypersecond-5|energyreceive-25|energytrans-1|hardstraight-0|hit-1|life-3616|lifegrowup-5221|lifereceive-246|movespeed-5.5|pattackgrowup-1069|physicquedefgrowup-280|physiqueattack-369|physiquedef-84|reactiontime-0|resist-11|sattackgrowup-855|spiritattack-296|spiritdef-75|spiritdefgrowup-224|struckenergy-300|toughness-1|viewrange-1000|volumeradius-0}|curattrdata-{curenergy-0|curlife-3616|maxenergy-0|maxlife-0}|fighting-1543|isplayer-false|position-0|rolebaseinfo-{careertype-5|exp-0|id-200001_1|level-20|modeid-200001|qualityid-200001_2|starlevel-1}|skilllist-[{buffid-[20000111]|extradamage-0|level-1|order-0|skilldamage-192|skillid-20000101_1|skillrate-2.19}-{extradamage-0|level-0|order-3|skilldamage-0|skillid-20000104_1|skillrate-0}-{extradamage-0|level-1|order-1|selfbuffid-[20000121-20000122-20000123]|skilldamage-0|skillid-20000102_1|skillrate-0}-{extradamage-0|level-1|order-2|skilldamage-0|skillid-20000103_1|skillrate-0}-{extradamage-0|level-1|order-6|skilldamage-0|skillid-20000100_1|skillrate-1}]}-{attrdata-{addcure-0|attackdistance-0|attackenergy-94|attackhurt-0|attackspeed-0|attackvampire-0|critical-1|criticalhurt-0|cutcrithurt-0|cutcure-0|cuthurt-0|dodge-1|dohurt-0|energy-1000|energypersecond-5|energyreceive-37|energytrans-1|hardstraight-0|hit-1|life-2286|lifegrowup-2791|lifereceive-85|movespeed-5.5|pattackgrowup-599|physicquedefgrowup-192|physiqueattack-213|physiquedef-56|reactiontime-0|resist-11|sattackgrowup-749|spiritattack-282|spiritdef-85|spiritdefgrowup-240|struckenergy-300|toughness-1|viewrange-1000|volumeradius-0}|curattrdata-{curenergy-0|curlife-2286|maxenergy-0|maxlife-0}|fighting-1411|isplayer-false|position-2|rolebaseinfo-{careertype-4|exp-0|id-204001_1|level-20|modeid-204001|qualityid-204001_2|starlevel-1}|skilllist-[{buffid-[20400111-20400112]|extradamage-0|level-1|order-0|skilldamage-0|skillid-20400101_1|skillrate-0}-{extradamage-0|level-0|order-3|skilldamage-0|skillid-20400104_1|skillrate-0}-{extradamage-0|level-1|order-1|skilldamage-60|skillid-20400102_1|skillrate-0.94}-{extradamage-0|level-1|order-2|skilldamage-0|skillid-20400103_1|skillrate-0}-{extradamage-0|level-1|order-6|skilldamage-0|skillid-20400100_1|skillrate-1}]}]}";
		String server = "{armymagic-{level-1|modelid-603001}|herolist-[{attrdata-{addcure-0|attackdistance-0|attackenergy-32|attackhurt-0|attackspeed-0|attackvampire-0|critical-1|criticalhurt-0|cutcrithurt-0|cutcure-0|cuthurt-0|dodge-22|dohurt-0|energy-1000|energypersecond-5|energyreceive-12|energytrans-1|hardstraight-0|hit-1|life-3343|lifegrowup-4065|lifereceive-373|movespeed-5.5|pattackgrowup-705|physicquedefgrowup-254|physiqueattack-274|physiquedef-88|reactiontime-0|resist-11|sattackgrowup-564|spiritattack-207|spiritdef-78|spiritdefgrowup-203|struckenergy-300|toughness-1|viewrange-1000|volumeradius-0}|curattrdata-{curenergy-0|curlife-3343|maxenergy-0|maxlife-0}|fighting-1478|isplayer-false|position-3|rolebaseinfo-{careertype-2|exp-0|id-201004_1|level-20|modeid-201004|qualityid-201004_2|starlevel-1}|skilllist-[{extradamage-0|level-0|order-3|skilldamage-0|skillid-20100404_1|skillrate-0}-{extradamage-0|level-1|order-0|skilldamage-41|skillid-20100401_1|skillrate-0.79}-{extradamage-0|level-1|order-1|skilldamage-55|skillid-20100402_1|skillrate-0.89}-{extradamage-0|level-1|order-2|skilldamage-0|skillid-20100403_1|skillrate-0}-{extradamage-0|level-1|order-6|skilldamage-0|skillid-20100400_1|skillrate-1}]}-{attrdata-{addcure-0|attackdistance-0|attackenergy-94|attackhurt-0|attackspeed-0|attackvampire-0|critical-1|criticalhurt-0|cutcrithurt-0|cutcure-0|cuthurt-0|dodge-1|dohurt-0|energy-1000|energypersecond-5|energyreceive-12|energytrans-1|hardstraight-0|hit-1|life-5376|lifegrowup-6514|lifereceive-598|movespeed-5.5|pattackgrowup-617|physicquedefgrowup-284|physiqueattack-237|physiquedef-93|reactiontime-0|resist-11|sattackgrowup-493|spiritattack-173|spiritdef-83|spiritdefgrowup-227|struckenergy-300|toughness-1|viewrange-1000|volumeradius-0}|curattrdata-{curenergy-0|curlife-5376|maxenergy-0|maxlife-0}|fighting-1390|isplayer-false|position-4|rolebaseinfo-{careertype-1|exp-0|id-200011_1|level-20|modeid-200011|qualityid-200011_2|starlevel-1}|skilllist-[{buffid-[20001111]|extradamage-0|level-1|order-0|selfbuffid-[20001112]|skilldamage-40|skillid-20001101_1|skillrate-0.76}-{buffid-[20001131]|extradamage-0|level-1|order-1|skilldamage-37|skillid-20001102_1|skillrate-0.7}-{extradamage-0|level-0|order-3|skilldamage-0|skillid-20001104_1|skillrate-0}-{extradamage-0|level-1|order-2|skilldamage-0|skillid-20001103_1|skillrate-0}-{extradamage-0|level-1|order-6|skilldamage-0|skillid-20001100_1|skillrate-1}]}-{attrdata-{addcure-0|attackdistance-0|attackenergy-94|attackhurt-0|attackspeed-0|attackvampire-0|critical-1|criticalhurt-0|cutcrithurt-0|cutcure-0|cuthurt-0|dodge-1|dohurt-0|energy-1000|energypersecond-5|energyreceive-25|energytrans-1|hardstraight-0|hit-1|life-3312|lifegrowup-4016|lifereceive-246|movespeed-5.5|pattackgrowup-822|physicquedefgrowup-215|physiqueattack-316|physiquedef-81|reactiontime-0|resist-11|sattackgrowup-658|spiritattack-245|spiritdef-52|spiritdefgrowup-172|struckenergy-300|toughness-1|viewrange-1000|volumeradius-0}|curattrdata-{curenergy-0|curlife-3312|maxenergy-0|maxlife-0}|fighting-1353|isplayer-false|position-1|rolebaseinfo-{careertype-5|exp-0|id-202001_1|level-20|modeid-202001|qualityid-202001_2|starlevel-1}|skilllist-[{buffid-[20200111]|extradamage-0|level-1|order-0|skilldamage-100|skillid-20200101_1|skillrate-1.42}-{extradamage-0|level-0|order-3|skilldamage-0|skillid-20200104_1|skillrate-0}-{extradamage-0|level-1|order-1|skilldamage-66|skillid-20200102_1|skillrate-0.94}-{extradamage-0|level-1|order-2|skilldamage-0|skillid-20200103_1|skillrate-0}-{extradamage-0|level-1|order-6|skilldamage-0|skillid-20200100_1|skillrate-1}]}-{attrdata-{addcure-0|attackdistance-0|attackenergy-94|attackhurt-0|attackspeed-0|attackvampire-0|critical-1|criticalhurt-0|cutcrithurt-0|cutcure-0|cuthurt-0|dodge-1|dohurt-0|energy-1000|energypersecond-5|energyreceive-25|energytrans-1|hardstraight-0|hit-1|life-3616|lifegrowup-5221|lifereceive-246|movespeed-5.5|pattackgrowup-1069|physicquedefgrowup-280|physiqueattack-369|physiquedef-84|reactiontime-0|resist-11|sattackgrowup-855|spiritattack-296|spiritdef-75|spiritdefgrowup-224|struckenergy-300|toughness-1|viewrange-1000|volumeradius-0}|curattrdata-{curenergy-0|curlife-3616|maxenergy-0|maxlife-0}|fighting-1543|isplayer-false|position-0|rolebaseinfo-{careertype-5|exp-0|id-200001_1|level-20|modeid-200001|qualityid-200001_2|starlevel-1}|skilllist-[{buffid-[20000111]|extradamage-0|level-1|order-0|skilldamage-192|skillid-20000101_1|skillrate-2.19}-{extradamage-0|level-0|order-3|skilldamage-0|skillid-20000104_1|skillrate-0}-{extradamage-0|level-1|order-1|selfbuffid-[20000121-20000122-20000123]|skilldamage-0|skillid-20000102_1|skillrate-0}-{extradamage-0|level-1|order-2|skilldamage-0|skillid-20000103_1|skillrate-0}-{extradamage-0|level-1|order-6|skilldamage-0|skillid-20000100_1|skillrate-1}]}-{attrdata-{addcure-0|attackdistance-0|attackenergy-94|attackhurt-0|attackspeed-0|attackvampire-0|critical-1|criticalhurt-0|cutcrithurt-0|cutcure-0|cuthurt-0|dodge-1|dohurt-0|energy-1000|energypersecond-5|energyreceive-37|energytrans-1|hardstraight-0|hit-1|life-2286|lifegrowup-2791|lifereceive-85|movespeed-5.5|pattackgrowup-599|physicquedefgrowup-192|physiqueattack-213|physiquedef-56|reactiontime-0|resist-11|sattackgrowup-749|spiritattack-282|spiritdef-85|spiritdefgrowup-240|struckenergy-300|toughness-1|viewrange-1000|volumeradius-0}|curattrdata-{curenergy-0|curlife-2286|maxenergy-0|maxlife-0}|fighting-1411|isplayer-false|position-2|rolebaseinfo-{careertype-4|exp-0|id-204001_1|level-20|modeid-204001|qualityid-204001_2|starlevel-1}|skilllist-[{buffid-[20400111-20400112]|extradamage-0|level-1|order-0|skilldamage-0|skillid-20400101_1|skillrate-0}-{extradamage-0|level-0|order-3|skilldamage-0|skillid-20400104_1|skillrate-0}-{extradamage-0|level-1|order-1|skilldamage-60|skillid-20400102_1|skillrate-0.94}-{extradamage-0|level-1|order-2|skilldamage-0|skillid-20400103_1|skillrate-0}-{extradamage-0|level-1|order-6|skilldamage-0|skillid-20400100_1|skillrate-1}]}]}";
		int span = 100;
		int index = client.length()/span;
		for (int i = 0; i < index; i++) {
			int start = i*span;
			int end = (i+1)*span;
			String cTmp = StringUtils.substring(client, start , end);
			String sTmp = StringUtils.substring(server, start , end);
			if(!cTmp.equals(sTmp)){
				System.out.println("ctemp:"+cTmp);
				System.out.println("stemp:"+sTmp);
			}
		}
		
		String clientmd5 = MD5.getMD5ofStr(client).toLowerCase();
		 String servermd5 = MD5.getMD5ofStr(server).toLowerCase();
		
		 System.out.println(clientmd5);
		 System.out.println(servermd5);
	}
	
}
