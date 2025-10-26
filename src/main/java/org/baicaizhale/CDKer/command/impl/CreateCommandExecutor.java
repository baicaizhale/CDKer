package org.baicaizhale.CDKer.command.impl;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.AbstractSubCommand;
import org.baicaizhale.CDKer.model.CdkRecord;
import org.baicaizhale.CDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateCommandExecutor extends AbstractSubCommand {

    public CreateCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(getUsage());
            return true;
        }

        try {
            // 1. 解析数量
            int amount = Integer.parseInt(args[0]);
            if (amount <= 0 || amount > 100) {
                sender.sendMessage("§c数量必须在1-100之间。");
                return true;
            }

            // 2. 智能解析命令字符串
            // 将 args[1] 及之后的所有内容拼接成一个字符串
            String fullArgsString = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            // 查找第一个被引号包裹的字符串作为命令
            Matcher matcher = Pattern.compile("\"([^\"]*)\"").matcher(fullArgsString);
            String commandString;
            String remainingArgsString;

            if (matcher.find()) {
                commandString = matcher.group(1); // 提取引号内的内容
                // 将命令部分和它后面的空格从原字符串中移除，得到剩余的参数
                remainingArgsString = fullArgsString.substring(matcher.end()).trim();
            } else {
                // 如果没有找到引号，这是一个错误用法
                sender.sendMessage("§c命令部分必须用英文双引号包裹。");
                sender.sendMessage(getUsage());
                return true;
            }

            List<String> commands = CommandUtils.parseCommands(commandString);
            if (commands.isEmpty()) {
                sender.sendMessage("§c命令不能为空。");
                return true;
            }

            // 3. 解析剩余的可选参数
            String[] optionalArgs = parseArguments(remainingArgsString);
            int uses = plugin.getConfig().getInt("cdk.default-uses", 1);
            String note = "";
            String expireTime = "forever";
            String cdkType = "";
            boolean perPlayerMultiple = false;

            if (optionalArgs.length > 0) {
                try {
                    uses = Integer.parseInt(optionalArgs[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c无效的兑换次数格式。");
                    return true;
                }
            }
            if (optionalArgs.length > 1) {
                note = optionalArgs[1].replace("\"", "");
            }
            if (optionalArgs.length > 2) {
                expireTime = optionalArgs[2].replace("\"", "");
            }
            if (optionalArgs.length > 3) {
                cdkType = optionalArgs[3].replace("\"", "");
            }
            if (optionalArgs.length > 4) {
                perPlayerMultiple = Boolean.parseBoolean(optionalArgs[4]);
            }


            // 4. 生成并存储CDK
            String charset = plugin.getConfig().getString("cdk.charset", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
            int length = plugin.getConfig().getInt("cdk.length", 12);

            StringBuilder codeList = new StringBuilder();
            for (int i = 0; i < amount; i++) {
                String code = CommandUtils.generateCdkCode(charset, length);
                CdkRecord record = new CdkRecord(code, uses, commands, expireTime, note, cdkType, perPlayerMultiple);
                plugin.getCdkRecordDao().createCdk(record);
                codeList.append(code).append(i < amount - 1 ? ", " : "");
            }
            if (amount == 1) {
                sender.sendMessage(String.format("§a成功创建CDK码: §f%s", codeList.toString()));
            } else {
                sender.sendMessage(String.format("§a成功创建 %d 个CDK码。类型: %s\n§f%s",
                        amount, cdkType.isEmpty() ? "无" : cdkType, codeList.toString()));
            }

            return true;
        } catch (Exception e) {
            sender.sendMessage("§c创建CDK时出错: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }

    /**
     * 解析命令行参数，正确处理引号中的空格
     * @param input 输入字符串
     * @return 解析后的参数数组
     */
    private String[] parseArguments(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new String[0];
        }
        
        List<String> args = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"([^\"]*)\"|(\\S+)").matcher(input);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                args.add(matcher.group(1)); // 引号内的内容
            } else {
                args.add(matcher.group(2)); // 非空格分隔的内容
            }
        }
        return args.toArray(new String[0]);
    }

    @Override
    public String getUsage() {
        return "§c用法: /cdk create <数量> \"<命令1|命令2|...>\" [次数] [备注] [过期时间] [类型] [允许多次使用]\n" +
               "§7  - 数量: 要生成的CDK个数 (1-100)\n" +
               "§7  - 命令: 被双引号包裹的命令序列，多个命令用 '|' 分隔\n" +
               "§7  - 次数: 每个CDK可兑换次数，默认取配置文件值\n" +
               "§7  - 备注: 可选文本说明，支持空格（建议用引号包裹）\n" +
               "§7  - 过期时间: 格式如 '2025-12-31 23:59' 或 'forever'\n" +
               "§7  - 类型: 可选分类标签\n" +
               "§7  - 允许多次使用: true/false，是否允许同一玩家多次使用同一个CDK";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("1", "5", "10", "50", "100");
        }
        if (args.length == 2) {
            return Arrays.asList(
                "give {player} diamond 1",
                "eco give {player} 1000",
                "give {player} diamond 1|give {player} emerald 1"
            );
        }
        if (args.length == 3) {
            return Arrays.asList("1", "5", "10", "-1");
        }
        if (args.length == 5) {
            return Arrays.asList("forever", "2025-12-31 23:59");
        }
        return new ArrayList<>();
    }
}