package org.baicaizhale.cDKer.model;

import java.util.List;

/**
 * CDK 实体类，用于表示一个 CDK 条目。
 */
public class CDK {
    private String type;
    private List<String> commands;
    private int remainingUses;
    private String expiration; // 可选，过期时间

    /**
     * 构造函数
     * @param type CDK 类型
     * @param commands CDK 包含的命令列表
     * @param remainingUses 剩余使用次数
     * @param expiration 过期时间字符串
     */
    public CDK(String type, List<String> commands, int remainingUses, String expiration) {
        this.type = type;
        this.commands = commands;
        this.remainingUses = remainingUses;
        this.expiration = expiration;
    }

    /**
     * 获取 CDK 类型
     * @return CDK 类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置 CDK 类型
     * @param type CDK 类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取 CDK 包含的命令列表
     * @return 命令列表
     */
    public List<String> getCommands() {
        return commands;
    }

    /**
     * 设置 CDK 包含的命令列表
     * @param commands 命令列表
     */
    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    /**
     * 获取剩余使用次数
     * @return 剩余使用次数
     */
    public int getRemainingUses() {
        return remainingUses;
    }

    /**
     * 设置剩余使用次数
     * @param remainingUses 剩余使用次数
     */
    public void setRemainingUses(int remainingUses) {
        this.remainingUses = remainingUses;
    }

    /**
     * 获取过期时间字符串
     * @return 过期时间字符串
     */
    public String getExpiration() {
        return expiration;
    }

    /**
     * 设置过期时间字符串
     * @param expiration 过期时间字符串
     */
    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }
}