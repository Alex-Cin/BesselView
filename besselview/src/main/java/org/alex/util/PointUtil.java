package org.alex.util;

import android.graphics.PointF;

/**
 * 作者：Alex
 * 时间：2016年12月02日
 * 简述：
 */

public class PointUtil {
    /**
     * 计算 两点之间的 距离
     *
     * @param pointF0
     * @param pointF1
     * @return
     */
    public static double getDistance(PointF pointF0, PointF pointF1) {
        double distance = 0;
        float dx = Math.abs(pointF0.x - pointF1.x);
        float dy = Math.abs(pointF0.y - pointF1.y);
        distance = Math.sqrt(dx * dx + dy * dy);
        return distance;
    }

    /**
     * 得到 三点之间的 角度 [0, 360]
     * 12点为 0度；3点为90 度
     *
     * @param stickPointF 固定不动的 点
     * @param dragPointF  被拖拽的 点
     * @return
     */
    public static double getAngle(PointF stickPointF, PointF dragPointF) {
        float dx = dragPointF.x - stickPointF.x;
        float dy = dragPointF.y - stickPointF.y;
        double distance = getDistance(stickPointF, dragPointF);
        double angle = Math.asin(dx / distance) * 180 / Math.PI;
        if (dy > 0) {
            angle = 180 - angle;
        } else {
            angle = 360 + angle;
        }
        //LogUtil.e("angle = " + angle);
        return angle;
    }

    /**
     * 根据 角度 和 距离，获取 PointF
     *
     * @param stickPointF
     * @param angle
     * @param distance
     * @return
     */
    public static PointF createPointFByAngle$Distance(PointF stickPointF, double angle, double distance) {
        PointF pointF = new PointF();
        double sin = Math.sin((angle / 180f) * Math.PI);
        double cos = Math.cos((angle / 180f) * Math.PI);
        pointF.x = (float) (stickPointF.x + distance * sin);
        pointF.y = (float) (stickPointF.y - distance * cos);
        return pointF;
    }

    /**
     * 参照 {@link  ../BesselView/preview/求切线坐标.png}
     * 求 固定的 小圆，的 一个 外切 PiontF
     *
     * @param stickPointF 固定的 小圆
     * @param dragPointF  拖拽的 大圆
     * @param angle       90 - α
     * @param stickRadius 固定的 小圆， 的半径
     * @param dragRadius  拖拽的 大圆，的半径
     * @return
     */
    public static PointF[] getTangentPointF4Stick(PointF stickPointF, PointF dragPointF, double angle, float stickRadius, float dragRadius) {
        PointF[] points = new PointF[2];
        double distance = getDistance(stickPointF, dragPointF);
        /*得到 γ */
        double c = Math.asin((dragRadius - stickRadius) / distance) * 180 / Math.PI;
        double a = 90 - angle;
        /*β = 180 - α - γ - 90*/
        double b = 90 - a - c;
        //LogUtil.e("angle = " + angle + " α = " + a + " β = " + b + " γ = " + c);
        points[0] = createPointFByAngle$Distance(stickPointF, angle - c - 90, stickRadius);
        points[1] = createPointFByAngle$Distance(stickPointF, angle + c + 90, stickRadius);
        return points;
    }

    /**
     * 参照 {@link  ../BesselView/preview/求切线坐标.png}
     * 求 固定的 小圆，的 一个 外切 PiontF
     *
     * @param stickPointF 固定的 小圆
     * @param dragPointF  拖拽的 大圆
     * @param angle       90 - α
     * @param stickRadius 固定的 小圆， 的半径
     * @param dragRadius  拖拽的 大圆，的半径
     * @return
     */
    public static PointF[] getTangentPointF4Drag(PointF stickPointF, PointF dragPointF, double angle, float stickRadius, float dragRadius) {
        PointF[] points = new PointF[2];
        double distance = getDistance(stickPointF, dragPointF);
        /*得到 γ */
        double c = Math.asin((dragRadius - stickRadius) / distance) * 180 / Math.PI;
        double p1p2Distance = (dragRadius - stickRadius) / Math.tan(Math.PI * c / 180F);
        double c1 = Math.atan(stickRadius / p1p2Distance) * 180 / Math.PI;
        points[0] = createPointFByAngle$Distance(stickPointF, angle - c - c1, distance);
        points[1] = createPointFByAngle$Distance(stickPointF, angle + c + c1, distance);
        return points;
    }
}
