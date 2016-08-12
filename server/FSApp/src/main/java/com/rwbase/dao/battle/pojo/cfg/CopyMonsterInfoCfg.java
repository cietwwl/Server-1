package com.rwbase.dao.battle.pojo.cfg;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

public class CopyMonsterInfoCfg {

	
	private String Id;
	
	/**对应的关卡id*/
	private String CopyId;
	
	/**战斗下标（波数）*/
	private int Index;
	
	/**战斗时间 s*/
	private int time;
	
	/**怪物1*/
	private String Enemy_1;
	
	/**怪物2*/
	private String Enemy_2;
	
	private String Enemy_3;
	
	private String Enemy_4;
	
	private String Enemy_5;
	
	private String Enemy_6;
	
	private List<String> EnemyList = new ArrayList<String>();
	
	
	

	public String getId() {
		return Id;
	}

	public String getCopyId() {
		return CopyId;
	}

	public int getIndex() {
		return Index;
	}

	public int getTime() {
		return time;
	}

	public String getEnemy_1() {
		return Enemy_1;
	}

	public String getEnemy_2() {
		return Enemy_2;
	}

	public String getEnemy_3() {
		return Enemy_3;
	}

	public String getEnemy_4() {
		return Enemy_4;
	}


	public String getEnemy_5() {
		return Enemy_5;
	}

	public String getEnemy_6() {
		return Enemy_6;
	}

	public List<String> getEnemyList() {
		return EnemyList;
	}

	public void formatData(){
		if(!StringUtils.isEmpty(Enemy_1)){
			EnemyList.add(Enemy_1);
		}
		if(!StringUtils.isEmpty(Enemy_2)){
			EnemyList.add(Enemy_2);
		}
		if(!StringUtils.isEmpty(Enemy_3)){
			EnemyList.add(Enemy_3);
		}
		if(!StringUtils.isEmpty(Enemy_4)){
			EnemyList.add(Enemy_4);
		}
		if(!StringUtils.isEmpty(Enemy_5)){
			EnemyList.add(Enemy_5);
		}
		if(!StringUtils.isEmpty(Enemy_6)){
			EnemyList.add(Enemy_6);
		}
	}
	
}
