# cdkadmin 命令使用文档

## 概述

`cdkadmin` 是一个用于管理 CDK（Code Development Kit）的命令行工具。它提供了一系列命令，用于创建、添加、删除、列出、重新加载配置以及导出CDK。

## 别名

`cdkadmin` 命令可以通过以下别名使用：

*   `cdkadmin`
*   `giftcode`

## 命令详情

### 1. `create` 命令

用于创建新的CDK。根据需求可以创建一次性CDK或可多次使用的CDK。

*   **创建一次性CDK**
    *   **用法**: `/cdkadmin create single <id> <数量> \"<命令1|命令2|...>\"`
    *   **参数**: 
        *   `<id>`: CDK的唯一标识符。
        *   `<数量>`: 该CDK可使用的次数。
        *   `\"<命令1|命令2|...>\"`: CDK被使用时将执行的命令列表，多个命令之间用 `|` 分隔。
    *   **示例**: `/cdkadmin create single myCDK 1 \"give %player% diamond 1|say %player% used myCDK\"`

*   **创建可多次使用CDK (指定名称)**
    *   **用法**: `/cdkadmin create multiple <name> <id> <数量> \"<命令1|命令2|...>\"`
    *   **参数**: 
        *   `<name>`: 可多次使用CDK的名称。
        *   `<id>`: CDK的唯一标识符。
        *   `<数量>`: 该CDK可使用的次数。
        *   `\"<命令1|命令2|...>\"`: CDK被使用时将执行的命令列表，多个命令之间用 `|` 分隔。
    *   **示例**: `/cdkadmin create multiple dailyReward dailyCDK 5 \"give %player% gold_ingot 10\"`

*   **创建可多次使用CDK (随机ID)**
    *   **用法**: `/cdkadmin create multiple random <数量> \"<命令1|命令2|...>\"`
    *   **参数**: 
        *   `<数量>`: 该CDK可使用的次数。
        *   `\"<命令1|命令2|...>\"`: CDK被使用时将执行的命令列表，多个命令之间用 `|` 分隔。
    *   **示例**: `/cdkadmin create multiple random 3 \"effect give %player% speed 30 1\"`

*   **相关代码文件**: <mcfile name="CreateCommandExecutor.java" path="src/main/java/org/baicaizhale/cDKer/command/CreateCommandExecutor.java"></mcfile>

### 2. `add` 命令

用于批量生成或增加现有CDK的使用次数。

*   **用法**: `/cdkadmin add <id> <数量>`
*   **参数**: 
    *   `<id>`: 要增加使用次数的CDK的唯一标识符。
    *   `<数量>`: 要增加的使用次数。
*   **示例**: `/cdkadmin add myCDK 10` (将 `myCDK` 的使用次数增加10次)

### 3. `delete` 命令

用于删除CDK或指定ID下的所有CDK。

*   **用法**: `/cdkadmin delete <cdk|id> <内容>`
*   **参数**: 
    *   `<cdk|id>`: 指定删除类型，可以是 `cdk` (删除单个CDK) 或 `id` (删除指定ID下的所有CDK)。
    *   `<内容>`: 根据删除类型，可以是CDK的ID或CDK的名称。
*   **示例**: 
    *   `/cdkadmin delete cdk myCDK` (删除ID为 `myCDK` 的CDK)
    *   `/cdkadmin delete id dailyReward` (删除名称为 `dailyReward` 的所有CDK)

### 4. `list` 命令

用于列出所有已创建的CDK。

*   **用法**: `/cdkadmin list`
*   **示例**: `/cdkadmin list`

### 5. `reload` 命令

用于重新加载配置和语言文件。

*   **用法**: `/cdkadmin reload`
*   **示例**: `/cdkadmin reload`

### 6. `export` 命令

用于导出所有CDK数据。

*   **用法**: `/cdkadmin export`
*   **示例**: `/cdkadmin export`