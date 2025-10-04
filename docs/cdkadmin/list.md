# cdkadmin list 命令详解

`cdkadmin list` 命令用于列出所有当前系统中存在的CDK。这个命令提供了一个快速查看所有已创建CDK的概览，包括它们的ID、类型、剩余使用次数等关键信息。

## 命令格式

`/cdkadmin list`

## 参数说明

此命令没有额外的参数。

## 使用示例

*   列出所有CDK：
    `/cdkadmin list`

## 输出示例

执行 `list` 命令后，您可能会看到类似以下的输出（具体格式可能因实现而异）：

```
--- CDK 列表 ---
ID: welcomeGift, 类型: single, 剩余次数: 1, 命令: give %player% gold_ingot 10
ID: loginBonus, 类型: multiple, 名称: dailyLogin, 剩余次数: 98, 命令: give %player% experience_bottle 5
ID: randomCDK123, 类型: multiple, 剩余次数: 45, 命令: give %player% enchanted_golden_apple 1
------------------
```

## 注意事项

*   输出内容可能包含CDK的ID、类型（一次性或多次使用）、名称（如果适用）、剩余使用次数以及激活后执行的命令等信息。
*   对于数量庞大的CDK，输出可能会很长。