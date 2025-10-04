# cdkadmin create 命令详解

`cdkadmin create` 命令用于创建新的CDK（Code Development Kit）。根据您的需求，您可以创建一次性CDK或可多次使用的CDK。此命令是CDK管理的核心，允许您定义CDK的类型、数量以及激活后执行的动作。

## 命令格式

`cdkadmin create <类型> ...`

## 子命令

### 1. 创建一次性CDK (`single`)

一次性CDK在被使用一次后即失效。适用于需要单次奖励或特定事件触发的场景。

*   **用法**: `/cdkadmin create single <id> <数量> \"<命令1|命令2|...>\" [有效时间]`

*   **参数说明**:
    *   `<id>` (必填): 为此CDK指定一个唯一的标识符。用户将使用此ID来激活CDK。
    *   `<数量>` (必填): 指定此CDK可被使用的总次数。对于 `single` 类型，通常设置为 `1`。
    *   `\"<命令1|命令2|...>\"` (必填): 当CDK被成功激活时，服务器将按顺序执行这些命令。多个命令之间必须使用 `|` 符号进行分隔。命令中可以使用占位符，例如 `%player%` 代表激活CDK的玩家名称。
    *   `[有效时间]` (可选): 指定CDK的有效期限，例如 `1d` (1天), `1h` (1小时), `1m` (1分钟)。如果未指定，CDK将永久有效直到使用次数耗尽。

*   **示例**:
    *   创建一个ID为 `welcomeGift`，可使用1次，激活后给予玩家10个金锭的CDK：
        `/cdkadmin create single welcomeGift 1 \"give %player% gold_ingot 10\"`
    *   创建一个ID为 `eventBonus`，可使用1次，激活后给予玩家一个钻石，并在1小时后失效的CDK：
        `/cdkadmin create single eventBonus 1 \"give %player% diamond 1\" 1h`

### 2. 创建可多次使用CDK (`multiple`)

可多次使用的CDK可以在其使用次数耗尽前被不同的玩家多次激活。适用于公共奖励、活动代码或需要重复使用的场景。

*   **用法**: `/cdkadmin create multiple <name> <id> <数量> \"<命令1|命令2|...>\" [有效时间]`
    *   **参数说明**:
        *   `<name>` (必填): 为此可多次使用CDK指定一个名称。这个名称可以用于管理和识别。
        *   `<id>` (必填): 为此CDK指定一个唯一的标识符。用户将使用此ID来激活CDK。
        *   `<数量>` (必填): 指定此CDK可被使用的总次数。
        *   `\"<命令1|命令2|...>\"` (必填): 当CDK被成功激活时，服务器将按顺序执行这些命令。多个命令之间必须使用 `|` 符号进行分隔。命令中可以使用占位符，例如 `%player%` 代表激活CDK的玩家名称。
        *   `[有效时间]` (可选): 指定CDK的有效期限。如果未指定，CDK将永久有效直到使用次数耗尽。

*   **示例**:
    *   创建一个名称为 `dailyLogin`，ID为 `loginBonus`，可使用100次，激活后给予玩家5个经验瓶的CDK：
        `/cdkadmin create multiple dailyLogin loginBonus 100 \"give %player% experience_bottle 5\"`

*   **用法 (随机ID)**: `/cdkadmin create multiple random <数量> \"<命令1|命令2|...>\" [有效时间]`
    *   **参数说明**:
        *   `random` (必填): 表示系统将自动生成一个随机的CDK ID。
        *   `<数量>` (必填): 指定此CDK可被使用的总次数。
        *   `\"<命令1|命令2|...>\"` (必填): 当CDK被成功激活时，服务器将按顺序执行这些命令。多个命令之间必须使用 `|` 符号进行分隔。
        *   `[有效时间]` (可选): 指定CDK的有效期限。如果未指定，CDK将永久有效直到使用次数耗尽。

*   **示例**:
    *   创建一个随机ID，可使用50次，激活后给予玩家一个附魔金苹果的CDK：
        `/cdkadmin create multiple random 50 \"give %player% enchanted_golden_apple 1\"`

## 相关代码文件

*   **命令执行逻辑**: <mcsymbol name="CreateCommandExecutor" filename="CreateCommandExecutor.java" path="src/main/java/org/baicaizhale/cDKer/command/CreateCommandExecutor.java" startline="19" type="class"></mcsymbol>