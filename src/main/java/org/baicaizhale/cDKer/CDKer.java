package org.baicaizhale.cDKer;

// 导入 Bukkit 插件相关类和配置文件处理类
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

// CDKer 主插件类，继承自 JavaPlugin
public class CDKer extends JavaPlugin {
    // CDK 配置文件对象
    private File cdkFile;
    // CDK 配置文件内容
    private FileConfiguration cdkConfig;
    // 已使用的兑换码文件对象
    private File usedCodesFile;
    // 已使用的兑换码配置内容
    private FileConfiguration usedCodesConfig;
    // 语言文件对象
    private File langFile;
    // 语言配置内容
    private FileConfiguration langConfig;
    // 插件消息前缀
    private String prefix;

    // 插件启用时调用的方法
    @Override
    public void onEnable() {
        getLogger().info("CDKer 插件已启用！"); // 输出插件启用日志
        saveDefaultConfig(); // 保存默认配置文件 config.yml
        loadOrSaveResource("cdk.yml"); // 加载或保存 cdk.yml 文件
        cdkFile = new File(getDataFolder(), "cdk.yml"); // 获取 cdk.yml 文件对象
        cdkConfig = YamlConfiguration.loadConfiguration(cdkFile); // 加载 cdk.yml 配置
        loadOrSaveResource("used_codes.yml"); // 加载或保存 used_codes.yml 文件
        usedCodesFile = new File(getDataFolder(), "used_codes.yml"); // 获取 used_codes.yml 文件对象
        usedCodesConfig = YamlConfiguration.loadConfiguration(usedCodesFile); // 加载 used_codes.yml 配置
        String language = getConfig().getString("language", "cn"); // 获取语言设置，默认为中文
        File langDir = new File(getDataFolder(), "lang"); // 获取 lang 目录对象
        if (!langDir.exists()) langDir.mkdirs(); // 若 lang 目录不存在则创建
        loadOrSaveResource("lang/lang_cn.yml"); // 加载或保存中文语言文件
        loadOrSaveResource("lang/lang_en.yml"); // 加载或保存英文语言文件
        langFile = new File(langDir, "lang_" + language + ".yml"); // 获取当前语言文件对象
        langConfig = YamlConfiguration.loadConfiguration(langFile); // 加载语言配置
        prefix = getConfig().getString("prefix", "&bCDKer &7> &f"); // 获取消息前缀
        if (cdkFile.exists()) {
            getLogger().info("cdk.yml 文件已成功加载。"); // 成功加载 cdk.yml 文件日志
        } else {
            getLogger().warning("cdk.yml 文件未找到！"); // 未找到 cdk.yml 文件警告
        }
        this.getCommand("cdk").setExecutor(new CDKCommandExecutor(this)); // 注册 cdk 命令执行器
        this.getCommand("cdk").setTabCompleter(new CDKTabCompleter(this)); // 注册 cdk 命令补全器
    }

    // 加载或保存资源文件的方法
    private void loadOrSaveResource(String resourcePath) {
        File file = new File(getDataFolder(), resourcePath); // 获取资源文件对象
        if (!file.exists()) {
            saveResource(resourcePath, false); // 若文件不存在则保存资源文件
        }
    }

    // 插件禁用时调用的方法
    @Override
    public void onDisable() {
        getLogger().info("CDKer 插件已禁用！"); // 输出插件禁用日志
        saveCDKConfig(); // 保存 cdk.yml 配置
        saveUsedCodesConfig(); // 保存 used_codes.yml 配置
    }

    // 获取 cdk 配置的方法
    public FileConfiguration getCDKConfig() { return cdkConfig; }
    // 设置 cdk 配置的方法
    public void setCDKConfig(FileConfiguration cdkConfig) { this.cdkConfig = cdkConfig; }
    // 获取已使用兑换码配置的方法
    public FileConfiguration getUsedCodesConfig() { return usedCodesConfig; }
    // 设置已使用兑换码配置的方法
    public void setUsedCodesConfig(FileConfiguration usedCodesConfig) { this.usedCodesConfig = usedCodesConfig; }
    // 获取语言配置的方法
    public FileConfiguration getLangConfig() { return langConfig; }
    // 设置语言配置的方法
    public void setLangConfig(FileConfiguration langConfig) { this.langConfig = langConfig; }
    // 获取消息前缀的方法
    public String getPrefix() { return prefix; }
    // 设置消息前缀的方法
    public void setPrefix(String prefix) { this.prefix = prefix; }

    // 保存 cdk.yml 配置文件的方法
    public void saveCDKConfig() {
        try {
            cdkConfig.save(cdkFile); // 保存 cdk.yml 文件
        } catch (IOException e) {
            getLogger().severe("保存 cdk.yml 配置文件失败: " + e.getMessage()); // 保存失败日志
            e.printStackTrace(); // 打印异常堆栈
        }
    }

    // 保存 used_codes.yml 配置文件的方法
    public void saveUsedCodesConfig() {
        try {
            usedCodesConfig.save(usedCodesFile); // 保存 used_codes.yml 文件
        } catch (IOException e) {
            getLogger().severe("保存 used_codes.yml 配置文件失败: " + e.getMessage()); // 保存失败日志
            e.printStackTrace(); // 打印异常堆栈
        }
    }

    // 重载插件配置的方法
    @Override
    public void reloadConfig() {
        super.reloadConfig(); // 调用父类方法
        cdkConfig = YamlConfiguration.loadConfiguration(cdkFile); // 重新加载 cdk.yml 配置
        usedCodesConfig = YamlConfiguration.loadConfiguration(usedCodesFile); // 重新加载 used_codes.yml 配置
        String language = getConfig().getString("language", "cn"); // 获取语言设置
        langFile = new File(getDataFolder(), "lang/lang_" + language + ".yml"); // 获取语言文件对象
        langConfig = YamlConfiguration.loadConfiguration(langFile); // 重新加载语言配置
        prefix = getConfig().getString("prefix", "&bCDKer &7> &f"); // 重新获取消息前缀
    }
}