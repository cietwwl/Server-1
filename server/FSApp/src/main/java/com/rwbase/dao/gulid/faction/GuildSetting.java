package com.rwbase.dao.gulid.faction;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


/**
 * 工会可以通过面板修改的属性
 * @author Allen
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class GuildSetting {

	private String name;
	private String des="";
	private int icon;
	private int iconBox;
	private GuildPrivacyType privacyType;
	private int changeName=0;//是否有改过名字
	private int joinLevel;//限制等级
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	public int getIconBox() {
		return iconBox;
	}
	public void setIconBox(int iconBox) {
		this.iconBox = iconBox;
	}

	
	public GuildPrivacyType getPrivacyType() {
		return privacyType;
	}
	public void setPrivacyType(GuildPrivacyType privacyType) {
		this.privacyType = privacyType;
	}
	public int getChangeName() {
		return changeName;
	}
	public void setChangeName(int changeName) {
		this.changeName = changeName;
	}
	public int getJoinLevel() {
		return joinLevel;
	}
	public void setJoinLevel(int joinLevel) {
		this.joinLevel = joinLevel;
	}
	
	
	
}
