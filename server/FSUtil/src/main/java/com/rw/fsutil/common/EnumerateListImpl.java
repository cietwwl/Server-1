package com.rw.fsutil.common;

import java.util.Iterator;

/**
 * 列表迭代器实现
 * @author Jamaz
 */
public class EnumerateListImpl<E> extends EnumerationImpl<E> implements EnumerateList<E> {

    private final int size;

    public EnumerateListImpl(Iterator<E> it, int size) {
        super(it);
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }
}
