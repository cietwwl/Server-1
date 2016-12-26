package com.bm.notice;

import com.rwbase.dao.gameNotice.TableGameNotice;
import com.rwbase.dao.notice.pojo.AnnouncementCfg;

public class Notice {
	private int id;                 	   	//公告id
	private int sort = 0;						//公告排序id				
	private int pushType;					//推送类型
	private String tagTitle = "活动";				//页签标题
	private String tagIcon = "act_3";					//标签图片
	private String title = "公告";					//页签标签
	private int annoceType = 0;					//公告类型
	private String bg;						//活动标题背景图
	private String announceTitle;			//公告标题
	private String announceContent;			//公告内容
	private int isShowButton = 0;				//是否显示按钮
	private String buttonText;				//按钮文本
	private int gotoType;					//跳转类型
	private int gotoTypeId;					//跳转界面
	private long startTime;					//开始时间
	private long endTime;					//结束时间
	private boolean isConfigNotice;         //是否配置公告
	private int pushLevel = 6;              //默认推送等级为6级
	
	public boolean isConfigNotice() {
		return isConfigNotice;
	}
	public void setConfigNotice(boolean isConfigNotice) {
		this.isConfigNotice = isConfigNotice;
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
		return annoceType;
	}
	public void setAnnoceType(int annoceType) {
		this.annoceType = annoceType;
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
	
	public int getPushLevel() {
		return pushLevel;
	}
	public void setPushLevel(int pushLevel) {
		this.pushLevel = pushLevel;
	}
	public void SetNotice(AnnouncementCfg cfg){
		this.id = cfg.getId();
		this.sort = cfg.getSort();				
		this.pushType = cfg.getPushType();
		this.tagTitle = cfg.getTagTitle();
		this.tagIcon = cfg.getTagIcon();
		this.title =cfg.getTitle();
		this.annoceType = cfg.getAnnoceType();
		this.bg = cfg.getBg();
		this.announceTitle = cfg.getAnnounceTitle();
		this.announceContent = cfg.getAnnounceContent();
		this.isShowButton = cfg.getIsShowButton();
		this.buttonText = cfg.getButtonText();
		this.gotoType = cfg.getGotoType();
		this.gotoTypeId = cfg.getGotoTypeId();
		this.startTime = cfg.getStartTime();
		this.endTime = cfg.getEndTime();
		this.isConfigNotice = true;
	}
	
	public void SetNotice(TableGameNotice notice){
		this.id = notice.getNoticeId();
		this.announceTitle = notice.getTitle();
		this.announceContent = notice.getContent();
		this.startTime = notice.getStartTime() * 1000;
		this.endTime = notice.getEndTime() * 1000;
		this.isConfigNotice = false;
		
	}
}
