package com.playerdata.activity.notice.cfg;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;

public class ActivityNoticeCfg implements ActivityCfgIF{
	
	private int id; //活动id
	
	private String pushTime; //活动的时间
	
	private String title;
	
	private String content;
	
	private int pushLevel;

	public int getId() {
		return id;
	}

	public String getPushTime() {
		return pushTime;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public int getPushLevel() {
		return pushLevel;
	}

	@Override
	public void ExtraInitAfterLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCfgId() {
		return id;
	}

	@Override
	public long getStartTime() {
		return 0;
	}

	@Override
	public long getEndTime() {
		return 0;
	}

	@Override
	public int getLevelLimit() {
		return pushLevel;
	}

	@Override
	public int getVipLimit() {
		return 0;
	}

	@Override
	public boolean isDailyRefresh() {
		return false;
	}

	@Override
	public boolean isEveryDaySame() {
		return false;
	}

	@Override
	public void setStartAndEndTime(String startTime, String endTime) {
		pushTime = startTime;
	}

	@Override
	public String getStartTimeStr() {
		return pushTime;
	}

	@Override
	public String getEndTimeStr() {
		return null;
	}

	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	public void setVersion(int version) {
		
	}

	@Override
	public String getActDesc() {
		return title;
	}

	@Override
	public void setActDesc(String actDesc) {
		this.title = actDesc;
	}
}
