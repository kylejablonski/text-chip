package com.divshark.text_chip;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Text Chip View
 * Created by kyle.jablonski on 4/22/16.
 */
public final class TextChip extends View {

    private static final String TAG = TextChip.class.getSimpleName();

    // Default values
    private static final String TEXT_CHIP = "Text Chip";
    private static final float HEIGHT = 32f;
    private static final float PADDING = 12f;
    private static final float DEFAULT_TEXT_SIZE = 14f;
    private static final float CHIP_CORNER_RADIUS = 2f;

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
    private boolean mIsUpperCase = true;

    private float mCornerRadius;
    private float mDefaultHeight;

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
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {

        if(mIsUpperCase){
            this.mText = text.toUpperCase().trim();
        }else{
            this.mText = text.trim();
        }

        computeBounds();
        invalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        mTextPaint.setColor(textColor);
        invalidate();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;

        mTextPaint.setTextSize(textSize);
        mCornerRadius = DeviceDimensionsHelper.convertDpToPixel(mTextSize / CHIP_CORNER_RADIUS, getContext());

        computeBounds();
        invalidate();
    }

    public void setUpperCase(boolean isUpperCase){
        this.mIsUpperCase = isUpperCase;

        if(mIsUpperCase){
            this.mText = mText.toUpperCase().trim();
        }else{
            this.mText = mText.trim();
        }

        invalidate();
    }

    public boolean isUpperCase(){
        return mIsUpperCase;
    }

    private void initDefaults(AttributeSet attrs){

        if(attrs != null){

            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TextChip);

            mBackgroundColor = typedArray.getColor(R.styleable.TextChip_tc_backgroundColor, getResources().getColor(R.color.accent_color));
            String text = typedArray.getString(R.styleable.TextChip_tc_text) != null ? typedArray.getString(R.styleable.TextChip_tc_text) : TEXT_CHIP;
            mTextColor = typedArray.getColor(R.styleable.TextChip_tc_textColor, getResources().getColor(R.color.primary_white));
            mTextSize = typedArray.getDimension(R.styleable.TextChip_tc_textSize, DeviceDimensionsHelper.convertDpToPixel(DEFAULT_TEXT_SIZE, getContext()));
            mIsUpperCase = typedArray.getBoolean(R.styleable.TextChip_tc_upperCase, true);

            if(mIsUpperCase){
                mText = text.toUpperCase();
            }else{
                mText = text;
            }

            typedArray.recycle();

            mCornerRadius = DeviceDimensionsHelper.convertDpToPixel(mTextSize / CHIP_CORNER_RADIUS, getContext());

        }

        mDefaultHeight = DeviceDimensionsHelper.convertDpToPixel(HEIGHT, getContext());

        initPainters();

        computeBounds();
    }

    private void initPainters(){

        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setStrokeWidth(1.5f);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setColor(mBackgroundColor);
    }

    private void computeBounds(){

        // Compute height & width
        mTextHeight = mTextPaint.getTextSize() - (mTextPaint.ascent() + mTextPaint.descent());
        mTextWidth = mTextPaint.measureText(mText);

        // Compute rect height as textHeight + (PADDING)
        int rectPadding = (int) DeviceDimensionsHelper.convertDpToPixel(PADDING, getContext());
        int rectHeight = (int) (mTextHeight + rectPadding);

        // Compute rect width as textWidth + (PADDING)

        Rect backgroundRect = new Rect(0, 0, (int) mTextWidth + (rectPadding), rectHeight);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int paddingTop = getPaddingTop();

        mRoundedRect = new RectF(backgroundRect.left + paddingLeft, backgroundRect.top + paddingTop,
                backgroundRect.right + paddingRight, backgroundRect.bottom + paddingBottom);

        Log.d(TAG, mRoundedRect.toString());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        int middleX = width / 2;
        int middleY = height / 2;

        // Paint the chip background
        canvas.drawRoundRect(mRoundedRect, mCornerRadius, mCornerRadius, mBackgroundPaint);

        // Paint the text in the middle of the View
        canvas.drawText(mText, middleX - (mTextPaint.measureText(mText) / 2) , middleY + (mTextPaint.getTextSize() / 2), mTextPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        computeBounds();

        invalidate();
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
}
