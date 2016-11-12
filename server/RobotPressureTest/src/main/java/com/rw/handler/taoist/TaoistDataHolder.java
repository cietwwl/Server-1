package com.rw.handler.taoist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rw.Client;
import com.rw.Robot;
import com.rw.common.RobotLog;
import com.rwproto.TaoistMagicProtos;
import com.rwproto.TaoistMagicProtos.TaoistInfo;

public class TaoistDataHolder {
	List<TaoistInfo> taoistInfoListList = new ArrayList<TaoistMagicProtos.TaoistInfo>();
	
	private final static int MAX_LEVEL = 50;

	public List<TaoistInfo> getTaoistInfoListList() {
		return taoistInfoListList;
	}

	public void setTaoistInfoListList(List<TaoistInfo> taoistInfoListList) {
		this.taoistInfoListList.clear();
		for (TaoistInfo taoistInfo : taoistInfoListList) {
			this.taoistInfoListList.add(taoistInfo);
		}
	}
	
	public boolean checkTaoistUpdate(int id, Client client){
		for (TaoistInfo taoistInfo : taoistInfoListList) {
			if(taoistInfo.getTaoistID() == id){
				if(taoistInfo.getLevel() == MAX_LEVEL){
					RobotLog.fail("TaoistHandler 已达到最高等级");
					return false;
				}else{
					RobotLog.info("TaoistHandler---------------|" + client.getUserId()
							+ "|taoisInfo id:" + taoistInfo.getTaoistID()
							+ "|level:" + taoistInfo.getLevel());
					return true;
				}
			}
		}
		RobotLog.fail("TaoistHandler 找不到对应的道术");
		return false;
	}
	
	
	public int getTaoistId(Client client) {
		Collections.shuffle(taoistInfoListList);
		for (TaoistInfo taoistInfo : taoistInfoListList) {
			if (taoistInfo.getLevel() >= MAX_LEVEL) {
				continue;
			} else {
				RobotLog.info("TaoistHandler***************|" + client.getUserId()
						+ "|taoisInfo id:" + taoistInfo.getTaoistID()
						+ "|level:" + taoistInfo.getLevel());
				return taoistInfo.getTaoistID();
			}
		}
		return 0;
	}
}
