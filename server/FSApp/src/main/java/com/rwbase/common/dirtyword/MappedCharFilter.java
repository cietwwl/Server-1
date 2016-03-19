package com.rwbase.common.dirtyword;

import java.util.Map;

/**
 * 映射过滤字符集
 * @author Rjx
 */
public interface MappedCharFilter {

    /**
     * 替换词语
     * @param content
     * @return 
     */
    public String replaceWords(String content);

    /**
     * 初始化
     * @param map 
     */
    public void init(Map<String, String> map);
}
