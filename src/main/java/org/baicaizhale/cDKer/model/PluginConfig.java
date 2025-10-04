package org.baicaizhale.cDKer.model;

/**
 * PluginConfig 实体类，用于表示 config.yml 中的插件配置。
 */
public class PluginConfig {
    private String language;
    private String prefix;

    /**
     * 构造函数
     * @param language 插件语言
     * @param prefix 消息前缀
     */
    public PluginConfig(String language, String prefix) {
        this.language = language;
        this.prefix = prefix;
    }

    /**
     * 获取插件语言
     * @return 插件语言
     */
    public String getLanguage() {
        return language;
    }

    /**
     * 设置插件语言
     * @param language 插件语言
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * 获取消息前缀
     * @return 消息前缀
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 设置消息前缀
     * @param prefix 消息前缀
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}