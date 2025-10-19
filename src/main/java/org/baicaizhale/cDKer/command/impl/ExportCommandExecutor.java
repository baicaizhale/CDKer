package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.baicaizhale.cDKer.database.DbToYmlExporter;
import org.bukkit.command.CommandSender;

import java.io.File;

public class ExportCommandExecutor extends AbstractSubCommand {
    public ExportCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        String fileName = "cdks.yml"; // 默认文件名
        if (args.length >= 1) {
            fileName = args[0];
        }

        File ymlFile = new File(plugin.getDataFolder(), fileName);

        try {
            DbToYmlExporter exporter = new DbToYmlExporter(plugin, plugin.getCdkRecordDao());
            exporter.exportToYml(ymlFile);
            sender.sendMessage("§a成功导出到 " + fileName);
        } catch (Exception e) {
            sender.sendMessage("§c导出失败: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public String getUsage() {
        return "§c用法: /cdk export [yml文件]";
    }
}