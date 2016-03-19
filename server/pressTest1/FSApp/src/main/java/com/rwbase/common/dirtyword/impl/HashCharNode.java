package com.rwbase.common.dirtyword.impl;

import java.util.Hashtable;
import java.util.TreeMap;

/**
 *
 * @author Rjx
 */
public class HashCharNode extends CharNode {

    public static final int MAX_FILLING_TIMES = 0;
    private final CharNodeEntry[] etnryArray;
    private final int length;
    public static TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
    private static volatile long totalLength;

    public HashCharNode(char key, String content, CharNode[] array) {
        super(key, content);
        int length_ = array.length;
        
//        int length_ = PrimeFinder.nextPrime(array.length << 1);
//        int length_ = findPower(array.length);
        CharNodeEntry[] total = new CharNodeEntry[array.length];
        for (int i = array.length; --i >= 0;) {
            total[i] = new CharNodeEntry(array[i]);
        }
        int i = 0;
        CharNodeEntry[] currentArray = null;
        for (; i <= MAX_FILLING_TIMES; i++) {
//            length_ += array.length * 20;
            length_ = PrimeFinder.nextPrime(length_ + length_/5);
//            length_ = findPower(length_) ;
            currentArray = new CharNodeEntry[length_];
            int j = total.length;
            boolean reuslt = true;
            label:
            for (; --j >= 0;) {
                CharNodeEntry newEntry = total[j];
//                int index = hash(newEntry.node.key) % length_;
                int index = newEntry.node.key % length_;
//                int index = indexOf(newEntry.node.key, length_);
                CharNodeEntry entry = currentArray[index];
                if (entry == null) {
                    currentArray[index] = newEntry;
                    Integer value = map.get(1);
                    if (value == null) {
                        value = 1;
                    } else {
                        value = value + 1;
                    }
                    map.put(1, value);
                } //                else if (i < MAX_FILLING_TIMES) {
                //                    reuslt = false;
                //                    break label;
                //                } 
                else {
                    int count = 2;
                    CharNodeEntry next = entry.next;
                    for (; next != null;) {
                        count++;
                        entry = next;
                        next = entry.next;
                    }
                    entry.next = newEntry;
                    Integer value = map.get(count);
                    if (value == null) {
                        value = 1;
                    } else {
                        value = value + 1;
                    }
                    map.put(count, value);
                }
            }
            if (reuslt) {
                break;
            }
        }
        this.etnryArray = currentArray;
        this.length = length_;
//        Integer value = map.get(i);
//        if (value == null) {
//            value = 1;
//        } else {
//            value = value + 1;
//        }
//        map.put(i, value);
        totalLength += currentArray.length;
        System.out.println("打印：" + map + ",节点数：" + array.length + ",长度：" + etnryArray.length + ",总长度：" + totalLength);
    }

    @Override
    public CharNode getNext(char c) {
        int index = c % this.length;
        CharNodeEntry[] array = this.etnryArray;
        CharNodeEntry entry = array[index];
        if (entry == null) {
            return null;
        }
        if (entry.node.key == c) {
            return entry.node;
        }
        entry = entry.next;
        for (; entry != null;) {
            if (entry.node.key == c) {
                return entry.node;
            }
            entry = entry.next;
        }
        return null;
    }

    private int findPower(int initialCapacity) {
        int capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        int r = capacity;
//        System.out.println(capacity+","+r);
        return r;
    }

    private int hash(int h){
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }
    
    private int indexOf(int h, int length) {
        h ^= (h >>> 20) ^ (h >>> 12);
        h = h ^ (h >>> 7) ^ (h >>> 4);
        return h & (length - 1);
//        return h % length;
    }

    static class CharNodeEntry {

        private final CharNode node;
        private CharNodeEntry next;

        public CharNodeEntry(CharNode node) {
            this.node = node;
        }
    }
}
