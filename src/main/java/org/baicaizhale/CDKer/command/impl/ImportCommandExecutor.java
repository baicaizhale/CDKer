package org.baicaizhale.CDKer.command.impl;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.AbstractSubCommand;
import org.baicaizhale.CDKer.database.YmlToDbImporter;
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
            sender.sendMessage(getMsg("command.common.no_permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(getMsg("command.import.usage"));
            return true;
        }

        String fileName = args[0];
        File ymlFile = new File(plugin.getDataFolder(), fileName);
        boolean replace = args.length > 1 && "replace".equalsIgnoreCase(args[1]);

        try {
            if (replace) {
                plugin.getCdkRecordDao().deleteAllCdks();
            }

            YmlToDbImporter importer = new YmlToDbImporter(plugin, plugin.getCdkRecordDao());
            importer.importFromYml(ymlFile);
            sender.sendMessage(getMsg("command.import.success", fileName));
        } catch (Exception e) {
            sender.sendMessage(getMsg("command.import.error", e.getMessage()));
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public String getUsage() {
        return getMsg("command.import.usage");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}