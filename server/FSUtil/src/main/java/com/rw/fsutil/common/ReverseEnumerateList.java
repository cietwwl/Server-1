package com.rw.fsutil.common;

import java.util.List;

/**
 * 逆序迭代器实现
 * @author Jamaz
 */
public class ReverseEnumerateList<E> extends ReverseEnumeration<E> implements EnumerateList<E> {

    private final int size;

    public ReverseEnumerateList(List<E> list, int size) {
        super(list);
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }
}
