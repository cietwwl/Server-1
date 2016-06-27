package com.playerdata.groupFightOnline.cfg;

public class GFTimeStruct {
	public boolean isNextWeek;
	public int dayOfWeek;
	public int hour;
	public int minute;
	public int second;
	
	public GFTimeStruct(boolean isNextWeek, int dayOfWeek, int hour, int minute, int second){
		this.isNextWeek = isNextWeek;
		this.dayOfWeek = dayOfWeek;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}
}
