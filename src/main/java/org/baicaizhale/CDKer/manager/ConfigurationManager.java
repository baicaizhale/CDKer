package org.baicaizhale.CDKer.manager;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.model.LanguageConfig;
import org.baicaizhale.CDKer.model.PluginConfig;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 配置管理类，负责加载、保存和管理插件的配置文件。
 */
public class ConfigurationManager {

    private final CDKer plugin;
    private PluginConfig pluginConfig;
    private Map<String, LanguageConfig> languageConfigs;

    public ConfigurationManager(CDKer plugin) {
        this.plugin = plugin;
        this.languageConfigs = new HashMap<>();
    }

    public void loadAllConfigs() {
        loadPluginConfig();
        loadLanguageConfigs();
    }

    public void reloadAllConfigs() {
        String oldDbType = plugin.getConfig().getString("cdk.database.type", "sqlite");
        // 重新从磁盘读取 config.yml，让所有 plugin.getConfig() 的读取方拿到新值
        plugin.reloadConfig();
        loadAllConfigs();
        String newDbType = plugin.getConfig().getString("cdk.database.type", "sqlite");
        if (!oldDbType.equals(newDbType)) {
            plugin.getLogger().warning("检测到数据库类型变更，需要重启服务器才能生效");
        }
    }

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

    private void loadLanguageConfigs() {
        languageConfigs.clear();
        languageConfigs.put("zh_CN", new LanguageConfig(loadLanguageMessages("lang_zh_CN.yml")));
        languageConfigs.put("en_US", new LanguageConfig(loadLanguageMessages("lang_en_US.yml")));
        languageConfigs.put("ja_JP", new LanguageConfig(loadLanguageMessages("lang_ja_JP.yml")));
    }

    /**
     * 读取某个语言文件。
     * 磁盘上的语言文件缺少的键会回退到 jar 内置的同名语言文件，
     * 保证插件升级后新增的消息键在老配置目录里也有兜底文案（不会显示为空行）。
     */
    private Map<String, String> loadLanguageMessages(String fileName) {
        String resourcePath = "lang/" + fileName;
        File langFile = new File(plugin.getDataFolder(), "lang" + File.separator + fileName);
        if (!langFile.exists()) {
            plugin.saveResource(resourcePath, false);
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(langFile);
        YamlConfiguration defaults = loadBundledLanguage(resourcePath);
        if (defaults != null) {
            yaml.setDefaults(defaults);
        }

        Set<String> keys = new LinkedHashSet<>(yaml.getKeys(true));
        if (defaults != null) {
            keys.addAll(defaults.getKeys(true));
        }

        Map<String, String> messages = new HashMap<>();
        for (String key : keys) {
            boolean stringInFile = yaml.isString(key);
            boolean stringInDefaults = defaults != null && defaults.isString(key);
            if (!stringInFile && !stringInDefaults) {
                continue;
            }
            // yaml.getString 会优先取磁盘文件的值，缺失时由 defaults 兜底
            String value = yaml.getString(key);
            if (value != null) {
                messages.put(key, value);
            }
        }
        return messages;
    }

    private YamlConfiguration loadBundledLanguage(String resourcePath) {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) {
                return null;
            }
            return YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            plugin.getLogger().warning("读取内置语言文件失败: " + resourcePath + " - " + e.getMessage());
            return null;
        }
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public LanguageConfig getLanguageConfig(String languageCode) {
        return languageConfigs.getOrDefault(languageCode,
            languageConfigs.get("zh_CN"));
    }
}
