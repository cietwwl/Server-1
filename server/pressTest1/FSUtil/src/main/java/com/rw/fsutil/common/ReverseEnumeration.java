package com.rw.fsutil.common;

import java.util.Enumeration;
import java.util.List;

/**
 * 基于{@link List}的逆序枚举迭代器
 * @author Jamaz
 */
public class ReverseEnumeration<E> implements Enumeration<E> {

    private final List<E> list;
    private int index;

    public ReverseEnumeration(List<E> list) {
        this.list = list;
        this.index = list.size() - 1;
    }

    @Override
    public boolean hasMoreElements() {
        return index >= 0;
    }

    @Override
    public E nextElement() {
        return list.get(index--);
    }
}
