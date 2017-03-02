package com.lib.circularlayoutlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;

public class CircularLayout extends View {

    private final static int TOTAL_DEGREE = 360;
    private final static int START_DEGREE = -90;

    private Paint mPaint;
    private RectF mOvalRect = null;

    private int mItemCount;
    private int mSweepAngle;

    private int mInnerRadius;
    private int mOuterRadius;
    private Bitmap mCenterIcon;
    private Bitmap[] mIcons;
    private int[] mColors;
    private String[] mTitles;
    private int mTitleColor;
    private int mTitleSize;
    private int mTitlePadding;
    private int mCenterColor;

    private SparseIntArray mHeightMap;
    private SparseIntArray mWidthMap;

    private OnCircularItemClickListener mClickListener;

    public CircularLayout(Context context) {
        this(context, null);
    }

    public CircularLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(2);

        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.CircularLayout, 0, 0);
        mItemCount = attr.getInteger(R.styleable.CircularLayout_item_count, 0);
        int centerIconResId = attr.getResourceId(R.styleable.CircularLayout_center_icon, 0);
        int iconsResId = attr.getResourceId(R.styleable.CircularLayout_item_icons, 0);
        int colorsResId = attr.getResourceId(R.styleable.CircularLayout_item_colors, 0);
        int namesResId = attr.getResourceId(R.styleable.CircularLayout_item_titles, 0);
        mInnerRadius = attr.getDimensionPixelSize(R.styleable.CircularLayout_inner_radius, 0);
        mOuterRadius = attr.getDimensionPixelSize(R.styleable.CircularLayout_outer_radius, 0);
        mTitleColor = attr.getResourceId(R.styleable.CircularLayout_title_color, 0);
        mTitleSize = attr.getDimensionPixelSize(R.styleable.CircularLayout_title_size, 0);
        mTitlePadding = attr.getDimensionPixelOffset(R.styleable.CircularLayout_title_padding, 0);
        mCenterColor = attr.getResourceId(R.styleable.CircularLayout_center_color, 0);
        attr.recycle();

        mSweepAngle = TOTAL_DEGREE / mItemCount;
        mColors = getResources().getIntArray(colorsResId);
        mCenterIcon = BitmapFactory.decodeResource(getResources(), centerIconResId);
        mTitles = getResources().getStringArray(namesResId);
        TypedArray icons = getResources().obtainTypedArray(iconsResId);

        mHeightMap = new SparseIntArray();
        mWidthMap = new SparseIntArray();
        mIcons = new Bitmap[icons.length()];
        for (int i = 0; i < icons.length(); i++) {
            mIcons[i] = BitmapFactory.decodeResource(getResources(), icons.getResourceId(i, 0));
            mHeightMap.put(i, mIcons[i].getHeight());
            mWidthMap.put(i, mIcons[i].getWidth());
        }
        icons.recycle();
    }

    @Override
    protected void onDetachedFromWindow() {
        mCenterIcon.recycle();
        for (Bitmap icon : mIcons)
            icon.recycle();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int width = getWidth();
        int height = getHeight();

        if (mOvalRect == null) {
            mOvalRect = new RectF(width / 2 - mOuterRadius, height / 2 - mOuterRadius, width / 2 + mOuterRadius, height / 2 + mOuterRadius);
        }


        for (int i = 0; i < mItemCount && i < mIcons.length; i++) {
            int startAngle = START_DEGREE + i * mSweepAngle;
            mPaint.setColor(mColors[i]);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawArc(mOvalRect, startAngle, mSweepAngle, true, mPaint);

            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawArc(mOvalRect, startAngle, mSweepAngle, true, mPaint);

            int centerX = (int) ((mOuterRadius + mInnerRadius) / 2 * Math.cos(Math.toRadians(startAngle + mSweepAngle / 2)));
            int centerY = (int) ((mOuterRadius + mInnerRadius) / 2 * Math.sin(Math.toRadians(startAngle + mSweepAngle / 2)));
            canvas.drawBitmap(mIcons[i], width / 2 + centerX - mIcons[i].getWidth() / 2, height / 2 + centerY - mIcons[i].getHeight() / 2, null);
        }

        mPaint.setColor(getResources().getColor(mTitleColor));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mTitleSize);
        for (int i = 0; i < mItemCount && i < mTitles.length; i++) {
            int h = mHeightMap.get(i);
            int w = mWidthMap.get(i);
            int startAngle = START_DEGREE + i * mSweepAngle;
            int centerX = (int) ((mOuterRadius + mInnerRadius) / 2 * Math.cos(Math.toRadians(startAngle + mSweepAngle / 2)));
            int centerY = (int) ((mOuterRadius + mInnerRadius) / 2 * Math.sin(Math.toRadians(startAngle + mSweepAngle / 2)));
            canvas.drawText(mTitles[i], width / 2 + centerX - w / 2, height / 2 + centerY + h / 2 + mTitlePadding, mPaint);
        }

        mPaint.setColor(getResources().getColor(mCenterColor));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2, mInnerRadius, mPaint);
        canvas.drawBitmap(mCenterIcon, width / 2 - mCenterIcon.getWidth() / 2, height / 2 - mCenterIcon.getHeight() / 2, null);

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int radius = mInnerRadius + mOuterRadius;
        if (event.getAction() == MotionEvent.ACTION_DOWN &&
                (Math.pow(event.getX() - (getWidth() / 2), 2) + Math.pow(event.getY() - (getHeight() / 2), 2) <= radius * radius)) {
            if ((Math.pow(event.getX() - (getWidth() / 2), 2) + Math.pow(event.getY() - (getHeight() / 2), 2) <= mInnerRadius * mInnerRadius)) {
                if (mClickListener != null) {
                    mClickListener.onCircularItemClick(-1);
                }
            } else {
                double angle = Math.toDegrees(Math.atan2(getHeight() / 2 - event.getY(), event.getX() - getWidth() / 2));
                if (angle < 0) angle = 360 + angle;

                int item = (int) angle / mSweepAngle;
                if (mClickListener != null) {
                    mClickListener.onCircularItemClick(Math.abs(item - mItemCount) % mItemCount);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void setOnCircularItemClickListener(OnCircularItemClickListener listener) {
        mClickListener = listener;
    }

    public interface OnCircularItemClickListener {
        void onCircularItemClick(int index);
    }
}
