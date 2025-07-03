package org.baicaizhale.cDKer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class CDKer extends JavaPlugin {

    private File cdkFile;
    private FileConfiguration cdkConfig;
    private File usedCodesFile;
    private FileConfiguration usedCodesConfig;
    private File langFile;
    private FileConfiguration langConfig;
    private String prefix;

    @Override
    public void onEnable() {
        getLogger().info("CDKer 插件已启用！");

        saveDefaultConfig();
        saveResource("cdk.yml", false);

        cdkFile = new File(getDataFolder(), "cdk.yml");
        cdkConfig = YamlConfiguration.loadConfiguration(cdkFile);

        usedCodesFile = new File(getDataFolder(), "used_codes.yml");
        if (!usedCodesFile.exists()) {
            saveResource("used_codes.yml", false);
        }
        usedCodesConfig = YamlConfiguration.loadConfiguration(usedCodesFile);

        String language = getConfig().getString("language", "cn");
        langFile = new File(getDataFolder(), "lang/lang_" + language + ".yml");
        if (!langFile.getParentFile().exists()) {
            langFile.getParentFile().mkdirs();
        }
        if (!langFile.exists()) {
            saveResource("lang/lang_cn.yml", false);
            saveResource("lang/lang_en.yml", false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);

        prefix = getConfig().getString("prefix", "&bCDKer &7> &f");

        if (cdkFile.exists()) {
            getLogger().info("cdk.yml 文件已成功加载。");
        } else {
            getLogger().warning("cdk.yml 文件未找到！");
        }

        this.getCommand("cdk").setExecutor(new CDKCommandExecutor(this));
        this.getCommand("cdk").setTabCompleter(new CDKTabCompleter(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("CDKer 插件已禁用！");
        saveCDKConfig();
        saveUsedCodesConfig();
    }

    public FileConfiguration getCDKConfig() {
        return cdkConfig;
    }

    public void setCDKConfig(FileConfiguration cdkConfig) {
        this.cdkConfig = cdkConfig;
    }

    public FileConfiguration getUsedCodesConfig() {
        return usedCodesConfig;
    }

    public void setUsedCodesConfig(FileConfiguration usedCodesConfig) {
        this.usedCodesConfig = usedCodesConfig;
    }

    public FileConfiguration getLangConfig() {
        return langConfig;
    }

    public void setLangConfig(FileConfiguration langConfig) {
        this.langConfig = langConfig;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void saveCDKConfig() {
        try {
            cdkConfig.save(cdkFile);
        } catch (Exception e) {
            getLogger().severe("保存 cdk.yml 配置文件失败: " + e.getMessage());
        }
    }

    public void saveUsedCodesConfig() {
        try {
            usedCodesConfig.save(usedCodesFile);
        } catch (Exception e) {
            getLogger().severe("保存 used_codes.yml 配置文件失败: " + e.getMessage());
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        cdkConfig = YamlConfiguration.loadConfiguration(cdkFile);
        usedCodesConfig = YamlConfiguration.loadConfiguration(usedCodesFile);
        String language = getConfig().getString("language", "cn");
        langFile = new File(getDataFolder(), "lang/lang_" + language + ".yml");
        langConfig = YamlConfiguration.loadConfiguration(langFile);
        prefix = getConfig().getString("prefix", "&bCDKer &7> &f");
    }
}