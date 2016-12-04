package org.alex.besselview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 作者：Alex
 * 时间：2016年12月01日
 * 简述：
 */

public class BezierView3_2 extends View {
    private Paint paint;

    /**
     * 视图的中心点
     */
    private int centerX, centerY;

    /**
     * 起始点
     */
    private PointF startPointF;
    /*结束点*/
    private PointF endPointF;
    /**
     * 控制点0
     */
    private PointF control0PointF;
    /**
     * 控制点1
     */
    private PointF control1PointF;

    /**
     * 通过mode判断当前控制哪一个控制点
     */
    private int mode;

    public void setMode(int newMode) {
        this.mode = newMode;
    }

    public BezierView3_2(Context context) {
        super(context);
    }

    public BezierView3_2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mode = 0;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(60);

        startPointF = new PointF(0, 0);
        endPointF = new PointF(0, 0);
        control0PointF = new PointF(0, 0);
        control1PointF = new PointF(0, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        centerX = w / 2;
        centerY = h / 2;

        /*初始化数据点和控制点的位置*/
        startPointF.x = centerX - 200;
        startPointF.y = centerY;

        endPointF.x = centerX + 200;
        endPointF.y = centerY;

        control0PointF.x = centerX;
        control0PointF.y = centerY - 100;

        control1PointF.x = centerX;
        control1PointF.y = centerY + 100;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*绘制数据点和控制点*/
        paint.setColor(Color.RED);
        paint.setStrokeWidth(20);
        canvas.drawPoint(startPointF.x, startPointF.y, paint);
        canvas.drawPoint(endPointF.x, endPointF.y, paint);
        paint.setColor(Color.BLUE);
        canvas.drawPoint(control0PointF.x, control0PointF.y, paint);
        canvas.drawPoint(control1PointF.x, control1PointF.y, paint);

        /*绘制辅助线*/
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        canvas.drawLine(startPointF.x, startPointF.y, control0PointF.x, control0PointF.y, paint);
        canvas.drawLine(control0PointF.x, control0PointF.y, control1PointF.x, control1PointF.y, paint);
        canvas.drawLine(control1PointF.x, control1PointF.y, endPointF.x, endPointF.y, paint);

        /*绘制贝塞尔曲线*/
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(4);

        Path path = new Path();
        path.moveTo(startPointF.x, startPointF.y);
        path.cubicTo(control0PointF.x, control0PointF.y, control1PointF.x, control1PointF.y, endPointF.x, endPointF.y);

        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mode == 0) {
            control0PointF.x = centerX;
            control0PointF.y = event.getY();
        } else if (mode == 1) {
            control1PointF.x = centerX;
            control1PointF.y = event.getY();
        }
        invalidate();
        return true;
    }

}
