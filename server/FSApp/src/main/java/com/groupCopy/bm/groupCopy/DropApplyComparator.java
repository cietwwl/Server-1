package com.groupCopy.bm.groupCopy;

import java.util.Comparator;

public class DropApplyComparator implements Comparator<DropApplyInteface>{

	@Override
	public int compare(DropApplyInteface o1, DropApplyInteface o2) {
		if(o1.getTime() < o2.getTime())
			return -1;
		return 1;
	}

}
