package com.rwbase.common.dirtyword.impl;

/**
 * 
 * @author Rjx
 */
public class SingleCharNode extends CharNode {

    private final CharNode next;

    public SingleCharNode(char key, String content,  CharNode c) {
        super(key, content);
        this.next = c;
    }

    @Override
    public CharNode getNext(char c) {
        if (c == next.key) {
            return next;
        }
        return null;
    }
}
