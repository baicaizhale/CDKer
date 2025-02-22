package org.baicaizhale.cDKer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class CDKCommandExecutor implements CommandExecutor {

    private final CDKer plugin;

    // 构造器
    public CDKCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // 获取语言文件中的消息
            FileConfiguration langConfig = plugin.getLangConfig();
            String prefix = plugin.getPrefix();  // 获取前缀

            // 确保输入了一个礼品码
            if (args.length == 1) {
                String cdkCode = args[0];

                // 获取 cdk.yml 配置文件
                FileConfiguration cdkConfig = plugin.getCDKConfig();
                FileConfiguration usedCodesConfig = plugin.getUsedCodesConfig();

                // 检查该礼品码是否存在
                if (cdkConfig.contains(cdkCode)) {
                    // 获取礼品码对应的命令和剩余次数
                    List<String> reservedCommands = cdkConfig.getStringList(cdkCode + ".commands");
                    int remainingUses = cdkConfig.getInt(cdkCode + ".remainingUses", 1);

                    // 检查玩家是否已经使用过该兑换码
                    if (usedCodesConfig.contains(player.getName() + "." + cdkCode)) {
                        // 使用颜色格式
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("messages.already_used")));
                        return true;
                    }

                    // 检查剩余次数
                    if (remainingUses > 0) {
                        // 执行命令
                        for (String commandText : reservedCommands) {
                            // 替换 %player% 占位符为玩家的名字
                            String commandToExecute = commandText.replace("%player%", player.getName());
                            player.getServer().dispatchCommand(player.getServer().getConsoleSender(), commandToExecute);
                        }

                        // 减少剩余次数
                        cdkConfig.set(cdkCode + ".remainingUses", remainingUses - 1);

                        // 记录玩家已使用该兑换码
                        usedCodesConfig.set(player.getName() + "." + cdkCode, true);

                        // 保存配置文件
                        plugin.saveCDKConfig();
                        plugin.saveUsedCodesConfig();

                        // 使用颜色格式
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("messages.success").replace("%command%", reservedCommands.toString())));

                        // 如果剩余次数为 0，标记该兑换码为无效
                        if (remainingUses - 1 == 0) {
                            cdkConfig.set(cdkCode, null);
                            plugin.saveCDKConfig();
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("messages.max_usage")));
                        }
                    } else {
                        // 使用颜色格式
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("messages.max_usage")));
                    }
                } else {
                    // 使用颜色格式
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("messages.invalid_code")));
                }
                return true;
            } else {
                // 使用颜色格式
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("messages.usage_info")));
                return false;
            }
        } else {
            sender.sendMessage("只有玩家可以使用此命令！");
            return false;
        }
    }
}
