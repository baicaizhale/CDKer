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

        // 加载默认的 config.yml 文件
        saveDefaultConfig();

        // 确保 cdk.yml 文件存在，并加载
        saveResource("cdk.yml", false);

        // 加载 cdk.yml 配置文件
        cdkFile = new File(getDataFolder(), "cdk.yml");
        cdkConfig = YamlConfiguration.loadConfiguration(cdkFile);

        // 加载玩家使用记录的文件
        usedCodesFile = new File(getDataFolder(), "used_codes.yml");
        if (!usedCodesFile.exists()) {
            saveResource("used_codes.yml", false); // 如果文件不存在，创建它
        }
        usedCodesConfig = YamlConfiguration.loadConfiguration(usedCodesFile);

        // 加载语言文件
        String language = getConfig().getString("language", "cn");
        langFile = new File(getDataFolder(), "lang/lang_" + language + ".yml");
        if (!langFile.exists()) {
            saveResource("lang/lang_cn.yml", false);  // 如果没有 lang_cn.yml 文件，则默认使用
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);

        // 获取自定义前缀
        prefix = getConfig().getString("prefix", "CDKer > ");

        // 确保 cdk.yml 文件加载成功
        if (cdkFile.exists()) {
            getLogger().info("cdk.yml 文件已成功加载。");
        } else {
            getLogger().warning("cdk.yml 文件未找到！");
        }

        // 注册命令
        this.getCommand("cdk").setExecutor(new CDKCommandExecutor(this));
        this.getCommand("mcdk").setExecutor(new CDKListCommandExecutor(this));  // 处理 /mcdk list
        this.getCommand("mcdk").setTabCompleter(new CDKTabCompleter());  // 注册 Tab 完成器
    }

    @Override
    public void onDisable() {
        getLogger().info("CDKer 插件已禁用！");
    }

    // 获取 cdk.yml 配置文件
    public FileConfiguration getCDKConfig() {
        return cdkConfig;
    }

    // 设置 cdk.yml 配置文件
    public void setCDKConfig(FileConfiguration cdkConfig) {
        this.cdkConfig = cdkConfig;
    }

    // 获取玩家使用记录的配置文件
    public FileConfiguration getUsedCodesConfig() {
        return usedCodesConfig;
    }

    // 获取语言文件
    public FileConfiguration getLangConfig() {
        return langConfig;
    }

    // 获取前缀
    public String getPrefix() {
        return prefix;
    }

    // 保存 cdk.yml 配置文件
    public void saveCDKConfig() {
        try {
            cdkConfig.save(cdkFile);
        } catch (Exception e) {
            getLogger().severe("保存 cdk.yml 配置文件失败: " + e.getMessage());
        }
    }

    // 保存玩家使用记录文件
    public void saveUsedCodesConfig() {
        try {
            usedCodesConfig.save(usedCodesFile);
        } catch (Exception e) {
            getLogger().severe("保存 used_codes.yml 配置文件失败: " + e.getMessage());
        }
    }
}
