package com.playerdata.dataSyn;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.trace.stat.MsgStatCollector;
import com.rw.trace.stat.MsgStatFactory;
import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.MsgDataSynList;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class SynDataInReqMgr {

	private Map<Object, SynDataInfo> synDataMap = new ConcurrentHashMap<Object, SynDataInfo>();
	private List<Object> orderList = new ArrayList<Object>();
	private AtomicLong threadId = new AtomicLong();

	public boolean setInReq() {
		long current = threadId.get();
		if (current != 0) {
			return false;
		}
		return threadId.compareAndSet(0, Thread.currentThread().getId());
	}

	public boolean addSynData(Object serverData, eSynType synType, MsgDataSyn.Builder msgDataSyn) {
		if (!checkInSynThread(synType)) {
			return false;
		}
		SynDataInfo synData = new SynDataInfo(synType, msgDataSyn);
		synDataMap.put(serverData, synData);
		if (!orderList.contains(serverData)) {
			orderList.add(serverData);
		}
		return true;
	}

	private boolean checkInSynThread(eSynType synType) {
		long current = threadId.get();
		if (current == 0) {
			return false;
		}
		if (threadId.get() != Thread.currentThread().getId()) {
			GameLog.error("SynDataInReqMgr", "#checkInSynThread()", "incorrect thread request combin:" + synType + "," + Thread.currentThread());
			return false;
		}
		return true;
	}

	//
	// public boolean doSyn(ChannelHandlerContext ctx, String userId) {
	// ByteString synData = getSynData(ctx, userId);
	// if(synData!=null){
	// nettyControler.sendAyncResponse(userId, ctx, Command.MSG_DATA_SYN, synData);
	// }
	// return true;
	// }

	public ByteString getSynData(ChannelHandlerContext ctx, String userId, Object recordKey) {
		ByteString syndata = null;
		if (ctx == null) {
			return syndata;
		}
		if (!checkInSynThread(null)) {
			return syndata;
		}
		try {
			// 这里临时处理，需要allen整合
			Player player = PlayerMgr.getInstance().find(userId);
			if (player != null) {
				UserTmpGameDataSynMgr.getInstance().synDataByFlag(player);
			}
			Collection<SynDataInfo> values = synDataMap.values();
			if (!values.isEmpty()) {
				MsgDataSynList.Builder msgDataSynList = MsgDataSynList.newBuilder();
				for (Object keyObject : orderList) {
					SynDataInfo synData = synDataMap.get(keyObject);
					if (synData != null) {
						msgDataSynList.addMsgDataSyn(synData.getContent());
					}
				}
				MsgDataSynList synList = msgDataSynList.build();
				syndata = synList.toByteString();
				MsgStatCollector statCollector = MsgStatFactory.getCollector();
				for (int i = synList.getMsgDataSynCount(); --i >= 0;) {
					MsgDataSyn dataSyn = synList.getMsgDataSyn(i);
					eSynType type = dataSyn.getSynType();
					statCollector.recordDataSynSize(type, dataSyn.getSerializedSize(), recordKey);
				}
			}
		} catch (Exception e) {
			GameLog.error(LogModule.COMMON.getName(), userId, "SynDataInReqMgr[doSyn] error synType:", e);
		} finally {
			orderList.clear();
			synDataMap.clear();
			threadId.set(0);
		}
		return syndata;
	}

}
