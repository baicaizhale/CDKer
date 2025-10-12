package org.baicaizhale.cDKer.command;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.impl.*;
import org.baicaizhale.cDKer.util.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class MainCommandExecutor implements CommandExecutor, TabCompleter {
    private final CDKer plugin;
    private final Map<String, SubCommandExecutor> subCommands;

    public MainCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
        this.subCommands = new LinkedHashMap<>();

        // 注册子命令
        registerSubCommand("help", new HelpCommandExecutor(plugin));
        registerSubCommand("create", new CreateCDKCommand(plugin));
        registerSubCommand("use", new UseCDKCommand(plugin));
        registerSubCommand("list", new ListCDKCommand(plugin));
        registerSubCommand("delete", new DeleteCDKCommand(plugin));
        registerSubCommand("reload", new ReloadCommandExecutor(plugin));
    }

    private void registerSubCommand(String name, SubCommandExecutor executor) {
        subCommands.put(name.toLowerCase(), executor);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // 无参数时显示帮助信息
            subCommands.get("help").execute(sender, args);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        SubCommandExecutor executor = subCommands.get(subCommand);

        if (executor == null) {
            CommandUtils.sendMessage(sender, "§c未知的命令。使用 /cdk help 查看帮助。");
            return true;
        }

        // 检查权限
        String permission = executor.getRequiredPermission();
        if (permission != null && !sender.hasPermission(permission)) {
            CommandUtils.sendMessage(sender, "§c你没有执行此命令的权限。");
            return true;
        }

        // 移除第一个参数（子命令名），传递剩余参数给子命令执行器
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        return executor.execute(sender, subArgs);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String partialCommand = args[0].toLowerCase();

            // 只显示玩家有权限使用的命令
            for (Map.Entry<String, SubCommandExecutor> entry : subCommands.entrySet()) {
                String subCommand = entry.getKey();
                SubCommandExecutor executor = entry.getValue();
                
                if (subCommand.startsWith(partialCommand) &&
                    (executor.getRequiredPermission() == null || 
                     sender.hasPermission(executor.getRequiredPermission()))) {
                    completions.add(subCommand);
                }
            }

            return completions;
        }

        // 获取子命令执行器进行补全
        SubCommandExecutor executor = subCommands.get(args[0].toLowerCase());
        if (executor != null) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            return executor.tabComplete(sender, subArgs);
        }

        return new ArrayList<>();
    }
}