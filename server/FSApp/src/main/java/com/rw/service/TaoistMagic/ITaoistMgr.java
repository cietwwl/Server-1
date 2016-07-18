package com.rw.service.TaoistMagic;

import java.util.Map;

import com.common.IRandomMgr;
import com.rw.fsutil.common.stream.IStream;
import com.rwbase.common.attribute.AttributeItem;
import com.rwproto.TaoistMagicProtos.TaoistInfo;

public interface ITaoistMgr extends IRandomMgr {

	IStream<Map<Integer, AttributeItem>> getEff();

	boolean setLevel(int tid, int level);

	int getLevel(int tid);

	Iterable<TaoistInfo> getMagicList();

	public Map<Integer, AttributeItem> getTaoistAttrMap();
}
