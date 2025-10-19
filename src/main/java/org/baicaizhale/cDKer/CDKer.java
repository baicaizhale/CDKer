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
    private volatile WatchService watcher;
    private volatile Thread watchThread;
    private long lastReloadTime = 0L; // 新增：记录上次重载时间

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
                            long currentTime = System.currentTimeMillis();
                            // 仅当变更的文件是 config.yml 或语言文件时才触发重载
                            String changedFileName = changed.getFileName().toString();
                            getLogger().info("文件变更检测: 文件名 = " + changedFileName + ", 是否为配置/语言文件 = " + ("config.yml".equals(changedFileName) || changedFileName.startsWith("lang_")));
                            if (("config.yml".equals(changedFileName) || changedFileName.startsWith("lang_")) && currentTime - lastReloadTime > 500) { // 500毫秒内只重载一次
                                getLogger().info("检测到配置文件变更: " + changed.getFileName() + "，正在重新加载...");
                                configurationManager.reloadAllConfigs();
                                lastReloadTime = currentTime;
                            }
                            // 移除 break; 语句，让所有事件都被处理，但通过时间戳控制重载频率
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