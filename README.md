## 各路大佬，对不起！！！！！！！！！
## 新手练手用插件，不是故意写成粪的，抱歉！！！！！！！！！！！
## 4.0.0 更新作出大量破坏性改动，需要删除旧版插件文件夹(./plugins/CDKer)再安装新版本！！！！！！！！

---

### **v4版本贡献者：[咡如夏](https://github.com/baicaizhale/CDKer/discussions/1)**
### **v4大量使用：[Gemini 2.5 Flash](https://gemini.google.com/app) ，并且对其他插件(如AnCDK)功能尝试还原**
# CDKer 插件

**CDKer** 是一个功能 [~~丰富~~](https://github.com/baicaizhale/CDKer/discussions/1) 的 Minecraft 服务器礼品码管理插件，允许服务器管理员创建、管理和分发各种类型的礼品码，玩家则可以通过简单的命令兑换奖励。插件支持灵活的配置，包括多语言、自定义消息前缀、一次性或多次使用的礼品码，以及过期时间设置。

## 特性

* **全面的礼品码管理**:
    * **创建**: 支持创建一次性 (`single`) 和可多次使用 (`multiple`) 的礼品码，可指定数量和绑定一个或多个命令。
    * **添加/增加**: 为现有礼品码增加使用次数，或创建新的礼品码。
    * **删除**: 可以删除单个礼品码，或根据 ID 删除所有相关礼品码。
    * **列表**: 列出所有有效的礼品码及其详细信息，包括类型、剩余次数、绑定命令和过期时间。
    * **导出**: 将所有礼品码数据导出到文本文件，方便备份和查看。
* **灵活的命令绑定**: 礼品码可以绑定任意数量的服务器命令，支持 `"%player%"` 占位符，在执行时自动替换为玩家名称。
* **使用次数控制**: 每个礼品码可设置总使用次数，并可限制每个玩家对特定礼品码的使用次数（针对一次性礼品码）。
* **过期时间**: 可为礼品码设置具体的过期时间，过期后自动失效。
* **多语言支持**: 通过 `config.yml` 配置语言，插件会自动加载对应的语言文件 (`lang/lang_cn.yml`, `lang/lang_en.yml`)。
* **自定义消息前缀**: 在 `config.yml` 中配置插件发送消息的自定义前缀，并支持 Minecraft 颜色代码。
* **Tab 自动补全**: 为管理命令提供 Tab 自动补全功能，提升管理员操作体验。

## 安装

1. 下载最新版本的 **CDKer.jar** 文件。
2. 将 **CDKer.jar** 文件放入 Minecraft 服务器的 `plugins/` 文件夹中。
3. 启动或重启服务器。插件将自动生成必要的配置文件和默认语言文件。

## 配置

插件的配置文件位于 `plugins/CDKer/` 文件夹内。

### `config.yml`

这是插件的主配置文件，用于设置全局选项。

```yaml
# CDKer 插件主配置文件
# -------------------------------------
# 此文件用于配置插件的全局行为，例如语言和消息前缀。
# 它是插件正常运行所必需的，请勿随意删除。
# -------------------------------------

# 使用的语言。插件将加载 'lang/lang_<language>.yml' 文件。
# 目前支持 'cn' (中文) 和 'en' (英文)。
language: "cn"

# 插件发送到聊天框的消息前缀。
# 支持 Minecraft 颜色代码，例如 &b (青色), &7 (灰色), &f (白色)。
prefix: "&bCDKer &7> &f"
````

### `cdk.yml`

此文件存储所有礼品码的数据。你可以手动编辑此文件来创建或修改礼品码，但更推荐使用游戏内命令进行管理。

```yaml
# 礼品码示例
# ------------------------------------------
# 礼品码结构:
#   type: "single" 或 "multiple" (一次性或多次使用)
#   commands: ["命令1", "命令2", ...]
#   remainingUses: 剩余使用次数
#   expiration: "YYYY-MM-dd HH:mm" (可选，过期时间)
# ------------------------------------------

# 一次性兑换码示例
ONETIME_DIAMOND_CDK:
  type: "single"
  commands:
    - "say %player% 兑换了一个一次性钻石CDK！"
    - "give %player% diamond 1"
  remainingUses: 1 # 一次性CDK通常只有1次使用

# 多次使用兑换码示例
VIP_REWARD_CDK:
  type: "multiple"
  commands:
    - "say %player% 兑换了VIP奖励！"
    - "give %player% gold_ingot 5"
  remainingUses: 999 # 多次使用CDK可以有多次使用
  expiration: "2025-12-31 23:59" # 设置过期时间

# 另一个一次性兑换码，带过期时间
LIMITED_TIME_SWORD:
  type: "single"
  commands:
    - "say %player% 获得了一把限时宝剑！"
    - "give %player% iron_sword 1"
  remainingUses: 1
  expiration: "2024-07-15 18:00" # 示例过期时间
```

### `lang/` 文件夹 (语言文件)

在 `plugins/CDKer/lang/` 文件夹中，你可以找到语言文件，如 `lang_cn.yml` 和 `lang_en.yml`。你可以根据需要编辑这些文件来自定义插件发送的每条消息。

**`lang/lang_cn.yml` (中文示例):**

```yaml
# CDK 中文语言文件 (zh_CN)

# 通用消息
prefix: "&7[&bCDK&7] &r"
no_permission: "&c你没有权限执行此命令！"
unknown_command: "&c未知命令！使用 /cdk help 查看命令列表。"
no_permission_use: "&c你没有权限使用此 CDK！"

# 帮助信息
help_header: "&6===== &e&lCDK 帮助 &6=====&r"
help_create: "&b/cdk create single <id> <数量> \"<命令1|命令2|...>\" [有效时间] &7- 创建一次性CDK"
help_create_multiple: "&b/cdk create multiple <name|random> <id> <数量> \"<命令1|命令2|...>\" [有效时间] &7- 创建可多次使用CDK"
help_add: "&b/cdk add <id> <数量> &7- 批量生成/增加可使用次数"
help_delete: "&b/cdk delete <cdk|id> <内容> &7- 删除CDK/删除此id和id下的所有CDK"
help_list: "&b/cdk list &7- 列出所有CDK"
help_reload: "&b/cdk reload &7- 重新加载配置和语言文件"
help_export: "&b/cdk export &7- 导出所有CDK"
help_use: "&b/cdk use <CDK> &7- 使用一个CDK"
help_footer: "&6========================="

# 创建CDK相关消息
create_usage_single: "用法: /cdk create single <id> <数量> \"<命令1|命令2|...>\" [有效时间]"
create_usage_multiple: "用法: /cdk create multiple <name|random> <id> <数量> \"<命令1|命令2|...>\" [有效时间]"
create_example_single: "示例: /cdk create single 兑换1钻石 5 \"give %player% diamond 1\" 2024-12-01 10:00"
create_example_multiple: "示例: /cdk create multiple vip666 兑换10钻石 999 \"give %player% diamond 10\" 2024-12-01 10:00"
invalid_quantity: "&c数量必须是一个有效的数字！"
invalid_cdk_type: "&c无效的 CDK 类型！请使用 'single' 或 'multiple'。"
invalid_date_format: "&c无效的日期格式！请使用YYYY-MM-dd HH:mm 格式。"
create_success_single: "&a成功创建一次性CDK，数量: &e%quantity%&a，ID: &e%id%"
create_success_multiple: "&a成功创建多次使用CDK，名称: %cdk%，使用次数: &e%quantity%&a，ID: &e%id%"

# 添加CDK相关消息
add_usage: "用法: /cdk add <CDK> <id> \"<命令1|命令2|...>\" [有效时间]"
add_example: "示例: /cdk add ABC123 兑换钻石 \"give %player% diamond 1\" 2024-12-01 10:00"
add_success: "&a成功添加了CDK，ID: &e%id%&a，数量：&e%quantity%"
cdk_already_exists: "&cCDK &e%cdk% &c已经存在！"

# 删除CDK相关消息
delete_usage: "用法: /cdk delete <cdk|id> <内容>"
delete_success: "&a成功删除了 CDK: &e%cdk%"
cdk_not_found: "&cCDK &e%cdk% &c不存在！"

# 列表相关消息
list_header: "&6===== &bCDK 列表 &6=====&r"
list_item: "&b%cdk% &7- &a%id% &7- &e%commands% &7- &c%expiration%"
list_footer: "&6===================="
list_empty: "&c当前没有任何 CDK！"

# 重载相关消息
reload_success: "&a成功重新加载配置和语言文件！"

# 导出相关消息
export_success: "&a成功导出所有 CDK 到 &e%file%&a！"
export_failed: "&c导出 CDK 时出错！"

# 使用CDK相关消息
use_usage: "用法: /cdk use <CDK>"
use_player_only: "&c只有玩家可以使用 CDK！"
use_success: "&a成功使用了 CDK: &e%cdk%&a！"
cdk_expired: "&cCDK &e%cdk% &c已过期！"
cdk_already_used: "&c你已经使用过这个 CDK 了！"

# 其他消息
save_config_error: "&c保存配置文件时出错！"
save_cdk_error: "&c保存 CDK 数据时出错！"
save_log_error: "&c保存日志数据时出错！"
```

## 使用

插件的主要命令是 `/cdk`，它支持多个子命令。

### 玩家使用

* **`/cdk use <CDKCode>`**: 使用一个礼品码来兑换奖励。
    * 示例: `/cdk use ONETIME_DIAMOND_CDK`

### 管理员命令 (需要相应权限)

* **`/cdk help`**: 查看所有命令的帮助信息。
* **`/cdk create single <id> <数量> "<命令1|命令2|...>" [有效时间]`**: 创建一次性礼品码。
    * `<id>`: 礼品码的唯一标识。
    * `<数量>`: 该礼品码的总使用次数（对于 single 类型通常为 1）。
    * `"<命令1|命令2|...>"`: 用双引号括起来，多个命令用 `|` 分隔。命令中可以使用 `%player%` 占位符。
    * `[有效时间]`: 可选，格式为 `YYYY-MM-dd HH:mm`。
    * 示例: `/cdk create single MY_CODE 1 "give %player% diamond 1"`
    * 示例: `/cdk create single LIMITED_SWORD 1 "give %player% iron_sword 1" 2024-07-15 18:00`
* **`/cdk create multiple <name|random> <id> <数量> "<命令1|命令2|...>" [有效时间]`**: 创建可多次使用的礼品码。
    * `<name|random>`: 如果是 `random`，则自动生成 ID；否则使用 `<id>` 作为名称。
    * `<id>`: 礼品码的唯一标识。
    * `<数量>`: 该礼品码的总使用次数。
    * `"<命令1|命令2|...>"`: 同上。
    * `[有效时间]`: 可选，格式为 `YYYY-MM-dd HH:mm`。
    * 示例: `/cdk create multiple VIP_BONUS VIP666 999 "give %player% gold_ingot 5"`
* **`/cdk add <CDK> <数量>`**: 增加现有礼品码的剩余使用次数，如果礼品码不存在则创建。
    * 示例: `/cdk add MY_CODE 10`
* **`/cdk delete <cdk|id> <内容>`**: 删除礼品码。
    * `cdk`: 删除指定的单个礼品码。
    * `id`: 删除所有以 `<内容>` 为 ID 前缀的礼品码。
    * 示例: `/cdk delete cdk MY_CODE`
    * 示例: `/cdk delete id VIP` (删除所有以 "VIP" 开头的礼品码)
* **`/cdk list`**: 列出所有当前有效的礼品码及其详细信息。
* **`/cdk reload`**: 重新加载插件的 `config.yml`、`cdk.yml` 和语言文件。
* **`/cdk export`**: 将所有礼品码数据导出到 `plugins/CDKer/cdk_export.txt` 文件中。

## 权限

插件使用以下权限节点进行功能控制：

* `cdk.help`: 允许查看帮助信息 (默认: `true`，所有玩家)
* `cdk.create`: 允许创建礼品码 (默认: `op`)
* `cdk.add`: 允许增加礼品码数量或创建新礼品码 (默认: `op`)
* `cdk.delete`: 允许删除礼品码 (默认: `op`)
* `cdk.list`: 允许查看礼品码列表 (默认: `op`)
* `cdk.reload`: 允许重载插件配置 (默认: `op`)
* `cdk.export`: 允许导出礼品码数据 (默认: `op`)
* `cdk.use`: 允许玩家使用礼品码 (默认: `true`，所有玩家)

## 贡献

欢迎通过提交 Pull Request 来改进此插件。如果您有任何问题或建议，可以通过 [GitHub Issues](https://github.com/baicaizhale/CDKer/issues) 进行反馈。

# `不要加QQ压力我，我真的会害怕TAT`

## License

此项目采用 MIT 许可证。

