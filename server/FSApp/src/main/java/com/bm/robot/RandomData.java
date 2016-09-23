package com.bm.robot;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.rwbase.dao.role.pojo.RoleCfg;


/**
 * 用来记录生成的机器人过程产生的随机数，以便生成一样的机器人。
 * @author allen
 *
 */

public class RandomData {

	private Random random = new Random();
	
	private String robotId;
	
	private String robotUserId;
	
	private int career;
	
	private int sex;
	
	private int fName;
	
	private int sName;
	
	private int heroTeam;
	
	private int heroCfg;
	
	private int mainRoleLevel;
	
	private int vipLevel;
	
	private int magicId;
	
	private int magicLevel;
	
	private boolean doHeroMakeUp = true; //上阵人数不够不够的时候补齐
	
	
	private boolean asRecord = true; //是否作为记录使用，否则作为数据使用
	
	public static RandomData newInstance(int robotId){
		
		RandomData randomData = new RandomData().asRecorder();
		randomData.robotId = String.valueOf(robotId);
		return randomData;
	}
	
	private RandomData asRecorder(){
		asRecord = true;
		return this;
	}
	
	public RandomData asData(){
		asRecord = false;
		return this;
	}
	
	public RandomData doHeroMakeup(boolean flag){
		doHeroMakeUp = flag;
		return this;
	}	
	
	
	public String getRobotId() {
		return robotId;
	}

	public String getRobotUserId(RoleCfg roleCfg) {
		
		if(asRecord || StringUtils.isBlank(robotUserId)){			
			StringBuilder sb = new StringBuilder();
			sb.append(roleCfg.getModelId()).append("_").append(System.currentTimeMillis());// 模拟生成一个角色Id，modelId_时间
			robotUserId = sb.toString();
		}
		
		return robotUserId;
	}

	private int getValue(int value, int size){
		int result = value;
		if(asRecord || result >= size){			
			result = random.nextInt(size);
		}
		return result;
	}
	public int getCareer(int size) {		
		career = getValue(career,size);		
		return career;
	}
	
	public int getSex(int size) {
		sex = getValue(sex,size);
		return sex;
	}

	public int getfName(int size) {
		fName = getValue(fName,size);
		return fName;
	}

	public int getsName(int size) {
		sName = getValue(sName,size);
		return sName;
	}

	public int getHeroTeam(int size) {
		heroTeam = getValue(heroTeam,size);
		return heroTeam;
	}

	public int getHeroCfg(int size) {
		heroCfg = getValue(heroCfg,size);
		return heroCfg;
	}
	
	public int getMainRoleLevel(int size) {
		mainRoleLevel = getValue(mainRoleLevel,size);
		return mainRoleLevel;
	}
	
	public int getVipLevel(int size) {
		vipLevel = getValue(vipLevel,size);
		return vipLevel;
	}
	
	public int getMagicLevel(int size) {
		magicLevel = getValue(magicLevel,size);
		return magicLevel;
	}
	public int getMagicId(int size) {
		magicId = getValue(magicId,size);
		return magicId;
	}

	public boolean isDoHeroMakeUp() {
		return doHeroMakeUp;
	}
	
	

}
