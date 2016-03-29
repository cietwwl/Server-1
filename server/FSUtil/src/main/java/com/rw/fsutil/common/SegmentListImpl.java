package com.rw.fsutil.common;

import java.util.ArrayList;
import java.util.List;

public class SegmentListImpl<E> extends SegmentEnumeration<E> implements SegmentList<E> {

	public SegmentListImpl(List<E> list, int startIndex, int endIndex) {
		super(list, startIndex, endIndex);
	}

	@Override
	public E get(int index) {
		return list.get(index + startIndex);
	}

	@Override
	public int getRefStartIndex() {
		return startIndex;
	}

	@Override
	public int getRefEndIndex() {
		return endIndex;
	}

	@Override
	public int getRefSize() {
		return size;
	}

	@Override
	public int getMaxSize() {
		return list.size();
	}

	@Override
	public List<E> getSemgentCopy(int start, int end) {
		//这里需要优化
		List<E> subList = list.subList(start, end);
		return new ArrayList<E>(subList);
	}

}
