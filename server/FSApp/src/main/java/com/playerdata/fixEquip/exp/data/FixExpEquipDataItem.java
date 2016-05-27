package com.playerdata.fixEquip.exp.data;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.fixEquip.cfg.FixEquipCfg;
import com.playerdata.fixEquip.cfg.FixEquipCfgDAO;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "fix_exp_equip_item")
public class FixExpEquipDataItem implements  IMapItem {

	@Id
	private String id;
	
	private String ownerId;

	@CombineSave
	private String cfgId;
	
	@CombineSave
	private int exp;
	@CombineSave
	private int level;
	@CombineSave
	private int quality;
	@CombineSave
	private int star;
	@CombineSave
	private int slot;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getCfgId() {
		return cfgId;
	}
	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getQuality() {
		return quality;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	public int getStar() {
		return star;
	}
	public void setStar(int star) {
		this.star = star;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}	

	public int getSlot() {
		return slot;
	}
	public void setSlot(int slot) {
		this.slot = slot;
	}
	public String getQualityPlanId(){
		FixEquipCfg fixEquipCfg = FixEquipCfgDAO.getInstance().getCfgById(getCfgId());
		return fixEquipCfg.getQualityPlanId();
	}
	public String getLevelPlanId(){
		FixEquipCfg fixEquipCfg = FixEquipCfgDAO.getInstance().getCfgById(getCfgId());
		return fixEquipCfg.getLevelPlanId();
	}
	public String getLevelCostPlanId(){
		FixEquipCfg fixEquipCfg = FixEquipCfgDAO.getInstance().getCfgById(getCfgId());
		return fixEquipCfg.getLevelCostPlanId();
	}
	public String getStarPlanId(){
		FixEquipCfg fixEquipCfg = FixEquipCfgDAO.getInstance().getCfgById(getCfgId());
		return fixEquipCfg.getStarPlanId();
	}
	
	
}
