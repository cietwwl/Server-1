package com.rw.handler.GroupCopy.data;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.rw.common.RobotLog;
import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.GroupCopyBattleProto.GroupCopyMonsterData;


/**
 * 帮派副本数据持有类
 * @author Alex
 *
 * 2016年8月5日 上午11:28:18
 */
public class GroupCopyDataHolder {
	

	private SynDataListHolder<GroupCopyLevelRecord> lvDataListHolder = new SynDataListHolder<GroupCopyLevelRecord>(GroupCopyLevelRecord.class);
	
	private SynDataListHolder<GroupCopyMapRecord> mapDataListHolder = new SynDataListHolder<GroupCopyMapRecord>(GroupCopyMapRecord.class);
	
	private SynDataListHolder<CopyItemDropAndApplyRecord> dropItemListHolder = new SynDataListHolder<CopyItemDropAndApplyRecord>(CopyItemDropAndApplyRecord.class);


	public void syn(MsgDataSyn msgDataSyn) {

		eSynType synType = msgDataSyn.getSynType();
		switch (synType) {
		case GROUP_COPY_LEVEL:
			RobotLog.testInfo("接收到服务器同步帮派副本关卡信息,数量:" + msgDataSyn.getSynDataCount());
			lvDataListHolder.Syn(msgDataSyn);
			break;
		case GROUP_COPY_REWARD:
			break;
		case GROUP_COPY_MAP:
			RobotLog.testInfo("接收到服务器同步帮派副本章节信息,数量:" + msgDataSyn.getSynDataCount());
			mapDataListHolder.Syn(msgDataSyn);
			break;
		case GROUP_ITEM_DROP_APPLY:
			RobotLog.testInfo("接收到服务器同步帮派副本战利品信息,数量:" + msgDataSyn.getSynDataCount());
			dropItemListHolder.Syn(msgDataSyn);
			break;

		default:
			break;
		}
	}

	
	public List<GroupCopyMapRecord> getMapRecordList(){
		return mapDataListHolder.getItemList();
	}


	
}
