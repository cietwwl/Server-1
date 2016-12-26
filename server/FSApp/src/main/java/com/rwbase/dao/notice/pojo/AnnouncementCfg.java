package com.rwbase.dao.notice.pojo;

public class AnnouncementCfg {
	private int id;                 	   	//公告id
	private int sort;						//公告排序id				
	private int pushType;					//推送类型
	private String pushTime;				//推送时间
	private String tagTitle;				//页签标题
	private String tagIcon;					//标签图片
	private String title;					//页签标签
	private int annonceType;					//公告类型
	private String bg;						//活动标题背景图
	private String announceTitle;			//公告标题
	private String announceContent;			//公告内容
	private int isShowButton;				//是否显示按钮
	private String buttonText;				//按钮文本
	private int gotoType;					//跳转类型
	private int gotoTypeId;					//跳转界面
	private long startTime;
	private long endTime;
	private int pushLevel;                  //推送等级
	
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public int getPushType() {
		return pushType;
	}
	public void setPushType(int pushType) {
		this.pushType = pushType;
	}
	public String getPushTime() {
		return pushTime;
	}
	public void setPushTime(String pushTime) {
		this.pushTime = pushTime;
	}
	public String getTagTitle() {
		return tagTitle;
	}
	public void setTagTitle(String tagTitle) {
		this.tagTitle = tagTitle;
	}
	public String getTagIcon() {
		return tagIcon;
	}
	public void setTagIcon(String tagIcon) {
		this.tagIcon = tagIcon;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getAnnoceType() {
		return annonceType;
	}
	public void setAnnoceType(int annoceType) {
		this.annonceType = annoceType;
	}
	public String getBg() {
		return bg;
	}
	public void setBg(String bg) {
		this.bg = bg;
	}
	public String getAnnounceTitle() {
		return announceTitle;
	}
	public void setAnnounceTitle(String announceTitle) {
		this.announceTitle = announceTitle;
	}
	public String getAnnounceContent() {
		return announceContent;
	}
	public void setAnnounceContent(String announceContent) {
		this.announceContent = announceContent;
	}
	public int getIsShowButton() {
		return isShowButton;
	}
	public void setIsShowButton(int isShowButton) {
		this.isShowButton = isShowButton;
	}
	public String getButtonText() {
		return buttonText;
	}
	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}
	public int getGotoType() {
		return gotoType;
	}
	public void setGotoType(int gotoType) {
		this.gotoType = gotoType;
	}
	public int getGotoTypeId() {
		return gotoTypeId;
	}
	public void setGotoTypeId(int gotoTypeId) {
		this.gotoTypeId = gotoTypeId;
	}
	public int getPushLevel() {
		return pushLevel;
	}
	public void setPushLevel(int pushLevel) {
		this.pushLevel = pushLevel;
	}
}
