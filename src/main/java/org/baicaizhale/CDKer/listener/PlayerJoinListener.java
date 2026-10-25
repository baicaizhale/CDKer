package org.baicaizhale.CDKer.listener;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.impl.UseCommandExecutor;
import org.baicaizhale.CDKer.model.PendingSequence;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.UUID;

/**
 * 玩家上线时恢复挂起的 CDK 剩余命令序列。
 * 挂起记录来自 sleep 等待期间玩家离线或服务器重启的场景；恢复静默进行，不向玩家发送提示。
 */
public class PlayerJoinListener implements Listener {

    private final CDKer plugin;
    private final UseCommandExecutor useCommandExecutor;

    public PlayerJoinListener(CDKer plugin, UseCommandExecutor useCommandExecutor) {
        this.plugin = plugin;
        this.useCommandExecutor = useCommandExecutor;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        // 查库放异步线程，避免上线高峰期阻塞主线程
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            List<PendingSequence> pendings;
            try {
                pendings = plugin.getPendingSequenceDao().findByPlayerUuid(uuid.toString());
            } catch (Exception e) {
                plugin.getLogger().severe("查询挂起的CDK序列失败: " + e.getMessage());
                return;
            }
            if (pendings.isEmpty()) {
                return;
            }
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                for (PendingSequence pending : pendings) {
                    scheduleResume(pending);
                }
            });
        });
    }

    /**
     * 到期时间已过的延后 2 tick 再执行（等玩家完全进入世界，避免 give 赶在登录完成前），
     * 还没到期的等到期后再执行。
     */
    private void scheduleResume(PendingSequence pending) {
        long delayMillis = pending.getResumeAt() - System.currentTimeMillis();
        long ticks = delayMillis <= 0 ? 2L : delayMillis / 50L + 1;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> resume(pending), ticks);
    }

    private void resume(PendingSequence pending) {
        if (Bukkit.getPlayer(pending.getPlayerUuid()) == null) {
            // 调度期间又离线了：记录保留，等下次上线
            return;
        }
        boolean deleted;
        try {
            deleted = plugin.getPendingSequenceDao().deleteById(pending.getId());
        } catch (Exception e) {
            plugin.getLogger().severe("删除挂起的CDK序列失败，本次不恢复: " + e.getMessage());
            return;
        }
        if (!deleted) {
            // 已被内存定时器路径接手，避免同一段剩余命令执行两次
            return;
        }
        useCommandExecutor.resumePending(pending);
    }
}
