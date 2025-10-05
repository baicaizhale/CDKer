package org.baicaizhale.cDKer.manager;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.model.CDK;
import org.baicaizhale.cDKer.model.LanguageConfig;
import org.baicaizhale.cDKer.model.PluginConfig;
import org.baicaizhale.cDKer.model.UsedCodeRecord;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * 配置管理类，负责加载、保存和管理插件的所有配置文件。
 */
public class ConfigurationManager {

    private final CDKer plugin;
    private PluginConfig pluginConfig;
    private Map<String, CDK> cdkMap;
    private Set<UsedCodeRecord> usedCodeRecords;
    private Map<String, LanguageConfig> languageConfigs;
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * 构造函数
     * @param plugin 插件主类实例
     */
    public ConfigurationManager(CDKer plugin) {
        this.plugin = plugin;
        this.cdkMap = new HashMap<>();
        this.usedCodeRecords = new HashSet<>();
        this.languageConfigs = new HashMap<>();
    }

    /**
     * 加载所有配置文件。
     */
    public void loadAllConfigs() {
        loadPluginConfig();
        loadCdkConfig();
        loadUsedCodesConfig();
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
        String language = configYaml.getString("language", "en");
        String prefix = configYaml.getString("prefix", "&bCDKer &7> &f");
        this.pluginConfig = new PluginConfig(language, prefix);
        plugin.getLogger().info("Loaded config.yml: language=" + language + ", prefix=" + prefix);
    }

    /**
     * 加载 cdk.yml 配置。
     */
    private void loadCdkConfig() {
        File cdkFile = new File(plugin.getDataFolder(), "cdk.yml");
        if (!cdkFile.exists()) {
            plugin.saveResource("cdk.yml", false);
        }
        YamlConfiguration cdkYaml = YamlConfiguration.loadConfiguration(cdkFile);
        cdkMap.clear();
        ConfigurationSection cdkSection = cdkYaml.getRoot();
        if (cdkSection != null) {
            for (String key : cdkSection.getKeys(false)) {
                ConfigurationSection cdkEntry = cdkSection.getConfigurationSection(key);
                if (cdkEntry != null) {
                    String type = cdkEntry.getString("type");
                    List<String> commands = cdkEntry.getStringList("commands");
                    int remainingUses = cdkEntry.getInt("remainingUses");
                    String expiration = cdkEntry.getString("expiration");
                    cdkMap.put(key, new CDK(type, commands, remainingUses, expiration));
                }
            }
        }
        plugin.getLogger().info("Loaded cdk.yml with " + cdkMap.size() + " CDKs.");
    }

    /**
     * 保存 cdk.yml 配置。
     */
    public void saveCdkConfig() {
        File cdkFile = new File(plugin.getDataFolder(), "cdk.yml");
        YamlConfiguration cdkYaml = new YamlConfiguration();
        for (Map.Entry<String, CDK> entry : cdkMap.entrySet()) {
            String key = entry.getKey();
            CDK cdk = entry.getValue();
            cdkYaml.set(key + ".type", cdk.getType());
            cdkYaml.set(key + ".commands", cdk.getCommands());
            cdkYaml.set(key + ".remainingUses", cdk.getRemainingUses());
            if (cdk.getExpiration() != null) {
                cdkYaml.set(key + ".expiration", cdk.getExpiration());
            }
        }
        try {
            cdkYaml.save(cdkFile);
            plugin.getLogger().info("Saved cdk.yml.");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save cdk.yml", e);
        }
    }

    /**
     * 加载 used_codes.yml 配置。
     */
    private void loadUsedCodesConfig() {
        File usedCodesFile = new File(plugin.getDataFolder(), "used_codes.yml");
        if (!usedCodesFile.exists()) {
            plugin.saveResource("used_codes.yml", false);
        }
        YamlConfiguration usedCodesYaml = YamlConfiguration.loadConfiguration(usedCodesFile);
        usedCodeRecords.clear();
        ConfigurationSection playersSection = usedCodesYaml.getRoot();
        if (playersSection != null) {
            for (String playerName : playersSection.getKeys(false)) {
                ConfigurationSection cdkSection = playersSection.getConfigurationSection(playerName);
                if (cdkSection != null) {
                    for (String cdkCode : cdkSection.getKeys(false)) {
                        usedCodeRecords.add(new UsedCodeRecord(playerName, cdkCode));
                    }
                }
            }
        }
        plugin.getLogger().info("Loaded used_codes.yml with " + usedCodeRecords.size() + " records.");
    }

    /**
     * 保存 used_codes.yml 配置。
     */
    public void saveUsedCodesConfig() {
        File usedCodesFile = new File(plugin.getDataFolder(), "used_codes.yml");
        YamlConfiguration usedCodesYaml = new YamlConfiguration();
        for (UsedCodeRecord record : usedCodeRecords) {
            usedCodesYaml.set(record.getPlayerName() + "." + record.getCdkCode(), true);
        }
        try {
            usedCodesYaml.save(usedCodesFile);
            plugin.getLogger().info("Saved used_codes.yml.");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save used_codes.yml", e);
        }
    }

    /**
     * 加载语言文件。
     */
    private void loadLanguageConfigs() {
        languageConfigs.clear();
        // Load Chinese language file
        File langCnFile = new File(plugin.getDataFolder(), "lang" + File.separator + "lang_cn.yml");
        if (!langCnFile.exists()) {
            plugin.saveResource("lang" + File.separator + "lang_cn.yml", false);
        }
        YamlConfiguration langCnYaml = YamlConfiguration.loadConfiguration(langCnFile);
        Map<String, String> cnMessages = new HashMap<>();
        for (String key : langCnYaml.getKeys(true)) {
            cnMessages.put(key, langCnYaml.getString(key));
        }
        languageConfigs.put("cn", new LanguageConfig(cnMessages));
        plugin.getLogger().info("Loaded lang_cn.yml with " + cnMessages.size() + " messages.");

        // Load English language file
        File langEnFile = new File(plugin.getDataFolder(), "lang" + File.separator + "lang_en.yml");
        if (!langEnFile.exists()) {
            plugin.saveResource("lang" + File.separator + "lang_en.yml", false);
        }
        YamlConfiguration langEnYaml = YamlConfiguration.loadConfiguration(langEnFile);
        Map<String, String> enMessages = new HashMap<>();
        for (String key : langEnYaml.getKeys(true)) {
            enMessages.put(key, langEnYaml.getString(key));
        }
        languageConfigs.put("en", new LanguageConfig(enMessages));
        plugin.getLogger().info("Loaded lang_en.yml with " + enMessages.size() + " messages.");
    }

    /**
     * 获取插件配置。
     * @return 插件配置对象
     */
    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    /**
     * 获取 CDK 映射。
     * @return CDK 映射
     */
    public Map<String, CDK> getCdkMap() {
        if (cdkMap.isEmpty()) {
            loadCdkConfig();
        }
        return cdkMap;
    }

    /**
     * 获取已使用的 CDK 记录。
     * @return 已使用的 CDK 记录集合
     */
    public Set<UsedCodeRecord> getUsedCodeRecords() {
        if (usedCodeRecords.isEmpty()) {
            loadUsedCodesConfig();
        }
        return usedCodeRecords;
    }

    /**
     * 根据语言代码获取语言配置。
     * @param langCode 语言代码 (e.g., "cn", "en")
     * @return 语言配置对象，如果不存在则返回默认语言配置
     */
    public LanguageConfig getLanguageConfig(String langCode) {
        return languageConfigs.getOrDefault(langCode, languageConfigs.get("en")); // Default to English
    }

    /**
     * 检查玩家是否已使用某个 CDK。
     * @param playerName 玩家名称
     * @param cdkCode CDK 码
     * @return 如果玩家已使用该 CDK 则返回 true，否则返回 false
     */
    public boolean hasPlayerUsedCdk(String playerName, String cdkCode) {
        return usedCodeRecords.contains(new UsedCodeRecord(playerName, cdkCode));
    }

    /**
     * 标记玩家已使用某个 CDK。
     * @param playerName 玩家名称
     * @param cdkCode CDK 码
     */
    public void markPlayerUsedCdk(String playerName, String cdkCode) {
        usedCodeRecords.add(new UsedCodeRecord(playerName, cdkCode));
    }

    /**
     * 获取格式化后的消息。
     * @param key 消息键
     * @return 格式化后的消息字符串
     */
    public String getFormattedMessage(String key) {
        String langCode = pluginConfig.getLanguage();
        LanguageConfig langConfig = getLanguageConfig(langCode);
        String message = langConfig.getMessage(key);
        if (message == null) {
            // Fallback to English if message not found in preferred language
            message = languageConfigs.get("en").getMessage(key);
        }
        return message != null ? message : "Missing message for key: " + key;
    }

    /**
     * 检查 CDK 是否过期。
     * @param cdk CDK 对象
     * @return 如果 CDK 已过期则返回 true，否则返回 false
     */
    public boolean isCdkExpired(CDK cdk) {
        if (cdk.getExpiration() == null || cdk.getExpiration().isEmpty()) {
            return false;
        }
        try {
            Date expirationDate = DATE_FORMAT.parse(cdk.getExpiration());
            return new Date().after(expirationDate);
        } catch (ParseException e) {
            plugin.getLogger().log(Level.WARNING, "Invalid expiration date format for CDK: " + cdk.getExpiration(), e);
            return false;
        }
    }
}