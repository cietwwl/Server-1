package com.rwbase.dao.item.pojo;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.common.ListParser;
import com.log.GameLog;
import com.rw.fsutil.common.Pair;


public class MagicCfg extends ItemBaseCfg {
	private int property; // 属性
	private int smeltperc; // 熔炼生成概率
	private String trainItemId; // 锻造材料ID
	private int skillId; // 法宝技能ID
	private String itemspit; // 物品吐槽
	private int energyReceive; // 能量回复
	private int attackType;
	private int struckEnergy;
	private int magicType;
	private int composeItemID;
	private int composeNeedNum;
	private int composeCostCoin;
	private String inlayLimit;
	private String hideAttr;
	private int initialEnergy;// 初始能量值
	
	public void ExtraInitAfterLoad(){
		ParseConversionGoods();
		ParseDecomposeGoods();
		ParseUpgradeNeedGoodList();
	}
	
	// region 法宝进阶用到的属性
	private String upMagic;
	private int uplevel;
	private String goods;
	private int upMagicCost;
	private int upMagicMoneyType;
	public int getUpMagicMoneyType() {
		return upMagicMoneyType;
	}

	public int getUpMagicCost() {
		return upMagicCost;
	}

	private List<Pair<Integer,Integer>> upgradeNeedGoodList;

	public List<Pair<Integer, Integer>> getUpgradeNeedGoodList() {
		return upgradeNeedGoodList;
	}
	
	private void ParseUpgradeNeedGoodList(){
		if (upgradeNeedGoodList == null){
			String module = "法宝";
			String moduleID = "配置错误";
			upgradeNeedGoodList = ListParser.ParsePairList(module, moduleID, ",","_",goods);
		}
	}
	
	public String getUpMagic() {
		return upMagic;
	}
	public int getUplevel() {
		return uplevel;
	}
	public String getGoods() {
		return goods;
	}
	// end region
	
	// region 分解材料需要用到的属性
	private String decomposeGoods;
	private float coefficient;
	private String conversionGoods;

	//derived read only properties
	private int convertedGoodModelId;
	public int getConvertedGoodModelId() {
		return convertedGoodModelId;
	}
	private void ParseConversionGoods(){
		try{
			if (!StringUtils.isBlank(conversionGoods))
			convertedGoodModelId = Integer.parseInt(conversionGoods);
		}catch(Exception ex){
			GameLog.error("法宝", "配置错误", "无效ModelID:"+conversionGoods);
		}
	}

	private List<Pair<Integer,Integer>> decomposeGoodList;
	public List<Pair<Integer, Integer>> getDecomposeGoodList() {
		return decomposeGoodList;
	}

	private void ParseDecomposeGoods(){
		if (decomposeGoodList == null){
			String module = "法宝";
			String moduleID = "配置错误";
			decomposeGoodList = ListParser.ParsePairList(module, moduleID, ",","_",decomposeGoods);
		}
	}

	public String getDecomposeGoods() {
		return decomposeGoods;
	}

	public float getCoefficient() {
		return coefficient;
	}

	public String getConversionGoods() {
		return conversionGoods;
	}
	// end region
	
	public String getItemspit() {
		return itemspit;
	}

	public void setItemspit(String itemspit) {
		this.itemspit = itemspit;
	}

	public int getProperty() {
		return property;
	}

	public void setProperty(int property) {
		this.property = property;
	}

	public int getSmeltperc() {
		return smeltperc;
	}

	public void setSmeltperc(int smeltperc) {
		this.smeltperc = smeltperc;
	}

	public String getTrainItemId() {
		return trainItemId;
	}

	public void setTrainItemId(String trainItemId) {
		this.trainItemId = trainItemId;
	}

	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}

	public int getEnergyReceive() {
		return energyReceive;
	}

	public void setEnergyReceive(int energyReceive) {
		this.energyReceive = energyReceive;
	}

	public int getAttackType() {
		return attackType;
	}

	public void setAttackType(int attackType) {
		this.attackType = attackType;
	}

	public int getStruckEnergy() {
		return struckEnergy;
	}

	public void setStruckEnergy(int struckEnergy) {
		this.struckEnergy = struckEnergy;
	}

	public int getMagicType() {
		return magicType;
	}

	public void setMagicType(int magicType) {
		this.magicType = magicType;
	}

	public int getComposeItemID() {
		return composeItemID;
	}

	public void setComposeItemID(int composeItemID) {
		this.composeItemID = composeItemID;
	}

	public int getComposeNeedNum() {
		return composeNeedNum;
	}

	public void setComposeNeedNum(int composeNeedNum) {
		this.composeNeedNum = composeNeedNum;
	}

	public int getComposeCostCoin() {
		return composeCostCoin;
	}

	public void setComposeCostCoin(int composeCostCoin) {
		this.composeCostCoin = composeCostCoin;
	}

	public String getInlayLimit() {
		return inlayLimit;
	}

	public void setInlayLimit(String inlayLimit) {
		this.inlayLimit = inlayLimit;
	}

	public String getHideAttr() {
		return hideAttr;
	}

	public void setHideAttr(String hideAttr) {
		this.hideAttr = hideAttr;
	}

	public int getInitialEnergy() {
		return initialEnergy;
	}

	public void setInitialEnergy(int initialEnergy) {
		this.initialEnergy = initialEnergy;
	}
}