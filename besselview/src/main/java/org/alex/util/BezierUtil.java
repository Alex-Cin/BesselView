package org.alex.util;

import android.graphics.PointF;

/**
 * 作者：Alex
 * 时间：2016年12月01日
 * 简述：
 */

public class BezierUtil {
    /**
     * 获取 二阶贝塞尔曲线的最高点
     *
     * @param startPointF   开始点
     * @param controlPointF 控制点
     * @param endPointF     结束点
     * @return
     */
    public static PointF getBezierPointF(PointF startPointF, PointF endPointF, PointF controlPointF) {
        return getBezierPointF(0.5F, startPointF, endPointF, controlPointF);
    }

    /**
     * 计算 二阶贝塞尔曲线的坐标
     *
     * @param t             曲线长度比例， 进度比例 [0, 1]
     * @param startPointF   开始点
     * @param controlPointF 控制点
     * @param endPointF     结束点
     * @return
     */
    public static PointF getBezierPointF(float t, PointF startPointF, PointF endPointF, PointF controlPointF) {
        PointF pointF = new PointF(0, 0);
        float tmp = 1 - t;
        pointF.x = tmp * tmp * startPointF.x + 2 * t * tmp * controlPointF.x + t * t * endPointF.x;
        pointF.y = tmp * tmp * startPointF.y + 2 * t * tmp * controlPointF.y + t * t * endPointF.y;
        return pointF;
    }


    /**
     * 根据 最高点，获取贝塞尔曲线的 控制点
     *
     * @param startPointF  开始点
     * @param endPointF    结束点
     * @param bezierPointF 最高点
     * @return
     */
    public static PointF getControlPointF(PointF startPointF, PointF endPointF, PointF bezierPointF) {
        PointF controlPointF = new PointF(0, 0);
        float tmp = 0.5F;
        float t = 0.5F;
        controlPointF.x = (bezierPointF.x - tmp * tmp * startPointF.x - t * t * endPointF.x) / (2 * t * tmp);
        controlPointF.y = (bezierPointF.y - tmp * tmp * startPointF.y - t * t * endPointF.y) / (2 * t * tmp);
        return controlPointF;
    }

}
