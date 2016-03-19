package com.rwbase.common.dirtyword.impl;

/**
 *
 * @author Rjx
 */
public class LeafCharNode extends CharNode {

    public LeafCharNode(char key, String content) {
        super(key, content);
    }

    @Override
    public CharNode getNext(char c) {
        return null;
    }
}
