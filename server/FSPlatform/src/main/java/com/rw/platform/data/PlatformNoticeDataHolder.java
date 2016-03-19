package com.rw.platform.data;

import com.rwbase.dao.platformNotice.TablePlatformNotice;
import com.rwbase.dao.platformNotice.TablePlatformNoticeDAO;

public class PlatformNoticeDataHolder extends ADataHolder{
	
	public PlatformNoticeDataHolder(){}
	
	
	public void updatePlatformNotice(TablePlatformNotice notice, boolean insert){
		synchronized (_lock) {
			TablePlatformNoticeDAO.getInstance().save(notice, insert);
		}
	}
	
	public TablePlatformNotice getPlatformNotice(){
		TablePlatformNotice platformNotice = TablePlatformNoticeDAO.getInstance().getPlatformNotice();
		return platformNotice;
	}
}
