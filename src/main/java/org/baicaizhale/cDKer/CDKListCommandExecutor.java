package org.baicaizhale.cDKer;

// 导入 Bukkit 聊天颜色工具类
import org.bukkit.ChatColor;
// 导入 Bukkit 命令相关接口
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
// 导入 Bukkit 配置文件处理类
import org.bukkit.configuration.file.FileConfiguration;
// 导入 Bukkit 玩家类
import org.bukkit.entity.Player;
// 导入 Bukkit Yaml 配置工具类
import org.bukkit.configuration.file.YamlConfiguration;

// 导入 Java IO、集合工具类
import java.io.File;
import java.util.Set;

/**
 * CDK 列表命令执行器，实现 CommandExecutor 接口。
 * 支持 list 和 reload 子命令。
 */
public class CDKListCommandExecutor implements CommandExecutor {
    // 插件主类实例
    private final CDKer plugin;

    /**
     * 构造方法，注入插件主类。
     * @param plugin 插件主类实例
     */
    public CDKListCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
    }

    /**
     * 命令处理主入口。
     * @param sender 命令发送者
     * @param command 命令对象
     * @param label 命令标签
     * @param args 命令参数
     * @return 是否成功处理命令
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 参数为空时不处理
        if (args.length == 0) {
            return false;
        }
        // list 命令，显示 CDK 列表
        if ("list".equalsIgnoreCase(args[0])) {
            return showCDKList(sender);
        }
        // reload 命令，重载配置
        if ("reload".equalsIgnoreCase(args[0])) {
            return reloadConfig(sender);
        }
        // 其他命令不处理
        return false;
    }

    /**
     * 检查权限，控制台默认有权限。
     * @param sender 命令发送者
     * @param permission 权限节点
     * @return 是否有权限
     */
    private boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission) || !(sender instanceof Player);
    }

    /**
     * 显示所有 CDK 列表。
     * @param sender 命令发送者
     * @return 是否成功显示
     */
    private boolean showCDKList(CommandSender sender) {
        FileConfiguration langConfig = plugin.getLangConfig(); // 获取语言配置
        String prefix = plugin.getPrefix(); // 获取消息前缀
        if (!hasPermission(sender, "cdk.list")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission")));
            return true;
        }
        FileConfiguration cdkConfig = plugin.getCDKConfig(); // 获取 CDK 配置
        Set<String> cdkCodes = cdkConfig.getKeys(false); // 获取所有兑换码 key
        if (cdkCodes.isEmpty()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("list_empty", "当前没有任何兑换码！")));
            return true;
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("list_header", "有效的兑换码列表:")));
        for (String cdkCode : cdkCodes) {
            int remainingUses = cdkConfig.getInt(cdkCode + ".remainingUses", 0); // 获取剩余次数
            String expiration = cdkConfig.getString(cdkCode + ".expiration", langConfig.getString("never_expires", "永不")); // 获取过期时间
            String status = remainingUses > 0 ? ChatColor.GREEN + langConfig.getString("cdk_available", "可用") : ChatColor.RED + langConfig.getString("cdk_used_up", "已用完"); // 状态
            sender.sendMessage(ChatColor.YELLOW + "兑换码: " + cdkCode + " | 剩余次数: " + remainingUses + " | 过期时间: " + expiration + " | 状态: " + status);
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("list_footer", "")));
        return true;
    }

    /**
     * 重载插件配置。
     * @param sender 命令发送者
     * @return 是否成功重载
     */
    private boolean reloadConfig(CommandSender sender) {
        FileConfiguration langConfig = plugin.getLangConfig(); // 获取语言配置
        String prefix = plugin.getPrefix(); // 获取消息前缀
        if (!hasPermission(sender, "cdk.reload")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission")));
            return true;
        }
        plugin.reloadConfig(); // 调用主类重载配置
        File cdkFile = new File(plugin.getDataFolder(), "cdk.yml"); // 获取 cdk.yml 文件
        if (cdkFile.exists()) {
            FileConfiguration cdkConfig = YamlConfiguration.loadConfiguration(cdkFile); // 重新加载 cdk.yml
            plugin.setCDKConfig(cdkConfig); // 设置新的配置
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("reload_success", "配置文件已成功重载！")));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("cdk_not_found", "未找到 cdk.yml 文件！")));
        }
        return true;
    }
}
