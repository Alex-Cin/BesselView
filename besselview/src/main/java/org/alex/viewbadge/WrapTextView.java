package org.alex.viewbadge;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.alex.besselview.R;

import static org.alex.viewbadge.BadgeShape.shapeOval;
import static org.alex.viewbadge.BadgeShape.shapeRectangle;

/**
 * 作者：Alex
 * 时间：2016年12月03日
 * 简述：
 */
public class WrapTextView extends View implements View.OnTouchListener {
    /**
     * 每次滑动，捕捉到的，滑动的距离，向右 向下滑动为正
     */
    private float lastX, lastY, distanceX, distanceY;
    protected String text;
    protected int textColor, backgroundColor;
    protected float textSize;
    protected Paint paint;
    protected int width, height, radius, centerX, centerY;
    protected int paddingRadius, paddingV, paddingH;
    protected float scale;
    protected int shape;
    protected int dismissBoomResId;
    private CanvasHelper canvasHelper;
    /**
     * 做爆照效果的 View
     */
    private ImageView dismissBoomView;
    private RelativeLayout dismissBoomLayout;
    private DraggingView draggingView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams wmLayoutParams;
    private OnDismissListener onDismissListener4WrapTextView;
    private OnDraggingStatusListener onDraggingStatusListener;
    private AnimationDrawable dismissBoomAnimationDrawable;
    /**
     * 粒子 爆炸 时间
     */
    private long duration;
    private RelativeLayout.LayoutParams dismissBoomViewParams;

    public WrapTextView(Context context) {
        super(context);
        initView(context, null);
    }

    public WrapTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);

    }

    public void initView(Context context, AttributeSet attrs) {
        duration = 0;
        scale = 1.0F;
        canvasHelper = new CanvasHelper();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WrapView);
        text = typedArray.getString(R.styleable.WrapView_wv_text);
        textSize = typedArray.getDimension(R.styleable.WrapView_wv_textSize, sp2px(14));
        paddingRadius = (int) typedArray.getDimension(R.styleable.WrapView_wv_paddingRadius, dp2px(4));
        paddingV = (int) typedArray.getDimension(R.styleable.WrapView_wv_paddingV, dp2px(4));
        paddingH = (int) typedArray.getDimension(R.styleable.WrapView_wv_paddingH, dp2px(8));
        textColor = typedArray.getColor(R.styleable.WrapView_wv_textColor, Color.parseColor("#FFFFFF"));
        backgroundColor = typedArray.getColor(R.styleable.WrapView_wv_backgroundColor, Color.parseColor("#FF0000"));
        shape = typedArray.getInteger(R.styleable.WrapView_wv_shape, 0);
        dismissBoomResId = typedArray.getResourceId(R.styleable.WrapView_wv_dismissBoomResId, -1);
        typedArray.recycle();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);

        setOnTouchListener(this);
        /****************/
        this.dismissBoomView = new ImageView(context);
        this.draggingView = new DraggingView(context);
        this.dismissBoomLayout = new RelativeLayout(context);

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initLayoutParams();
        //draggingView.setOnTouchListener(this);
        onDraggingStatusListener = new MyOnDraggingStatusListener();
        draggingView.setOnDraggingStatusListener(onDraggingStatusListener);
        draggingView.setCanvasHelper(canvasHelper);
        if (dismissBoomResId > 0) {
            setDismissBoomResId(dismissBoomResId);
        }
        dismissBoomView.setVisibility(View.GONE);
        dismissBoomLayout.addView(dismissBoomView);
        dismissBoomLayout.setVisibility(View.GONE);
        dismissBoomViewParams = (RelativeLayout.LayoutParams) dismissBoomView.getLayoutParams();
        windowManager.addView(dismissBoomLayout, wmLayoutParams);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(backgroundColor);
        canvasHelper.drawBackgroundShape(canvas, paint);
        canvasHelper.drawText(canvas, paint, centerX, centerY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = getTextWidth(paint, text);
        height = (int) (getTextHeight(paint) * getScale());
        int size = Math.max(width + paddingH, height + paddingV);
        radius = size / 2;

        if (shape == shapeOval) {
            width = size;
            height = size;
            setMeasuredDimension(size, size);
        } else if (shape == shapeRectangle) {
            width += paddingH;
            height += paddingV;
            setMeasuredDimension(width, height);
        }
        centerX = width / 2;
        centerY = height / 2;
        canvasHelper.setCenterX(centerX).setCenterY(centerY)
                .setWidth(width).setHeight(height).setRadius(radius)
                .setShape(shape).setTextSize(textSize)
                .setBackgroundColor(backgroundColor).setTextColor(textColor).setText(text);
        int[] location = getCenterLocationInContentView(this);
        draggingView.setLocation(location[0], location[1]);
        draggingView.setDragRadius(width / 2);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (MotionEvent.ACTION_DOWN == action) {
            lastX = event.getRawX();
            lastY = event.getRawY();
            showDraggingView();
        } else if (MotionEvent.ACTION_MOVE == action) {
            distanceX = event.getRawX() - lastX;
            distanceY = event.getRawY() - lastY;
            if (Math.abs(distanceX) > 6 || Math.abs(distanceY) > 6) {
                setVisibility(View.INVISIBLE);
            } else {
                return true;
            }
        }
        return draggingView.onTouch(draggingView, event);
    }

    /**
     * 0.6-1.0之间，控制文本的留白区域
     */
    public float getScale() {
        return scale;
    }

    /**
     * 获取文字的宽度
     *
     * @param paint
     * @param str
     * @return
     */
    public int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    /**
     * 计算文字的高度
     *
     * @param paint
     * @return
     */
    public int getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
    }

    /**
     * sp转px
     */
    public int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getContext().getResources().getDisplayMetrics());
    }

    /**
     * 数据转换: dp---->px
     */
    private float dp2px(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

    private void initLayoutParams() {
        wmLayoutParams = new WindowManager.LayoutParams();
        wmLayoutParams.gravity = Gravity.LEFT + Gravity.TOP;
        wmLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmLayoutParams.format = PixelFormat.TRANSLUCENT;
        wmLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        wmLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    public void setDismissBoomResId(int dismissBoomResId) {
        dismissBoomView.setImageResource(dismissBoomResId);

        dismissBoomAnimationDrawable = (dismissBoomAnimationDrawable == null) ? (AnimationDrawable) dismissBoomView.getDrawable() : dismissBoomAnimationDrawable;
        for (int i = 0; i < dismissBoomAnimationDrawable.getNumberOfFrames(); i++) {
            duration += dismissBoomAnimationDrawable.getDuration(i);
        }
    }

    public int[] getCenterLocationInContentView(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        location[0] += view.getMeasuredWidth() / 2;
        location[1] -= getStatusBarHeight() - view.getMeasuredHeight() / 2;
        return location;
    }

    /**
     * 获得状态栏的高度
     */
    private int getStatusBarHeight() {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = getContext().getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        }
        return statusHeight;
    }

    /**
     * 设置  要展示的 文本
     *
     * @param text
     * @return
     */
    public WrapTextView setText(Object text) {
        this.text = (text == null) ? "" : text.toString();
        invalidate();
        return this;
    }

    private final class MyOnDraggingStatusListener implements OnDraggingStatusListener {
        /**
         *
         */
        @Override
        public void onDraggingRollback() {
            hiddenDraggingView();
            showBadge();
        }

        @Override
        public void onDraggingDisconnect(float centerX, float centerY) {
            windowManager.removeView(draggingView);
            startDismissBoomAnim(centerX, centerY);
        }
    }

    /**
     * 开始  爆照动画
     *
     * @param centerX 可拖动大圆的 中心点
     * @param centerY 可拖动大圆的 中心点
     */
    private void startDismissBoomAnim(final float centerX, final float centerY) {
        showDismissBoomView();
        int l = (int) (centerX - dismissBoomView.getMeasuredWidth() - width / 2);
        int t = (int) (centerY - dismissBoomView.getMeasuredHeight() - height / 2);
        dismissBoomViewParams.leftMargin = l;
        dismissBoomViewParams.topMargin = t;
        dismissBoomView.setLayoutParams(dismissBoomViewParams);
        dismissBoomAnimationDrawable = (dismissBoomAnimationDrawable == null) ? (AnimationDrawable) dismissBoomView.getDrawable() : dismissBoomAnimationDrawable;
        dismissBoomAnimationDrawable.stop();
        dismissBoomAnimationDrawable.start();
        hiddenBadge();
        invokeOnDismiss4WrapTextView(centerX, centerY, BoomStatus.start, duration);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                hiddenDismissBoomView();
                invokeOnDismiss4WrapTextView(centerX, centerY, BoomStatus.end, duration);
            }
        }, duration);
    }

    /**
     * 展示 通知数量，
     */
    public void showBadge() {
        setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏 通知数量，
     */
    public void hiddenBadge() {
        setVisibility(View.INVISIBLE);
    }

    private void showDismissBoomView() {
        dismissBoomView.setVisibility(View.VISIBLE);
        dismissBoomLayout.setVisibility(View.VISIBLE);
    }

    private void hiddenDismissBoomView() {
        dismissBoomView.setVisibility(View.GONE);
        dismissBoomLayout.setVisibility(View.GONE);
    }

    private void hiddenDraggingView() {
        windowManager.removeView(draggingView);
    }

    private void showDraggingView() {
        if (draggingView.getParent() == null) {
            windowManager.addView(draggingView, wmLayoutParams);
        }
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener4WrapTextView = onDismissListener;
    }

    /**
     * 响应 爆炸效果的回调
     *
     * @param centerX
     * @param centerY
     * @param boomStatus
     * @param duration
     */
    private void invokeOnDismiss4WrapTextView(float centerX, float centerY, String boomStatus, long duration) {
        if (onDismissListener4WrapTextView != null) {
            onDismissListener4WrapTextView.onDismissBoom(centerX, centerY, boomStatus, duration);
        }
    }


}
