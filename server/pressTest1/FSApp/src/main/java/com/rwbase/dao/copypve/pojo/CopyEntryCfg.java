package com.rwbase.dao.copypve.pojo;

public class CopyEntryCfg {

	private int copyType;		//类型
	private int sortNum;		//排序
	private String name;		//名字
	private String titleIcon;	//标题
	private String icon;		//名字图标
	private String intro;		//介绍
	private String des;			//入口简介
	private int cdSeconds;		//cd秒数

	public int getCopyType() {
		return copyType;
	}

	public void setCopyType(int copyType) {
		this.copyType = copyType;
	}

	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitleIcon() {
		return titleIcon;
	}

	public void setTitleIcon(String titleIcon) {
		this.titleIcon = titleIcon;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public int getCdSeconds() {
		return cdSeconds;
	}

	public void setCdSeconds(int cdSeconds) {
		this.cdSeconds = cdSeconds;
	}

}
