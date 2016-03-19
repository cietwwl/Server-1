package com.rwbase.common.dirtyword.impl;

import java.util.Arrays;

/**
 * 
 * @author Rjx
 */
public class MappingCharNode extends CharNode {

    private final CharNode[] array;
    private final int max;
    private final int offset;

    public MappingCharNode(char key, String content, CharNode[] chars) {
        super(key, content);
        Arrays.sort(chars, comparator);
        CharNode first_ = chars[0];
        CharNode last_ = chars[chars.length - 1];
        this.offset = first_.key;
        this.max = last_.key - offset;
        this.array = new CharNode[max + 1];
        for (int i = chars.length; --i >= 0;) {
            CharNode current = chars[i];
            array[current.key - offset] = current;
        }
    }

    @Override
    public CharNode getNext(char c) {
        int index = c - offset;
        if (index < 0 || index > max) {
            return null;
        }
        return array[index];
    }

    @Override
    public String toString() {
        return "length:" + array.length + "";
    }
}
