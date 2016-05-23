package com.rw.service.TaoistMagic;

import com.common.IRandomMgr;
import com.rw.fsutil.common.stream.IStream;
import com.rwbase.dao.fashion.IEffectCfg;
import com.rwproto.TaoistMagicProtos.TaoistInfo;

public interface ITaoistMgr extends IRandomMgr {

	IStream<IEffectCfg> getEff();

	boolean setLevel(int tid, int level);

	int getLevel(int tid);

	Iterable<TaoistInfo> getMagicList();

}
