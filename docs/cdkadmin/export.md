# cdkadmin export 命令详解

`cdkadmin export` 命令用于将所有CDK数据导出到文件中。这个功能对于数据备份、迁移或进行离线分析非常重要。

## 命令格式

`/cdkadmin export`

## 参数说明

此命令没有额外的参数。

## 使用示例

*   导出所有CDK数据：
    `/cdkadmin export`

## 导出文件位置与格式

*   通常，导出的文件会保存在 `cdkadmin` 插件的数据文件夹中，例如 `plugins/CDKer/data/cdk_export.yml` 或 `cdk_export.json`，具体取决于插件的实现。
*   导出的数据格式通常是 YAML 或 JSON，便于机器读取和人工审查。

## 注意事项

*   导出操作会包含所有当前有效的CDK数据，包括它们的ID、类型、剩余次数、执行命令等。
*   在进行服务器维护、升级或迁移之前，建议定期执行此命令以备份CDK数据。
*   导出的文件可能包含敏感信息（如命令），请妥善保管。