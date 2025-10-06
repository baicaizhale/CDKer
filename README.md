# CDKer

## 简介

CDKer 是一个为 Minecraft 服务器设计的插件，旨在提供灵活的兑换码（CDK）系统。玩家可以使用兑换码来获取游戏内物品、执行命令等。

**注意：本项目目前正在重构中。如果您遇到问题，建议暂时使用 v3 版本。**

## 文档

更详细的文档请访问：[baicaizhale.icu/notes/CDKer/](https://baicaizhale.icu/notes/CDKer/)


## 功能

-   **多种兑换码类型**：支持一次性兑换码和可多次使用的兑换码。
-   **自定义奖励**：兑换码可以绑定一个或多个命令，实现丰富的奖励机制。
-   **过期时间设置**：可以为兑换码设置有效期。
-   **权限控制**：通过权限系统管理兑换码的创建、删除和使用。
-   **多语言支持**：支持中文和英文。

## 安装与构建

1.  **克隆仓库**：
    ```bash
    git clone https://github.com/your-username/CDKer.git
    cd CDKer
    ```
2.  **构建项目**：
    ```bash
    mvn clean install
    ```
    构建成功后，您将在 `target/` 目录下找到 `CDKer-SNAPSHOT.jar` 文件。

## 使用方法

1.  将生成的 `CDKer-SNAPSHOT.jar` 文件放入您的 Minecraft 服务器的 `plugins/` 文件夹中。
2.  启动或重启您的 Minecraft 服务器。
3.  插件将自动生成配置文件和语言文件。

### 玩家命令

-   `/cdk use <CDK>`：使用一个兑换码。

### 管理员命令

-   `/cdkadmin create single <id> <quantity> "<command1|command2|>" [expiration_time]`：创建一个一次性兑换码。
-   `/cdkadmin create multiple <name> <id> <quantity> "<command1|command2|>" [expiration_time]`：创建一个可多次使用的兑换码。
-   `/cdkadmin add <id> <quantity>`：批量生成/增加可使用次数。
-   `/cdkadmin delete <cdk|id> <content>`：删除一个兑换码或一个ID下的所有兑换码。
-   `/cdkadmin list`：列出所有兑换码。
-   `/cdkadmin reload`：重新加载配置和语言文件。
-   `/cdkadmin export`：导出所有兑换码。

## 配置

插件的配置文件位于 `plugins/CDKer/config.yml`，语言文件位于 `plugins/CDKer/lang/` 目录下。您可以根据需要修改这些文件。

## 许可证

本项目采用 MIT 许可证。详情请参阅 `LICENSE` 文件。