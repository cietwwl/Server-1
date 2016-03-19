package com.rwbase.common.dirtyword;

/**
 * <pre>
 * 字符过滤器
 * 包括隐形字符、脏词、敏感词、脏词与敏感词的连接字符，由配置统一管理
 * 
 * ============= 隐形字符 ====================
 * 隐形字符：主要是指手机上或者PC上可能显示不了的字符，例如回车符，换行符等。
 * 剔除隐形字符，主要是防止玩家使用隐形字符以达到逃避脏词屏蔽的目的。
 * 
 * ============= 脏词 =======================
 * 脏词是指不允许出现在游戏中的字符,主要是玩家输入的字符;例如聊天,注册家族,注册角色,发送邮件等;
 * 被检查内容中的脏词可以被检查,被替换
 * 
 * ============= 敏感词 =======================
 * 敏感词是指不允许出现在游戏中作为名称的字符,主要是玩家输入的字符;例如注册角色,注册家族等;
 * 被检查内容中的敏感词可以被检查,不能被替换
 * 
 * ============ 脏词、敏感词的连接字符 ==============
 * 指在脏词、敏感词中添加连接字符来逃避检查。比如把空格定义为脏词、敏感词的连接字符
 * "fuck"被过滤，"f u c k"也会被过滤。但正常字符不会受影响，"你 好 吗"还是按"你 好 吗"输出
 * 
 * =========== 关于使用 ========================
 * 用于交流类的输入(例如频道聊天,邮件,个人描述等):应该进行隐形字符剔除,然后进行脏词替换(不需要处理敏感字)
 * 用于名称注册类的输入(例如注册角色,注册家族等):应该进行隐形字符检查、敏感词检查、脏词检查
 * 
 * ============ 关于性能 =======================
 * 大小写敏感的性能比忽略大小的性能更高，可以在不包含英文的场合使用大小写敏感的方法
 * </pre>
 * @author Rjx
 */
public interface CharFilter {

    /**
     * <pre>
     * 使用指定的词语替换内容中所有脏词，如果内容不包括脏词(或隐性字符)，返回原参数对象
     * 脏词由脏词表定义，另外可定义脏词的连接字符，如空格：fuck被规律，f u c k也会被过滤
     * 如被检查内容为fuck you，用于替换脏词的词语是"[屏蔽]"，替换后返回"[屏蔽]you"
     * 可以用于聊天、邮件内容的过滤
     * </pre>
     * @param content           被检查的内容           
     * @param replaceWord       用于替换脏词的词语
     * @param isClearUnseeChar  是否清除所有的隐性字符
     * @return 
     */
    public String replaceDiryWords(String content, String replaceWord, boolean isClearUnseeChar, boolean isIgnoreCase);

    /**
     * <pre>
     * 检查是否含有被过滤字符，可以通过参数控制检查的内容：包括隐性字符、敏感词、脏词，如果包含其中一项，都会返回true
     * 可以用于角色名字、公会名字、宠物名字等由玩家定义名字的检查
     * 注：isIgnoreCase只为checkSensitiveWord与checkDirtyWord服务，如果checkSensitiveWord与checkDirtyWord都为false，那么isIgnoreCase就没有意义
     * </pre>
     * @param content               被检查的内容
     * @param checkUnseeChar        是否检查隐性字符
     * @param checkSensitiveWord    是否检查敏感词
     * @param checkDirtyWord        是否检查脏词
     * @param isIgnoreCase          是否忽略大小写
     * @return 
     */
    public boolean checkWords(String content, boolean checkUnseeChar, boolean checkSensitiveWord, boolean checkDirtyWord, boolean isIgnoreCase);

    /**
     * 移除内容中的所有隐性字符，如果不包含隐性字符，返回原参数对象
     * @param content
     * @return 
     */
    public String removeUnseeChar(String content);

    /**
     * 检查是否包含脏词，返回首先包含的脏词，若不包含，返回null
     * @param content       被检查的内容
     * @param isIgnoreCase  是否忽略大小写
     * @return 
     */
    public String checkDirtyWord(String content, boolean isIgnoreCase);

    /**
     * 检查是否包含敏感词，返回首先包含的敏感词，若不包含，返回null
     * @param content       被检查的内容
     * @param isIgnoreCase  是否忽略大小写
     * @return 
     */
    public String checkSensitiveWord(String content, boolean isIgnoreCase);

    /**
     * 构造一个映射的字符集
     * @return 
     */
    public MappedCharFilter newMappedCharFilter();
}
