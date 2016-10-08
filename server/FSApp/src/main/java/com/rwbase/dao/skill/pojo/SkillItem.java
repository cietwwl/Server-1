package com.rwbase.dao.skill.pojo;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Id;
import javax.persistence.Table;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.dao.annotation.OwnerId;

@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
@Table(name = "skill_item")
public class SkillItem implements RoleExtProperty, SkillIF {

	@Id
	private Integer id;
	@OwnerId
	private String ownerId;

	private String skillId;

	private int level;// 如果0级说明没开放
	
	private int order;// 第几个技能
	@JsonIgnore
	private float skillRate;
	@JsonIgnore
	private int extraDamage;
	@JsonIgnore
	private int skillDamage;
	@JsonIgnore
	private List<String> skillListeners;// 监听的Id

	public SkillItem() {
		skillListeners = new ArrayList<String>();
	}

	public Integer getId() {
		return id;
	}

	public String strId() {
		return String.valueOf(id);
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public float getSkillRate() {
		return skillRate;
	}

	public void setSkillRate(float skillRate) {
		this.skillRate = skillRate;
	}

	public int getExtraDamage() {
		return extraDamage;
	}

	public void setExtraDamage(int extraDamage) {
		this.extraDamage = extraDamage;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	public int getSkillDamage() {
		return skillDamage;
	}

	public void setSkillDamage(int skillDamage) {
		this.skillDamage = skillDamage;
	}

	public List<String> getSkillListeners() {
		return skillListeners;
	}

	public void setSkillListeners(List<String> skillListeners) {
		this.skillListeners = skillListeners;
	}

}