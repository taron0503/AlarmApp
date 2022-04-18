package com.example.alarmapp.UI;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.alarmapp.R;

public class DismissButtonView extends View {
    public interface onSwapListener{
        void onSwap();
    }
    private onSwapListener swapListener;

    private Paint textPaint;
    private String text;
    private float textSize;

    private float centerX;
    private float centerY;
    private Paint circleButtonPaint;
    private Paint bgCirclePaint;
    private float buttonSize;
    private float bgCircle_stroke_width;
    private int innerCircleRadius;
    private float mainCircleradiusF = 1;

    private ValueAnimator valueAnimator;

    public DismissButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        startAnimation();
    }

    private void startAnimation(){
        valueAnimator = ValueAnimator.ofFloat(0,1);
        valueAnimator.setDuration(1500);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float size = (float) valueAnimator.getAnimatedValue();
                setMainCircleRadiusF(2f/3f+size/3);
                bgCirclePaint.setAlpha((int) ((1-size) *255));
                invalidate();
            }
        });
        valueAnimator.start();
    }



    public void setOnSwipeListener(onSwapListener swipeListener) {
        this.swapListener = swipeListener;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float dx = event.getX() - centerX;
        float dy = event.getY() - centerY;
        int radius = (int) Math.sqrt(dx*dx+dy*dy);

        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                valueAnimator.cancel();
                setInnerCircleRadius(radius);
                setMainCircleRadiusF(1);
                bgCirclePaint.setAlpha(255);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if(radius>buttonSize){
                    swapListener.onSwap();
                }
                setInnerCircleRadius(radius);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                setInnerCircleRadius(0);
                valueAnimator.start();
        }
          return true;
//        return super.onTouchEvent(event);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DismissButtonView,
                0, 0);

        try {
            text = typedArray.getString(R.styleable.DismissButtonView_setText);
            textSize = typedArray.getDimensionPixelSize(R.styleable.DismissButtonView_textSize,40);
        } finally {
            typedArray.recycle();
        }

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStrokeWidth(2);
        textPaint.setTextSize(textSize);

        circleButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleButtonPaint.setColor(getResources().getColor(R.color.colorPrimaryDark));

        bgCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgCirclePaint.setColor(Color.parseColor("#2249D3"));
        bgCirclePaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = getWidth()/2;
        centerY = getHeight()/2;
        if(getWidth() < getHeight()){
            buttonSize = getWidth()/2;
        }else{
            buttonSize = getHeight()/2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float circleButtonRadius = buttonSize/1.5f;

        bgCircle_stroke_width = buttonSize-innerCircleRadius;
        float bgCircleRadius = mainCircleradiusF * buttonSize - bgCircle_stroke_width/2;
        if(bgCircle_stroke_width>0){
            bgCirclePaint.setStrokeWidth(bgCircle_stroke_width);
            canvas.drawCircle(centerX, centerY, bgCircleRadius,bgCirclePaint);
        }

        canvas.drawCircle(centerX, centerY,circleButtonRadius, circleButtonPaint);
        canvas.drawText(text, centerX,(int) (centerY - ((textPaint.descent() + textPaint.ascent()) / 2)) , textPaint);
//
//
//        bgCircle_stroke_width = circleButtonRadius*1.5f-innerCircleRadius;
//        bgCircleRadius = mainCircleradiusF * circleButtonRadius*1.5f - bgCircle_stroke_width/2;
//        if(bgCircle_stroke_width>0){
//            bgCirclePaint.setStrokeWidth(bgCircle_stroke_width);
//            canvas.drawCircle(centerX, centerY, bgCircleRadius,bgCirclePaint);
//        }
//
//        canvas.drawCircle(centerX, centerY,circleButtonRadius, circleButtonPaint);
//        canvas.drawText(text, centerX,(int) (centerY - ((textPaint.descent() + textPaint.ascent()) / 2)) , textPaint);
    }

    private void setMainCircleRadiusF(float mainCircleRadiusF){
        this.mainCircleradiusF = mainCircleRadiusF;
    }

    private void setInnerCircleRadius(int innerCircleRadius) {
        this.innerCircleRadius = innerCircleRadius;
    }



//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int desiredWidth = 100;
//        int desiredHeight = 100;
//
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        Log.d("widthMeasureSpec",widthMeasureSpec+"");
//        Log.d("heightMeasureSpec",heightMeasureSpec+"");
//        Log.d("widthSize",widthSize+"");
//        Log.d("heightSize",heightSize+"");
//        Log.d("widthMode",widthMode+"");
//
//        int width;
//        int height;
//
//        //Measure Width
//        if (widthMode == MeasureSpec.EXACTLY) {
//            //Must be this size
//            width = widthSize;
//        } else if (widthMode == MeasureSpec.AT_MOST) {
//            //Can't be bigger than...
//            width = Math.min(desiredWidth, widthSize);
//        } else {
//            //Be whatever you want
//            width = desiredWidth;
//        }
//
//        //Measure Height
//        if (heightMode == MeasureSpec.EXACTLY) {
//            //Must be this size
//            height = heightSize;
//        } else if (heightMode == MeasureSpec.AT_MOST) {
//            //Can't be bigger than...
//            height = Math.min(desiredHeight, heightSize);
//        } else {
//            //Be whatever you want
//            height = desiredHeight;
//        }
//
//        //MUST CALL THIS
//        Log.d("height",height+"");
//        Log.d("width",width+"");
//
//        setMeasuredDimension(width, height);
//    }
}

