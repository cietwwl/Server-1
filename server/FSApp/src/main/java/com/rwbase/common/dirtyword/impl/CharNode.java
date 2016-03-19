package com.rwbase.common.dirtyword.impl;

import java.util.Comparator;

/**
 * 字符节点
 * @author Rjx
 */
public abstract class CharNode {

    public final char key;
    public final String content;
    public static final Comparator<CharNode> comparator = new Comparator<CharNode>() {

        @Override
        public int compare(CharNode o1, CharNode o2) {
            return o1.key > o2.key ? 1 : -1;
        }
    };

    public CharNode(char key, String content) {
        this.key = key;
        this.content = content;
        CharFilterImpl.count(getClass());
    }

    @Override
    public String toString() {
        return String.valueOf(key) + "," + content;
    }

    public abstract CharNode getNext(char c);
}
