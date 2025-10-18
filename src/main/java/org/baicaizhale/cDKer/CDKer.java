package org.baicaizhale.cDKer;

import org.baicaizhale.cDKer.command.MainCommandExecutor;
import org.baicaizhale.cDKer.manager.ConfigurationManager;
import org.baicaizhale.cDKer.database.DatabaseManager;
import org.baicaizhale.cDKer.database.CdkRecordDao;
import org.baicaizhale.cDKer.database.CdkLogDao;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

public class CDKer extends JavaPlugin {
    private ConfigurationManager configurationManager;
    private DatabaseManager databaseManager;
    private CdkRecordDao cdkRecordDao;
    private CdkLogDao cdkLogDao;
    private WatchService watcher;
    private Thread watchThread;

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

        // 启动文件监听
        startWatchingConfigs();

        getLogger().info("CDKer plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // 停止文件监听
        stopWatchingConfigs();

        // 保存配置
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

    /**
     * 启动配置文件监听服务。
     */
    private void startWatchingConfigs() {
        try {
            watcher = FileSystems.getDefault().newWatchService();
            Path dataFolderPath = this.getDataFolder().toPath();
            registerRecursive(dataFolderPath);

            watchThread = new Thread(() -> {
                WatchKey key;
                while (true) {
                    try {
                        key = watcher.take();
                    } catch (InterruptedException e) {
                        getLogger().info("配置文件监听线程已中断。");
                        return;
                    }

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }

                        // 确保事件是关于文件的修改或创建
                        Path changed = (Path) event.context();
                        if (changed.toString().endsWith(".yml")) {
                            getLogger().info("检测到配置文件变更: " + changed.getFileName() + "，正在重新加载...");
                            configurationManager.reloadAllConfigs();
                            break; // 只处理一次重载，避免重复触发
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        getLogger().warning("配置文件监听键已失效，停止监听。");
                        break;
                    }
                }
            }, "CDKer-配置监听");
            watchThread.start();
            getLogger().info("已启动配置文件变更监听。");
        } catch (IOException e) {
            getLogger().severe("启动配置文件监听失败: " + e.getMessage());
        }
    }

    /**
     * 递归注册目录监听。
     */
    private void registerRecursive(Path path) throws IOException {
        // 注册当前目录
        path.register(watcher, 
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_DELETE);

        // 递归注册子目录
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            for (Path child : ds) {
                if (Files.isDirectory(child)) {
                    registerRecursive(child);
                }
            }
        }
    }

    /**
     * 停止配置文件监听服务。
     */
    private void stopWatchingConfigs() {
        if (watchThread != null) {
            watchThread.interrupt();
            try {
                watchThread.join(TimeUnit.SECONDS.toMillis(5));
            } catch (InterruptedException e) {
                getLogger().warning("中断配置文件监听线程时出错: " + e.getMessage());
            }
        }
        if (watcher != null) {
            try {
                watcher.close();
            } catch (IOException e) {
                getLogger().warning("关闭配置文件监听服务时出错: " + e.getMessage());
            }
        }
    }
}