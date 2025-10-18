package org.baicaizhale.cDKer.manager;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.model.LanguageConfig;
import org.baicaizhale.cDKer.model.PluginConfig;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

/**
 * 配置管理类，负责加载、保存和管理插件的配置文件。
 */
public class ConfigurationManager {

    private final CDKer plugin;
    private PluginConfig pluginConfig;
    private Map<String, LanguageConfig> languageConfigs;

    /**
     * 构造函数
     * @param plugin 插件主类实例
     */
    public ConfigurationManager(CDKer plugin) {
        this.plugin = plugin;
        this.languageConfigs = new HashMap<>();
    }

    /**
     * 加载所有配置文件。
     */
    public void loadAllConfigs() {
        loadPluginConfig();
        loadLanguageConfigs();
    }

    /**
     * 重新加载所有配置文件。
     */
    public void reloadAllConfigs() {
        loadAllConfigs();
    }

    /**
     * 加载 plugin.yml 配置。
     */
    private void loadPluginConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        YamlConfiguration configYaml = YamlConfiguration.loadConfiguration(configFile);
        String language = configYaml.getString("cdk.language", "zh_CN");
        String prefix = configYaml.getString("cdk.prefix", "&bCDKer &7> &f");
        this.pluginConfig = new PluginConfig(language, prefix);
        plugin.getLogger().info("Loaded config.yml: language=" + language + ", prefix=" + prefix);
    }

    /**
     * 加载语言文件。
     */
    private void loadLanguageConfigs() {
        languageConfigs.clear();
        
        // Load Simplified Chinese language file (lang_zh_CN.yml)
        File langZhCNFile = new File(plugin.getDataFolder(), "lang" + File.separator + "lang_zh_CN.yml");
        if (!langZhCNFile.exists()) {
            plugin.saveResource("lang" + File.separator + "lang_zh_CN.yml", false);
        }
        YamlConfiguration langZhCNYaml = YamlConfiguration.loadConfiguration(langZhCNFile);
        Map<String, String> zhCNMessages = new HashMap<>();
        for (String key : langZhCNYaml.getKeys(true)) {
            if (langZhCNYaml.isString(key)) {
                zhCNMessages.put(key, langZhCNYaml.getString(key));
            }
        }
        languageConfigs.put("zh_CN", new LanguageConfig(zhCNMessages));
        plugin.getLogger().info("Loaded lang_zh_CN.yml with " + zhCNMessages.size() + " messages.");

        // Load English language file (lang_en_US.yml)
        File langEnUsFile = new File(plugin.getDataFolder(), "lang" + File.separator + "lang_en_US.yml");
        if (!langEnUsFile.exists()) {
            plugin.saveResource("lang" + File.separator + "lang_en_US.yml", false);
        }
        YamlConfiguration langEnUsYaml = YamlConfiguration.loadConfiguration(langEnUsFile);
        Map<String, String> enUsMessages = new HashMap<>();
        for (String key : langEnUsYaml.getKeys(true)) {
            if (langEnUsYaml.isString(key)) {
                enUsMessages.put(key, langEnUsYaml.getString(key));
            }
        }
        languageConfigs.put("en_US", new LanguageConfig(enUsMessages));
        plugin.getLogger().info("Loaded lang_en_US.yml with " + enUsMessages.size() + " messages.");
    }

    /**
     * 获取插件配置。
     * @return 插件配置对象
     */
    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    /**
     * 根据语言代码获取语言配置。
     * @param languageCode 语言代码
     * @return 语言配置对象
     */
    public LanguageConfig getLanguageConfig(String languageCode) {
        return languageConfigs.getOrDefault(languageCode, 
            languageConfigs.get("zh_CN")); // 默认使用中文配置
    }

    /**
     * 获取所有语言配置。
     * @return 语言配置映射
     */
    public Map<String, LanguageConfig> getLanguageConfigs() {
        return languageConfigs;
    }

    /**
     * 保存CDK配置。
     */
    public void saveCdkConfig() {
        // 数据库模式下不需要保存CDK配置到YAML文件
        // 此方法保留以避免调用时出现错误
    }

    /**
     * 保存所有已使用的CDK配置。
     */
    public void saveUsedCodesConfig() {
        // 数据库模式下不需要保存使用记录到YAML文件
        // 此方法保留以避免调用时出现错误
    }

    /**
     * 保存所有配置。
     */
    public void saveAllConfigs() {
        saveCdkConfig();
        saveUsedCodesConfig();
    }
}