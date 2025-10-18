package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.baicaizhale.cDKer.database.YmlToDbImporter;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportCommandExecutor extends AbstractSubCommand {

    public ImportCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("cdk.admin")) {
            sender.sendMessage("§c您没有权限执行此命令。");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§c用法: /cdk import <文件> [replace|append]");
            return true;
        }
        
        String fileName = args[0]; // 修复参数索引错误
        File ymlFile = new File(plugin.getDataFolder(), fileName);
        boolean replace = args.length > 1 && "replace".equalsIgnoreCase(args[1]); // 修复参数索引错误
        
        try {
            if (replace) {
                plugin.getCdkRecordDao().deleteAllCdks();
            }

            YmlToDbImporter importer = new YmlToDbImporter(plugin, plugin.getCdkRecordDao());
            importer.importFromYml(ymlFile);
            sender.sendMessage("§a成功从 " + fileName + " 导入CDK。");
        } catch (Exception e) {
            sender.sendMessage("§c导入失败: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public String getUsage() {
        return "§c用法: /cdk import <yml文件> [replace|append]";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}