package org.alex.viewbadge;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import org.alex.callback.SimpleAnimatorListener;
import org.alex.util.PointUtil;

/**
 * 作者：Alex
 * 时间：2016年12月02日
 * 简述：
 */
public class DraggingView extends View implements View.OnTouchListener {
    /**
     * 中心点坐标；控件本身宽高
     */
    private int centerX, centerY, width, height;
    /**
     * 固定对的小圆点的颜色； 可以拖拽的圆点的颜色；中间曲线的颜色
     */
    private int stickColor, dragColor, bezierColor;
    /**
     * 两个圆之间可分离的最大距离大小
     */
    private int maxDragDistance;
    /**
     * 小圆半径；在大圆向外拖动是，小圆所展示的半径；大圆半径；
     */
    private float stickRadius, realStickRadius, dragRadius;

    /**
     * 每次滑动，捕捉到的，滑动的距离，向右 向下滑动为正
     */
    private float lastX, lastY, distanceX, distanceY;
    /**
     * 固定的小圆 - 圆心坐标；可拖动的 大圆的 - 圆心坐标
     */
    private PointF stickyCenterPointF, dragCenterPointF;

    /**
     * 固定的 小圆上的 外切点；
     * 拖动的 大圆 上的 外切点；
     * 控制点（两圆中间的  某个中点）
     */
    private PointF stickTangent0PointF, stickTangent1PointF, dragTangent0PointF, dragTangent1PointF, control0PointF, control1PointF;

    /**
     * 內圆  最小展示 百分比
     */
    private static final float minPercent = 0.20F;
    /**
     * 內圆  最大展示 百分比
     */
    private static final float maxPercent = 1F;

    /**
     * 进度 = 两个圆心之间的距离 /  maxDragDistance  ∈ [0, 1]
     */
    private float percent;

    /**
     * 展示 贝塞尔曲线 的 控制元素
     */
    private boolean isDrawElement4Bezier;
    /**
     * 贝塞尔曲线的 路径 轨迹
     */
    private Path bezierPath;
    private Paint paint;
    /**
     * 可拖动的大圆，旋转的角度，12点为0°，顺时针度数增加
     */
    private double angle;
    /**
     * 可拖动的外圆，从最远处，回滚到最初状态，需要的时间
     */
    private long maxDragRollback;
    private TimeInterpolator interpolator;
    /**
     * 固定的小圆与 最初的固定点 断开了
     */
    private boolean isDisconnect;
    private CanvasHelper canvasHelper;

    public DraggingView(Context context) {
        this(context, null);
    }

    public DraggingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        percent = 0;
        maxDragRollback = 200;
        isDrawElement4Bezier = false;
        isDisconnect = false;
        setDragRadius(dp2px(4));
        /*  FF0000  00FF00  0000FF  */
        stickColor = Color.parseColor("#FF0000");
        dragColor = Color.parseColor("#FF0000");
        bezierColor = Color.parseColor("#FF0000");
        stickTangent0PointF = new PointF(0, 0);
        dragTangent0PointF = new PointF(0, 0);
        stickTangent0PointF = new PointF(0, 0);
        dragTangent0PointF = new PointF(0, 0);
        stickTangent1PointF = new PointF(0, 0);
        dragTangent1PointF = new PointF(0, 0);
        control0PointF = new PointF(0, 0);
        control1PointF = new PointF(0, 0);
        stickyCenterPointF = new PointF(0, 0);
        dragCenterPointF = new PointF(0, 0);
        bezierPath = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        //interpolator = new AnticipateOvershootInterpolator();
        interpolator = new OvershootInterpolator(2);
        simpleAnimatorListener = new MySimpleAnimatorListener();
        calculateStick$Drag$ControlPointF();
        setOnTouchListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isDisconnect) {
            /*画 固定的 小圆*/
            paint.setColor(stickColor);
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(stickyCenterPointF.x, stickyCenterPointF.y, realStickRadius, paint);
        }
        /*画 可拖动的 大圆*/
        paint.setColor(dragColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(dragCenterPointF.x, dragCenterPointF.y, dragRadius, paint);
        if (isDrawElement4Bezier && !isDisconnect) {
            drawElement4Bezier(canvas);
        }
        if (!isDisconnect) {
            /*画  贝塞尔曲线*/
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(bezierColor);
            paint.setStrokeWidth(2);
            bezierPath.reset();
            bezierPath.moveTo(stickTangent0PointF.x, stickTangent0PointF.y);
            bezierPath.quadTo(control0PointF.x, control0PointF.y, dragTangent0PointF.x, dragTangent0PointF.y);
            bezierPath.lineTo(dragTangent1PointF.x, dragTangent1PointF.y);
            bezierPath.quadTo(control1PointF.x, control1PointF.y, stickTangent1PointF.x, stickTangent1PointF.y);
            bezierPath.close();
            canvas.drawPath(bezierPath, paint);
        }
        canvasHelper.drawText(canvas, paint, dragCenterPointF.x, dragCenterPointF.y);
    }

    /**
     * 画 文本
     *
     * @param canvas
     */
    public void drawText(Canvas canvas, Paint paint, float centerX, float centerY) {
        //dragCenterPointF.x, dragCenterPointF.y, dragRadius, paint
        String text = "999+";
        int textColor = Color.parseColor("#FFFFFF");
        if (text != null) {
            paint.setColor(textColor);
            /*计算Baseline绘制的起点X轴坐标 ，计算方式：画布宽度的一半 - 文字宽度的一半*/
            int baseX = (int) (centerX - paint.measureText(text) / 2);
            /* 计算Baseline绘制的Y坐标 ，计算方式：画布高度的一半 - 文字总高度的一半*/
            int baseY = (int) ((centerY) - (paint.descent() + paint.ascent()) / 2);
            /*居中画一个文字*/
            canvas.drawText(text, baseX, baseY, paint);
        }
    }

    public DraggingView setLocation(float x, float y) {
        stickyCenterPointF.x = x;
        stickyCenterPointF.y = y;
        dragCenterPointF.x = x;
        dragCenterPointF.y = y;
        centerX = (int) x;
        centerY = (int) y;
        invalidate();
        return this;
    }


    /**
     * 展示 贝塞尔曲线 的 控制元素
     *
     * @param canvas
     */
    private void drawElement4Bezier(Canvas canvas) {
        /*画 两圆心之间 的连线*/
        paint.setColor(bezierColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#000000"));
        canvas.drawLine(stickyCenterPointF.x, stickyCenterPointF.y, dragCenterPointF.x, dragCenterPointF.y, paint);

        /*画 可拖动的 大圆的 最大旋转范围 - 范围圆*/
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#000000"));
        canvas.drawCircle(centerX, centerY, maxDragDistance, paint);

        /*画 固定的小圆上的 外切点*/
        paint.setColor(Color.parseColor("#009688"));
        paint.setStrokeWidth(2);
        canvas.drawPoint(stickTangent0PointF.x, stickTangent0PointF.y, paint);
        canvas.drawPoint(stickTangent1PointF.x, stickTangent1PointF.y, paint);

         /*画 可拖动的大圆上的 外切点*/
        paint.setColor(Color.parseColor("#FF0000"));
        paint.setStrokeWidth(4);
        canvas.drawPoint(dragTangent0PointF.x, dragTangent0PointF.y, paint);
        canvas.drawPoint(dragTangent1PointF.x, dragTangent1PointF.y, paint);

         /*画 控制点*/
        paint.setColor(Color.parseColor("#FF0000"));
        canvas.drawPoint(control0PointF.x, control0PointF.y, paint);
        canvas.drawPoint(control1PointF.x, control1PointF.y, paint);
    }

    /**
     * 返回 true 事件已经消费了，不会再有事件传递
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (MotionEvent.ACTION_DOWN == action) {
            lastX = event.getRawX();
            lastY = event.getRawY();
        } else if (MotionEvent.ACTION_MOVE == action) {
            distanceX += event.getRawX() - lastX;
            distanceY += event.getRawY() - lastY;
            dragCenterPointF.x = centerX + distanceX;
            dragCenterPointF.y = centerY + distanceY;
            percent = (float) (PointUtil.getDistance(stickyCenterPointF, dragCenterPointF) / maxDragDistance);
            angle = PointUtil.getAngle(stickyCenterPointF, dragCenterPointF);
            if (percent > 1) {
                //dragCenterPointF = PointUtil.createPointFByAngle$Distance(stickyCenterPointF, angle, maxDragDistance);
                percent = maxPercent;
                isDisconnect = true;
            } else {
                isDisconnect = false;
            }
            calculateStick$Drag$ControlPointF();
            invalidate();
            lastX = event.getRawX();
            lastY = event.getRawY();
        } else if (MotionEvent.ACTION_UP == action || MotionEvent.ACTION_CANCEL == action) {
            distanceX = 0;
            distanceY = 0;
            if (percent < maxPercent) {/*可拖动的大圆，回滚到初始位置*/
                ObjectAnimator dragRollbackAnimator = ObjectAnimator.ofFloat(this, "dragRollbackPercent", percent, 0);
                dragRollbackAnimator.setDuration((long) (maxDragRollback * percent));
                dragRollbackAnimator.setInterpolator(interpolator);
                dragRollbackAnimator.addListener(simpleAnimatorListener);
                dragRollbackAnimator.start();
            } else {/*展示控件消失 的动画*/
                invokeOnDismiss(true);
            }
        }
        return true;
    }

    private void invokeOnDismiss(boolean isDisconnect) {
        if (onDraggingStatusListener != null) {
            if (isDisconnect) {
                onDraggingStatusListener.onDraggingDisconnect(dragCenterPointF.x, dragCenterPointF.y);
            } else {
                onDraggingStatusListener.onDraggingRollback();
            }
        }
    }

    private SimpleAnimatorListener simpleAnimatorListener;

    private final class MySimpleAnimatorListener extends SimpleAnimatorListener {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            invokeOnDismiss(false);
        }
    }

    /**
     * 设置 可拖动大圆，回滚到初始状态的进度跟踪
     * 在MotionEvent.ACTION_UP 的时候，供  dragRollbackAnimator 调用
     *
     * @param percent
     */
    protected void setDragRollbackPercent(float percent) {
        this.percent = percent;
        dragCenterPointF = PointUtil.createPointFByAngle$Distance(stickyCenterPointF, angle, maxDragDistance * percent);
        calculateStick$Drag$ControlPointF();
        invalidate();
    }

    /**
     * 计算 固定小圆 的外切点，可拖动的大圆的外切点，控制点；
     * 固定的小圆当时需要展示的 半径大小
     */
    private void calculateStick$Drag$ControlPointF() {
        realStickRadius = (maxPercent - (maxPercent - minPercent) * percent) * stickRadius;
        stickTangent0PointF = PointUtil.getTangentPointF4Stick(stickyCenterPointF, dragCenterPointF, angle, realStickRadius, dragRadius)[0];
        stickTangent1PointF = PointUtil.getTangentPointF4Stick(stickyCenterPointF, dragCenterPointF, angle, realStickRadius, dragRadius)[1];
        dragTangent0PointF = PointUtil.getTangentPointF4Drag(stickyCenterPointF, dragCenterPointF, angle, realStickRadius, dragRadius)[0];
        dragTangent1PointF = PointUtil.getTangentPointF4Drag(stickyCenterPointF, dragCenterPointF, angle, realStickRadius, dragRadius)[1];
        control0PointF.x = (stickTangent0PointF.x + dragCenterPointF.x) / 2;
        control0PointF.y = (stickTangent0PointF.y + dragCenterPointF.y) / 2;
        control1PointF.x = (stickTangent1PointF.x + dragCenterPointF.x) / 2;
        control1PointF.y = (stickTangent1PointF.y + dragCenterPointF.y) / 2;
    }

    /**
     * 数据转换: dp---->px
     */
    private float dp2px(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

    /**
     * 设置 可拖动的大圆的 半径
     *
     * @param dragRadius 半径， 单位px
     * @return
     */
    public DraggingView setDragRadius(float dragRadius) {
        this.dragRadius = dragRadius;
        this.stickRadius = dragRadius * 0.9F;
        this.maxDragDistance = (int) (3 * dragRadius * 2);
        return this;
    }

    private OnDraggingStatusListener onDraggingStatusListener;

    public void setOnDraggingStatusListener(OnDraggingStatusListener onDraggingStatusListener) {
        this.onDraggingStatusListener = onDraggingStatusListener;
    }


    public void setCanvasHelper(CanvasHelper canvasHelper) {
        this.canvasHelper = canvasHelper;
    }

}