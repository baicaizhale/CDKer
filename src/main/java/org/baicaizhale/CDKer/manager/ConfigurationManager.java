package org.baicaizhale.CDKer.manager;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.model.LanguageConfig;
import org.baicaizhale.CDKer.model.PluginConfig;
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

    public ConfigurationManager(CDKer plugin) {
        this.plugin = plugin;
        this.languageConfigs = new HashMap<>();
    }

    public void loadAllConfigs() {
        loadPluginConfig();
        loadLanguageConfigs();
    }

    public void reloadAllConfigs() {
        loadAllConfigs();
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

        File langJaJPFile = new File(plugin.getDataFolder(), "lang" + File.separator + "lang_ja_JP.yml");
        if (!langJaJPFile.exists()) {
            plugin.saveResource("lang" + File.separator + "lang_ja_JP.yml", false);
        }
        YamlConfiguration langJaJPYaml = YamlConfiguration.loadConfiguration(langJaJPFile);
        Map<String, String> jaJPMessages = new HashMap<>();
        for (String key : langJaJPYaml.getKeys(true)) {
            if (langJaJPYaml.isString(key)) {
                jaJPMessages.put(key, langJaJPYaml.getString(key));
            }
        }
        languageConfigs.put("ja_JP", new LanguageConfig(jaJPMessages));
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public LanguageConfig getLanguageConfig(String languageCode) {
        return languageConfigs.getOrDefault(languageCode,
            languageConfigs.get("zh_CN"));
    }
}
