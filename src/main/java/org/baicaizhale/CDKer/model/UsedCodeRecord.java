package org.baicaizhale.CDKer.model;

import java.util.Objects;

/**
 * UsedCodeRecord 实体类，用于表示玩家使用过的 CDK 记录。
 */
public class UsedCodeRecord {
    private String playerName;
    private String cdkCode;

    /**
     * 构造函数
     * @param playerName 玩家名称
     * @param cdkCode CDK 码
     */
    public UsedCodeRecord(String playerName, String cdkCode) {
        this.playerName = playerName;
        this.cdkCode = cdkCode;
    }

    /**
     * 获取玩家名称
     * @return 玩家名称
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * 设置玩家名称
     * @param playerName 玩家名称
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * 获取 CDK 码
     * @return CDK 码
     */
    public String getCdkCode() {
        return cdkCode;
    }

    /**
     * 设置 CDK 码
     * @param cdkCode CDK 码
     */
    public void setCdkCode(String cdkCode) {
        this.cdkCode = cdkCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsedCodeRecord that = (UsedCodeRecord) o;
        return Objects.equals(playerName, that.playerName) &&
               Objects.equals(cdkCode, that.cdkCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerName, cdkCode);
    }

    @Override
    public String toString() {
        return "UsedCodeRecord{" +
               "playerName='" + playerName + '\'' +
               ", cdkCode='" + cdkCode + '\'' +
               '}';
    }
}