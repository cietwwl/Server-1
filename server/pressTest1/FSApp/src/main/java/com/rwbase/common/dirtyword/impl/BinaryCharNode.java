package com.rwbase.common.dirtyword.impl;

import java.util.Arrays;

/**
 *
 * @author Rjx
 */
public class BinaryCharNode extends CharNode {

    private final CharNode[] array;
    private final int max;

    public BinaryCharNode(char key, String content, CharNode[] chars) {
        super(key, content);
        Arrays.sort(chars, comparator);
        int length = chars.length;
        this.array = new CharNode[length];
        System.arraycopy(chars, 0, array, 0, length);
        this.max = length - 1;
    }

    @Override
    public CharNode getNext(char c) {
        CharNode[] charNodes = array;
        int right = max;
        int left = 0;
        for (; left <= right;) {
            int mid = left + right >> 1;
            CharNode node = charNodes[mid];
            int nodeKey = node.key;
            if (c > nodeKey) {
                left = mid + 1;
            } else if (c < nodeKey) {
                right = mid - 1;
            } else {
                return node;
            }
        }
        return null;
    }
//    @Override
//    public CharNode getNext(char c) {
//        CharNode current = root;
//        int left = 0;
//        int right = array.length - 1;
//        int index = middleIndex;
//        for (;;) {
//            int currentKey = current.key;
//            if (currentKey == c) {
//                return current;
//            }
//            if (c > currentKey) {
//                if (index == right) {
//                    return null;
//                }
//                left = index;
//                index = (index + right + 1) >> 1;
//            } else {
//                if (index == left) {
//                    return null;
//                }
//                right = index;
//                index = (left + index) >> 1;
//
//            }
//            current = array[index];
//        }
//    }
}
