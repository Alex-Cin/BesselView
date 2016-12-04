package org.alex.viewbadge;

/**
 * 作者：Alex
 * 时间：2016年12月04日
 * 简述：
 */

public interface OnDraggingStatusListener {
    /**
     * 可拖动的 大圆，回滚到 最初的位置
     */
    void onDraggingRollback();

    /**
     * 固定的小圆，与最初的位置 断裂
     */
    void onDraggingDisconnect(float centerX, float centerY);
}
