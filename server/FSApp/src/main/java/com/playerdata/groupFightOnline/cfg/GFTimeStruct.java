package com.playerdata.groupFightOnline.cfg;

public class GFTimeStruct {
	public boolean isNextWeek;
	public int dayOfWeek;
	public int hour;
	public int minute;
	public int second;
	
	public GFTimeStruct(int dayOfWeek, int hour, int minute, int second){
		this.isNextWeek = false;
		this.dayOfWeek = dayOfWeek;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}
}
