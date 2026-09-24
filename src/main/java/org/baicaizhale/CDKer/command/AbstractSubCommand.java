package org.baicaizhale.CDKer.command;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.model.LanguageConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSubCommand {
    protected final CDKer plugin;

    public AbstractSubCommand(CDKer plugin) {
        this.plugin = plugin;
    }

    /**
     * 执行子命令
     *
     * @param sender 命令发送者
     * @param args   命令参数
     * @return 是否执行成功
     */
    public boolean execute(CommandSender sender, String[] args) {
        return onCommand(sender, args);
    }

    /**
     * 处理命令逻辑
     *
     * @param sender 命令发送者
     * @param args   命令参数
     * @return 是否执行成功
     */
    public abstract boolean onCommand(CommandSender sender, String[] args);

    /**
     * 获取命令用法
     *
     * @return 命令用法字符串
     */
    public String getUsage() {
        return getMsg("command.common.usage_invalid");
    }

    /**
     * 检查命令发送者是否为玩家
     */
    protected boolean requirePlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    /**
     * 便捷方法：从当前语言配置中获取消息并替换占位符，同时加上 config.yml 中的 cdk.prefix 前缀
     */
    protected String getMsg(String key) {
        return withPrefix(getRawMsg(key));
    }

    /**
     * 便捷方法：获取消息并替换占位符 {0}, {1}, ...，同时加上 cdk.prefix 前缀
     */
    protected String getMsg(String key, String... args) {
        return withPrefix(getRawMsg(key, args));
    }

    /**
     * 获取不带前缀的原始消息，供拼接多条消息时使用（避免前缀重复出现）
     */
    protected String getRawMsg(String key) {
        LanguageConfig lang = plugin.getConfigurationManager()
                .getLanguageConfig(plugin.getConfigurationManager().getPluginConfig().getLanguage());
        String message = lang.getMessage(key);
        return message == null ? "" : message;
    }

    /**
     * 获取不带前缀的原始消息并替换占位符 {0}, {1}, ...
     */
    protected String getRawMsg(String key, String... args) {
        String msg = getRawMsg(key);
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                msg = msg.replace("{" + i + "}", args[i] != null ? args[i] : "");
            }
        }
        return msg;
    }

    /**
     * 为消息拼接 config.yml 中 cdk.prefix 配置的前缀，并翻译 & 颜色代码；
     * 空消息不拼接前缀，避免出现"只有前缀没有内容"的提示。
     */
    protected String withPrefix(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        String prefix = plugin.getConfigurationManager().getPluginConfig().getPrefix();
        if (prefix == null || prefix.isEmpty()) {
            return message;
        }
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    /**
     * 命令自动补全
     */
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}