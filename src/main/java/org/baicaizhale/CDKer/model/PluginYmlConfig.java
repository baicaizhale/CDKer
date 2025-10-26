package org.baicaizhale.CDKer.model;

import java.util.List;
import java.util.Map;

/**
 * PluginYmlConfig 实体类，用于表示 plugin.yml 中的插件配置。
 */
public class PluginYmlConfig {
    private String name;
    private String version;
    private String main;
    private String apiVersion;
    private Map<String, CommandConfig> commands;
    private Map<String, PermissionConfig> permissions;

    /**
     * 构造函数
     * @param name 插件名称
     * @param version 插件版本
     * @param main 插件主类
     * @param apiVersion API 版本
     * @param commands 命令配置
     * @param permissions 权限配置
     */
    public PluginYmlConfig(String name, String version, String main, String apiVersion, Map<String, CommandConfig> commands, Map<String, PermissionConfig> permissions) {
        this.name = name;
        this.version = version;
        this.main = main;
        this.apiVersion = apiVersion;
        this.commands = commands;
        this.permissions = permissions;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Map<String, CommandConfig> getCommands() {
        return commands;
    }

    public void setCommands(Map<String, CommandConfig> commands) {
        this.commands = commands;
    }

    public Map<String, PermissionConfig> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, PermissionConfig> permissions) {
        this.permissions = permissions;
    }

    /**
     * 命令配置内部类
     */
    public static class CommandConfig {
        private String description;
        private String usage;
        private List<String> aliases;
        private String tabCompleter;

        /**
         * 构造函数
         * @param description 命令描述
         * @param usage 命令用法
         * @param aliases 命令别名
         * @param tabCompleter Tab 补全器
         */
        public CommandConfig(String description, String usage, List<String> aliases, String tabCompleter) {
            this.description = description;
            this.usage = usage;
            this.aliases = aliases;
            this.tabCompleter = tabCompleter;
        }

        // Getters and Setters
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUsage() {
            return usage;
        }

        public void setUsage(String usage) {
            this.usage = usage;
        }

        public List<String> getAliases() {
            return aliases;
        }

        public void setAliases(List<String> aliases) {
            this.aliases = aliases;
        }

        public String getTabCompleter() {
            return tabCompleter;
        }

        public void setTabCompleter(String tabCompleter) {
            this.tabCompleter = tabCompleter;
        }
    }

    /**
     * 权限配置内部类
     */
    public static class PermissionConfig {
        private String description;
        private String defaultValue;

        /**
         * 构造函数
         * @param description 权限描述
         * @param defaultValue 默认值
         */
        public PermissionConfig(String description, String defaultValue) {
            this.description = description;
            this.defaultValue = defaultValue;
        }

        // Getters and Setters
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }
}