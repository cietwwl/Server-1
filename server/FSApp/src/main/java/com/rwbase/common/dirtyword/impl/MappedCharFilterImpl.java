package com.rwbase.common.dirtyword.impl;

import java.util.HashMap;
import java.util.Map;

import com.rwbase.common.dirtyword.MappedCharFilter;

/**
 * 基于映射替换的字符过滤实现
 * 得闲整理再抽取
 * @author Rjx
 */
public class MappedCharFilterImpl implements MappedCharFilter {

    private CharNode root;
    private HashMap<String, String> replacedMap = new HashMap<String, String>();

    public String replaceWords(String content) {
        String oldContent = content;
        StringBuilder sb = null;
        int length = content.length();
        int lastDiryIndex = 0;
        for (int i = 0; i < length; i++) {
            char c = content.charAt(i);
            CharNode node = root.getNext(c);
            if (node == null) {
                continue;
            }
            String dirtyString = node.content;
            //第一个符合的下标，记录下先
            int firstIndex = i;
            int index = firstIndex + 1;
            for (; index < length; index++) {
                char nextChar = content.charAt(index);
                CharNode nextNode = node.getNext(nextChar);
                if (nextNode == null) {
                    break;
                }
                String str = nextNode.content;
                if (str != null) {
                    dirtyString = str;
                }
                node = nextNode;
            }
            if (dirtyString != null) {
                if (sb == null) {
                    sb = new StringBuilder(length + 10);
                }
                //将之前的放进去
                for (; lastDiryIndex < firstIndex; lastDiryIndex++) {
                    sb.append(oldContent.charAt(lastDiryIndex));
                }
                sb.append(replacedMap.get(dirtyString));
                i = index - 1;
                lastDiryIndex = index;
            }
        }
        if (sb == null) {
            return oldContent;
        }
        if (lastDiryIndex < length) {
            for (; lastDiryIndex < length; lastDiryIndex++) {
                sb.append(oldContent.charAt(lastDiryIndex));
            }
        }
        return sb.toString();
    }

    public synchronized void init(Map<String, String> map) {
        if (root != null) {
            return;
        }
        CharNodeContainer origal = new CharNodeContainer((char) -1);

        for (String s : map.keySet()) {
            origal.add(s, 0);
        }
        this.root = origal.toCharNode();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            this.replacedMap.put(entry.getKey(), entry.getValue());
        }
    }

    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("/npc1:", "n1");
        map.put("/npc2:", "n2");
        map.put("/npc3:", "n3");
        MappedCharFilterImpl filter = new MappedCharFilterImpl();
        filter.init(map);
        System.out.println(filter.replaceWords("/npc1:你好啊，你是/npc2:的朋友吗，我是/npc3:的朋友啊"));
        System.out.println(filter.replaceWords("/npc1你好啊，你是/npc2的朋友吗，我是/npc3的朋友啊"));
    }
}
