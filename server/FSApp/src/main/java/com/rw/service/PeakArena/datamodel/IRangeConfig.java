package com.rw.service.PeakArena.datamodel;

import com.rw.fsutil.common.IReadOnlyPair;

public interface IRangeConfig {
	IReadOnlyPair<Integer,Integer> getRange();
}
