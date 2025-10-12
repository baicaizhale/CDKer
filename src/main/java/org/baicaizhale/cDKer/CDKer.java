package org.baicaizhale.cDKer;

import org.baicaizhale.cDKer.command.MainCommandExecutor;
import org.baicaizhale.cDKer.manager.ConfigurationManager;
import org.baicaizhale.cDKer.database.DatabaseManager;
import org.baicaizhale.cDKer.database.CdkRecordDao;
import org.baicaizhale.cDKer.database.CdkLogDao;
import org.bukkit.plugin.java.JavaPlugin;

public class CDKer extends JavaPlugin {
    private ConfigurationManager configurationManager;
    private DatabaseManager databaseManager;
    private CdkRecordDao cdkRecordDao;
    private CdkLogDao cdkLogDao;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // 初始化配置管理器
        configurationManager = new ConfigurationManager(this);
        configurationManager.loadAllConfigs();

        // 初始化数据库
        try {
            databaseManager = new DatabaseManager(this);
            cdkRecordDao = new CdkRecordDao(this, databaseManager);
            cdkLogDao = new CdkLogDao(this, databaseManager);
        } catch (Exception e) {
            getLogger().severe("初始化数据库失败: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 注册命令
        MainCommandExecutor mainCommandExecutor = new MainCommandExecutor(this);
        getCommand("cdk").setExecutor(mainCommandExecutor);
        getCommand("cdk").setTabCompleter(mainCommandExecutor);

        getLogger().info("CDKer plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        if (configurationManager != null) {
            configurationManager.saveCdkConfig();
        }
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("CDKer plugin has been disabled!");
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public CdkRecordDao getCdkRecordDao() {
        return cdkRecordDao;
    }

    public CdkLogDao getCdkLogDao() {
        return cdkLogDao;
    }
}