package org.baicaizhale.cDKer;

import org.baicaizhale.cDKer.manager.ConfigurationManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CDKer extends JavaPlugin {

    private ConfigurationManager configurationManager;

    @Override
    public void onEnable() {
        // 初始化配置管理器
        this.configurationManager = new ConfigurationManager(this);
        // 加载所有配置
        configurationManager.loadAllConfigs();

        // 注册命令执行器 (稍后创建)
        this.getCommand("cdk").setExecutor(new CDKCommandExecutor(this));
        this.getCommand("cdk").setTabCompleter(new CDKTabCompleter(configurationManager));

        getLogger().info("CDKer has been enabled!");
    }

    @Override
    public void onDisable() {
        // 保存配置
        configurationManager.saveCdkConfig();
        configurationManager.saveUsedCodesConfig();
        getLogger().info("CDKer 插件已禁用！");
    }

    /**
     * 获取配置管理器实例。
     * @return ConfigurationManager 实例
     */
    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }
}