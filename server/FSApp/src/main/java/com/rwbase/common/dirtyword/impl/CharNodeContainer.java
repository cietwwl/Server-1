package com.rwbase.common.dirtyword.impl;

import java.util.HashMap;

/**
 *
 * @author Rjx
 */
public class CharNodeContainer {

    private char key;
    private String content;
    private HashMap<Character, CharNodeContainer> map = new HashMap<Character, CharNodeContainer>();

    public CharNodeContainer(char key) {
        this.key = key;
    }

    public void add(String str, int index) {
        char c = str.charAt(index);
        Character character = c;
        CharNodeContainer node = map.get(character);
        if (node == null) {
            node = new CharNodeContainer(character);
            map.put(character, node);
        }
        int nextIndex = index + 1;
        if (nextIndex >= str.length()) {
            node.content = str;
        } else {
            node.add(str, nextIndex);
        }
    }

    public String getContent() {
        return this.content;
    }

    public CharNode toCharNode() {
        int size = map.size();
        if (size == 0) {
            return new LeafCharNode(key, content);
        }
        if (size == 1) {
            for (CharNodeContainer c : map.values()) {
                return new SingleCharNode(key, content, c.toCharNode());
            }
        }
        if (size == 2) {
            CharNode one = null;
            CharNode two = null;
            for (CharNodeContainer c : map.values()) {
                if (one == null) {
                    one = c.toCharNode();
                } else {
                    two = c.toCharNode();
                }
            }
            return new DoubleCharNode(key, content, one, two);
        }
        if (size == 3) {
            CharNode one = null;
            CharNode two = null;
            CharNode three = null;
            for (CharNodeContainer c : map.values()) {
                if (one == null) {
                    one = c.toCharNode();
                } else if (two == null) {
                    two = c.toCharNode();
                } else {
                    three = c.toCharNode();
                }
            }
            return new ThreeCharNode(key, content, one, two, three);
        }

        CharNode[] node = new CharNode[size];
        int count = 0;
        for (CharNodeContainer c : map.values()) {
            node[count++] = c.toCharNode();
        }
        if (count < 8) {
            return new IterativeCharNode(key, content, node);
        } else {
//            if (count < 100000) {
            int minChar = 0;
            int maxChar = 0;
            for (int i = size; --i >= 0;) {
                int current = node[i].key;
                if (current > maxChar) {
                    maxChar = current;
                } else if (current < minChar) {
                    minChar = current;
                }
            }
//                if ((maxChar - minChar) > (size * HashCharNode.MAX_FILLING_TIMES)) {
            return new HashCharNode(key, content, node);
//                }
//            }
//            return new MappingCharNode(key, content, node);
        }
    }
}
