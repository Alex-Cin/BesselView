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
 *
 */

public class BezierView2_2 extends View {
    protected Paint paint;
    protected int width, height, centerX, centerY;
    protected PointF startPointF, endPointF, controlPointF;

    public BezierView2_2(Context context, AttributeSet attrs) {
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

        PointF bezierPointF = new PointF();
        bezierPointF.x = centerX;
        bezierPointF.y = centerY - 100;
        controlPointF = BezierUtil.getControlPointF(startPointF, endPointF, bezierPointF);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF bezierPointF = new PointF();
        bezierPointF.x = centerX;
        bezierPointF.y = event.getY();
        controlPointF = BezierUtil.getControlPointF(startPointF, endPointF, bezierPointF);
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
        /*画 最高点*/
        PointF bezierPointF = BezierUtil.getBezierPointF(0.5F, startPointF, endPointF, controlPointF);
        canvas.drawPoint(bezierPointF.x, bezierPointF.y, paint);


        /*绘制贝塞尔曲线*/
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(4);

        Path path = new Path();
        path.moveTo(startPointF.x, startPointF.y);
        path.quadTo(controlPointF.x, controlPointF.y, endPointF.x, endPointF.y);
        canvas.drawPath(path, paint);
    }


}
