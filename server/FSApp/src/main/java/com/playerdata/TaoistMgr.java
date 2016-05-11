package com.playerdata;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.common.RandomMgr;
import com.playerdata.common.PlayerEventListener;
import com.rw.fsutil.common.stream.IStream;
import com.rw.fsutil.common.stream.StreamImpl;
import com.rw.service.TaoistMagic.ITaoistMgr;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicHolder;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicRecord;
import com.rwbase.dao.fashion.IEffectCfg;
import com.rwproto.TaoistMagicProtos.TaoistInfo;

public class TaoistMgr extends RandomMgr implements PlayerEventListener,ITaoistMgr{
	private StreamImpl<IEffectCfg> taoistMagicEff;
	@Override
	public IStream<IEffectCfg> getEff(){
		return taoistMagicEff;
	}
	
	private Player player;
	@Override
	public void notifyPlayerCreated(Player player) {
		// TODO Auto-generated method stub
		this.player = player;
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		// TODO Auto-generated method stub
		this.player = player;
	}

	@Override
	public void init(Player player) {
		// TODO Auto-generated method stub
		this.player = player;
	}

	@Override
	public boolean setLevel(int tid, int level) {
		TaoistMagicHolder holder = TaoistMagicHolder.getInstance();
		TaoistMagicRecord record = holder.getOrCreate(player.getUserId());
		return holder.setLevel(record,tid, level);
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
