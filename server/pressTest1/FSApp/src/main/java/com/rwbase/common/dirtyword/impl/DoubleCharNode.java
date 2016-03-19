package com.rwbase.common.dirtyword.impl;

/**
 * 
 * @author Rjx
 */
public class DoubleCharNode extends CharNode {

    private final CharNode next1;
    private final CharNode next2;

    public DoubleCharNode(char key, String content, CharNode c1, CharNode c2) {
        super(key, content);
        this.next1 = c1;
        this.next2 = c2;
    }

    @Override
    public CharNode getNext(char c) {
        if (next1.key == c) {
            return next1;
        }
        if (next2.key == c) {
            return next2;
        }
        return null;
    }
}
