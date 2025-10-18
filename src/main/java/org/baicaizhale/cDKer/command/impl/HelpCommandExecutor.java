package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.baicaizhale.cDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpCommandExecutor extends AbstractSubCommand {

    public HelpCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§6=== CDK 帮助 ===");
        sender.sendMessage("§f/cdk create <数量> \"<命令1|命令2|...>\" [次数] [备注] [过期时间] [类型] [允许多次使用] §7- 创建CDK");
        sender.sendMessage("  §7参数说明:");
        sender.sendMessage("  §7<数量>: 生成CDK数量 (1-100)");
        sender.sendMessage("  §7<命令>: 玩家兑换时执行的命令，多个用|分隔");
        sender.sendMessage("  §7[次数]: 每个CDK可被兑换的总次数，默认1");
        sender.sendMessage("  §7[备注]: 该CDK的备注信息");
        sender.sendMessage("  §7[过期时间]: 过期时间，格式yyyy-MM-dd HH:mm");
        sender.sendMessage("  §7[类型]: 自定义类型标识");
        sender.sendMessage("  §7[允许多次使用]: true/false，是否允许同一玩家多次兑换");
        sender.sendMessage("  §7示例: /cdk create 5 \"give %player% diamond 1|say 恭喜获得钻石\" 1 \"新手礼包\" 2025-12-01 10:00 vip true");
        sender.sendMessage("§f/cdk use <兑换码> §7- 使用CDK");
        sender.sendMessage("  §7示例: /cdk use ABC123XYZ789");
        sender.sendMessage("§f/cdk add <id/cdk> <标识符> <数量> §7- 增加使用次数");
        sender.sendMessage("  §7示例: /cdk add id 1 5 或 /cdk add cdk ABC123XYZ789 3");
        sender.sendMessage("§f/cdk del <id/cdk> <标识符> §7- 删除CDK");
        sender.sendMessage("  §7示例: /cdk del id 1 或 /cdk del cdk ABC123XYZ789");
        sender.sendMessage("§f/cdk list [页码] [类型] §7- 列出所有CDK，支持分页和类型筛选");
        sender.sendMessage("  §7示例: /cdk list 2 或 /cdk list vip 或 /cdk list vip 2");
        sender.sendMessage("§f/cdk query <id/cdk> <标识符> §7- 查询CDK信息");
        sender.sendMessage("  §7示例: /cdk query id 1 或 /cdk query cdk ABC123XYZ789");
        sender.sendMessage("§f/cdk set <id/cdk> <标识符> <属性> <值> §7- 设置CDK属性");
        sender.sendMessage("  §7可设置的属性: remaining_uses, commands, expire_time, note, cdk_type, per_player_multiple");
        sender.sendMessage("  §7示例: /cdk set id 1 remaining_uses 10");
        sender.sendMessage("  §7示例: /cdk set cdk ABC123XYZ789 commands \"give {player} diamond 5|say 获得钻石\"");
        sender.sendMessage("  §7示例: /cdk set id 1 expire_time \"2025-12-31 23:59\"");
        sender.sendMessage("  §7示例: /cdk set cdk ABC123XYZ789 note \"更新备注\"");
        sender.sendMessage("  §7示例: /cdk set id 1 cdk_type \"vip\"");
        sender.sendMessage("  §7示例: /cdk set cdk ABC123XYZ789 per_player_multiple true");
        sender.sendMessage("§f/cdk reload §7- 重新加载配置");
        sender.sendMessage("§f/cdk import <文件> [replace|append] §7- 从yml导入CDK");
        sender.sendMessage("  §7replace: 替换现有所有CDK，append: 追加到现有CDK");
        sender.sendMessage("  §7示例: /cdk import cdks.yml replace");
        sender.sendMessage("§f/cdk export <文件> §7- 导出CDK到yml");
        sender.sendMessage("  §7示例: /cdk export cdks.yml");
        sender.sendMessage("§6==============");

        return true;
    }

    @Override
    public String getUsage() {
        return "§c用法: /cdk help";
    }
}