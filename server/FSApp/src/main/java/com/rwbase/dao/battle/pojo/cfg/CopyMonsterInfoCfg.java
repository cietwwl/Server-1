package com.rwbase.dao.battle.pojo.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.playerdata.army.ArmyVector3;

public class CopyMonsterInfoCfg {

	
	private String Id;
	
	/**对应的关卡id*/
	private String CopyId;
	
	/**战斗下标（波数）*/
	private int Index;
	
	/**战斗时间 s*/
	private int time;
	
	private String Enemy_1; // 怪物1...
	private int Enemy_1_x; // x轴位置...
	private int Enemy_1_z; // z轴位置...
	private String Enemy_2; // 怪物2...
	private int Enemy_2_x; // x轴位置...
	private int Enemy_2_z; // z轴位置...
	private String Enemy_3; // 怪物3...
	private int Enemy_3_x; // x轴位置...
	private int Enemy_3_z; // z轴位置...
	private String Enemy_4; // 怪物4...
	private int Enemy_4_x; // x轴位置...
	private int Enemy_4_z; // z轴位置...
	private String Enemy_5; // 怪物5...
	private int Enemy_5_x; // x轴位置...
	private int Enemy_5_z; // z轴位置...
	private String Enemy_6; // 怪物6...
	private int Enemy_6_x; // x轴位置...
	private int Enemy_6_z; // z轴位置...

	private Map<String, ArmyVector3> positionMap = new HashMap<String, ArmyVector3>();
	
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
			positionMap.put(Enemy_1, new ArmyVector3(Enemy_1_x, 0, Enemy_1_z));
		}
		if(!StringUtils.isEmpty(Enemy_2)){
			EnemyList.add(Enemy_2);
			positionMap.put(Enemy_2, new ArmyVector3(Enemy_2_x, 0, Enemy_2_z));
		}
		if(!StringUtils.isEmpty(Enemy_3)){
			EnemyList.add(Enemy_3);
			positionMap.put(Enemy_3, new ArmyVector3(Enemy_3_x, 0, Enemy_3_z));
		}
		if(!StringUtils.isEmpty(Enemy_4)){
			EnemyList.add(Enemy_4);
			positionMap.put(Enemy_4, new ArmyVector3(Enemy_4_x, 0, Enemy_4_z));
		}
		if(!StringUtils.isEmpty(Enemy_5)){
			EnemyList.add(Enemy_5);
			positionMap.put(Enemy_5, new ArmyVector3(Enemy_5_x, 0, Enemy_5_z));
		}
		if(!StringUtils.isEmpty(Enemy_6)){
			EnemyList.add(Enemy_6);
			positionMap.put(Enemy_6, new ArmyVector3(Enemy_6_x, 0, Enemy_6_z));
		}
	}
	
	
	
	public ArmyVector3 getPosition(String enemyID){
		return positionMap.get(enemyID);
	}
}
