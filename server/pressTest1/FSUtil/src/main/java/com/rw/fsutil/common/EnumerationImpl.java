package com.rw.fsutil.common;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * 枚举迭代器实现，基于{@link Iterator}
 * @author jamaz
 */
public class EnumerationImpl<E> implements Enumeration<E> {

    private final Iterator<E> it;

    public EnumerationImpl(Iterator<E> it) {
        this.it = it;
    }

    @Override
    public boolean hasMoreElements() {
        return it.hasNext();
    }

    @Override
    public E nextElement() {
        return it.next();
    }
}
