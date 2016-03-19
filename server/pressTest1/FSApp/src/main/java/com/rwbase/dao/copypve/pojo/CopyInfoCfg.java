package com.rwbase.dao.copypve.pojo;

import com.playerdata.readonly.CopyInfoCfgIF;

public class CopyInfoCfg implements CopyInfoCfgIF {

	private int id;
	private int type;
	private String name;
	private String titleIcon;
	private String nameIcon;
	private String degreeID;
	private int count;
	private int openLv;
	private int bg;
	private int cost;
	private String time;
	private String prizeDes;
	private String timeDes;
	private int sweepLv;
	private int cdSeconds;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	public String getNameIcon() {
		return nameIcon;
	}

	public void setNameIcon(String nameIcon) {
		this.nameIcon = nameIcon;
	}

	public String getDegreeID() {
		return degreeID;
	}

	public void setDegreeID(String degreeID) {
		this.degreeID = degreeID;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getOpenLv() {
		return openLv;
	}

	public void setOpenLv(int openLv) {
		this.openLv = openLv;
	}

	public int getBg() {
		return bg;
	}

	public void setBg(int bg) {
		this.bg = bg;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPrizeDes() {
		return prizeDes;
	}

	public void setPrizeDes(String prizeDes) {
		this.prizeDes = prizeDes;
	}

	public String getTimeDes() {
		return timeDes;
	}

	public void setTimeDes(String timeDes) {
		this.timeDes = timeDes;
	}

	public int getSweepLv() {
		return sweepLv;
	}

	public void setSweepLv(int sweepLv) {
		this.sweepLv = sweepLv;
	}

	public int getCdSeconds() {
		return cdSeconds;
	}

	public void setCdSeconds(int cdSeconds) {
		this.cdSeconds = cdSeconds;
	}

}
