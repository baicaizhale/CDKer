package org.baicaizhale.CDKer.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
public class CdkLog {
    private int id;
    private String playerName;
    private String playerUUID;
    private String cdkCode;
    private String cdkType;
    private String commandsExecuted;
    private Date useTime;

    public CdkLog(String playerName, String playerUUID, String cdkCode, String cdkType, String commandsExecuted) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.cdkCode = cdkCode;
        this.cdkType = cdkType;
        this.commandsExecuted = commandsExecuted;
        this.useTime = new Date();
    }
}