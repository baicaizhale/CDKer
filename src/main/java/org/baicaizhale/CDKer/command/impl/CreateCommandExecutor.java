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
        if (!CommandUtils.hasPermission(sender, "cdk.create")) {
            CommandUtils.sendMessage(sender, getMsg("command.common.no_permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(getMsg("command.create.usage"));
            return true;
        }

        try {
            int amount = Integer.parseInt(args[0]);
            if (amount <= 0 || amount > 100) {
                sender.sendMessage(getMsg("command.create.amount_range"));
                return true;
            }

            String fullArgsString = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            Matcher matcher = Pattern.compile("\"([^\"]*)\"").matcher(fullArgsString);
            String commandString;
            String remainingArgsString;

            if (matcher.find()) {
                commandString = matcher.group(1);
                remainingArgsString = fullArgsString.substring(matcher.end()).trim();
            } else {
                sender.sendMessage(getMsg("command.create.need_quotes"));
                sender.sendMessage(getMsg("command.create.usage"));
                return true;
            }

            List<String> commands = CommandUtils.parseCommands(commandString);
            if (commands.isEmpty()) {
                sender.sendMessage(getMsg("command.create.command_empty"));
                return true;
            }

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
                    sender.sendMessage(getMsg("command.create.invalid_uses"));
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
                sender.sendMessage(getMsg("command.create.success_single", codeList.toString()));
            } else {
                sender.sendMessage(getMsg("command.create.success_multi",
                        String.valueOf(amount), cdkType.isEmpty() ? "无" : cdkType, codeList.toString()));
            }

            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("创建CDK时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, getMsg("command.common.internal_error"));
            return true;
        }
    }

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
        return getMsg("command.create.usage");
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