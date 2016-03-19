package com.rwbase.dao.userrole.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "user_star_grow_cfg")
public class UserStarGrowCfg {

	private String id;
	private int level;//星级
	private float lifeGrow;//生命成长
	private float attactGrow;//攻击成长
	private float physiqueDefGrow; // 体魄防御...
	private float spiritDefGrow; // 精神防御...
	
	public UserStarGrowCfg() {
	}
	@Id
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public float getLifeGrow() {
		return lifeGrow;
	}
	public void setLifeGrow(float lifeGrow) {
		this.lifeGrow = lifeGrow;
	}
	public float getAttactGrow() {
		return attactGrow;
	}
	public void setAttactGrow(float attactGrow) {
		this.attactGrow = attactGrow;
	}
	public float getPhysiqueDefGrow() {
		return physiqueDefGrow;
	}
	public void setPhysiqueDefGrow(float physiqueDefGrow) {
		this.physiqueDefGrow = physiqueDefGrow;
	}
	public float getSpiritDefGrow() {
		return spiritDefGrow;
	}
	public void setSpiritDefGrow(float spiritDefGrow) {
		this.spiritDefGrow = spiritDefGrow;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
}
