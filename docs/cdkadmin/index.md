# cdkadmin 命令概览

`cdkadmin` 是一个功能强大的命令行工具，专为管理游戏服务器中的 CDK（Code Development Kit）而设计。它允许管理员创建、修改、查询和导出各种类型的CDK，以实现灵活的游戏内物品分发、活动奖励或其他自定义功能。

## 主要功能

*   **CDK 创建**: 支持创建一次性CDK和可重复使用的CDK，并可自定义CDK激活后执行的命令。
*   **CDK 管理**: 能够增加现有CDK的使用次数，以及删除不再需要的CDK。
*   **CDK 查询**: 提供列出所有CDK的功能，方便管理员总览。
*   **配置管理**: 允许重新加载工具的配置和语言文件，以便实时更新设置。
*   **数据导出**: 支持将所有CDK数据导出，便于备份和迁移。

## 别名

为了方便使用，`cdkadmin` 命令还支持以下别名：

*   `cdkadmin`
*   `giftcode`

管理员可以在游戏内控制台或通过其他命令执行器使用这些别名来调用 `cdkadmin` 的各项功能。

## 相关文件

*   **命令别名配置**: <mcfile name="plugin.yml" path="src/main/resources/plugin.yml"></mcfile> (第10行)
*   **语言文件**: <mcfile name="lang_cn.yml" path="src/main/resources/lang/lang_cn.yml"></mcfile> (包含命令帮助信息)