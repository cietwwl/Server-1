package com.playerdata;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import com.common.RandomMgr;
import com.playerdata.common.PlayerEventListener;
import com.rw.fsutil.common.stream.IStream;
import com.rw.fsutil.common.stream.StreamImpl;
import com.rw.service.TaoistMagic.ITaoistMgr;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicHolder;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicRecord;
import com.rwbase.common.attribute.AttributeItem;
import com.rwproto.TaoistMagicProtos.TaoistInfo;

public class TaoistMgr extends RandomMgr implements PlayerEventListener, ITaoistMgr {
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
	}

	@Override
	public void init(Player player) {
		this.player = player;
	}

	@Override
	public boolean setLevel(int tid, int level) {
		TaoistMagicHolder holder = TaoistMagicHolder.getInstance();
		TaoistMagicRecord record = holder.getOrCreate(player.getUserId());
		boolean result = holder.setLevel(record, tid, level);
		if (result) {
			// IEffectCfg old = taoistMagicEff.sample();
			// IEffectCfg newVal = getEffects(record);
			Map<Integer, AttributeItem> effects = getEffects(record);
			taoistMagicEff.fire(effects);
			// if ((old == null && newVal != null) || !old.equals(newVal)) {
			// taoistMagicEff.fire(newVal);
			// }
		}
		return result;
	}

	// private IEffectCfg getEffects(TaoistMagicRecord record){
	// Iterable<Entry<Integer, Integer>> lst = record.getAll();
	// IEffectCfg result = TaoistMagicCfgHelper.getInstance().getEffect(lst);
	// return result;
	// }

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
}
