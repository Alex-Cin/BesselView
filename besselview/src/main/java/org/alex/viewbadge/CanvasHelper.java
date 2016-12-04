package org.alex.viewbadge;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * 作者：Alex
 * 时间：2016年12月03日
 * 简述：
 */

public class CanvasHelper {
    protected String text;
    protected int textColor, backgroundColor;
    protected float textSize;
    protected int width, height, radius, centerX, centerY;
    protected int paddingRadius, paddingV, paddingH;
    protected float scale;
    protected int shape;

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public CanvasHelper setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public int getCenterX() {
        return centerX;
    }

    public CanvasHelper setCenterX(int centerX) {
        this.centerX = centerX;
        return this;
    }

    public int getCenterY() {
        return centerY;
    }

    public CanvasHelper setCenterY(int centerY) {
        this.centerY = centerY;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public CanvasHelper setHeight(int height) {
        this.height = height;
        return this;
    }

    public int getPaddingH() {
        return paddingH;
    }

    public CanvasHelper setPaddingH(int paddingH) {
        this.paddingH = paddingH;
        return this;
    }

    public int getPaddingRadius() {
        return paddingRadius;
    }

    public CanvasHelper setPaddingRadius(int paddingRadius) {
        this.paddingRadius = paddingRadius;
        return this;
    }

    public int getPaddingV() {
        return paddingV;
    }

    public CanvasHelper setPaddingV(int paddingV) {
        this.paddingV = paddingV;
        return this;
    }

    public int getRadius() {
        return radius;
    }

    public CanvasHelper setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public float getScale() {
        return scale;
    }

    public CanvasHelper setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public int getShape() {
        return shape;
    }

    public CanvasHelper setShape(int shape) {
        this.shape = shape;
        return this;
    }

    public String getText() {
        return text;
    }

    public CanvasHelper setText(String text) {
        this.text = text;
        return this;
    }

    public int getTextColor() {
        return textColor;
    }

    public CanvasHelper setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public float getTextSize() {
        return textSize;
    }

    public CanvasHelper setTextSize(float textSize) {
        this.textSize = textSize;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public CanvasHelper setWidth(int width) {
        this.width = width;
        return this;
    }

    /**
     * 画  徽章  的背景
     *
     * @param canvas
     */
    public void drawBackgroundShape(Canvas canvas, Paint paint) {
        if (shape == BadgeShape.shapeOval) {
            canvas.drawCircle(centerX, centerY, radius, paint);
        } else if (shape == BadgeShape.shapeRectangle) {
            RectF rectF = new RectF(0, 0, width, height);
            canvas.drawRoundRect(rectF, 300, 300, paint);
        }
    }

    /**
     * 画 文本
     *
     * @param canvas
     */
    public void drawText(Canvas canvas, Paint paint, float centerX, float centerY) {
        if (text != null) {
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            /*计算Baseline绘制的起点X轴坐标 ，计算方式：画布宽度的一半 - 文字宽度的一半*/
            int baseX = (int) (centerX - paint.measureText(text) / 2);
            /* 计算Baseline绘制的Y坐标 ，计算方式：画布高度的一半 - 文字总高度的一半*/
            int baseY = (int) ((centerY) - (paint.descent() + paint.ascent()) / 2);
            /*居中画一个文字*/
            canvas.drawText(text, baseX, baseY, paint);
        }
    }
}
