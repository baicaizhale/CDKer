package org.baicaizhale.CDKer.database;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.model.CdkRecord;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class YmlToDbImporter {
    private final CDKer plugin;
    private final CdkRecordDao cdkRecordDao;

    public YmlToDbImporter(CDKer plugin, CdkRecordDao cdkRecordDao) {
        this.plugin = plugin;
        this.cdkRecordDao = cdkRecordDao;
    }

    /**
     * 从 YML 文件导入 CDK。
     *
     * @param ymlFile 待导入的文件
     * @param replace 是否覆盖：为 true 时会在同一个事务里先清空原有记录再写入，
     *                中途失败会整体回滚，避免"清空后导入失败导致数据丢失"
     * @return 实际写入的记录数
     */
    public int importFromYml(File ymlFile, boolean replace) throws IOException, SQLException {
        if (ymlFile == null || !ymlFile.isFile()) {
            throw new IOException("导入文件不存在: " + (ymlFile == null ? "null" : ymlFile.getName()));
        }

        YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(ymlFile);
        ConfigurationSection cdkSection = ymlConfig.getRoot();

        if (cdkSection == null) {
            plugin.getLogger().warning("YML文件为空或格式错误: " + ymlFile.getName());
            return 0;
        }

        List<CdkRecord> records = new ArrayList<>();
        for (String code : cdkSection.getKeys(false)) {
            ConfigurationSection cdkData = cdkSection.getConfigurationSection(code);
            if (cdkData == null) continue;

            String type = cdkData.getString("type", "");
            List<String> commands = cdkData.getStringList("commands");
            int remainingUses = cdkData.getInt("remainingUses", 1);
            String expireTime = cdkData.getString("expiration", "forever");
            String note = cdkData.getString("note", "");
            boolean perPlayerMultiple = cdkData.getBoolean("perPlayerMultiple", false);

            records.add(new CdkRecord(code, remainingUses, commands, expireTime, note, type, perPlayerMultiple));
        }

        cdkRecordDao.importRecords(records, replace);
        plugin.getLogger().info("成功导入 " + records.size() + " 个CDK" + (replace ? "（已覆盖原有数据及使用记录）" : "") + "。");
        return records.size();
    }
}
