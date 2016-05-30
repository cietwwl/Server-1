package com.playerdata.dataSyn;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.controler.FsNettyControler;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.MsgDataSynList;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.MsgDef.Command;

public class SynDataInReqMgr {

	private static FsNettyControler nettyControler = SpringContextUtil.getBean("fsNettyControler");
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

	public boolean doSyn(ChannelHandlerContext ctx, String userId) {
		if (ctx == null) {
			return false;
		}
		if (!checkInSynThread(null)) {
			return false;
		}
		try {
			// 这里临时处理，需要allen整合
			Player player = PlayerMgr.getInstance().find(userId);
			if (player != null) {
				UserTmpGameDataSynMgr.getInstance().synDataByFlag(player);
			}
			Collection<SynDataInfo> values = synDataMap.values();
			if (values.isEmpty()) {
				return true;
			}
			MsgDataSynList.Builder msgDataSynList = MsgDataSynList.newBuilder();
			for (Object keyObject : orderList) {
				SynDataInfo synData = synDataMap.get(keyObject);
				if (synData != null) {
					msgDataSynList.addMsgDataSyn(synData.getContent());
				}
			}
			nettyControler.sendAyncResponse(userId, ctx, Command.MSG_DATA_SYN, msgDataSynList.build().toByteString());
			return true;
		} catch (Exception e) {
			GameLog.error(LogModule.COMMON.getName(), userId, "SynDataInReqMgr[doSyn] error synType:", e);
			return false;
		} finally {
			orderList.clear();
			synDataMap.clear();
			threadId.set(0);
		}
	}

}
