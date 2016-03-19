package com.rwbase.dao.role.pojo;

public class QualityTypeCfg {

	private int id;
	private String name;
	private String qualityBg;
	private int maxAttachLevel;
	private int attachId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQualityBg() {
		return qualityBg;
	}
	public void setQualityBg(String qualityBg) {
		this.qualityBg = qualityBg;
	}
	public int getMaxAttachLevel() {
		return maxAttachLevel;
	}
	public void setMaxAttachLevel(int maxAttachLevel) {
		this.maxAttachLevel = maxAttachLevel;
	}
	public int getAttachId() {
		return attachId;
	}
	public void setAttachId(int attachId) {
		this.attachId = attachId;
	}
}
