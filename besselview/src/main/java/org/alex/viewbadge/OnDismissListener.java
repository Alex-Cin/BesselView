package org.alex.viewbadge;

/**
 * 作者：Alex
 * 时间：2016年12月04日
 * 简述：
 */
public interface OnDismissListener {

    /**
     * 可拖动的 大圆消失
     *
     * @param centerX    可拖动大圆的 中心点
     * @param centerY    可拖动大圆的 中心点
     * @param boomStatus 爆炸状态，参见 OnDismissListener#BoomStatus
     * @param duration   爆炸的时间长度
     */
    void onDismissBoom(float centerX, float centerY, String boomStatus, long duration);

}