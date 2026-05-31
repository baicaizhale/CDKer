package org.baicaizhale.CDKer.command;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.model.LanguageConfig;
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
        return getMsg("command.usage_invalid");
    }

    /**
     * 检查命令发送者是否为玩家
     */
    protected boolean requirePlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    /**
     * 便捷方法：从当前语言配置中获取消息并替换占位符
     */
    protected String getMsg(String key) {
        LanguageConfig lang = plugin.getConfigurationManager()
                .getLanguageConfig(plugin.getConfigurationManager().getPluginConfig().getLanguage());
        return lang.getMessage(key);
    }

    /**
     * 便捷方法：获取消息并替换占位符 {0}, {1}, ...
     */
    protected String getMsg(String key, String... args) {
        String msg = getMsg(key);
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                msg = msg.replace("{" + i + "}", args[i] != null ? args[i] : "");
            }
        }
        return msg;
    }

    /**
     * 命令自动补全
     */
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}