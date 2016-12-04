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

import org.alex.util.BezierUtil;

/**
 * 作者：Alex
 * 时间：2016年12月01日
 * 简述：
 */

public class BezierView2 extends View {
    protected Paint paint;
    protected int width, height, centerX, centerY;
    protected PointF startPointF, endPointF, controlPointF;

    public BezierView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5f);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(60);

        startPointF = new PointF(0, 0);
        endPointF = new PointF(0, 0);
        controlPointF = new PointF(0, 0);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        centerX = w / 2;
        centerY = h / 2;

        /*初始化数据点和控制点的位置*/
        startPointF.x = centerX - 200;
        startPointF.y = centerY;

        endPointF.x = centerX + 200;
        endPointF.y = centerY;

        controlPointF.x = centerX;
        controlPointF.y = centerY - 100;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controlPointF.x = event.getX();
        controlPointF.y = event.getY();
        invalidate();
        return true;
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
        canvas.drawPoint(controlPointF.x, controlPointF.y, paint);

        /*绘制辅助线*/
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        canvas.drawLine(startPointF.x, startPointF.y, controlPointF.x, controlPointF.y, paint);
        canvas.drawLine(controlPointF.x, controlPointF.y, endPointF.x, endPointF.y, paint);

        /*绘制贝塞尔曲线*/
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(4);

        Path path = new Path();
        path.moveTo(startPointF.x, startPointF.y);
        path.quadTo(controlPointF.x, controlPointF.y, endPointF.x, endPointF.y);

        canvas.drawPath(path, paint);
        /*画 最高点*/
        float bezierPointY = BezierUtil.getBezierPointF(0.5F, startPointF, endPointF, controlPointF).y;

        canvas.drawLine(0, bezierPointY, width, bezierPointY, paint);

    }


}
