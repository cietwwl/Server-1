package com.rwbase.dao.copy.cfg;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemProbabilityCfg 
{
	private int itemid;
	private int num;
	private int max;
	private int quality;
	private int itemsFormula;
	private int probability; 
	
	public int getItemid() {
		return itemid;
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getQuality() {
		return quality;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	public int getItemsFormula() {
		return itemsFormula;
	}
	public void setItemsFormula(int itemsFormula) {
		this.itemsFormula = itemsFormula;
	}
	public int getProbability() {
		return probability;
	}
	public void setProbability(int probability) {
		this.probability = probability;
	}
	
}
