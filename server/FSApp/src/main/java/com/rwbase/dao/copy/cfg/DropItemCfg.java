package com.rwbase.dao.copy.cfg;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DropItemCfg 
{
	private int id;
	private int noItemProb;
	private int max;
	private int whiteProb;
	private int greenProb;
	private int blueProb;
	private int purpleProb;
	private int goldProb;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNoItemProb() {
		return noItemProb;
	}
	public void setNoItemProb(int noItemProb) {
		this.noItemProb = noItemProb;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getWhiteProb() {
		return whiteProb;
	}
	public void setWhiteProb(int whiteProb) {
		this.whiteProb = whiteProb;
	}
	public int getGreenProb() {
		return greenProb;
	}
	public void setGreenProb(int greenProb) {
		this.greenProb = greenProb;
	}
	public int getBlueProb() {
		return blueProb;
	}
	public void setBlueProb(int blueProb) {
		this.blueProb = blueProb;
	}
	public int getPurpleProb() {
		return purpleProb;
	}
	public void setPurpleProb(int purpleProb) {
		this.purpleProb = purpleProb;
	}
	public int getGoldProb() {
		return goldProb;
	}
	public void setGoldProb(int goldProb) {
		this.goldProb = goldProb;
	}
}
