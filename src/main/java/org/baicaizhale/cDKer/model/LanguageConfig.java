package org.baicaizhale.cDKer.model;

import java.util.Map;

/**
 * LanguageConfig 实体类，用于表示语言文件（如 lang_cn.yml）中的配置。
 */
public class LanguageConfig {
    private Map<String, String> messages;

    /**
     * 构造函数
     * @param messages 语言消息的映射
     */
    public LanguageConfig(Map<String, String> messages) {
        this.messages = messages;
    }

    /**
     * 获取所有语言消息
     * @return 语言消息的映射
     */
    public Map<String, String> getMessages() {
        return messages;
    }

    /**
     * 设置语言消息
     * @param messages 语言消息的映射
     */
    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }

    /**
     * 根据键获取特定的语言消息
     * @param key 消息的键
     * @return 对应的消息字符串，如果不存在则返回 null
     */
    public String getMessage(String key) {
        return messages.get(key);
    }
}