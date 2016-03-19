package com.playerdata.readonly;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.playerdata.CopyLevelRecordHolder;
import com.playerdata.CopyMapRecordHolder;
import com.playerdata.IRole;
import com.rw.service.copy.CopyRewards;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.cfg.GiftCfg;
import com.rwbase.dao.copy.cfg.GiftCfgDAO;
import com.rwbase.dao.copy.cfg.MapCfg;
import com.rwbase.dao.copy.cfg.MapCfgDAO;
import com.rwbase.dao.copy.common.CopySubType;
import com.rwbase.dao.copy.pojo.CopyMapRecord;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwproto.CopyServiceProtos.ERequestType;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.MsgDef.Command;

/*
 * 副本关卡记录管理接口
 */

public interface CopyRecordMgrIF {
	public CopyRewardsIF getCopyRewards();
	/*
	 * 获取当前用户的副本地图记录,以"0,0,0"记录下"是否领取1,是否领取2,是否领取3"，
	 */
	public List<String> getMapRecordList();

	/*
	 * 获取当前用户的副本关卡记录,以"id_100010,3,0,0"的形式记录下"关卡id,通关星级,今天打的次数,购买次数"的信息
	 */
	public List<String> getLevelRecordList();

	public CopyLevelRecordIF getLevelRecord(int levelID);


	public Boolean isMapClear(int mapID);

	public Boolean isOpen(CopyCfg copyCfg);

	/**
	 * 
	 * @param mapID
	 * @param index
	 *            这里的index是从1开始(不要问我为什么 我也想吐槽)
	 * @return
	 */
	public String getGift(int mapID, int frontIndex);
	// ------------------------------------------------
	public int getMapCurrentStar(int mapID);
	public boolean IsCanSweep(CopyLevelRecordIF copyRecord, CopyCfg copyCfg,int times, ERequestType requestType);
}	
