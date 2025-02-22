package org.baicaizhale.cDKer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CDKer extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("CDKer 插件已启用！");

        // 加载配置文件
        saveDefaultConfig(); // 创建默认的配置文件（config.yml）
        saveResource("cdk.yml", false); // 不存在时复制默认的 cdk.yml 文件到 plugins/cdker 目录

        // 确认 cdk.yml 是否成功加载
        File cdkFile = new File(getDataFolder(), "cdk.yml");
        getLogger().info("检查 cdk.yml 是否存在...");

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
