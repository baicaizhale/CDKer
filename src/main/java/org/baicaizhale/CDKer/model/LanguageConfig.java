package org.baicaizhale.CDKer.model;

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
    public String getMessage(String key) {
        return messages.getOrDefault(key, "");
    }
}