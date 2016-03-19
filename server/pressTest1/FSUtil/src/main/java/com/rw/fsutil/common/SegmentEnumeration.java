package com.rw.fsutil.common;

import java.util.List;

/**
 * 分段迭代器
 * @author Jamaz
 */
public class SegmentEnumeration<E> implements EnumerateList<E> {

    protected final List<E> list;
    protected final int endIndex;
    protected int startIndex;
    protected final int size;

    public SegmentEnumeration(List<E> list, int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex < 0 || toIndex < fromIndex) {
            throw new IllegalArgumentException("from :" + fromIndex + ",to :" + toIndex);
        }
        this.list = list;
        int last = list.size() - 1;
        if (toIndex > last) {
            toIndex = last;
        }
        int s = toIndex - fromIndex + 1;
        if (s < 0) {
            s = 0;
        }
        this.size = s;
        this.endIndex = toIndex;
        this.startIndex = fromIndex;
    }

    @Override
    public boolean hasMoreElements() {
        return startIndex <= endIndex;
    }

    @Override
    public E nextElement() {
        return list.get(startIndex++);
    }

    @Override
    public int size() {
        return size;
    }
}
