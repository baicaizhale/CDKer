package org.baicaizhale.cDKer;

import org.baicaizhale.cDKer.manager.ConfigurationManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

public class CDKer extends JavaPlugin {

    private ConfigurationManager configurationManager;
    private WatchService watcher;
    private Thread watchThread;

    @Override
    public void onEnable() {
        // 初始化配置管理器
        this.configurationManager = new ConfigurationManager(this);
        // 加载所有配置
        configurationManager.loadAllConfigs();

        // 注册命令执行器
        this.getCommand("cdk").setExecutor(new CDKCommandExecutor(this));
        this.getCommand("cdk").setTabCompleter(new CDKTabCompleter(configurationManager));

        // 启动文件监听
        startWatchingConfigs();

        getLogger().info("CDKer has been enabled!");
    }

    @Override
    public void onDisable() {
        // 停止文件监听
        stopWatchingConfigs();

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
                        getLogger().info("Config watch thread interrupted.");
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
                            getLogger().info("Detected config file change: " + changed.getFileName() + ". Reloading configs...");
                            configurationManager.reloadAllConfigs();
                            break; // 只处理一次重载，避免重复触发
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        getLogger().warning("Config watch key no longer valid, stopping watcher.");
                        break;
                    }
                }
            }, "CDKer-ConfigWatcher");
            watchThread.start();
            getLogger().info("Started watching config files for changes.");
        } catch (IOException e) {
            getLogger().severe("Error starting config file watcher: " + e.getMessage());
        }
    }

    /**
     * 递归注册目录及其子目录到 WatchService。
     * @param root 根目录路径
     * @throws IOException 如果注册失败
     */
    private void registerRecursive(final Path root) throws IOException {
        // 注册根目录
        root.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

        // 遍历子目录并注册
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, java.nio.file.attribute.BasicFileAttributes attrs) throws IOException {
                dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 停止配置文件监听服务。
     */
    private void stopWatchingConfigs() {
        if (watchThread != null) {
            watchThread.interrupt();
            try {
                watchThread.join(TimeUnit.SECONDS.toMillis(5)); // 等待线程结束，最多5秒
            } catch (InterruptedException e) {
                getLogger().warning("Interrupted while waiting for config watch thread to terminate.");
            }
        }
        if (watcher != null) {
            try {
                watcher.close();
            } catch (IOException e) {
                getLogger().severe("Error closing config file watcher: " + e.getMessage());
            }
        }
        getLogger().info("Stopped watching config files.");
    }
}