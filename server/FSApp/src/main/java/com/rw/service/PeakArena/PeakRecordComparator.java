package com.rw.service.PeakArena;

import java.util.Comparator;

import com.rw.service.PeakArena.datamodel.PeakRecordInfo;

public class PeakRecordComparator implements Comparator<PeakRecordInfo> {

	@Override
	public int compare(PeakRecordInfo o1, PeakRecordInfo o2) {
		return o1.getTime() > o2.getTime() ? -1 : 1;
	}

}
