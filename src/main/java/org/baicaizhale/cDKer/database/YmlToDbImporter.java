package org.baicaizhale.cDKer.database;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.model.CdkRecord;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class YmlToDbImporter {
    private final CDKer plugin;
    private final CdkRecordDao cdkRecordDao;

    public YmlToDbImporter(CDKer plugin, CdkRecordDao cdkRecordDao) {
        this.plugin = plugin;
        this.cdkRecordDao = cdkRecordDao;
    }

    public void importFromYml(File ymlFile) throws IOException, SQLException {
        YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(ymlFile);
        ConfigurationSection cdkSection = ymlConfig.getRoot();

        if (cdkSection == null) {
            plugin.getLogger().warning("YML文件为空或格式错误: " + ymlFile.getName());
            return;
        }

        for (String code : cdkSection.getKeys(false)) {
            ConfigurationSection cdkData = cdkSection.getConfigurationSection(code);
            if (cdkData == null) continue;

            String type = cdkData.getString("type", "");
            List<String> commands = cdkData.getStringList("commands");
            int remainingUses = cdkData.getInt("remainingUses", 1);
            String expireTime = cdkData.getString("expiration", "forever");
            String note = cdkData.getString("note", "");

            CdkRecord record = new CdkRecord(code, remainingUses, commands, expireTime, note, type);
            try {
                cdkRecordDao.createCdk(record);
                plugin.getLogger().info("已导入CDK: " + code);
            } catch (SQLException e) {
                plugin.getLogger().warning("导入CDK失败: " + code + ", 原因: " + e.getMessage());
            }
        }
    }
}