package org.baicaizhale.cDKer.database;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.model.CdkRecord;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class DbToYmlExporter {
    private final CDKer plugin;
    private final CdkRecordDao cdkRecordDao;

    public DbToYmlExporter(CDKer plugin, CdkRecordDao cdkRecordDao) {
        this.plugin = plugin;
        this.cdkRecordDao = cdkRecordDao;
    }

    public void exportToYml(File ymlFile) throws IOException, SQLException {
        YamlConfiguration ymlConfig = new YamlConfiguration();
        List<CdkRecord> records = cdkRecordDao.getAllCdks();

        for (CdkRecord record : records) {
            String code = record.getCdkCode();
            ymlConfig.set(code + ".type", record.getCdkType());
            ymlConfig.set(code + ".commands", record.getCommands());
            ymlConfig.set(code + ".remainingUses", record.getRemainingUses());
            ymlConfig.set(code + ".expiration", DATE_FORMAT.format(record.getExpireTime()));
            ymlConfig.set(code + ".note", record.getNote());
            ymlConfig.set(code + ".perPlayerMultiple", record.isPerPlayerMultiple());
        }

        if (ymlFile.exists()) {
            File backupFile = new File(ymlFile.getParentFile(), 
                ymlFile.getName() + "." + System.currentTimeMillis() + ".bak");
            if (!ymlFile.renameTo(backupFile)) {
                plugin.getLogger().warning("无法创建配置文件备份: " + backupFile.getName());
            } else {
                plugin.getLogger().info("已创建配置文件备份: " + backupFile.getName());
            }
        }

        ymlConfig.save(ymlFile);
        plugin.getLogger().info("成功导出 " + records.size() + " 个CDK到配置文件。");
    }
}