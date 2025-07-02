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
        // 注意：如果 cdk.yml 结构发生变化，可能需要删除旧文件让新文件生成
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
        // 确保 lang 文件夹存在
        if (!langFile.getParentFile().exists()) {
            langFile.getParentFile().mkdirs();
        }
        if (!langFile.exists()) {
            // 如果指定语言文件不存在，默认保存并加载中文
            saveResource("lang/lang_cn.yml", false);
            // 尝试保存英文文件，以防用户切换语言
            saveResource("lang/lang_en.yml", false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);

        // 获取自定义前缀
        prefix = getConfig().getString("prefix", "&bCDKer &7> &f");

        // 确保 cdk.yml 文件加载成功
        if (cdkFile.exists()) {
            getLogger().info("cdk.yml 文件已成功加载。");
        } else {
            getLogger().warning("cdk.yml 文件未找到！");
        }

        // 注册命令。现在所有子命令都由 CDKCommandExecutor 处理
        this.getCommand("cdk").setExecutor(new CDKCommandExecutor(this));
        this.getCommand("cdk").setTabCompleter(new CDKTabCompleter(this)); // 注册 Tab 完成器，并传入插件实例
    }

    @Override
    public void onDisable() {
        getLogger().info("CDKer 插件已禁用！");
        // 确保在禁用时保存所有配置，以防万一
        saveCDKConfig();
        saveUsedCodesConfig();
    }

    // 获取 cdk.yml 配置文件
    public FileConfiguration getCDKConfig() {
        return cdkConfig;
    }

    // 设置 cdk.yml 配置文件 (供重载使用)
    public void setCDKConfig(FileConfiguration cdkConfig) {
        this.cdkConfig = cdkConfig;
    }

    // 获取玩家使用记录的配置文件
    public FileConfiguration getUsedCodesConfig() {
        return usedCodesConfig;
    }

    // 设置玩家使用记录的配置文件 (供重载使用)
    public void setUsedCodesConfig(FileConfiguration usedCodesConfig) {
        this.usedCodesConfig = usedCodesConfig;
    }

    // 获取语言文件
    public FileConfiguration getLangConfig() {
        return langConfig;
    }

    // 设置语言文件 (供重载使用)
    public void setLangConfig(FileConfiguration langConfig) {
        this.langConfig = langConfig;
    }

    // 获取前缀
    public String getPrefix() {
        return prefix;
    }

    // 设置前缀 (供重载使用)
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    // 保存 cdk.yml 配置文件
    public void saveCDKConfig() {
        try {
            cdkConfig.save(cdkFile);
        } catch (Exception e) {
            getLogger().severe("保存 cdk.yml 配置文件失败: " + e.getMessage());
            // 可以在这里向控制台发送一个错误消息，使用语言文件中的消息
            // getLogger().severe(langConfig.getString("save_cdk_error"));
        }
    }

    // 保存玩家使用记录文件
    public void saveUsedCodesConfig() {
        try {
            usedCodesConfig.save(usedCodesFile);
        } catch (Exception e) {
            getLogger().severe("保存 used_codes.yml 配置文件失败: " + e.getMessage());
            // getLogger().severe(langConfig.getString("save_log_error"));
        }
    }

    // 重载所有配置文件的方法 (供外部调用，例如通过 /cdk reload)
    @Override
    public void reloadConfig() {
        super.reloadConfig(); // 重载 config.yml
        // 重新加载 cdk.yml
        cdkConfig = YamlConfiguration.loadConfiguration(cdkFile);
        // 重新加载 used_codes.yml
        usedCodesConfig = YamlConfiguration.loadConfiguration(usedCodesFile);
        // 重新加载语言文件
        String language = getConfig().getString("language", "cn");
        langFile = new File(getDataFolder(), "lang/lang_" + language + ".yml");
        langConfig = YamlConfiguration.loadConfiguration(langFile);
        // 更新前缀
        prefix = getConfig().getString("prefix", "&bCDKer &7> &f");
    }
}
