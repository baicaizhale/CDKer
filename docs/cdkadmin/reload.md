# cdkadmin reload 命令详解

`cdkadmin reload` 命令用于重新加载 `cdkadmin` 插件的配置和语言文件。这在您修改了配置文件（例如 `config.yml` 或语言文件 `lang_cn.yml`）后，无需重启服务器即可使更改生效时非常有用。

## 命令格式

`/cdkadmin reload`

## 参数说明

此命令没有额外的参数。

## 使用示例

*   重新加载 `cdkadmin` 的配置和语言文件：
    `/cdkadmin reload`

## 注意事项

*   执行此命令后，所有对 `cdkadmin` 配置文件和语言文件的修改将立即生效。
*   如果配置文件中存在语法错误，重新加载可能会失败，并可能在服务器控制台输出错误信息。
*   此命令通常由服务器管理员在进行配置调整后使用。

## 相关文件

*   **语言文件**: <mcfile name="lang_cn.yml" path="src/main/resources/lang/lang_cn.yml"></mcfile>