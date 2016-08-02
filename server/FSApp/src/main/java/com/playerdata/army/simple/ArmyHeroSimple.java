package com.playerdata.army.simple;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.common.BeanCopyer;
import com.playerdata.Hero;
import com.playerdata.army.CurAttrData;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.common.attrdata.AttrData;


/**
 * 战斗用临时数据，不能持久化
 * @author Administrator
 *
 */

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmyHeroSimple {
	
	private String id;    //英雄uuid	

	private int modeId;//英雄模型Id
	private int level;//等级
	private int starLevel;//星级
	private String qualityId;//品阶Id
	
	private CurAttrData curAttrData = new CurAttrData();
	private int fighting;
	
	public static ArmyHeroSimple  newInstance(Hero hero) {
		AttrData totalAttrData = hero.getAttrMgr().getTotalAttrData();
//		RoleBaseInfo baseInfo = hero.getRoleBaseInfoMgr().getBaseInfo();
		
		ArmyHeroSimple armyHero = new ArmyHeroSimple();
//		BeanCopyer.copy(baseInfo, armyHero);	
		BeanCopyer.copy(hero, armyHero);
		armyHero.curAttrData.setMaxLife(totalAttrData.getLife());
		armyHero.curAttrData.setMaxEnergy(totalAttrData.getEnergy());
		armyHero.curAttrData.setCurLife(totalAttrData.getLife());
		armyHero.curAttrData.setCurEnergy(0);
//		armyHero.curAttrData.setId(hero.getHeroData().getId());
		armyHero.curAttrData.setId(hero.getId());
		armyHero.fighting = hero.getFighting();
		
		return armyHero;		
	}
	
	public static ArmyHeroSimple newBlankInstance() {
		ArmyHeroSimple armyHero = new ArmyHeroSimple();	
		armyHero.id = "0";
		armyHero.qualityId = "";
		armyHero.curAttrData.setId("");
		return armyHero;
	}
	
	public String getId() {
		return id;
	}
	
	public int getModeId() {
		return modeId;
	}
	
	public int getLevel() {
		return level;
	}

	public int getStarLevel() {
		return starLevel;
	}

	public String getQualityId() {
		return qualityId;
	}

	public CurAttrData getCurAttrData() {
		return curAttrData;
	}

	public void setCurAttrData(CurAttrData attr){
		this.curAttrData = attr;
	}
	
	public int getFighting() {
		return fighting;
	}
}
