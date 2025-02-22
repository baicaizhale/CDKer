package org.baicaizhale.cDKer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CDKCommandExecutor implements CommandExecutor {

    private CDKer plugin;

    // 构造器
    public CDKCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // 确保输入了一个礼品码
            if (args.length == 1) {
                String cdkCode = args[0];

                // 获取 cdk.yml 配置文件
                FileConfiguration config = plugin.getCDKConfig();

                // 输出当前检查的礼品码
                plugin.getLogger().info("正在检查礼品码: " + cdkCode);

                // 检查是否包含这个礼品码
                if (config.contains(cdkCode)) {
                    plugin.getLogger().info("礼品码 " + cdkCode + " 存在!");

                    // 获取与礼品码关联的命令
                    String reservedCommand = config.getString(cdkCode);
                    if (reservedCommand != null && !reservedCommand.isEmpty()) {
                        plugin.getLogger().info("执行命令: " + reservedCommand);

                        // 执行命令
                        player.getServer().dispatchCommand(player.getServer().getConsoleSender(), reservedCommand);

                        // 移除已经使用的礼品码
                        config.set(cdkCode, null);
                        plugin.saveConfig();  // 保存修改后的配置文件

                        player.sendMessage("兑换成功，已执行命令: " + reservedCommand);
                    } else {
                        player.sendMessage("礼品码对应的命令为空或无效！");
                        plugin.getLogger().warning("礼品码 " + cdkCode + " 的命令为空！");
                    }
                } else {
                    player.sendMessage("无效的礼品码！");
                    plugin.getLogger().warning("礼品码 " + cdkCode + " 无效！");
                }
                return true;
            } else {
                player.sendMessage("使用方法: /cdk <cdkCode>");
                return false;
            }
        } else {
            sender.sendMessage("只有玩家可以使用此命令！");
            return false;
        }
    }
}
