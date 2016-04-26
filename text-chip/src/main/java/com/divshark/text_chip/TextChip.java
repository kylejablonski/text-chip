package com.divshark.text_chip;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Text Chip View
 * Created by kyle.jablonski on 4/22/16.
 */
public final class TextChip extends View {

    // TAG for Logging
    private static final String TAG = TextChip.class.getSimpleName();

    // Default values
    private static final String TEXT_CHIP = "Text Chip";
    private static final float HEIGHT = 32f;
    private static final float PADDING = 6f;
    private static final float DEFAULT_TEXT_SIZE = 26f;
    private static final float CHIP_CORNER_RADIUS = 2f;
    private static final float DEFAULT_STROKE_WIDTH = 0.5f;

    private static final String DEFAULT_BG_COLOR = "#EBEBEB";

    // Painters
    private Paint mTextPaint;
    private Paint mBackgroundPaint;

    // Background RectF for the Chip
    private RectF mRoundedRect;

    // Styleable attributes
    private int mTextColor;
    private int mBackgroundColor;
    private float mTextSize;
    private float mTextHeight;
    private float mTextWidth;
    private String mText;
    private String mOriginalText;
    private boolean mIsUpperCase = true;
    private float mStrokeWidth = 1.0f;

    private float mCornerRadius;
    private float mInternalPadding;
    private float mDefaultHeight;

    private Drawable mForeGroundDrawable;

    public TextChip(Context context, String text) {
        super(context);
        this.mText = text;
    }

    public TextChip(Context context, AttributeSet attrs) {
        super(context, attrs);

        initDefaults(attrs);
    }

    public TextChip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initDefaults(attrs);
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
        mBackgroundPaint.setColor(backgroundColor);
        invalidate();
        requestLayout();
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mOriginalText = text;

        if(mIsUpperCase){
            this.mText = text.toUpperCase().trim();
        }else{
            this.mText = mOriginalText.trim();
        }
        computeBounds();
        invalidate();
        requestLayout();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        mTextPaint.setColor(textColor);
        invalidate();
        requestLayout();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
        mTextPaint.setTextSize(textSize);
        computeBounds();
        invalidate();
        requestLayout();
    }

    public void setUpperCase(boolean isUpperCase){
        this.mIsUpperCase = isUpperCase;

        if(mIsUpperCase){
            this.mText = getText().toUpperCase().trim();
        }else{
            this.mText = mOriginalText.trim();
        }

        computeBounds();
        invalidate();
        requestLayout();
    }

    public boolean isUpperCase(){
        return mIsUpperCase;
    }


    public float getStrokeWidth(){
        return this.mStrokeWidth;
    }

    public void setStrokeWidth(float strokeWidth){
        this.mStrokeWidth = strokeWidth;

        mTextPaint.setStrokeWidth(mStrokeWidth);

        computeBounds();
        invalidate();
        requestLayout();
    }

    private void initDefaults(AttributeSet attrs){

        if(attrs != null) {

            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TextChip);

            mBackgroundColor = typedArray.getColor(R.styleable.TextChip_tc_backgroundColor, Color.parseColor(DEFAULT_BG_COLOR));
            mTextColor = typedArray.getColor(R.styleable.TextChip_tc_textColor, getResources().getColor(R.color.primary_black));
            mTextSize = typedArray.getDimension(R.styleable.TextChip_tc_textSize, DeviceDimensionsHelper.convertDpToPixel(DEFAULT_TEXT_SIZE, getContext()));
            mStrokeWidth = typedArray.getDimension(R.styleable.TextChip_tc_strokeWidth, DeviceDimensionsHelper.convertDpToPixel(DEFAULT_STROKE_WIDTH, getContext()));
            mIsUpperCase = typedArray.getBoolean(R.styleable.TextChip_tc_upperCase, true);
            String text = typedArray.hasValue(R.styleable.TextChip_tc_text) ? typedArray.getString(R.styleable.TextChip_tc_text) : TEXT_CHIP;

            if (mIsUpperCase) {
                mText = text.toUpperCase();
            } else {
                mText = text;
            }

            typedArray.recycle();
        }

        mForeGroundDrawable = getResources().getDrawable(R.drawable.chip_selector);
        mForeGroundDrawable.setCallback(this);

        mDefaultHeight = DeviceDimensionsHelper.convertDpToPixel(HEIGHT, getContext());

        // Compute the internal padding
        mInternalPadding = DeviceDimensionsHelper.convertDpToPixel(2 * PADDING, getContext());

        initPainters();

        computeBounds();
    }

    private void initPainters(){

        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setStrokeWidth(mStrokeWidth);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setColor(mBackgroundColor);
    }

    private void computeBounds(){

        // Compute height & width
        mTextHeight = mTextPaint.getTextSize() - (mTextPaint.ascent() + mTextPaint.descent());
        mTextWidth = mTextPaint.measureText(mText);

        // Compute the corner radius
        mCornerRadius = DeviceDimensionsHelper.convertDpToPixel(mTextHeight / CHIP_CORNER_RADIUS, getContext());

        int rectWidth = (int) (mTextWidth + (2f * mInternalPadding));
        int rectHeight = (int) Math.max(mDefaultHeight, mTextHeight + mInternalPadding);

        // Compute rect width as textWidth + (rectPadding)
        Rect backgroundRect = new Rect(0, 0, rectWidth, rectHeight);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int paddingTop = getPaddingTop();

        mRoundedRect = new RectF(backgroundRect.left + paddingLeft, backgroundRect.top + paddingTop,
                backgroundRect.right + paddingRight, backgroundRect.bottom + paddingBottom);



    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int middleX = getWidth() / 2;
        int middleY = getHeight() / 2;

        // Paint the chip background
        // Rect, x radius, y radius, paint
        canvas.drawRoundRect(mRoundedRect, mCornerRadius, mCornerRadius, mBackgroundPaint);

        // Paint the text in the middle of the View
        // text, x, y, paint
        float textX = middleX - (mTextWidth / 2f);
        float textY = middleY + (mTextHeight / 4f);
        canvas.drawText(mText, textX, textY, mTextPaint);

        mForeGroundDrawable.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Compute height
        float verticalPadding = getPaddingBottom() + getPaddingTop();
        int desiredHeight = (int) (mRoundedRect.height() + verticalPadding);

        // compute width
        float horizontalPadding = getPaddingLeft() + getPaddingRight();
        int desiredWidth = (int) mRoundedRect.width() + (int) horizontalPadding;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        mForeGroundDrawable.setState(getDrawableState());

        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mForeGroundDrawable.setBounds((int) mRoundedRect.left,(int) mRoundedRect.top,(int) mRoundedRect.right,(int) mRoundedRect.bottom);
    }

    @SuppressWarnings("NewApi")
    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        if(mForeGroundDrawable != null){
            mForeGroundDrawable.setHotspot(x,y);
        }
    }
}
