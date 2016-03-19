package com.rwbase.dao.sign.pojo;
import java.util.Calendar;

public class SignData 
{
	private boolean isOpen;		//是否开启
	private boolean isDouble; 	//是否双倍可用
	private boolean isResign;	//是否补签可用
	private Calendar openSignDate;	//该签到开放时间
	private Calendar lastSignDate;	//上一次签到日期(包括上面的双倍和补签)
	
	public boolean isDouble() {
		return isDouble;
	}
	public boolean isResign() {
		return isResign;
	}
	public void setDouble(boolean isDouble) {
		this.isDouble = isDouble;
	}
	public void setResign(boolean isResign) {
		this.isResign = isResign;
	}
	public Calendar getOpenSignDate() {
		return openSignDate;
	}
	public void setOpenSignDate(Calendar openSignDate) {
		this.openSignDate = openSignDate;
	}
	public Calendar getLastSignDate() {
		return lastSignDate;
	}
	public void setLastSignDate(Calendar lastSignDate) {
		this.lastSignDate = lastSignDate;
	}
	public boolean isOpen() {
		return isOpen;
	}
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
}
