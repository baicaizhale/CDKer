package org.baicaizhale.cDKer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class CDKer extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("CDKer 插件已启用！");

        // 加载默认的配置文件 config.yml
        saveDefaultConfig();

        // 确保 cdk.yml 存在，如果不存在则从 JAR 文件复制默认的 cdk.yml 文件
        saveResource("cdk.yml", false);

        // 加载 cdk.yml 配置文件
        reloadConfig(); // 确保配置文件被重新加载

        // 确保 cdk.yml 文件加载成功
        File cdkFile = new File(getDataFolder(), "cdk.yml");
        if (cdkFile.exists()) {
            getLogger().info("cdk.yml 文件已成功加载。");
        } else {
            getLogger().warning("cdk.yml 文件未找到！");
        }

        // 注册命令
        this.getCommand("cdk").setExecutor(new CDKCommandExecutor(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("CDKer 插件已禁用！");
    }

    // 获取 cdk.yml 配置文件
    public FileConfiguration getCDKConfig() {
        return getConfig();
    }
}
