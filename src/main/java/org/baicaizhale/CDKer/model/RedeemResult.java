package org.baicaizhale.CDKer.model;

/**
 * 兑换结果，由 CdkRecordDao.redeem 在单个事务内判定。
 */
public enum RedeemResult {
    /** 兑换成功，奖励已记账 */
    SUCCESS,
    /** 兑换码不存在 */
    INVALID,
    /** 该玩家已兑换过此码（且该码不允许重复使用） */
    ALREADY_USED,
    /** 已过期或剩余次数不足 */
    USED_UP
}
