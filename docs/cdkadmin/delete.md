# cdkadmin delete 命令详解

`cdkadmin delete` 命令用于删除CDK或指定ID下的所有CDK。这在您需要清理过期、无效或不再需要的CDK时非常有用。

## 命令格式

`/cdkadmin delete <cdk|id> <内容>`

## 参数说明

*   `<cdk|id>` (必填): 指定删除的类型。
    *   `cdk`: 表示您要删除一个特定的CDK。
    *   `id`: 表示您要删除某个名称下的所有CDK（通常用于删除 `multiple` 类型的CDK组）。
*   `<内容>` (必填): 根据删除类型，提供相应的CDK标识符或名称。
    *   如果 `<cdk|id>` 是 `cdk`，则 `<内容>` 应该是要删除的CDK的唯一ID。
    *   如果 `<cdk|id>` 是 `id`，则 `<内容>` 应该是要删除的CDK组的名称。

## 使用示例

*   删除ID为 `expiredCDK` 的单个CDK：
    `/cdkadmin delete cdk expiredCDK`

*   删除名称为 `oldEventRewards` 的所有可多次使用CDK：
    `/cdkadmin delete id oldEventRewards`

## 注意事项

*   删除操作是不可逆的，请在执行前仔细确认。
*   确保您提供的CDK ID或名称是准确的，以避免误删。