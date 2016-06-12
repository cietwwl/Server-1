package com.playerdata;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import com.common.RandomMgr;
import com.log.GameLog;
import com.playerdata.common.PlayerEventListener;
import com.rw.fsutil.common.stream.IStream;
import com.rw.fsutil.common.stream.IStreamListner;
import com.rw.fsutil.common.stream.StreamImpl;
import com.rw.service.TaoistMagic.ITaoistMgr;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfg;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicHolder;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicRecord;
import com.rwbase.common.attribute.AttributeItem;
import com.rwproto.TaoistMagicProtos.TaoistInfo;

public class TaoistMgr extends RandomMgr implements PlayerEventListener, ITaoistMgr, IStreamListner<Integer> {
	private StreamImpl<Map<Integer, AttributeItem>> taoistMagicEff = new StreamImpl<Map<Integer, AttributeItem>>();

	@Override
	public IStream<Map<Integer, AttributeItem>> getEff() {
		return taoistMagicEff;
	}

	private Player player;

	@Override
	public void notifyPlayerCreated(Player player) {
		this.player = player;
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		this.player = player;
		TaoistMagicHolder holder = TaoistMagicHolder.getInstance();
		TaoistMagicRecord record = holder.getOrCreate(player.getUserId());
		onEffectChange(record);
	}

	private boolean hasSubscribeLevel = false;
	@Override
	public void init(Player player) {
		this.player = player;
		if (!hasSubscribeLevel){
			player.getLevelNotification().subscribe(this);
			hasSubscribeLevel = true;
		}
	}

	@Override
	public boolean setLevel(int tid, int level) {
		TaoistMagicHolder holder = TaoistMagicHolder.getInstance();
		TaoistMagicRecord record = holder.getOrCreate(player.getUserId());
		boolean result = holder.setLevel(record, tid, level);
		if (result) {
			onEffectChange(record);
		}
		return result;
	}

	private void onEffectChange(TaoistMagicRecord record) {
		Map<Integer, AttributeItem> effects = getEffects(record);
		taoistMagicEff.hold(effects);
		taoistMagicEff.fire(effects);
	}
	
	private Map<Integer, AttributeItem> getEffects(TaoistMagicRecord record) {
		return TaoistMagicCfgHelper.getInstance().getEffectAttr(record.getAll());
	}

	@Override
	public Iterable<TaoistInfo> getMagicList() {
		TaoistMagicHolder holder = TaoistMagicHolder.getInstance();
		TaoistMagicRecord record = holder.getOrCreate(player.getUserId());
		Iterable<Entry<Integer, Integer>> lst = record.getAll();
		ArrayList<TaoistInfo> result = new ArrayList<TaoistInfo>();
		for (Entry<Integer, Integer> entry : lst) {
			TaoistInfo.Builder item = TaoistInfo.newBuilder();
			item.setTaoistID(entry.getKey());
			item.setLevel(entry.getValue());
			result.add(item.build());
		}
		return result;
	}

	@Override
	public int getLevel(int tid) {
		TaoistMagicHolder holder = TaoistMagicHolder.getInstance();
		TaoistMagicRecord record = holder.getOrCreate(player.getUserId());
		Integer val = record.getLevel(tid);
		return val == null ? 1 : val;
	}

	@Override
	public void onChange(Integer newValue) {
		if (newValue == null) return;
		int openLevel = newValue;
		TaoistMagicCfgHelper cfgHelper = TaoistMagicCfgHelper.getInstance();
		Iterable<TaoistMagicCfg> openList = cfgHelper.getOpenList(openLevel);
		if (openList == null) return;
		
		TaoistMagicHolder holder = TaoistMagicHolder.getInstance();
		TaoistMagicRecord record = holder.getOrCreate(player.getUserId());
		for (TaoistMagicCfg taoistMagicCfg : openList) {
			int taoistKey = taoistMagicCfg.getKey();
			Integer oldLevel = record.getLevel(taoistKey);
			if (oldLevel != null) {
				continue;
			}
			boolean result = holder.setLevel(record, taoistKey, 1);
			if (!result) {
				GameLog.error("道术", player.getUserId(), "更新数据失败");
			}
		}
		
		onEffectChange(record);
	}

	@Override
	public void onClose(IStream<Integer> whichStream) {
		// nothing to do, for now
	}
}
