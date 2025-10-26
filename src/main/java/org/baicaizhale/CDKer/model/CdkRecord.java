package org.baicaizhale.CDKer.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class CdkRecord {
    /**
     * 是否允许同一玩家多次兑换该CDK
     */
    private boolean perPlayerMultiple = false;
    private int id;
    private String cdkCode;
    private int remainingUses;
    private List<String> commands;
    private String expireTime;
    private String note;
    private String cdkType;
    private Date createdTime;

    public CdkRecord(String cdkCode, int remainingUses, List<String> commands, String expireTime, String note, String cdkType, boolean perPlayerMultiple) {
        this.cdkCode = cdkCode;
        this.remainingUses = remainingUses;
        this.commands = commands;
        this.expireTime = expireTime;
        this.note = note;
        this.cdkType = cdkType;
        this.perPlayerMultiple = perPlayerMultiple;
        this.createdTime = new Date();
    }

    public CdkRecord(String cdkCode, int remainingUses, List<String> commands, String expireTime, String note, String cdkType) {
        this(cdkCode, remainingUses, commands, expireTime, note, cdkType, false);
    }

    public boolean isPerPlayerMultiple() {
        return perPlayerMultiple;
    }
    public void setPerPlayerMultiple(boolean perPlayerMultiple) {
        this.perPlayerMultiple = perPlayerMultiple;
    }

    public boolean isExpired() {
        if ("forever".equals(expireTime)) {
            return false;
        }
        try {
            Date expiryDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").parse(expireTime);
            return new Date().after(expiryDate);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canBeUsed() {
        return !isExpired() && remainingUses > 0;
    }
}