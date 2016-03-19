package com.rwbase.common.dirtyword.impl;

/**
 *
 * @author Rjx
 */
public class IterativeCharNode extends CharNode {

    private final CharNode[] chars;
    private final int size;

    public IterativeCharNode(char key, String content, CharNode[] chars) {
        super(key, content);
        for (int i = chars.length; --i >= 0;) {
            if (chars[i] == null) {
                throw new ExceptionInInitializerError("数组不能包含null..");
            }
        }
        this.chars = new CharNode[chars.length];
        System.arraycopy(chars, 0, this.chars, 0, chars.length);
        this.size = chars.length;
    }

    @Override
    public CharNode getNext(char c) {
        CharNode[] charArray = chars;
        for (int i = size; --i >= 0;) {
            if (charArray[i].key == c) {
                return charArray[i];
            }
        }
        return null;
    }
}
