# 新手练手用插件，回复有点bot，抱歉

# CDKer 插件

**CDKer** 是一个 Minecraft 插件，用于创建并管理礼品码系统，允许玩家通过输入礼品码来兑换奖励。插件支持多次使用的兑换码、一次性兑换码、以及根据玩家名称替换命令中的占位符，还支持多语言和自定义消息前缀。

## 特性

- 支持礼品码与命令绑定，玩家可以通过输入礼品码来执行特定的命令。
- 支持为每个礼品码设置使用次数，并且可以控制每个玩家使用兑换码的次数。
- 支持多语言，通过配置文件自定义语言，并且可以使用 `&` 符号改变消息颜色。
- 在 `cdk.yml` 配置文件中，可以为每个礼品码绑定多个命令。
- 支持 `"%player%"` 占位符，能够在命令中动态替换为玩家的名字。
- 支持自定义消息前缀，在 `config.yml` 中配置。

## 安装

1. 下载 **CDKer.jar** 文件。
2. 将 **CDKer.jar** 放入 Minecraft 服务器的 `plugins/` 文件夹中。
3. 重启服务器，插件会自动生成配置文件。

## 配置

### `config.yml`

在 `config.yml` 中，你可以设置插件的语言和自定义消息前缀。
```
# 使用的语言,如果不自己加的话支持cn与en.配置文件位于./lang
language: "cn"

# 插件发送的消息前缀
prefix: "&bCDKer &7> &f"
```
### `cdk.yml`
在 `cdk.yml` 中，定义每个礼品码与对应的命令，以及剩余使用次数，示例如下。
```
giftcode:
  commands:
    - "say 这是一个礼品"
    - "give %player% diamond"
  remainingUses: 3
```
### `lang/lang_cn.yml` 和 `lang/lang_en.yml`
你可以根据需要编辑语言文件，在 `lang/lang_cn.yml` 和 `lang/lang_en.yml` 中定义插件的语言消息。

lang/lang_cn.yml（中文）：
```
messages:
  success: "&a兑换成功."
  already_used: "&c您已经使用过该兑换码！"
  invalid_code: "&c无效的礼品码！"
  max_usage: "&c该兑换码已达到使用次数上限，不能再使用！"
  usage_info: "&e使用方法: /cdk <cdkCode>"
```
lang/lang_en.yml（英文）：
```
messages:
  success: "&aSuccessfully redeemed."
  already_used: "&cYou have already used this code!"
  invalid_code: "&cInvalid gift code!"
  max_usage: "&cThis gift code has reached its usage limit and cannot be used again!"
  usage_info: "&eUsage: /cdk <cdkCode>"
```
## 使用
1. 玩家在游戏中输入 /cdk <cdkCode>，例如 /cdk giftcode1。
2. 如果该兑换码有效且未被使用，插件将执行绑定的命令。
3. 每个兑换码可以设置剩余使用次数，次数用完后将不再有效。
4. 每个玩家只能使用一次某个兑换码。
5.cdk.yml 中设置的命令可以包含 %player% 占位符，插件会自动替换为执行命令的玩家的名字。

## 贡献
欢迎提交 PR 来改进此插件。如果你有任何问题或建议，可以通过 issues 进行反馈。

## License
MIT License.
