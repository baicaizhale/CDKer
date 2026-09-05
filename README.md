# CDKer

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17%2B-red.svg)](https://adoptium.net/)

CDKer 是一个运行在Spigot1.18+的Minecraft服务器插件，用于管理和分发兑换码（CDK）。它允许服务器管理员创建可兑换的代码，玩家可以使用这些代码获得奖励，如物品、金钱或其他游戏内福利。

## 目录

- [功能特性](#功能特性)
- [安装说明](#安装说明)
- [配置说明](#配置说明)
- [使用教程](#使用教程)
  - [创建CDK](#创建cdk)
  - [使用CDK](#使用cdk)
  - [管理CDK](#管理cdk)
- [权限节点](#权限节点)
- [数据库支持](#数据库支持)
- [命令参考](#命令参考)
- [构建项目](#构建项目)
- [贡献](#贡献)
- [许可证](#许可证)

## 功能特性

- 🎁 **创建多种类型的CDK**：支持创建一次性使用或可多次使用的兑换码
- 📦 **绑定命令奖励**：每个CDK可以绑定多个命令，如给予物品、金钱等
- ⏰ **设置过期时间**：可以为CDK设置过期时间，过期后无法使用
- 🔐 **权限控制**：区分玩家与管理员权限，确保安全操作
- 🌍 **多语言支持**：支持中文和英文界面
- 💾 **数据持久化**：支持SQLite和MySQL数据库，确保数据安全
- 📤 **导入导出功能**：可以将CDK数据导出为YAML文件或从YAML文件导入
- 📋 **列表分页**：支持分页浏览CDK列表，便于管理大量CDK
- 📢 **使用广播**：可配置CDK使用时的全服广播通知
- 🔄 **热重载配置**：支持在不重启服务器的情况下重载配置文件

## 安装说明

1. 下载最新版本的插件JAR文件
2. 将JAR文件放入服务器的 `plugins` 文件夹中
3. 启动或重启服务器
4. 插件会自动生成配置文件，位于 `plugins/CDKer/` 目录下
5. 根据需要修改配置文件
6. 使用 `/cdk reload` 命令重新加载配置

## 配置说明

插件的主要配置文件位于 `plugins/CDKer/config.yml`：

```yaml
# CDK配置
cdk:
  # CDK字符集
  charset: "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
  # CDK长度
  length: 12
  # 默认兑换次数
  default-uses: 1
  # 语言配置 (支持: zh_CN, en_US, ja_JP)
  language: "zh_CN"
  # 消息前缀
  prefix: "&bCDKer &7> &f"
  # 数据库配置
  database:
    type: "sqlite" # sqlite 或 mysql
    sqlite:
      file: "cdk.db"
    mysql:
      host: "localhost"
      port: 3306
      database: "cdk"
      username: "root"
      password: "password"
      table-prefix: "cdk_"

# 插件基础设置
settings:
  # 是否在使用CDK时广播
  broadcast: false
  # 广播消息模板 (支持 {player} 和 {type} 变量)
  broadcast-message: "§e玩家 {player} 使用了一个 {type} CDK!"

# 权限相关配置
permissions:
  # 管理员权限节点
  admin: "cdk.admin"
  # 使用CDK权限节点
  use: "cdk.use"
  # 创建CDK权限节点
  create: "cdk.create"
  # 查询CDK权限节点
  query: "cdk.query"
```

## 使用教程

### 创建CDK

创建CDK的基本语法：
```
/cdk create <数量> "<命令1|命令2|...>" [次数] [备注] [过期时间] [类型] [允许多次使用]
```

示例：
```
/cdk create 5 "give {player} diamond 1|say 恭喜获得钻石" 1 "新手礼包" "2025-12-31 23:59" "newbie" true
```

参数说明：
- `<数量>`: 要生成的CDK数量 (1-100)
- `<命令>`: 玩家兑换时执行的命令，多个命令用 `|` 分隔
- `[次数]`: 每个CDK可被兑换的总次数，默认为配置文件中的默认兑换次数
- `[备注]`: CDK的备注信息
- `[过期时间]`: 格式为 `yyyy-MM-dd HH:mm`，默认为 "forever"
- `[类型]`: 自定义类型标识
- `[允许多次使用]`: true/false，是否允许同一玩家多次兑换

### 使用CDK

玩家可以使用以下命令兑换CDK：
```
/cdk use <兑换码>
```

### 管理CDK

#### 查询CDK信息
```
/cdk query <id/cdk> <标识符>
```

#### 列出所有CDK
```
/cdk list [页码] [类型]
```

#### 删除CDK
```
/cdk del <id/cdk> <标识符>
```

#### 设置CDK属性
```
/cdk set <id/cdk> <标识符> <属性> <值>
```

可设置的属性包括：
- `remaining_uses`: 剩余使用次数
- `commands`: 命令列表
- `expire_time`: 过期时间
- `note`: 备注
- `cdk_type`: CDK类型
- `per_player_multiple`: 是否允许多次使用

#### 增加CDK使用次数
```
/cdk add <id/cdk> <标识符> <数量>
```

#### 导入CDK
```
/cdk import <文件名> [replace|append]
```
> 其中replace表示覆盖，append表示追加

#### 导出CDK
```
/cdk export <文件名>
```

## 权限节点

- `cdk.admin`: 管理员权限，可以执行所有命令
- `cdk.use`: 使用CDK权限
- `cdk.create`: 创建CDK权限
- `cdk.query`: 查询CDK信息权限

## 数据库支持

CDKer支持两种数据库类型：

### SQLite（默认）
轻量级的文件数据库，适合小型服务器。

### MySQL
适合大型服务器，支持网络连接和更好的性能。

要使用MySQL，请在配置文件中修改数据库设置：
```yaml
cdk:
  database:
    type: "mysql"
    mysql:
      host: "your_mysql_host"
      port: 3306
      database: "your_database_name"
      username: "your_username"
      password: "your_password"
      table-prefix: "cdk_"
```

## 命令参考

| 命令 | 权限 | 说明 |
|------|------|------|
| `/cdk help` | 无 | 显示帮助信息 |
| `/cdk create <数量> "<命令>" [次数] [备注] [过期时间] [类型] [允许多次使用]` | cdk.create | 创建CDK |
| `/cdk use <兑换码>` | cdk.use | 使用CDK |
| `/cdk query <id/cdk> <标识符>` | cdk.query | 查询CDK信息 |
| `/cdk view <id/cdk> <标识符>` | cdk.query | 在控制台或非玩家环境中查看CDK详情（纯文本、不带 hover） |
| `/cdk del <id/cdk> <标识符>` | cdk.admin | 删除CDK |
| `/cdk list [页码] [类型]` | cdk.admin | 列出所有CDK |
| `/cdk log [页码]` | cdk.log | 列出玩家兑换记录，支持分页和筛选（/cdk log filter <player|uuid|type> <值> [页码]） |
|  |  |  注: 在客户端中，鼠标悬停玩家名会显示 UUID；悬停命令数会显示命令列表。使用 `/cdk log view <id>` 可查看单条记录详情（含完整命令列表）。 |
| `/cdk set <id/cdk> <标识符> <属性> <值>` | cdk.admin | 设置CDK属性 |
| `/cdk add <id/cdk> <标识符> <数量>` | cdk.admin | 增加CDK使用次数 |
| `/cdk reload` | cdk.admin | 重新加载配置 |
| `/cdk import <文件> [replace\|append]` | cdk.admin | 从文件导入CDK |
| `/cdk export <文件>` | cdk.admin | 导出CDK到文件 |

## 构建项目

要从源代码构建插件，您需要：

1. JDK 17 
2. Apache Maven 3.6 

构建步骤：
```bash
git clone https://github.com/baicaizhale/CDKer.git
cd CDKer
mvn clean package
```

构建后的JAR文件将位于 `target/` 目录中。

## 贡献

欢迎任何形式的贡献！如果您发现了bug或有改进建议，请提交[Issue](https://github.com/baicaizhale/CDKer/issues)或[Pull Request](https://github.com/baicaizhale/CDKer/pulls)。

## 第三方工具
- [MC-CDKer-Generator](https://github.com/EndlessPixel/MC-CDKer-Generator)：一个简单的 CDK 批量生成脚本，支持自定义物品池和权重，采用20位随机字符串作为cdk代码，目前仅支持give

## 许可证

本项目采用MIT许可证开源，详情请见 [LICENSE](LICENSE) 文件。
