package org.baicaizhale.CDKer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * 挂起的 CDK 剩余命令序列。
 * sleep 等待期间玩家离线或服务器重启时，剩余命令连同绝对到期时间一起落库，
 * 玩家上线后由 PlayerJoinListener 恢复执行。记录一直保留到被恢复执行为止。
 */
@Data
@NoArgsConstructor
public class PendingSequence {
    private int id;
    private UUID playerUuid;
    private String cdkCode;
    private String cdkType;
    private List<String> remainingCommands;
    /** 恢复执行前，序列中是否已有命令执行失败（决定最终回滚与否，跨挂起保持） */
    private boolean hadFailure;
    /** 绝对到期时间（epoch 毫秒，按服务器墙钟折算），到期后玩家在线即继续 */
    private long resumeAt;

    public PendingSequence(UUID playerUuid, String cdkCode, String cdkType,
                           List<String> remainingCommands, boolean hadFailure, long resumeAt) {
        this.playerUuid = playerUuid;
        this.cdkCode = cdkCode;
        this.cdkType = cdkType;
        this.remainingCommands = remainingCommands;
        this.hadFailure = hadFailure;
        this.resumeAt = resumeAt;
    }
}
