package com.rwbase.common.dirtyword.impl;

/**
 * 
 * @author Rjx
 */
public class ThreeCharNode extends CharNode {

    private final CharNode middle;
    private final CharNode left;
    private final CharNode right;

    public ThreeCharNode(char key, String content, CharNode n1, CharNode n2, CharNode n3) {
        super(key, content);
        CharNode temp = null;
        if (n1.key > n2.key) {
            temp = n1;
            n1 = n2;
            n2 = temp;
        }
        if (n2.key > n3.key) {
            temp = n2;
            n2 = n3;
            n3 = temp;
        }
        if (n1.key > n2.key) {
            temp = n1;
            n1 = n2;
            n2 = temp;
        }
        left = n1;
        middle = n2;
        right = n3;
    }

    @Override
    public CharNode getNext(char c) {
        char m = middle.key;
        if (c == m) {
            return middle;
        }
        if (c > m) {
            return c == right.key ? right : null;
        } else {
            return c == left.key ? left : null;
        }
    }
}
